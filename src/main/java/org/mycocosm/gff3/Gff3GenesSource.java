package org.mycocosm.gff3;

import org.mycocosm.framework.text.TextHelper;

public enum Gff3GenesSource {
	nuclear,mito;
	
	public static final Gff3GenesSource of(String str) {
		if (!TextHelper.isNullOrEmpty(str)) {
			switch (str) {
			case "nuclear": return nuclear;
			case "mitohondria":
			case "mito": return mito;
			default: return Gff3GenesSource.valueOf(str);
			}
		} else {
			return null;
		}
	}
}
