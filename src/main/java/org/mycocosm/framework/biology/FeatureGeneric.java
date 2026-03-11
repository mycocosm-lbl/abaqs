package org.mycocosm.framework.biology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.mycocosm.framework.text.TextHelper;
import org.mycocosm.gff3.Gff3Record;
import org.mycocosm.gff3.Gff3Type;

public class FeatureGeneric {
	public final FeatureKey key;
	public final int id;
	public final String name;
	public final int sfCount;
	public final int[] sfStarts;
	public final int[] sfEnds;
	public final String subtype;
	public final String url;
	public final String urlDesc;
	
	FeatureGeneric(FeatureKey key, int id, String name, int sfCount, int[] sfStarts, int[] sfEnds, String subtype, String url, String urlDesc) {
		this.key = key;
		this.id = id;
		this.name = name;
		this.sfCount = sfCount;
		this.sfStarts = sfStarts;
		this.sfEnds = sfEnds;
		this.subtype=subtype;
		this.url=url;
		this.urlDesc=urlDesc;
	}
	
	public static FeatureGeneric fromGff3mRNA(Gff3Record mRNA, Supplier<Integer> idGeneator) {
		List<SequenceSegment> exons = mRNA.getSegmentsByType(Gff3Type.exon);
		int[] starts = new int[exons.size()];
		int[] ends = new int[exons.size()];
		for (int index=0;index<exons.size();index++) {
			starts[index]=exons.get(index).start;
			ends[index]=exons.get(index).end;
		}
		return new FeatureGeneric (
			FeatureKey.fromGff3mRNA(mRNA),	
			idGeneator.get(), // Id
			mRNA.id, // Name
			exons.size(), // sf_count
			starts,
			ends,
			null, // subtype
			null, // url
			null // url_desc
		);
	}

	protected static final int[] toSortedIntArray(String str) {
		return TextHelper.splitAndParceToCollection(new ArrayList<>(), str, Integer::valueOf).stream().sorted().mapToInt(i->i).toArray();
	}
	
	public int getLengthGenomic() {
		return key.locus.getLength();
	}
	public int getEffectiveStart() {
		return key.locus.start;
	}
	public int getEffectiveEnd() {
		return key.locus.end;
	}
	
	/*
	 * @return value between 0.0 and 1.0 where 0.0 mean no overlap and 1.0 is full overlap (both sides starts and ends are equal)  
	 */
	public double computeOverlap(FeatureGeneric other) {
		if (key.locus.scaffold.equals(other.key.locus.scaffold) && key.locus.strand.equals(other.key.locus.strand)) {
			int thisFrom = Math.min(getEffectiveStart(), getEffectiveEnd());
			int thisTo = Math.max(getEffectiveStart(), getEffectiveEnd());
			int otherFrom = Math.min(other.getEffectiveStart(), other.getEffectiveEnd());
			int otherTo = Math.max(other.getEffectiveStart(), other.getEffectiveEnd());
			if (thisFrom<otherTo && thisTo>otherFrom) {
				int base = Math.max(thisTo, otherTo) - Math.min(thisFrom, otherFrom);
				int overlap = Math.min(thisTo, otherTo) - Math.max(thisFrom, otherFrom);
				return (double) overlap / (double) base;
			} else {
				return 0.0;
			}
		} else {
			return 0.0;
		}
	}
	public boolean isStructurallyEquivalentTo(FeatureGeneric other) {
		return 	sfCount == other.sfCount &&
				Arrays.equals(sfStarts, other.sfStarts) &&
				Arrays.equals(sfEnds, other.sfEnds);
	}
	public boolean isStructurallyEqual(FeatureGeneric modelB) {
		return  key.equals(modelB.key) && 
				isStructurallyEquivalentTo(modelB);
	}
	public static final String getLoadTrackSql(String track) {
		return String.format("select * from `%s`", track);
	}
	public static final int compareByScaffoldPosition(FeatureGeneric f1, FeatureGeneric f2) {
		return Locus.compareByScaffoldPosition(f1.key.locus, f2.key.locus);
	}
	public List<SequenceSegment> getExonsSegments() {
		List<SequenceSegment> ret = new ArrayList<>();
		for (int index=0;index<sfCount;index++) {
			ret.add(new SequenceSegment(sfStarts[index],sfEnds[index]));
		}
		return ret;
	}
	public List<SequenceSegment> getIntronsSegments() {
		List<SequenceSegment> ret = new ArrayList<>();
		for (int index=0;index<sfCount-1;index++) {
			ret.add(new SequenceSegment(sfEnds[index],sfStarts[index+1]));
		}
		return ret;
	}

}
