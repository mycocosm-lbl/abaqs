package org.mycocosm.framework.fasta;

public enum SequenceSource {
	protein(SequenceType.aminoacid),
	transcript(SequenceType.nucleotide),
	genomic(SequenceType.nucleotide),
	cds(SequenceType.nucleotide),
	assembly(SequenceType.nucleotide),
	assembly_repeatmasked(SequenceType.nucleotide),
	mito_assembly(SequenceType.nucleotide);

	public final SequenceType type;

	private SequenceSource(SequenceType type) {
		this.type = type;
	}
	

}
