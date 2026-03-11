package org.mycocosm.framework.biology;

import java.util.List;
import java.util.function.Supplier;

import org.mycocosm.gff3.Gff3Record;
import org.mycocosm.gff3.Gff3Type;

public class FeatureModel extends FeatureGeneric {
	public final int cdsStart;
	public final int cdsEnd;
	FeatureModel(FeatureKey key, int id, String name, int cdsStart, int cdsEnd, int sfCount, int[] sfStarts, int[] sfEnds, String subtype, String url, String urlDesc) {
		super(key,id,name,sfCount,sfStarts,sfEnds,subtype,url,urlDesc);
		this.cdsStart = cdsStart;
		this.cdsEnd = cdsEnd;
	}
	
	public static FeatureModel fromGff3mRNA(Gff3Record mRNA, Supplier<Integer> idGeneator) {
		List<SequenceSegment> exons = mRNA.getSegmentsByType(Gff3Type.exon);
		int[] starts = new int[exons.size()];
		int[] ends = new int[exons.size()];
		for (int index=0;index<exons.size();index++) {
			starts[index]=exons.get(index).start;
			ends[index]=exons.get(index).end;
		}
		List<SequenceSegment> cds = mRNA.getSegmentsByType(Gff3Type.CDS);
		int cdsStart = Integer.MAX_VALUE;
		int cdsEnd = Integer.MIN_VALUE;
		for (int index=0;index<cds.size();index++) {
			cdsStart=Math.min(cdsStart, cds.get(index).start);
			cdsEnd=Math.max(cdsEnd, cds.get(index).end);
		}
		return new FeatureModel (
			FeatureKey.fromGff3mRNA(mRNA),	
			idGeneator.get(), // Id
			mRNA.id, // Name
			cdsStart,
			cdsEnd,
			exons.size(), // sf_count
			starts,
			ends,
			null, // subtype
			null, // url
			null // url_desc
		);
	}

	
	public int getEffectiveStart() {
		return cdsStart;
	}
	public int getEffectiveEnd() {
		return cdsEnd;
	}

	public boolean isStructurallyEquivalentTo(FeatureModel other) {
		return 	super.isStructurallyEquivalentTo(other) &&
				cdsStart == other.cdsStart &&
				cdsEnd == other.cdsEnd;
	}

}
