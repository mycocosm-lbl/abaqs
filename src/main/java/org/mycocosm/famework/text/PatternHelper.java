package org.mycocosm.famework.text;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternHelper {

	public static final Pattern ANY = Pattern.compile(".*");
	public static final Pattern ALL_DIGITS = Pattern.compile("\\d+");
	public static final Pattern NUMBER = Pattern.compile("\\-?\\d+(.\\d*)");
	public static final Pattern TAB = Pattern.compile("\\t");
	public static final Pattern COMMENT_LINE = Pattern.compile("^\\s*\\#(.*)");
	public static final Pattern SPACE_LINE = Pattern.compile("\\s+");
	public static final Pattern PATH_SPLIT = Pattern.compile("\\:+"); 
	public static final Pattern NEWLINE = Pattern.compile("\\n"); 
	public static final Pattern URL_PATTERN = Pattern.compile("((https?)://)?([^/]+)(/([^\\?]*)\\??(.*))?",Pattern.CASE_INSENSITIVE);
	public static final Pattern TABLE_DOES_NOT_EXIST = Pattern.compile(".*table\\s+`?(\\S+)`?\\s+doesn't\\s+exist.*",Pattern.CASE_INSENSITIVE|Pattern.DOTALL|Pattern.MULTILINE); 

	public static final Pattern SECTION_NAME_PATTERN = Pattern.compile("[a-z0-9_][a-z0-9_/\\.\\ \\-\\*]*",Pattern.CASE_INSENSITIVE);
	public static final Pattern NAME_PATTERN = Pattern.compile("[a-z_][a-z0-9\\[\\]_/\\.\\ \\-\\*:*]*",Pattern.CASE_INSENSITIVE);
	public static final Pattern PORTAL_NAME_PATTERN = Pattern.compile("[a-z0-9]\\w*",Pattern.CASE_INSENSITIVE);
	
	public static final Pattern NULL_STRING = Pattern.compile("\\s*null\\s*",Pattern.CASE_INSENSITIVE); 
	
	public static final String nullSafeGroupIfMatchOrElse(Pattern pattern, String str, int group, String orElse) {
		if (str!=null) {
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return matcher.group(group);
			} else {
				return orElse;
			}
		} else {
			return orElse;
		}
	}
	public static final String nullSafeGroupIfMatchOrNull(Pattern pattern, String str, int group) {
		return nullSafeGroupIfMatchOrElse(pattern, str, group, null);
	}
	public static final boolean nullSafeMatches(Pattern pattern, String str) {
		return str != null && pattern.matcher(str).matches();
	}
	public static final boolean nullSafeNotMatches(Pattern pattern, String str) {
		return str != null && !pattern.matcher(str).matches();
	}
	public static final boolean nullSafeMatchesPossibleNullPattern(Pattern pattern, String str) {
		return pattern!=null && str != null && pattern.matcher(str).matches();
	}
	public static final boolean nullSafeMatchesNullPatternMatchesAny(Pattern pattern, String str) {
		return pattern==null || str != null && pattern.matcher(str).matches();
	}
	public static final <T> T splitSupport(String[] split, int index, Function<String,T> func, T defaultValue) {
		if (split!=null && split.length>index) {
			return func.apply(split[index]);
		} else {
			return defaultValue;
		}
	}
	public static final <T> T splitSupport(String[] split, int index, Function<String,T> func) {
		if (split!=null && split.length>index) {
			return func.apply(split[index]);
		} else {
			throw new IllegalArgumentException(String.format("size of passed split %d must be greater than index %d", split.length, index));
		}
	}

	private static final Pattern VAR = Pattern.compile("\\$?\\{([^}]+)\\}");
	public static final String substituteVariables(String input, Function<MatchResult,String> replaceWith) {
		if (!TextHelper.isNullOrEmpty(input)) {
			Matcher matcher = VAR.matcher(input);
			return matcher.replaceAll(replaceWith);
		} else {
			return input;
		}
	}
	public static final String substituteVariables(String input, Map<String,String> variables) {
		return substituteVariables(input, (MatchResult t)->{
			String ret = variables.get(t.group(1));
			if (ret!=null) {
				return ret;
			} else {
				return "";
			}
		});
	}

	public static final Pattern nullSafeCompile(String pattern, int flags, Pattern defaultValue) {
		if (!TextHelper.isNullOrEmpty(pattern)) {
			return Pattern.compile(pattern, flags);
		} else {
			return defaultValue;
		}
	}
	public static final Pattern nullSafeCompile(String pattern, int flags) {
		return nullSafeCompile(pattern,flags,null);
	}
}
