package org.mycocosm.framework.fasta;

public class SimpleFastaSequenceWithIdAndExtra extends SimpleFastaSequenceWithId {

	public final String extra;

	public SimpleFastaSequenceWithIdAndExtra(String id, String extra, String sequence, SequenceType type) {
		super(id,sequence,type);
		this.extra = extra;
	}
}
