package org.mycocosm.framework.biology;

import org.mycocosm.gff3.Gff3Record;

public final class Locus extends LocusKey {
	public final int start;
	public final int end;
	public Locus(String scaffold, Strand strand, int start, int end) {
		super(scaffold,strand);
		this.start = start;
		this.end = end;
	}
	public final LocusKey key() {
		return new LocusKey(scaffold, strand);
	}
	public static final Locus fromGff3mRNA(Gff3Record mRNA) {
		int start = mRNA.start;
		int end = mRNA.end;
		return new Locus(
			mRNA.seqid,
			mRNA.strand,
			Math.min(start, end),
			Math.max(start, end)
		);
	}

	public int getLength() {
		return end-start+1;
	}

	public static final int compareByScaffoldPosition(Locus l1, Locus l2) {
		int ret = Integer.compare(l1.start, l2.start);
		if (ret==0) {
			return Integer.compare(l1.end, l2.end);
		} else {
			return ret;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + end;
		result = prime * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Locus other = (Locus) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
}
