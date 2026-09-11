package org.mycocosm.abaqs.main;

import java.util.Map;
import java.util.Set;

public final class ABAQSData {
	public final double isoformsFactor;
	public final double buscoCompleteFactor;
	public final double buscoDuplicatedFactor;
	public final double incompleteGenesFactor;
	public final double transposableElementsFactor;
	public final double proteinLengthsDistributionFactor;
	public final double abaqsScore;
	public final Map<GeneRecord,Set<GeneRecord>> isoforms;
	
	protected ABAQSData(double isoformsFactor, double buscoCompleteFactor, double buscoDuplicatedFactor, double incompleteGenesFactor, double transposableElementsFactor, double proteinLengthsDistributionFactor, Map<GeneRecord,Set<GeneRecord>> isoforms) {
		this.isoformsFactor = isoformsFactor;
		this.buscoCompleteFactor = buscoCompleteFactor;
		this.buscoDuplicatedFactor = buscoDuplicatedFactor;
		this.incompleteGenesFactor = incompleteGenesFactor;
		this.transposableElementsFactor = transposableElementsFactor;
		this.proteinLengthsDistributionFactor = proteinLengthsDistributionFactor;
		this.isoforms = isoforms;
		abaqsScore = upper() / lower();
	}

	private double upper() {
		return Math.sqrt(proteinLengthsDistributionFactor * incompleteGenesFactor);
	}
	private double lower() {
		return 1.0 + 0.5 * (transposableElementsFactor + isoformsFactor + buscoDuplicatedFactor + 1.0 - buscoCompleteFactor);
	}
}
