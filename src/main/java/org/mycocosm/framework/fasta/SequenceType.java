package org.mycocosm.framework.fasta;

import org.mycocosm.framework.text.TextHelper;

public enum SequenceType {
	nucleotide("nucl"), 
	aminoacid("prot");
	
	public final String blastDbType;
	private SequenceType(String blastDbType) {
		this.blastDbType = blastDbType;
	}

	public static final SequenceType of(String str) {
		if (!TextHelper.isNullOrEmpty(str)) {
			switch (str) {
			case "nucl":
			case "nucleotide": return nucleotide;
			case "prot":
			case "aminoacid":
				return aminoacid;
			default: throw new IllegalArgumentException("blast db type must be ether: 'nucleotide','nucl','aminoacid','prot'");
			}
		} else {
			throw new IllegalArgumentException("blast db type must be ether: 'nucleotide','nucl','aminoacid','prot'");
		}
	}

}
