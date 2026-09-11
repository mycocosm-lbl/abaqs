package org.mycocosm.gff3;

@FunctionalInterface
public interface Gff3RecordFilter {
	Gff3RecordFilteringResult test(Gff3Record record);
}
