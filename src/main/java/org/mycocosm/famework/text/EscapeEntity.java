package org.mycocosm.famework.text;

import java.util.regex.Pattern;

public final class EscapeEntity {
	public final Pattern pattern;
	public final String replacement;
	public EscapeEntity(Pattern pattern, String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
	}
	public EscapeEntity(String pattern, String replacement) {
		this(Pattern.compile(pattern),replacement);
	}
	public EscapeEntity(String pattern, int flags, String replacement) {
		this(Pattern.compile(pattern,flags),replacement);
	}
}
