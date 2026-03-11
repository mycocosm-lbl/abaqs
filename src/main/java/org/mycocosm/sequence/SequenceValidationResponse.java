package org.mycocosm.sequence;

import java.util.ArrayList;
import java.util.List;

public final class SequenceValidationResponse {
	public final List<SequenceValidationError> errors;
	public SequenceValidationResponse() {
		this.errors = new ArrayList<>();
	}
	public SequenceValidationResponse(SequenceValidationCode errorCode, String errorMessage) {
		this();
		addError(errorCode, errorMessage);
	}
	public final void addError(SequenceValidationCode errorCode, String errorMessage) {
		errors.add(new SequenceValidationError(errorCode, errorMessage));
	}
	public final SequenceValidationResponse merge(SequenceValidationResponse other) {
		other.errors.forEach(e->{
			addError(e.code, e.message);
		});
		return this;
	}
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
}
