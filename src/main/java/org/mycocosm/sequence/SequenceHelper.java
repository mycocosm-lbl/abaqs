package org.mycocosm.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.mycocosm.framework.text.TextHelper;

public class SequenceHelper {

	public static final int DEFAULT_FASTA_WIDTH = 70;
	static final Pattern NUCLEOTYDE_SEQUENCE_VALID = Pattern.compile("[atcgnx]+",Pattern.CASE_INSENSITIVE);
	static final Pattern AMINOACID_SEQUENCE_VALID = Pattern.compile("[arndcqeghilkmfpstwyuvbzx]+\\*?",Pattern.CASE_INSENSITIVE);
	static final Pattern AMINOACID_STOPS = Pattern.compile("^[\\*]+$",Pattern.CASE_INSENSITIVE);
	public static final SequenceValidationResponse validateNucleotideSequence(String sequence, SequenceValidateOption option) {
		return SequenceContent.nucleotide.validateSequence(sequence,option);
	}
	public static final SequenceValidationResponse validateAminoacidSequence(String sequence, SequenceValidateOption option) {
		return SequenceContent.aminoacid.validateSequence(sequence,option);
	}
	
	public static final String formatAsFastaWithId(String id, String extra, int maxWidth, String sequence, boolean commentOut) {
		StringBuilder ret = new StringBuilder();
		if (commentOut) {
			ret.append(';');
		}
		ret.append('>');
		ret.append(id);
		if (!TextHelper.isNullOrEmpty(extra)) {
			ret.append(' ');
			ret.append(extra);
		}
		ret.append('\n');
		for (String s:splitSequence(maxWidth, sequence)) {
			if (commentOut) {
				ret.append(';');
			}
			ret.append(s);
			ret.append('\n');
		}
		return ret.toString();
	}

	public static final String formatAsFastaWithId(String id, int maxWidth, String sequence) {
		return formatAsFastaWithId(id, null, maxWidth, sequence, false);
	}

	public static final String formatAsFastaWithId(String id, int maxWidth, String sequence, boolean commentOut) {
		return formatAsFastaWithId(id, null, maxWidth, sequence, commentOut);
	}

	private static final List<String> splitSequence(int maxWidth, String sequence) {
		List<String> ret = new ArrayList<>(); 
		int start=0;
		while (start<sequence.length()) {
			ret.add(TextHelper.safeSubstring(sequence,start,start+maxWidth));
			start+=maxWidth;
		}
		return ret;
	}

}
