package org.mycocosm.abaqs.compare_tracks;

import org.mycocosm.framework.biology.FeatureGeneric;

public class FeaturePair {
	public final FeatureGeneric a;
	public final FeatureGeneric b;
	public final double overlap;
	public FeaturePair(FeatureGeneric a, FeatureGeneric b, double overlap) {
		this.a = a;
		this.b = b;
		this.overlap = overlap;
	}
	public static final int compareByOverlapDesc(FeaturePair f1, FeaturePair f2) {
		return Double.compare(f2.overlap, f1.overlap);
	}
}
