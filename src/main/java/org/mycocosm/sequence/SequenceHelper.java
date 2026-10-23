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
	static final Pattern ENDING_STOP_CODON = Pattern.compile("[\\*]+$",Pattern.CASE_INSENSITIVE);
	static final Pattern ALL_STOP_CODON = Pattern.compile("[\\*]+",Pattern.CASE_INSENSITIVE);
	public static final SequenceValidationResponse validateNucleotideSequence(String sequence, SequenceValidateOption option) {
		return SequenceContent.nucleotide.validateSequence(sequence,option);
	}
	public static final SequenceValidationResponse validateAminoacidSequence(String sequence, SequenceValidateOption option) {
		return SequenceContent.aminoacid.validateSequence(sequence,option);
	}
	
	public enum StopCodonsRemove {
		none, ending, all;
	}
	
	public static final String formatAsFastaWithId(String id, String extra, int maxWidth, String sequence, boolean commentOut, StopCodonsRemove removeStopCodons) {
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
		for (String s:splitSequence(maxWidth, sequence, removeStopCodons)) {
			if (commentOut) {
				ret.append(';');
			}
			ret.append(s);
			ret.append('\n');
		}
		return ret.toString();
	}

	public static final String formatAsFastaWithId(String id, String extra, int maxWidth, String sequence, boolean commentOut) {
		return formatAsFastaWithId(id, extra, maxWidth, sequence, commentOut, StopCodonsRemove.none);
	}
	
	public static final String formatAsFastaWithId(String id, int maxWidth, String sequence) {
		return formatAsFastaWithId(id, null, maxWidth, sequence, false, StopCodonsRemove.none);
	}

	public static final String formatAsFastaWithId(String id, int maxWidth, String sequence, boolean commentOut) {
		return formatAsFastaWithId(id, null, maxWidth, sequence, commentOut, StopCodonsRemove.none);
	}
	public static final String processStopCodons(String sequence, StopCodonsRemove removeStopCodons) {
		switch (removeStopCodons) {
		case ending: return ENDING_STOP_CODON.matcher(sequence).replaceAll("");
		case all: return ALL_STOP_CODON.matcher(sequence).replaceAll("");
		default: return sequence;
		}
	}
	private static final List<String> splitSequence(int maxWidth, String sequence, StopCodonsRemove removeStopCodons) {
		List<String> ret = new ArrayList<>(); 
		int start=0;
		String seq = processStopCodons(sequence, removeStopCodons);
		while (start<seq.length()) {
			ret.add(TextHelper.safeSubstring(seq,start,start+maxWidth));
			start+=maxWidth;
		}
		return ret;
	}

}
