package org.mycocosm.gff3;

import org.mycocosm.framework.utils.EnumsHelper;

public enum Gff3Type {
	gene,mRNA,tRNA,rRNA,five_prime_UTR,three_prime_UTR,exon,CDS,
	intron,TF_binding_site,mobile_genetic_element,repeat_region,
	transcript /* for JTF format*/,
	long_terminal_repeat,snoRNA,origin_of_replication,centromere,pseudogene,regulatory_region,ncRNA,telomere,antisense_RNA,region,telomerase_RNA,sequence_feature,snRNA,SRP_RNA,RNase_P_RNA,RNase_MRP_RNA; /* from Gebebank examples*/
	
	public static Gff3Type valueOfOrNull(String name) {
		return EnumsHelper.nullSafeValueOfOrNull(name, values());
	}
}
