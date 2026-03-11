package org.mycocosm.gff3;

import org.mycocosm.framework.utils.EnumsHelper;

public enum Gff3Type {
	gene,mRNA,tRNA,rRNA,five_prime_UTR,three_prime_UTR,exon,CDS,
	intron,TF_binding_site,mobile_genetic_element,repeat_region;
	
	public static Gff3Type valueOfOrNull(String name) {
		return EnumsHelper.nullSafeValueOfOrNull(name, values());
	}
}
