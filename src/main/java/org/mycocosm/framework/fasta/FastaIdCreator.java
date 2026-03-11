package org.mycocosm.framework.fasta;

@FunctionalInterface
public interface FastaIdCreator {
	public String getId(AbstractFastaSequence sequence);
//	public String getId(AbstractFastaSequence sequence, Map<Integer,FeatureLinkRecord> recordsByProteinId);
}
