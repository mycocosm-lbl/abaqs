package org.mycocosm.gff3;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.mutable.MutableInt;
import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.text.TextHelper;
import org.mycocosm.framework.utils.ExceptionsHelper;

public enum GffParserSupport {
	gff {
		public Map<String,String> parseAttributes(String attributes) {
			Map<String,String> ret = new HashMap<>();
			if (!TextHelper.isNullOrEmpty(attributes)) {
				Arrays.stream(GFF_ATTRIBUTES_SPLIT.split(attributes)).forEach(attr->{
					Matcher attrMatcher = GFF_ATTRIBUTE_PARSE.matcher(attr);
					if (attrMatcher.matches()) {
						String key = unEscape(attrMatcher.group(1));
						String value = unEscape(attrMatcher.group(2));
						ret.put(key, value);
					} else {
						throw ExceptionsHelper.newRuntimeException("Unable to parse attributes string:'%s', problem is:'%s'", attributes, attr);
					}
				});
			}
			return ret;
		};
		public String getId(Gff3Type type, Map<String,String> attributes) {
			return attributes.get(Gff3Record.ATTRIBUTE_ID);
		};
		public String formatAttributes(Gff3Record record) {
			List<String> ret = new ArrayList<>();
			if (record.id!=null) {
				ret.add(formatGffAttribute(Gff3Record.ATTRIBUTE_ID, record.id));
			}
			if (record.parent!=null) {
				ret.add(formatGffAttribute(Gff3Record.ATTRIBUTE_PARENT, record.parent.id));
			}
			CollectionsHelper.asEntriesStream(record.attributes,(e1,e2)->e1.getKey().compareToIgnoreCase(e2.getKey())).forEach(entry->{
				if (!Gff3Record.ATTRIBUTE_ID.equals(entry.getKey()) && !Gff3Record.ATTRIBUTE_PARENT.equals(entry.getKey())) {
					ret.add(formatGffAttribute(entry.getKey(), entry.getValue()));
				}
			});
			return ret.stream().collect(Collectors.joining(";"));
		};
		public String toKey(Gff3Record record) {
			return record.id;
		};
		public String toParentKey(Gff3Record record) {
			return record.attributes.get(Gff3Record.ATTRIBUTE_PARENT);
		};
		public boolean isAcceptedForOutput(Gff3Record record) {
			return true;
		};
		public Gff3Type toType(String typeStr) {
			return Gff3Type.valueOfOrNull(typeStr);
		}
		public String fromType(Gff3Type type) {
			return type.name();
		}
		
	}, 
	gtf {
		public Map<String,String> parseAttributes(String attributes) {
			Map<String,String> ret = new HashMap<>();
			if (!TextHelper.isNullOrEmpty(attributes)) {
				Arrays.stream(GTF_ATTRIBUTES_SPLIT.split(attributes)).forEach(attr->{
					Matcher attrMatcher = GTF_ATTRIBUTE_PARSE.matcher(attr);
					if (attrMatcher.matches()) {
						String key = unEscape(attrMatcher.group(1));
						String value = TextHelper.trimBothEnds(unEscape(attrMatcher.group(2)),'"');
						ret.put(key, value);
					} else {
						Matcher idMatcher = GTF_SINGLE_ATTRIBUTE_VALUE_PARSE.matcher(attr);
						if (idMatcher.matches()) {
							String value = TextHelper.trimBothEnds(unEscape(attr),'"');
							ret.put(Gff3Record.ATTRIBUTE_ID, value);
						} else {
							throw ExceptionsHelper.newRuntimeException("Unable to parse attributes string:'%s', problem is:'%s'", attributes, attr);
						}
					}
				});
			}
			return ret;
		};
		public String formatAttributes(Gff3Record record) {
			List<String> ret = new ArrayList<>();
			String geneId = getAncestorId(record, Gff3Type.gene);
			String transcriptId = getAncestorId(record, Gff3Type.mRNA);
			if (geneId!=null) {
				ret.add(formatGtfAttribute(GTF_GENE_ID_ATTRIBUTE, geneId));
			}
			if (transcriptId!=null) {
				ret.add(formatGtfAttribute(GTF_TRANSCRIPT_ID_ATTRIBUTE, transcriptId));
			}
			CollectionsHelper.asEntriesStream(record.attributes,(e1,e2)->e1.getKey().compareToIgnoreCase(e2.getKey())).forEach(entry->{
				if (!EXCLUDED_ATTRIBUTES_GTF.contains(entry.getKey())) {
					ret.add(formatGtfAttribute(entry.getKey(), entry.getValue()));
				}
			});
			return ret.stream().collect(Collectors.joining("; "));
		};
		public boolean isAcceptedForOutput(Gff3Record record) {
			return Gff3RecordCategory.regular.equals(record.catergory) ;
		};
		public Gff3Type toType(String typeStr) {
			Gff3Type ret = Gff3Type.valueOfOrNull(typeStr); 
			switch (ret) {
			case transcript: return Gff3Type.mRNA;
			default: return ret;
			}
		};
		public String fromType(Gff3Type type) {
			switch (type) {
			case mRNA: return Gff3Type.transcript.name();
			default: return type.name();
			}
		}

		
		private final Map<Gff3Type, MutableInt> idGenerator = new HashMap<>();
		public String getId(Gff3Type type, Map<String,String> attributes) {
			switch (type) {
			case gene:
			case mRNA:
				return attributes.get(Gff3Record.ATTRIBUTE_ID);
			default:
				return String.format("%s_%d",type.name(),idGenerator.computeIfAbsent(type, k->new MutableInt()).getAndIncrement());
			}
		};
		public String toKey(Gff3Record record) {
			return record.id;
		};
		public String toParentKey(Gff3Record record) {
			switch (record.type) {
			case gene:
				return null; // always on the top
			case mRNA:
			case transcript:
				String geneIdAttribute = record.attributes.get(GTF_GENE_ID_ATTRIBUTE);
				if (geneIdAttribute!=null) {
					return geneIdAttribute;
				} else {
					// let do a dirty hack: gene_id.transcript_id 
					Matcher matcher = WHILD_GUESS_TRANSCRIPT_ID_PATTERN.matcher(record.id);
					if (matcher.matches()) {
						String geneId = matcher.group(1);
						return geneId;
					} else {
						throw new RuntimeException(String.format("Unable to get parent id for transcript '%s'",record.id));
					}
				}
			default:
				String transcriptIdAttribute = record.attributes.get(GTF_TRANSCRIPT_ID_ATTRIBUTE);
				if (transcriptIdAttribute!=null) {
					return transcriptIdAttribute;
				} else {
					throw new RuntimeException(String.format("Unable to get parent id for %s '%s'",record.type,record.id));
				}
			}
		}

	};
	private static final Pattern GFF_ATTRIBUTES_SPLIT = Pattern.compile(";");
	private static final Pattern GFF_ATTRIBUTE_PARSE = Pattern.compile("(\\S+)=(.*)");
	private static final Pattern GTF_ATTRIBUTES_SPLIT = Pattern.compile(";\\s*");
	private static final Pattern GTF_ATTRIBUTE_PARSE = Pattern.compile("(\\S+)[= ](.*)");
	private static final Pattern GTF_SINGLE_ATTRIBUTE_VALUE_PARSE = Pattern.compile("\\S+");
	private static final Pattern ESCAPED = Pattern.compile("%([a-f,\\d]{2,2})",Pattern.CASE_INSENSITIVE);
	private static final Pattern WHILD_GUESS_TRANSCRIPT_ID_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)");
	
	private static final String GTF_GENE_ID_ATTRIBUTE = "gene_id";
	private static final String GTF_TRANSCRIPT_ID_ATTRIBUTE = "transcript_id";
	private static final Set<String> EXCLUDED_ATTRIBUTES_GTF = CollectionsHelper.asSet(GTF_GENE_ID_ATTRIBUTE,GTF_TRANSCRIPT_ID_ATTRIBUTE, Gff3Record.ATTRIBUTE_ID, Gff3Record.ATTRIBUTE_PARENT);
	
	private static final String getAncestorId(Gff3Record rec, Gff3Type idType) {
		if (rec.type.equals(idType)) {
			return rec.id;
		} else if (rec.parent!=null) {
			return getAncestorId(rec.parent, idType);
		} else {
			return null;
		}
	}
	
	private static final String formatGffAttribute(String name, String value) {
		return escape(name)+'='+escape(value);
	}
	public static final String unEscape(String line) {
		if (!TextHelper.isNullOrEmpty(line)) {
			return ESCAPED.matcher(line).replaceAll(result->{
				return String.valueOf((char)Integer.valueOf(result.group(1),16).intValue());
			});
		} else {
			return line;
		}
	}
	private static final String formatGtfAttribute(String name, String value) {
		return escape(name)+" \""+escape(value)+"\"";
	}

	private static final Pattern GTF_PATTERN = Pattern.compile(".*\\.gtf.*",Pattern.CASE_INSENSITIVE);
	public static final GffParserSupport inferParserFromFileName(Path input) {
		if (GTF_PATTERN.matcher(input.getFileName().toString()).matches()) { 
			return GffParserSupport.gtf;
		} else {
			return GffParserSupport.gff;
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
	public static final String escape(String str) {
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

	public String getId(Gff3Type type, Map<String,String> attributes) {
		throw new NotImplementedException();
	}
	public Map<String,String> parseAttributes(String attributes) {
		throw new NotImplementedException();
	}
	public String formatAttributes(Gff3Record record) {
		throw new NotImplementedException();
	}
	public String toKey(Gff3Record record) {
		throw new NotImplementedException();
	}
	public String toParentKey(Gff3Record record) {
		throw new NotImplementedException();
	}
	public boolean isAcceptedForOutput(Gff3Record record) {
		throw new NotImplementedException();
	}
	public Gff3Type toType(String typeStr) {
		throw new NotImplementedException();
	}
	public String fromType(Gff3Type type) {
		throw new NotImplementedException();
	}

	//	private String formatAttributes() {
	//		List<String> ret = new ArrayList<>();
	//		if (id!=null) {
	//			ret.add(formatAttribute(ATTRIBUTE_ID, id));
	//		}
	//		if (parent!=null) {
	//			ret.add(formatAttribute(ATTRIBUTE_PARENT, parent.id));
	//		}
	//		CollectionsHelper.asEntriesStream(attributes,(e1,e2)->e1.getKey().compareToIgnoreCase(e2.getKey())).forEach(entry->{
	//			if (!ATTRIBUTE_ID.equals(entry.getKey()) && !ATTRIBUTE_PARENT.equals(entry.getKey())) {
	//				ret.add(formatAttribute(entry.getKey(), entry.getValue()));
	//			}
	//		});
	//		return ret.stream().collect(Collectors.joining(";"));
	//	}
	//	private static final Pattern ATTRIBUTES_SPLIT = Pattern.compile(";");
	//	private static final Pattern ATTRIBUTE_PARSE = Pattern.compile("(\\S+)=(.*)");
	//	private static final Map<String,String> parseAttributes(String str) {
	//		Map<String,String> ret = new HashMap<>();
	//		if (!TextHelper.isNullOrEmpty(str)) {
	//			Arrays.stream(ATTRIBUTES_SPLIT.split(str)).forEach(attr->{
	//				Matcher attrMatcher = ATTRIBUTE_PARSE.matcher(attr);
	//				if (attrMatcher.matches()) {
	//					String key = unEscape(attrMatcher.group(1));
	//					String value = unEscape(attrMatcher.group(2));
	//					ret.put(key, value);
	//				} else {
	//					throw ExceptionsHelper.newRuntimeException("Unable to parse attributes string:'%s', problem is:'%s'", str, attr);
	//				}
	//			});
	//		}
	//		return ret;
	//	}


}
