package org.mycocosm.framework.biology;

import java.util.regex.Pattern;

public enum TrackType {
	generic,model,geneCatalog,user,gc,fcurve,vista,methylation,curve,undefined;

	private static final Pattern GENE_CATALOG_NAME_PATTERN = Pattern.compile("GeneCatalog.*", Pattern.CASE_INSENSITIVE); 

	public static final TrackType fromValue(String value, String trackName) {
		if (GENE_CATALOG_NAME_PATTERN.matcher(trackName).matches()) { //overwrite track type by track name for GeneCatalog tracks
			return geneCatalog;
		} else {
			switch (value) {
			case "generic": return generic;
			case "model": return model;
			case "geneCatalog": return geneCatalog;
			case "user": return user;
			case "gc": return gc;
			case "fcurve": return fcurve;
			case "vista": return vista;
			case "methylation": return methylation;
			case "curve": return curve;
			default: return undefined;
			}
		}
	}
}
