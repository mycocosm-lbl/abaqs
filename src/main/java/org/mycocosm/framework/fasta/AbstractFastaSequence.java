package org.mycocosm.framework.fasta;

import java.util.function.Function;

import org.mycocosm.sequence.SequenceHelper;
import org.mycocosm.sequence.SequenceValidateOption;
import org.mycocosm.sequence.SequenceValidationResponse;

public abstract class AbstractFastaSequence {
	public final String sequence;

	protected AbstractFastaSequence(String sequence) {
		this.sequence = sequence;
	}

	public abstract SequenceKey<?> key();
	public abstract String getDefaultIdString();
	public abstract SequenceValidationResponse isValid(SequenceValidateOption option);
	
	public String formatAsFastaWithId(int maxWidth) {
		return formatAsFastaWithId(getDefaultIdString(), maxWidth);
	}
	public String formatAsFastaWithId(int maxWidth, Function<String,String> sequenceTransformer) {
		return formatAsFastaWithId(getDefaultIdString(), maxWidth, sequenceTransformer);
	}
	public String formatAsFastaWithId(String id, int maxWidth) {
		return SequenceHelper.formatAsFastaWithId(id, maxWidth, sequence);
	}
	public String formatAsFastaWithId(String id, int maxWidth, Function<String,String> sequenceTransformer) {
		return SequenceHelper.formatAsFastaWithId(id, maxWidth, sequenceTransformer.apply(sequence));
	}
	public String formatAsFastaWithId(String id, int maxWidth, Function<String,String> sequenceTransformer, boolean commentOut) {
		return SequenceHelper.formatAsFastaWithId(id, maxWidth, sequenceTransformer.apply(sequence),commentOut);
	}
}
