package org.mycocosm.framework.biology;

import org.mycocosm.gff3.Gff3Record;

public class FeatureKey {
	public final Locus locus;
	public FeatureKey(Locus locus) {
		this.locus = locus;
	}
	public static final FeatureKey fromGff3mRNA(Gff3Record mRNA) {
		return new FeatureKey(Locus.fromGff3mRNA(mRNA));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locus == null) ? 0 : locus.hashCode());
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
		FeatureKey other = (FeatureKey) obj;
		if (locus == null) {
			if (other.locus != null)
				return false;
		} else if (!locus.equals(other.locus))
			return false;
		return true;
	}
}
