package org.mycocosm.framework.fasta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScaffoldDef {
	
	private static final Pattern DEF_PATTERN = Pattern.compile("(\\S+)\\s*(.+)?");

	public final String name;
	public final String extra;

	public static final ScaffoldDef parseDef(String def) {
		Matcher m = DEF_PATTERN.matcher(def);
		if (m.matches()) {
			return new ScaffoldDef(m.group(1),m.group(2));
		} else {
			throw new IllegalArgumentException(String.format("Illegal scaffold def: '%s'",def));
		}
		
	}

	private ScaffoldDef(String name, String extra) {
		this.name=name;
		this.extra=extra;
	}
	private ScaffoldDef(ScaffoldDef def) {
		this.name=def.name;
		this.extra=def.extra;
	}
	
	protected ScaffoldDef(String def) {
		this(parseDef(def));
	}
}
