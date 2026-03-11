package org.mycocosm.framework.biology;

public interface MaskerFunction {
	char mask(char c);
	boolean isMasked(char c);
	String id();
	
	public static final MaskerFunction TO_LOWER_CASE = new MaskerFunction() {
		@Override
		public char mask(char c) {
			return Character.toLowerCase(c);
		}
		@Override
		public boolean isMasked(char c) {
			return Character.isLowerCase(c);
		}
		@Override
		public String id() {
			return "TO_LOWER_CASE";
		}
	};

	public static final MaskerFunction TO_N = new MaskerFunction() {
		@Override
		public char mask(char c) {
			return 'N';
		}
		@Override
		public boolean isMasked(char c) {
			return 'N'==c || 'n'==c;
		}
		@Override
		public String id() {
			return "TO_N";
		}
	};

	public static final MaskerFunction TO_DOT = new MaskerFunction() {
		@Override
		public char mask(char c) {
			return '.';
		}
		@Override
		public boolean isMasked(char c) {
			return '.'==c;
		}
		@Override
		public String id() {
			return "TO_DOT";
		}
	};

	public static MaskerFunction of(String str) {
		switch (str) {
		case "to_lower_case":
		case "TO_LOWER_CASE": return TO_LOWER_CASE;
		case "to_n":
		case "TO_N": return TO_N;
		case "to_dot":
		case "TO_DOT": return TO_N;
		default: throw new IllegalArgumentException(String.format("Illegal masker function name:'%s', accepted valuse are 'to_lower_case','to_n'", str));
		}
	}
}
