package org.mycocosm.abaqs.compare_tracks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableInt;
import org.mycocosm.framework.biology.FeatureGeneric;
import org.mycocosm.framework.biology.FeatureModel;
import org.mycocosm.framework.biology.LocusKey;
import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.logger.LoggerHelper;
import org.mycocosm.framework.time.PeriodicActivity;
import org.mycocosm.framework.utils.ProgressTracker;
import org.mycocosm.gff3.GFF3Data;
import org.mycocosm.gff3.Gff3Record;
import org.mycocosm.gff3.Gff3Type;

public class FeatureTrack {
	public final String name;
	public final FeatureTrackType type;
	public final Set<FeatureGeneric> features;
	private Map<LocusKey,FeatureGeneric[]> featuresIndexByLocusKey; // ordered by feature.start
	private final Pattern scaffoldsToInclude;
	private final Pattern scaffoldsToExclude;
	public FeatureTrack(String name, FeatureTrackType type, Pattern scaffoldsToInclude, Pattern scaffoldsToExclude) {
		this.name = name;
		this.type = type;
		this.features = new HashSet<>();
		this.featuresIndexByLocusKey=null;
		this.scaffoldsToInclude = scaffoldsToInclude;
		this.scaffoldsToExclude = scaffoldsToExclude;
	}
	
	public static final TrackToTrackMapping mapTwoTracksByPositionOverlap (
			Logger logger, 
			FeatureTrack featureTrackA,
			FeatureTrack featureTrackB,
			Comparator<FeaturePair> bestPossibleMatchComparator
			) {
		Map<FeatureGeneric,FeaturePair> mappedPairs = new HashMap<>(); // featureB -> pair

		// Map side A to side B by overlap

		ProgressTracker tracker = new ProgressTracker(featureTrackA.features.size());
		PeriodicActivity reporter = new PeriodicActivity(Duration.ofSeconds(15), ()->{
			LoggerHelper.log(logger,Level.INFO,"Processed %,d(%.2f%%) features, mapped: %,d",tracker.getCounter(),100.0*tracker.getProgress(),mappedPairs.size());
		}).excludingInitialRun();


		// Need to reconsider mapping in case if later we got better mapping from some other featureA to the same featureB
		List<FeatureGeneric> reconsiderSideAFeatures = new ArrayList<>(); 
		List<FeatureGeneric> sideAFeatures = new ArrayList<>(featureTrackA.features);
		do {
			for (FeatureGeneric featureA:sideAFeatures) {
				FeaturePair pair = featureTrackB.findBestFeatureByOverlap(mappedPairs,featureA,reconsiderSideAFeatures,bestPossibleMatchComparator); 
				if (pair!=null) {
					mappedPairs.put(pair.b,pair);
				}
				tracker.increaseAndGetProgress();
				reporter.mayRun();
			}
			sideAFeatures = new ArrayList<>(reconsiderSideAFeatures);
			reconsiderSideAFeatures.clear();
		} while (!sideAFeatures.isEmpty());
		reporter.run();
		
		Set<FeatureGeneric> sideANotMapped = new HashSet<>(featureTrackA.features);
		Set<FeatureGeneric> sideBNotMapped = new HashSet<>(featureTrackB.features);
		mappedPairs.values().forEach(p->{
			sideANotMapped.remove(p.a);
			sideBNotMapped.remove(p.b);
		});
		return new TrackToTrackMapping (
				CollectionsHelper.asList(mappedPairs.values()), 
				sideANotMapped, 
				sideBNotMapped);
	}

	private FeaturePair findBestFeatureByOverlap(Map<FeatureGeneric,FeaturePair> mappedPairs, FeatureGeneric featureA, List<FeatureGeneric> reconsiderQueue, Comparator<FeaturePair> bestPossibleMatchComparator) {
		FeatureGeneric[] segment = featuresIndexByLocusKey.get(featureA.key.locus.key());
		if (segment!=null && segment.length>0) {
			int left=0;
			int right=segment.length-1;			
			int mid=left+(right-left)/2;
			int featureEnd = featureA.getEffectiveEnd();
			boolean found=false;
			while (!found) {
				if (left>=right-1) { // close to the end
					found=true;
					mid=right;
				} else {
					int midFeatureStart = segment[mid].getEffectiveStart();
					int downFeatureStart = segment[mid-1].getEffectiveStart();
					if (downFeatureStart<featureEnd && midFeatureStart>=featureEnd) {
						// found the middle point, exiting while loop
						found=true;
					} else if (downFeatureStart<featureEnd && midFeatureStart<featureEnd) { // move up
						left=mid;
						mid=left+(right-left)/2;
					} else { // move down
						right=mid;
						mid=left+(right-left)/2;
					}
				}
			}
			// move down
			int current=mid;

			List<FeaturePair> allPossibleMappings = new ArrayList<>();

			while(current>=0 && segment[current].getEffectiveEnd()>featureA.getEffectiveStart()) {
				if (!segment[current].equals(featureA)) {
					double overlap = segment[current].computeOverlap(featureA);
					if (overlap>0.0) {
						allPossibleMappings.add(new FeaturePair(featureA, segment[current], overlap));
					}
				}
				current--;
			}
			if (!allPossibleMappings.isEmpty()) {
				// sort all found mappings  best on top
				for (FeaturePair m:allPossibleMappings.stream().sorted((p1,p2)->bestPossibleMatchComparator.compare(p2,p1)/* reverted order! */).collect(Collectors.toList())) {
					// check if featureB was already mapped, and this mapping is better than previous mapping to the same fearureB
					// than delete previous mapping to the same featureB and put corresponding  featureA from deleted mapping
					// to the queue to "reconsider" mappings, and than return new mapping to that featureB
					FeaturePair possiblePreviousMappingforSameFeatureB = mappedPairs.get(m.b);
					if (possiblePreviousMappingforSameFeatureB!=null) {
						if (bestPossibleMatchComparator.compare(possiblePreviousMappingforSameFeatureB, m)<0) {
						// m is better than possiblePreviousMappingforSameFeatureB
							mappedPairs.remove(m.b);
							reconsiderQueue.add(possiblePreviousMappingforSameFeatureB.a);
							return m;
						}
					} else {
						// no other mapping to the same featureB
						return m;
					}
				}
				// some mappings found, but already mapped by same side b with better score
				return null;
			} else {
				// no mapping found
				return null;
			}
		} else {
			return null;
		}
	}
	protected void consumemRNA(Gff3Record mRNA, Supplier<Integer> idGeneator) {
		String chrom = mRNA.seqid;
		if (includeByScaffold(chrom)) {
			switch (type) {
			case generic:
				features.add(FeatureGeneric.fromGff3mRNA(mRNA, idGeneator));
				break;
			case model:
				features.add(FeatureModel.fromGff3mRNA(mRNA, idGeneator));
				break;
			}
		}
	}

	private boolean includeByScaffold(String chrom) {
		if (scaffoldsToInclude==null && scaffoldsToExclude==null) {
			return true;
		} else if (scaffoldsToInclude!=null && scaffoldsToExclude==null) { // use include only
			return scaffoldsToInclude.matcher(chrom).matches();
		} else if (scaffoldsToInclude==null && scaffoldsToExclude!=null) { // use exclude only
			return !scaffoldsToExclude.matcher(chrom).matches();
		} else { // both non null
			boolean include = scaffoldsToInclude.matcher(chrom).matches();
			boolean exclude = scaffoldsToExclude.matcher(chrom).matches();
			if (include) {
				return true;
			} else if (exclude) {
				return false;
			} else {
				return true;
			}
		}
	}
	public static final FeatureTrack createTrackFromGff3Data(GFF3Data gff3, TrackNameAndType track, Pattern scaffoldsToInclude, Pattern scaffoldsToExclude) {
		FeatureTrack ret = new FeatureTrack(track.name,track.type,scaffoldsToInclude,scaffoldsToExclude);
		final MutableInt featureId = new MutableInt(); 
		gff3.getRecordsByPredicate(r->Gff3Type.mRNA.equals(r.type)).forEach(mRNA->{
			ret.consumemRNA(mRNA, featureId::incrementAndGet);
		});
		ret.createFeaturesIndex();
		return ret;
	}

	private void createFeaturesIndex() {
		Map<LocusKey,List<FeatureGeneric>> tempIndex = new HashMap<>();
		features.forEach(feature->{
			tempIndex.computeIfAbsent(feature.key.locus.key(), k->new ArrayList<>()).add(feature);
		});
		this.featuresIndexByLocusKey = new HashMap<>();
		tempIndex.forEach((key,value)->{
			this.featuresIndexByLocusKey.put(key, value.stream().sorted((f1,f2)->Integer.compare(f1.getEffectiveStart(),f2.getEffectiveStart())).toArray(FeatureGeneric[]::new));
		});
	}
}
