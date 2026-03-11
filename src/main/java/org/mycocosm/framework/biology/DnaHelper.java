package org.mycocosm.framework.biology;

public class DnaHelper {
	public static final char complementNucleotide(char n) {
		switch (n) {
		case 'A': return 'T';
		case 'a': return 't';
		case 'T': return 'A';
		case 't': return 'a';
		case 'G': return 'C';
		case 'g': return 'c';
		case 'C': return 'G';
		case 'c': return 'g';
		default:  return n;
		}
	}
	public static final String complementSequence(String s) {
		char buf[] = new char[s.length()];
		int index = s.length()-1;
		for (byte x:s.getBytes()) {
			buf[index--]=complementNucleotide((char)x);
		}
		return new String(buf);
	}
}
