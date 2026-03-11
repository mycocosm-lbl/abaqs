package org.mycocosm.framework.biology;

public class LocusKey {
	public final String scaffold;
	public final Strand strand;
	public LocusKey(String scaffold, Strand strand) {
		this.scaffold = scaffold;
		this.strand = strand;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scaffold == null) ? 0 : scaffold.hashCode());
		result = prime * result + ((strand == null) ? 0 : strand.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocusKey other = (LocusKey) obj;
		if (scaffold == null) {
			if (other.scaffold != null)
				return false;
		} else if (!scaffold.equals(other.scaffold))
			return false;
		if (strand != other.strand)
			return false;
		return true;
	}
}
