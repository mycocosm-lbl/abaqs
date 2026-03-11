package org.mycocosm.abaqs.main;

import java.util.Set;

import org.mycocosm.framework.fasta.SimpleFastaSequenceWithIdAndExtra;
import org.mycocosm.gff3.Gff3Record;

public class GeneRecord {
	public final Gff3Record mRNA; // mRNA
	public final int mRNACount;
	public final int cdsStart;
	public final int cdsEnd;
	public final SimpleFastaSequenceWithIdAndExtra protein;
	public final Set<PfamDomain> domains;
	public final double portionOfCDSMasked; // 0.0 to 1.0
	public final boolean detectedTtransposableElement;
	protected GeneRecord(Gff3Record mRNA, int mRNACount, int cdsStart, int cdsEnd, SimpleFastaSequenceWithIdAndExtra protein, Set<PfamDomain> domains, double portionOfCDSMasked, boolean detectedTtransposableElement) {
		this.mRNA = mRNA;
		this.mRNACount = mRNACount;
		this.cdsStart = cdsStart;
		this.cdsEnd = cdsEnd;
		this.domains = domains;
		this.protein = protein;
		this.portionOfCDSMasked = portionOfCDSMasked;
		this.detectedTtransposableElement = detectedTtransposableElement;
	}
}
