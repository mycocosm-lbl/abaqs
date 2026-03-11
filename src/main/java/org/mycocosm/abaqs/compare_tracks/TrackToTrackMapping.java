package org.mycocosm.abaqs.compare_tracks;

import java.util.List;
import java.util.Set;

import org.mycocosm.framework.biology.FeatureGeneric;

public class TrackToTrackMapping {
	public final List<FeaturePair> mappedPairs;
	public final Set<FeatureGeneric> sideANotMapped;
	public final Set<FeatureGeneric> sideBNotMapped;

	public TrackToTrackMapping(List<FeaturePair> mappedPairs, Set<FeatureGeneric> sideANotMapped, Set<FeatureGeneric> sideBNotMapped) {
		this.mappedPairs = mappedPairs;
		this.sideANotMapped = sideANotMapped;
		this.sideBNotMapped = sideBNotMapped;
	}
	
	public double getPercentMapped() {
		return (double)mappedPairs.size() / (mappedPairs.size()+sideANotMapped.size()+sideBNotMapped.size());
	}
}
