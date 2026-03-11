package org.mycocosm.framework.biology;

import org.mycocosm.framework.text.TextHelper;

public enum Strand {
	plus("+"),minus("-"),none("."),unknown("?");
	public static final Strand fromChar(char c) {
		switch (c) {
		case '+': return plus;
		case '-': return minus;
		case '.': return none;
		case '?': return unknown;
		default: throw new IllegalArgumentException(String.format("Illegal strand character: '%c' ", c));
		}
	}
	public static final Strand of(String str) {
		return parse(str);
	}	
	public final String label;
	private Strand(String label) {
		this.label=label;
	}
	public static final Strand fromByte(byte c) {
		return fromChar((char)c);
	}
	public static final String fromStrand(Strand strand) {
		switch (strand) {
		case plus: return "+";
		case minus: return "-";
		case none: return ".";
		case unknown: return "?";
		default: throw new IllegalArgumentException(String.format("fromStrand: Illegal strand: %s ",strand.toString()));
		}
	}
	public static final Strand parse(String s) {
		if (!TextHelper.isNullOrEmpty(s)) {
			return fromChar(s.charAt(0));
		} else {
			return null;
		}
	}
}
