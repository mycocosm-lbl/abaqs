package org.mycocosm.sequence;

public enum SequenceValidateOption {
	no {
		public boolean needValidation() {
			return false;
		}
	},
	soft,
	hard;
	
	public static final SequenceValidateOption of(String name, SequenceValidateOption defaultValue) {
		if (name!=null) {
			switch (name.toLowerCase()) {
			case "no":
			case "none":
			case "false": return no;
			case "soft": return soft;
			case "hard":
			case "true": return hard;
			default: return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}
	public static final SequenceValidateOption of(String name) {
		return of(name, hard);
	}
	public boolean needValidation() {
		return true;
	}
}