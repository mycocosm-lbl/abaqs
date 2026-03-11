package org.mycocosm.gff3;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mycocosm.framework.biology.DnaHelper;
import org.mycocosm.framework.biology.SequenceSegment;
import org.mycocosm.framework.biology.Strand;
import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.comparators.ComparableHelper;
import org.mycocosm.framework.text.TextHelper;
import org.mycocosm.framework.utils.ExceptionsHelper;
import org.mycocosm.framework.utils.KeyValue;
import org.mycocosm.ncbi.GeneCode;
import org.mycocosm.ncbi.GeneCode.TranslationResult;


/*
 * Generic GFF3 record
 * See https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md
 * 
 * 
scaffold_1	prediction	gene			33548	37914	0	+	.	ID=gene_9;Name=jgi.p|Flaful1|249;portal_id=Flaful1;product_name=hypothetical protein;proteinId=249;transcriptId=519
scaffold_1	prediction	mRNA			33548	37914	.	+	.	ID=mRNA_9;Name=jgi.p|Flaful1|249;Parent=gene_9;proteinId=249;track=FilteredModels1;transcriptId=519
scaffold_1	prediction	exon			33548	33931	.	+	.	ID=exon_9_1;Parent=mRNA_9
scaffold_1	prediction	five_prime_UTR	33548	33857	.	+	.	ID=UTR5_9;Parent=mRNA_9
scaffold_1	prediction	CDS				33858	33931	.	+	0	ID=CDS_9;Parent=mRNA_9
scaffold_1	prediction	exon			34050	34237	.	+	.	ID=exon_9_2;Parent=mRNA_9
scaffold_1	prediction	CDS				34050	34237	.	+	1	ID=CDS_9;Parent=mRNA_9
scaffold_1	prediction	exon			34290	37246	.	+	.	ID=exon_9_3;Parent=mRNA_9
scaffold_1	prediction	CDS				34290	37246	.	+	2	ID=CDS_9;Parent=mRNA_9
scaffold_1	prediction	exon			37297	37368	.	+	.	ID=exon_9_4;Parent=mRNA_9
scaffold_1	prediction	CDS				37297	37368	.	+	0	ID=CDS_9;Parent=mRNA_9
scaffold_1	prediction	exon			37421	37914	.	+	.	ID=exon_9_5;Parent=mRNA_9
scaffold_1	prediction	CDS				37421	37600	.	+	0	ID=CDS_9;Parent=mRNA_9
scaffold_1	prediction	three_prime_UTR	37601	37914	.	+	.	ID=UTR3_9;Parent=mRNA_9

scaffold_1	prediction	gene			38154	39302	0	-	.	ID=gene_10;Name=jgi.p|Flaful1|337;portal_id=Flaful1;product_name=trafficking protein-like protein particle complex subunit 5;proteinId=337;transcriptId=607
scaffold_1	prediction	mRNA			38154	39302	.	-	.	ID=mRNA_10;Name=jgi.p|Flaful1|337;Parent=gene_10;proteinId=337;track=FilteredModels1;transcriptId=607
scaffold_1	prediction	exon			39031	39302	.	-	.	ID=exon_10_1;Parent=mRNA_10
scaffold_1	prediction	five_prime_UTR	39213	39302	.	-	.	ID=UTR5_10;Parent=mRNA_10
scaffold_1	prediction	CDS				39031	39212	.	-	0	ID=CDS_10;Parent=mRNA_10
scaffold_1	prediction	exon			38260	38979	.	-	.	ID=exon_10_2;Parent=mRNA_10
scaffold_1	prediction	CDS				38478	38979	.	-	1	ID=CDS_10;Parent=mRNA_10
scaffold_1	prediction	three_prime_UTR	38260	38477	.	-	.	ID=UTR3_10;Parent=mRNA_10
scaffold_1	prediction	exon			38154	38208	.	-	.	ID=exon_10_3;Parent=mRNA_10
scaffold_1	prediction	three_prime_UTR	38154	38208	.	-	.	ID=UTR3_10;Parent=mRNA_10

 * 
 * 
 */
public class Gff3Record {
	public final Gff3RecordCategory catergory;
	// Regular records 
	// Base fields
	public final String seqid; // scaffold
	public final String source; // 'prediction'
	public final Gff3Type type;
	public final int start;
	public final int end;
	public final double score; // NaN if '.'
	public final Strand strand;
	public final int phase; // -1 if '.'
	public final String id;
	// Extra atributes
	public final Map<String,String> attributes;
	public final Gff3Record parent;
	public final List<Gff3Record> children;
	// Meta data
	public final KeyValue<String, String> meta;

	// Well known attributes
	public static final String ATTRIBUTE_ID = "ID";
	public static final String ATTRIBUTE_PARENT = "Parent";
	public static final String ATTRIBUTE_NAME = "Name";
	public static final String ATTRIBUTE_PORTAL_ID = "portal_id";
	public static final String ATTRIBUTE_PROTEIN_ID = "proteinId";
	public static final String ATTRIBUTE_TRANSCRIPT_ID = "transcriptId";
	public static final String ATTRIBUTE_TRACK = "track";
	//	public static final String ATTRIBUTE_PRODUCT_NAME = "product_name";
	public static final String ATTRIBUTE_PRODUCT = "product";
	public static final String ATTRIBUTE_MOBILE_ELEMENT_TYPE = "mobile_element_type";
	public static final String ATTRIBUTE_FEATURE_NAME = "feature_name";
	public static final String ATTRIBUTE_GENE = "gene";
	public static final String ATTRIBUTE_PRODUCT_NAME = "product_name";

	private Gff3Record(Gff3RecordCategory category, Gff3Record parent, String id, String seqid, String source, Gff3Type type, int start, int end, double score,  Strand strand, int phase, KeyValue<String, String> meta) {
		this.catergory = category;
		this.parent = parent;
		this.id = id;
		this.seqid = seqid;
		this.source = source;
		this.type = type;
		this.start = start;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.attributes = new HashMap<>();
		this.children = new ArrayList<>();
		if (parent!=null) {
			parent.children.add(this);
		}
		this.meta = meta;
	}

	static final Gff3Record regular(Gff3Record parent, String id, String seqid, String source, Gff3Type type, int start, int end, double score,  Strand strand, int phase) {
		return new Gff3Record(Gff3RecordCategory.regular, parent, id, seqid, source, type, start, end, score, strand, phase, null);
	}
	static final Gff3Record meta(String key, String value) {
		return new Gff3Record(Gff3RecordCategory.meta, null, null, null, null, null, 0, 0, Double.NaN, null, -1, new KeyValue<String, String>(key, value));
	}
	static final Gff3Record fasta() {
		return new Gff3Record(Gff3RecordCategory.fasta, null, null, null, null, null, 0, 0, Double.NaN, null, -1, null);
	}

	protected Gff3Record setParent(Gff3Record parent) {
		Gff3Record ret = new Gff3Record(this.catergory, parent, this.id, this.seqid, this.source, this.type, this.start, this.end, this.score, this.strand, this.phase, this.meta);
		ret.attributes.putAll(this.attributes);
		ret.children.addAll(this.children);
		return ret;
	}

	protected void addAttribute(String name, String value) {
		if (!TextHelper.isNullOrEmpty(value)) {
			attributes.put(name,value);
		}
	}
	protected Gff3Record withAttribute(String name, String value) {
		addAttribute(name, value);
		return this;
	}
	protected Gff3Record withAttributes(Map<String,String> attr) {
		if (!CollectionsHelper.isNullOrEmpty(attr)) {
			this.attributes.putAll(attr);
		}
		return this;
	}
	public static final int sortByPositionOnly(Gff3Record r1, Gff3Record r2) {
		return ComparableHelper.compare2LevelInt(r1.start, r2.start, r1.end, r2.end);
	}
	public static final int sortByStartLengthAndType(Gff3Record r1, Gff3Record r2) {
		int ret = Integer.compare(r1.start, r2.start);
		if (ret==0) {
			ret = Integer.compare(r2.length(), r1.length());
			if (ret==0) {
				return Integer.compare(r1.type.ordinal(), r2.type.ordinal());
			} else {
				return ret;
			}
		} else {
			return ret;
		}
	}

	public void print(PrintWriter writer) {
		switch (catergory) {
		case regular:
			writer.printf("%s\t%s\t%s\t%d\t%d\t%s\t%s\t%s\t%s%n",
					escape(seqid),
					escape(source),
					type.name(),
					start,
					end,
					formatScore(),
					strand.label,
					formatPhase(),
					formatAttributes()
					);
			children.forEach(child->child.print(writer));
			break;
		case meta:
			if (meta.value!=null) {
				writer.printf("##%s %s%n",escape(meta.key),escape(meta.value));
			} else {
				writer.printf("##%s%n",escape(meta.key));
			}
			break;
		case fasta:
			writer.printf("##%s%n","FASTA");
			break;
		}
	}
	private static final String DOT = ".";
	private static final String ZERO = "0";
	private String formatScore() {
		if (!Double.isNaN(score)) {
			if (score==0.0) {
				return ZERO;
			} else {
				return String.valueOf(score);
			}
		} else {
			return DOT;
		}
	}
	private static final double parseScore(String str) {
		if (TextHelper.isNullOrEmpty(str) || DOT.equals(str)) {
			return Double.NaN;
		} else {
			return Double.valueOf(str);
		}
	}
	private String formatPhase() {
		if (phase>=0) {
			return String.valueOf(phase);
		} else {
			return DOT;
		}
	}
	private static final int parsePhase(String str) {
		if (TextHelper.isNullOrEmpty(str) || DOT.equals(str)) {
			return -1;
		} else {
			return Integer.valueOf(str);
		}
	}

	/*
    tab (%09)
    newline (%0A)
    carriage return (%0D)
    % percent (%25)
    control characters (%00 through %1F, %7F)

In addition, the following characters have reserved meanings in column 9 and must be escaped when used in other contexts:

    ; semicolon (%3B)
    = equals (%3D)
    & ampersand (%26)
    , comma (%2C)
	 */
	private static final String escape(String str) {
		StringBuilder ret = new StringBuilder();
		if (str!=null) {
			str.chars().forEach(c->{
				if (c<=0x1f) {
					ret.append(String.format("%%%02X",c));
				} else {
					switch (c) {
					case '\t':
					case '\n':
					case '\r':
					case '%':
					case ';':
					case '=':
					case '&':
					case ',':
					case 0x7f:
						ret.append(String.format("%%%02X",c));
						break;
					default:
						ret.append((char)c);
					}
				}
			});
		}
		return ret.toString();
	}

	private static final Pattern ESCAPED = Pattern.compile("%([a-f,\\d]{2,2})",Pattern.CASE_INSENSITIVE);
	private static final String unEscape(String line) {
		if (!TextHelper.isNullOrEmpty(line)) {
			return ESCAPED.matcher(line).replaceAll(result->{
				return String.valueOf((char)Integer.valueOf(result.group(1),16).intValue());
			});
		} else {
			return line;
		}
	}

	private String formatAttribute(String name, String value) {
		return escape(name)+'='+escape(value);
	}
	private String formatAttributes() {
		List<String> ret = new ArrayList<>();
		if (id!=null) {
			ret.add(formatAttribute(ATTRIBUTE_ID, id));
		}
		if (parent!=null) {
			ret.add(formatAttribute(ATTRIBUTE_PARENT, parent.id));
		}
		CollectionsHelper.asEntriesStream(attributes,(e1,e2)->e1.getKey().compareToIgnoreCase(e2.getKey())).forEach(entry->{
			if (!ATTRIBUTE_ID.equals(entry.getKey()) && !ATTRIBUTE_PARENT.equals(entry.getKey())) {
				ret.add(formatAttribute(entry.getKey(), entry.getValue()));
			}
		});
		return ret.stream().collect(Collectors.joining(";"));
	}
	private static final Pattern ATTRIBUTES_SPLIT = Pattern.compile(";");
	private static final Pattern ATTRIBUTE_PARSE = Pattern.compile("(\\S+)=(.*)");
	private static final Map<String,String> parseAttributes(String str) {
		Map<String,String> ret = new HashMap<>();
		if (!TextHelper.isNullOrEmpty(str)) {
			Arrays.stream(ATTRIBUTES_SPLIT.split(str)).forEach(attr->{
				Matcher attrMatcher = ATTRIBUTE_PARSE.matcher(attr);
				if (attrMatcher.matches()) {
					String key = unEscape(attrMatcher.group(1));
					String value = unEscape(attrMatcher.group(2));
					ret.put(key, value);
				} else {
					throw ExceptionsHelper.newRuntimeException("Unable to parse attributes string:'%s', problem is:'%s'", str, attr);
				}
			});
		}
		return ret;
	}

	public int length() {
		return Math.abs(end-start);
	}

	public static void main(String[] args) {
		StringBuilder str = new StringBuilder("string: '");
		str.append((char)0x00);
		str.append('-');
		str.append((char)0x1f);
		str.append('-');
		str.append('\t');
		str.append('-');
		str.append('\n');
		str.append('-');
		str.append('\r');
		str.append('-');
		str.append('%');
		str.append('-');
		str.append(';');
		str.append('-');
		str.append('=');
		str.append('-');
		str.append('&');
		str.append('-');
		str.append(',');
		str.append('-');
		str.append((char)0x7f);
		str.append("' completed");

		String input = str.toString();
		String escaped = escape(input);
		System.out.printf("%s%n",escaped);
		String output = unEscape(escaped);
		System.out.printf("%s %b%n",output, input.equals(output));

	}


	private static final Pattern COMMENT = Pattern.compile("#\\s+.*|#");
	private static final Pattern META = Pattern.compile("##(\\S+)\\s*(.*)");

	//	static final boolean isComment(String line) {
	//		return COMMENT.matcher(line).matches();
	//	}
	//	static final KeyValue<String,String> isMeta(String line) {
	//		Matcher matcher = META.matcher(line);
	//		if (matcher.matches()) {
	//			return new KeyValue<String, String>(matcher.group(1), matcher.group(2));
	//		} else {
	//			return null;
	//		}
	//	}

	private static final Pattern TAB = Pattern.compile("\\t");
	/*
	 * Parse line and return record (
	 */
	static final Gff3Record fromLine(String line, boolean acceptMissingId) {
		try {
			if (TextHelper.isNullOrEmpty(line) || COMMENT.matcher(line).matches()) { // simply ignore comments and empty lines
				return null; 
			} else {
				Matcher meta = META.matcher(line); // FASTA also matches META
				if (meta.matches()) {
					String key = unEscape(meta.group(1));
					String value = unEscape(meta.group(2));
					switch (key) {
						case "FASTA": return Gff3Record.fasta();
						default: return Gff3Record.meta(key, value);
					}
				} else {
					String[] split = TAB.split(line);
					String seqid = unEscape(split[0]);
					String source = unEscape(split[1]);
					Gff3Type type = Gff3Type.valueOfOrNull(split[2]);
					int start = toIntegerRelaxed(split[3]);
					int end = toIntegerRelaxed(split[4]);
					double score = parseScore(split[5]);
					Strand strand = Strand.of(split[6]);
					int phase = parsePhase(split[7]);
					Map<String,String> attributes;
					if (split.length>8) {
						attributes = parseAttributes(split[8]);
					} else {
						attributes = new HashMap<>();
					}
					String id = attributes.get(ATTRIBUTE_ID);
					if (id!=null) {
						// set all parents later when all records are parsed to allow forward reference for parents
						return regular(null, id, seqid, source, type, start, end, score, strand, phase).withAttributes(attributes);
					} else if (acceptMissingId) {
						return regular(null, null, seqid, source, type, start, end, score, strand, phase).withAttributes(attributes);
					} else {
						throw ExceptionsHelper.newRuntimeException("Missing ID attribute:'%s'", line);
					}
				}
			}
		} catch (Exception e) {
			throw ExceptionsHelper.newRuntimeException(e, "Exception while parsing line:'%s'", line);
		}
	}

	private static final Pattern POSITION_RELAXED = Pattern.compile("[<>]?(\\d+)");
	private static final int toIntegerRelaxed(String str) {
		Matcher matcher = POSITION_RELAXED.matcher(str);
		if (matcher.matches()) {
			return Integer.valueOf(matcher.group(1));
		} else {
			throw new NumberFormatException(String.format("Invalid position string:'%s'",str));
		}
	}

	public static final boolean filterForTopLevelRegularRecords(Gff3Record rec) {
		return Gff3RecordCategory.regular.equals(rec.catergory) && rec.parent==null;
	}

	public List<Gff3Record> getAllByPredicateInclusive(Predicate<Gff3Record> predicate) {
		List<Gff3Record> ret = new ArrayList<>();
		loadAllByPredicateInclusive(ret, predicate);
		return ret;
	}
	private void loadAllByPredicateInclusive(List<Gff3Record> all, Predicate<Gff3Record> predicate) {
		if (predicate.test(this)) {
			all.add(this);
		}
		children.forEach(child->child.loadAllByPredicateInclusive(all, predicate));
	}
	
	public TranslationResult getTranslatedAminoacidSequence(String scaffold, GeneCode code) {
		final StringBuilder cds = new StringBuilder();
//		System.out.printf("id:%s (%s) name:%s%n", id, strand.label, attributes.get("feature_name"));
		getAllByPredicateInclusive(r->Gff3Type.CDS.equals(r.type)).stream().sorted(Gff3Record::sortByPositionOnly).forEach(cdsRec->{
			
			
			String cdsSeq = scaffold.substring(cdsRec.start-1, cdsRec.end);
//			System.out.printf("scaffold:%s %d-%d ex:%s%n", scaffold.id,cdsRec.start,cdsRec.end,cdsSeq);
			cds.append(cdsSeq);
		});
		String cdsEffective;
		if (Strand.plus.equals(strand)) {
			cdsEffective = cds.toString(); 
//			System.out.printf("cds:%s%n",cds.toString());
		} else {
			cdsEffective = DnaHelper.complementSequence(cds.toString()); 
//			System.out.printf("cds:%s%n",cds.toString());
//			System.out.printf("cds eff:%s%n", cdsEffective);
		}
		TranslationResult ret = code.translateToAminoAcid(cdsEffective);
//		System.out.printf("protein:%s%n", ret.output);
//		System.out.printf("over:%s%n", ret.overhang);
		return ret;
	}

	public String getTranscriptSequence(String scaffold) {
		final StringBuilder transcript = new StringBuilder();
		getAllByPredicateInclusive(r->Gff3Type.exon.equals(r.type)).stream().sorted(Gff3Record::sortByPositionOnly).forEach(cdsRec->{
			String transcriptSeq = scaffold.substring(cdsRec.start-1, cdsRec.end);
			transcript.append(transcriptSeq);
		});
		if (Strand.plus.equals(strand)) {
			return transcript.toString(); 
		} else {
			return DnaHelper.complementSequence(transcript.toString()); 
		}
	}

	public List<SequenceSegment> getSegmentsByType(Gff3Type type) {
		return getAllByPredicateInclusive(r->type.equals(r.type)).stream().sorted(Gff3Record::sortByPositionOnly).map(r->new SequenceSegment(r.start,r.end)).collect(Collectors.toList());
	}
}
