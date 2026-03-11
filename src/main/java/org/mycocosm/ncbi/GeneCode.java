package org.mycocosm.ncbi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.text.TextHelper;

public class GeneCode {
	public final int id;
	public final String[] names;
	public final Map<String,Character> ncbieaa;
	public final Map<String,Character> sncbieaa;
	public final String base_1;
	public final String base_2;
	public final String base_3;

	public static final String NAME = "name";
	public static final String NCBIEAA = "ncbieaa";
	public static final String SNCBIEAA = "sncbieaa";
	public static final String BASE_1 = "base1";
	public static final String BASE_2 = "base2";
	public static final String BASE_3 = "base3";

	public static final int DEFAULT_GENE_CODE = 1;
	public static final int DEFAULT_MITO_GENE_CODE = 4;

	private GeneCode(int id, String[] names, Map<String,Character> ncbieaa, Map<String,Character> sncbieaa, String base_1, String base_2, String base_3) {
		this.id = id;
		this.names = names;
		this.ncbieaa = ncbieaa;
		this.sncbieaa = sncbieaa;
		this.base_1 = base_1;
		this.base_2 = base_2;
		this.base_3 = base_3;
	}

	
	private static final Pattern SPLIT_1 = Pattern.compile("[\\n\\r\\t\\{\\,]+");
	private static final Pattern BASE_START = Pattern.compile("^[\\s\\-]+");
	private static final Pattern SPLIT_2 = Pattern.compile("(\\w+)\\s+(.+)");
	
	protected static final GeneCode parseGeneCodeFromNcbiGCPrint(String input) {
		String[] lines = SPLIT_1.split(input);
		List<String> names = new ArrayList<>();
		int id = -1;
		String ncbieaa = null;
		String sncbieaa = null;
		String base_1 = null;
		String base_2 = null;
		String base_3 = null;
		for (String line:lines) {
			if (!TextHelper.isNullOrEmpty(line)) {
				Matcher matcher = SPLIT_2.matcher(BASE_START.matcher(line).replaceAll(""));
				if (matcher.matches()) {
					String name = matcher.group(1).toLowerCase();
					String value = TextHelper.trimEtherEnd(matcher.group(2),' ','"');
					switch (name) {
					case "name": names.add(value); break;
					case "id": id = Integer.valueOf(value); break;
					case "ncbieaa": ncbieaa = value; break;
					case "sncbieaa":sncbieaa = value; break;
					case "base1": base_1 = value; break;
					case "base2": base_2 = value; break;
					case "base3": base_3 = value; break;
					}
				}
			}
		}
		char[] nnBuffer = new char[3];
		Map<String,Character> ncbieaaMap = new HashMap<>();
		Map<String,Character> sncbieaaMap = new HashMap<>();
		for (int index=0;index<base_1.length();index++) {
			nnBuffer[0] = base_1.charAt(index);
			nnBuffer[1] = base_2.charAt(index);
			nnBuffer[2] = base_3.charAt(index);
			ncbieaaMap.put(String.copyValueOf(nnBuffer), ncbieaa.charAt(index));
			sncbieaaMap.put(String.copyValueOf(nnBuffer), sncbieaa.charAt(index));
		}
		return new GeneCode(id,CollectionsHelper.asArray(String.class, names),ncbieaaMap,sncbieaaMap,base_1,base_2,base_3);
	}

	public final static class TranslationResult {
		public final String output;
		public final String overhang;
		private TranslationResult(String output, String overhang) {
			this.output = output;
			this.overhang = overhang;
		}
		public boolean hasOverhang() {
			return !TextHelper.isNullOrEmpty(overhang);
		}
	}

	public TranslationResult translateToAminoAcid(String nucleotideSequence) {
		return translate(nucleotideSequence, ncbieaa);
	}
	public TranslationResult translateSncbieaaMap(String nucleotideSequence) {
		return translate(nucleotideSequence, sncbieaa);
	}

	// Any nucleotides apart from ATCG translated to 'x'

	private static final Pattern VALID_NUCLEOTIDES = Pattern.compile("[ATCG]+");

	private TranslationResult translate(String sequence, Map<String,Character> translationTable) {
		int index=0;
		StringBuilder result = new StringBuilder();
		while (index<=sequence.length()-3) {
			String key = sequence.substring(index, index+3).toUpperCase();
			if (VALID_NUCLEOTIDES.matcher(key).matches()) {
				Character r = translationTable.get(key);
				if (r!=null) {
					result.append(r);
				} else {
					throw new IllegalArgumentException(String.format("Missing triplet '%s' in translation table '%s' for position %d in input '%s'", key,names[0],index,sequence));
				}
			} else {
				// invalid nucleotide here like NNN, translated to gap 'x'
				result.append('x');
			}
			index+=3;
		}
		String overhang;
		if (index<sequence.length()) {
			overhang=sequence.substring(index);
		} else {
			overhang=null;
		}
		return new TranslationResult(result.toString(), overhang);
	}
}
