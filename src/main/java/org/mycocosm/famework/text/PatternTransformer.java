package org.mycocosm.famework.text;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mycocosm.famework.collections.CollectionsHelper;

public class PatternTransformer  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2012821101230011661L;
	private final Pattern pattern;
	private final PatternTransformerElement[] transformer;

	private PatternTransformer(Pattern pattern, PatternTransformerElement[] target) {
		this.pattern = pattern;
		this.transformer = target;
	}

	private static final Pattern TARGET = Pattern.compile("\\{(\\w+)\\}"); 
	private static final Pattern TARGET_COMMENT = Pattern.compile("(//--|<-).*$");
	private static final Pattern SPLIT1 = Pattern.compile("\\s*\\-\\>\\s*");
	private static final Pattern SPLIT2 = Pattern.compile("\\s*\\<\\<\\s*");
	private static final Pattern SPLIT3 = Pattern.compile("[\\s\\;\\,]+");

	public static final PatternTransformer NEVER_MATCH = new PatternTransformer(null,(PatternTransformerElement[])null);
	public static final PatternTransformer SAME = PatternTransformer.of(".*->{0}") ;

	public PatternTransformer(String pattern, String transformer) {
		this(Pattern.compile(pattern),parseAliasTarget(transformer));
	}

	public PatternTransformer(String pattern, int patternFlags, String transformer) {
		this(Pattern.compile(pattern,patternFlags),parseAliasTarget(transformer));
	}

	public boolean matches(String input) {
		if (pattern!=null) {
			return TextHelper.nullSafeMatches(pattern, input);
		} else {
			return false;
		}
	}
	public String transform(String input) {
		if (pattern!=null) {
			Matcher matcher  = pattern.matcher(input);
			if (matcher.matches()) {
				if (transformer!=null) {
					StringBuilder ret = new StringBuilder();
					for (PatternTransformerElement element:transformer) {
						ret.append(element.getElement(matcher));
					}
					return ret.toString();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public TransformResult getTransformResult(String input) {
		if (pattern!=null) {
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches()) {
				if (transformer!=null) {
					StringBuilder ret = new StringBuilder();
					for (PatternTransformerElement element:transformer) {
						ret.append(element.getElement(matcher));
					}
					return new TransformResult(true, ret.toString());
				} else {
					return new TransformResult(true, null);
				}
			} else {
				return new TransformResult(false, null);
			}
		} else {
			return new TransformResult(false, null);
		}
	}


	public static final class TransformResult {
		private final boolean match;
		private final String result;
		private TransformResult(boolean match, String result) {
			this.match = match;
			this.result = result;
		}
		public boolean isMatch() {
			return match;
		}
		public String getResult() {
			return result;
		}
		/**
		 * 
		 * 
		 */
		public String getResultOrNull() {
			if (match) {
				return result;
			} else {
				return null;
			}
		}
		@Override
		public String toString() {
			return match?"MATCH:'"+result+"'":"NO MATCH";
		}

	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(pattern!=null?pattern.toString():"<<null>>");
		ret.append("->");
		if (transformer!=null) {
			for (PatternTransformerElement e:transformer) {
				ret.append(e.toString());
			}
		}
		return ret.toString();
	}

	/**
	 * 
	 * 
	 * 
	 * @param def - definition string
	 * @return - Instance of PatternTramsformer class
	 * 
	 * Definition format
	 * 
	 *  pattern (<< flags)? -> transformer//-- comment
	 * 
	 *  Pattern is according to @java.util.regex.Pattern class
	 *  Transformer looks like string literal with possible inclusions of {number} that resolved as a group from provided pattern
	 *  Flags are comma separated list of flags from following cases:
	 *  
	 *  CANON_EQ / CE
	 *  CASE_INSENSITIVE / CI
	 *  COMMENTS / CO
	 *  DOTALL / DA
	 *  LITERAL / LI
	 *  MULTILINE / ML
	 *  UNICODE_CASE / UC
	 *  UNICODE_CHARACTER_CLASS / UN
	 *  UNIX_LINES / UL
	 *  
	 *  examples:
	 *  
	 *  	".*->{0}"
	 *		"xyz->"  
	 *  	"abc(.+)->xyz-{1}"
	 *      "abc(?<name>.+)->xyz-{name}"
	 *  	"ABC(.+)<<CI,DA->"
	 *  	"ABC(.+)<<CI,DA->//-- comment"
	 */

	public static final PatternTransformer valueOf(String def, int... defaultFags) {
		return parseNameAliasFromDef(def,defaultFags);
	}
	public static final PatternTransformer of(String def, int... defaultFags) {
		return parseNameAliasFromDef(def,defaultFags);
	}
	public static final PatternTransformer[] of(String[] defs, int... defaultFags) {
		List<PatternTransformer> ret = new ArrayList<>();
		for (String def:defs) {
			PatternTransformer t = parseNameAliasFromDef(def,defaultFags);
			if (!NEVER_MATCH.equals(t)) {
				ret.add(t);
			}; 
		}
		return CollectionsHelper.asArray(PatternTransformer.class, ret);
	}

	public static final PatternTransformer parseNameAliasFromDef(String def, int... defaultFlags) {
		String pattern=null;
		String target=null;
		def = TARGET_COMMENT.matcher(def).replaceFirst("");
		if (def.isEmpty()) {
			return NEVER_MATCH;	
		} else {
			String split1[] = SPLIT1.split(def);
			int flags = applyDefaultFlags(defaultFlags);
			if (split1.length==2) {
				pattern = TextHelper.nullSafeTrim(split1[0],' ','\t');
				target =  TextHelper.nullSafeTrim(split1[1],' ','\t');
			} else if (split1.length==1) {
				pattern = TextHelper.nullSafeTrim(split1[0],' ','\t');
			} else {
				throw new IllegalArgumentException("Illegal pattern transformer definition string: '"+def+"'");
			}
			String[] split2 = SPLIT2.split(pattern);
			if (split2.length==2) {
				pattern = TextHelper.nullSafeTrim(split2[0],' ','\t');
				flags = toPatternFlags(flags,TextHelper.nullSafeTrim(split2[1],' ','\t'));
			} else if (split2.length!=1) {
				throw new IllegalArgumentException("Illegal pattern transformer definition string: '"+def+"'");
			}
			return new PatternTransformer(Pattern.compile(pattern,flags), parseAliasTarget(target));
		}
	}
	private static final int applyDefaultFlags(int[] defaultFlags) {
		if (defaultFlags!=null && defaultFlags.length>0) {
			int ret = 0;
			for (int f:defaultFlags) {
				ret |= f;
			}
			return ret;
		}
		return 0;
	}

	private static final int toPatternFlags(int defaultFlags, String flagsStr) {
		if (!TextHelper.isNullOrEmpty(flagsStr)) {
			String[] split3 = SPLIT3.split(flagsStr.toUpperCase());
			int ret = defaultFlags;
			for (String s:split3) {
				switch (s) {
				case "CANON_EQ":
				case "CE":
					ret |= Pattern.CANON_EQ;
					break;
				case "CASE_INSENSITIVE":
				case "CI":
					ret |= Pattern.CASE_INSENSITIVE;
					break;
				case "COMMENTS":
				case "CO":
					ret |= Pattern.COMMENTS;
					break;
				case "DOTALL":
				case "DA":
					ret |= Pattern.DOTALL;
					break;
				case "LITERAL":
				case "LI":
					ret |= Pattern.LITERAL;
					break;
				case "MULTILINE":
				case "ML":
					ret |= Pattern.MULTILINE;
					break;
				case "UNICODE_CASE":
				case "UC":
					ret |= Pattern.UNICODE_CASE;
					break;
				case "UNICODE_CHARACTER_CLASS":
				case "UN":
					ret |= Pattern.UNICODE_CHARACTER_CLASS;
					break;
				case "UNIX_LINES":
				case "UL":
					ret |= Pattern.UNIX_LINES;
					break;
				default:
				}
			}
			return ret;
		} else {
			return defaultFlags;
		}
	}

	private static final PatternTransformerElement[] parseAliasTarget(String target) {
		if (!TextHelper.isNullOrEmpty(target)) {
			List<PatternTransformerElement> ret = new ArrayList<>();
			int currentPosition = 0;
			Matcher matcher = TARGET.matcher(TARGET_COMMENT.matcher(target).replaceFirst(""));
			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				if (currentPosition<start) {
					String literal = target.substring(currentPosition,start);
					ret.add(new LiteralElement(literal));
				}
				String groupName = matcher.group(1);
				if (PatternHelper.ALL_DIGITS.matcher(groupName).matches()) {
					ret.add(new NumberedGroupElement(Integer.valueOf(groupName)));
				} else {
					ret.add(new NamedGroupElement(groupName));
				}
				currentPosition=end;
			}
			if (currentPosition<target.length()) {
				String reminder = target.substring(currentPosition);
				ret.add(new LiteralElement(reminder));
			}
			return ret.toArray(new PatternTransformerElement[ret.size()]);
		} else {
			return null;
		}
	}


	private static final class LiteralElement implements PatternTransformerElement {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3761475781344317371L;
		private final String value;
		private LiteralElement(String value) {
			this.value = value;
		}

		@Override
		public String getElement(Matcher matcher) {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

	}

	private static final class NumberedGroupElement implements PatternTransformerElement {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4007094699053918574L;
		private final int group;
		private NumberedGroupElement(int group) {
			this.group = group;
		}

		@Override
		public String getElement(Matcher matcher) {
			return matcher.group(group);
		}

		@Override
		public String toString() {
			return "{"+group+"}";
		}
	}

	private static final class NamedGroupElement implements PatternTransformerElement {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4007094699053918574L;
		private final String name;
		private NamedGroupElement(String name) {
			this.name = name;
		}

		@Override
		public String getElement(Matcher matcher) {
			return matcher.group(name);
		}

		@Override
		public String toString() {
			return "{"+name+"}";
		}
	}

}
