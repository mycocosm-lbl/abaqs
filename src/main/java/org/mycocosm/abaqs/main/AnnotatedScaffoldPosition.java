package org.mycocosm.abaqs.main;

import java.util.ArrayList;
import java.util.List;

import org.mycocosm.gff3.Gff3Record;


public class AnnotatedScaffoldPosition {
	public final char letter;
	public final List<Gff3Record> mappedmRNAs;
	protected AnnotatedScaffoldPosition(char letter) {
		this.letter = letter;
		this.mappedmRNAs = new ArrayList<>();
	}
	protected void appendMrna(Gff3Record mrna) {
		this.mappedmRNAs.add(mrna);
	}
	public boolean isRepeatmasked() {
		return Character.isLowerCase(letter);
	}
}
