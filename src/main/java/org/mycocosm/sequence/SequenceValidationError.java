package org.mycocosm.sequence;

public class SequenceValidationError {
	public final SequenceValidationCode code;
	public final String message;
	SequenceValidationError(SequenceValidationCode code, String message) {
		this.code = code;
		this.message = message;
	}
}	
