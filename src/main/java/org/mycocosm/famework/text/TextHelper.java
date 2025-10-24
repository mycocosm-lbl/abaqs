package org.mycocosm.famework.text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mycocosm.famework.collections.CollectionsHelper;
import org.mycocosm.famework.utils.KeyValue;

public class TextHelper {

	public static final String parseField(String field, String body) {
		return parseFieldByPrefixAndSuffix("<" + field + ">", "</" + field + ">", body);
	}

	public static final boolean parseBooleanField(String field, String body) {
		return body.indexOf("<" + field + "/>") >= 0;
	}

	public static final String parseFieldByPrefixAndSuffix(String prefix, String suffix, String body) {
		int beginIndex = body.indexOf(prefix);
		if (beginIndex >= 0) {
			int endIndex = body.indexOf(suffix, beginIndex + prefix.length());
			if (endIndex >= 0) {
				return body.substring(beginIndex + prefix.length(), endIndex);
			} else {
				return body.substring(beginIndex + prefix.length());
			}
		} else {
			return null;
		}
	}

	public static final String stripFileExtention(String fileName) {
		int p = fileName.lastIndexOf('.');
		if (p>=0) {
			return fileName.substring(0,p);
		} else {
			return fileName;
		}
	}

	public static final String stripFileName(String fileName) {
		int p = fileName.lastIndexOf('/');
		if (p>=0) {
			return fileName.substring(0,p);
		} else {
			return fileName;
		}
	}

	public static final String estractFileExtention(String fileName) {
		int p = fileName.lastIndexOf('.');
		if (p>=0) {
			return fileName.substring(p);
		} else {
			return "";
		}
	}

	public static final String estractFileName(String path) {
		int p = path.lastIndexOf('/');
		if (p>=0 && p+1<path.length()) {
			return path.substring(p+1);
		} else {
			return "";
		}
	}

	public static final boolean isNullOrEmpty(String str) {
		return str==null || str.isEmpty();
	}
	public static final boolean isValidString(String str) {
		if(str !=null && !str.isEmpty() && !str.equalsIgnoreCase("null"))
		  return true;
		else
		  return false;
	}
	public static final boolean isEmptyButNotNull(String str) {
		return str!=null && str.isEmpty();
	}

	public static final String safeSubstring(String str, int from) {
		if (!isNullOrEmpty(str)) {
			if (from<str.length()) {
				return str.substring(from);
			} else {
				return "";
			}
		} else {
			return str;
		}
	}

	public static final String safeSubstring(String str, int from, int to) {
		if (!isNullOrEmpty(str)) {
			if (from<0) {
				from=0;
			}
			if (from<str.length()) {
				if (to<=str.length()) {
					return str.substring(from,to);
				} else {
					return str.substring(from);
				}
			} else {
				return "";
			}
		} else {
			return str;
		}
	}

	private static final boolean mustBeTrimmed(char[] chars, char ch) {
		if (ch <= ' ') {
			return true;
		} else {
			for (char c:chars) {
				if (ch==c) {
					return true;
				}
			}
		}
		return false;
	}

	public static final String nullSafeTrim(String str, char... extraCharactersToTrim) {
		if (isNullOrEmpty(str)) {
			return str;
		} else {
			if (extraCharactersToTrim!=null && extraCharactersToTrim.length>0) {
				int leftPos = 0;
				int rightPos = str.length();
				while (leftPos<rightPos && mustBeTrimmed(extraCharactersToTrim, str.charAt(leftPos))) {
					leftPos++;
				}
				while (leftPos<rightPos && mustBeTrimmed(extraCharactersToTrim, str.charAt(rightPos-1))) {
					rightPos--;
				}
				if (leftPos<rightPos) {
					return str.substring(leftPos, rightPos);
				} else {
					return "";
				}
			} else {
				return str.trim();
			}
		}
	}

	public static final int nullSafeCompare(String s1, String s2) {
		if (s1!=null&&s2!=null) {
			return s1.compareTo(s2);
		} else if (s1==null&&s2!=null) {
			return -1;
		} else if (s1!=null&&s2==null) {
			return 1;
		} else {
			return 0;
		}
	}

	public static final <T> int nullSafeCompareWithMap(T s1, T s2, Function<T, String> map) {
		if (s1!=null&&s2!=null) {
			return nullSafeCompare(map.apply(s1), map.apply(s2));
		} else if (s1==null&&s2!=null) {
			return -1;
		} else if (s1!=null&&s2==null) {
			return 1;
		} else {
			return 0;
		}
	}

	public static final int nullSafeCompareReverseNullsOrEmpty(String s1, String s2) {
		if (!isNullOrEmpty(s1) && !isNullOrEmpty(s2)) {
			return s1.compareTo(s2);
		} else if (isNullOrEmpty(s1) && !isNullOrEmpty(s2)) {
			return 1;
		} else if (!isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
			return -1;
		} else {
			return 0;
		}
	}

	public static final boolean nullSafeEquals(Object s1, Object s2) {
		if (s1!=null&&s2!=null) {
			return s1.equals(s2);
		} else {
			return false;
		}
	}

	public static final boolean nullSafeEqualsIgnoreCase(String s1, String s2) {
		if (s1!=null&&s2!=null) {
			return s1.equalsIgnoreCase(s2);
		} else {
			return false;
		}
	}

	public static final <T> boolean nullSafeEqualsNullEqualToNull(T s1, T s2) {
		return nullSafeEqualsRelaxed(s1, s2);
	}
	public static final <T> boolean nullSafeEqualsRelaxed(T s1, T s2) {
		if (s1!=null&&s2!=null) {
			return s1.equals(s2);
		} else if (s1==null&&s2==null) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isNumeric(String str) { 
		try {  
			Double.parseDouble(str);  
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}
	public static final int nullSafeCompareIgnoreCase(String s1, String s2) {
		if (s1!=null&&s2!=null) {
			return s1.compareToIgnoreCase(s2);
		} else if (s1==null&&s2!=null) {
			return -1;
		} else if (s1!=null&&s2==null) {
			return 1;
		} else {
			return 0;
		}
	}
	public static final int nullSafeMixedTypesCompare(String s1, String s2) {
		if (s1!=null&&s2!=null) {
			if(isNumeric(s1) && !isNumeric(s2))
				return 1;
			else if(!isNumeric(s1) && isNumeric(s2))
				return -1;
			else if(isNumeric(s1) && isNumeric(s2)) {
				if(Long.parseLong(s1) > Long.parseLong(s2))
					return 1;
				else if(Long.parseLong(s1) < Long.parseLong(s2))
					return -1;
				else
					return 0;
			}
			else 
				return s1.compareToIgnoreCase(s2);
		} else if ((s1==null || s1.isEmpty())&&s2!=null) {
			return -1;
		} else if (s1!=null&&(s2==null || s2.isEmpty())) {
			return 1;
		} else {
			return 0;
		}
	}

	public static final <T> int nullSafeCompareIgnoreCaseWithMap(T s1, T s2, Function<T, String> map) {
		if (s1!=null&&s2!=null) {
			return nullSafeCompareIgnoreCase(map.apply(s1), map.apply(s2));
		} else if (s1==null&&s2!=null) {
			return -1;
		} else if (s1!=null&&s2==null) {
			return 1;
		} else {
			return 0;
		}
	}


	public static final class Contact {

		public Contact(String email, String name) {
			this.email = email;
			this.name = name;
		}
		private String email;
		private String name;
		public String getEmail() {
			return email;
		}
		public String getName() {
			return name;
		}
		public boolean equals(Contact other) {
			return this.email.equals(other.email) && this.name.equals(other.name);
		}	
	}

	public static final Contact parseEmailValue(String emailStr) {
		String name=null;
		String email=null;
		if (emailStr != null && !emailStr.isEmpty()) {
			if (emailStr.contains("<") && emailStr.contains("@")) {// extract name that comes before < email address>
				StringTokenizer tokenizer = new StringTokenizer(emailStr, "<>");
				if (tokenizer.hasMoreTokens()) {
					name = tokenizer.nextToken();
				}
				if (tokenizer.hasMoreTokens()) {
					email = tokenizer.nextToken();
				}
			} else {
				name = emailStr;// this is probably an email address without name in front of it
				email = emailStr;
			}
			return new Contact(email, name);
		}
		return null;
	}


//	public static final String transformAllSegmentsExclusive(String content, String prefix, String suffix, Transformer transformer) {
//		int currentPos = 0;
//		StringBuilder ret = new StringBuilder();
//		int begin = content.indexOf(prefix,currentPos);
//		while (currentPos<content.length()&&begin>=0) {
//			int end = content.indexOf(suffix,begin+prefix.length());
//			String left;
//			String mid;
//			left = content.substring(currentPos,begin+prefix.length());
//			ret.append(left);
//			if (end>=0) {
//				mid = content.substring(begin+prefix.length(),end);
//				ret.append(transformer.transform(mid));
//				ret.append(suffix);
//				currentPos = end+suffix.length();
//			} else {
//				mid = content.substring(begin+prefix.length());
//				ret.append(transformer.transform(mid));
//				currentPos = content.length();
//			}
//			begin = content.indexOf(prefix,currentPos);
//		}
//		if (currentPos<content.length()) {
//			ret.append(content.substring(currentPos));
//		}
//		return ret.toString();
//	}	
//	public static final String transformAllSegmentsInclusive(String content, String prefix, String suffix, Transformer transformer) {
//		int currentPos = 0;
//		StringBuilder ret = new StringBuilder();
//		int begin = content.indexOf(prefix,currentPos);
//		while (currentPos<content.length()&&begin>=0) {
//			int end = content.indexOf(suffix,begin+prefix.length());
//			String left;
//			String mid;
//			left = content.substring(currentPos,begin);
//			ret.append(left);
//			if (end>=0) {
//				mid = content.substring(begin,end+suffix.length());
//				ret.append(transformer.transform(mid));
//				currentPos = end+suffix.length();
//			} else {
//				mid = content.substring(begin);
//				ret.append(transformer.transform(mid));
//				currentPos = content.length();
//			}
//			begin = content.indexOf(prefix,currentPos);
//		}
//		if (currentPos<content.length()) {
//			ret.append(content.substring(currentPos));
//		}
//		return ret.toString();
//	}
//	public static final String transformAllSegmentsRegexpInclusive(String str, String prefixPattern, String suffixPattern, Transformer transformer) {
//		Pattern prefix = Pattern.compile(prefixPattern);
//		Pattern suffix = Pattern.compile(suffixPattern);
//		Matcher prefixMatcher = prefix.matcher(str);
//		Matcher suffixMatcher = suffix.matcher(str);
//		int currentBegin = 0;
//		StringBuilder ret = new StringBuilder();
//		while (currentBegin<str.length()&&prefixMatcher.find(currentBegin)) {
//			int pStart = prefixMatcher.start();
//			int pEnd = prefixMatcher.end();
//			if (suffixMatcher.find(pEnd)) {
//				int sEnd = suffixMatcher.end();
//				ret.append(str.substring(currentBegin,pStart));
//				ret.append(transformer.transform(str.substring(pStart,sEnd)));
//				currentBegin=sEnd;
//			} else {
//				ret.append(str.substring(currentBegin,pStart));
//				ret.append(transformer.transform(str.substring(pStart)));
//				currentBegin=str.length();
//			}
//		}
//		if (currentBegin<str.length()) {
//			ret.append(str.substring(currentBegin));
//		}
//		return ret.toString();
//	}
//
//	public static final String transformAllSegmentsRegexpExclusive(String str, String prefixPattern, String suffixPattern, Transformer transformer) {
//		Pattern prefix = Pattern.compile(prefixPattern);
//		Pattern suffix = Pattern.compile(suffixPattern);
//		Matcher prefixMatcher = prefix.matcher(str);
//		Matcher suffixMatcher = suffix.matcher(str);
//		int currentBegin = 0;
//		StringBuilder ret = new StringBuilder();
//		while (currentBegin<str.length()&&prefixMatcher.find(currentBegin)) {
//			int pEnd = prefixMatcher.end();
//			if (suffixMatcher.find(pEnd)) {
//				int sStart = suffixMatcher.start();
//				int sEnd = suffixMatcher.end();
//				ret.append(str.substring(currentBegin,pEnd));
//				ret.append(transformer.transform(str.substring(pEnd,sStart)));
//				ret.append(str.substring(sStart,sEnd));
//				currentBegin=sEnd;
//			} else {
//				ret.append(str.substring(currentBegin,pEnd));
//				ret.append(transformer.transform(str.substring(pEnd)));
//				currentBegin=str.length();
//			}
//		}
//		if (currentBegin<str.length()) {
//			ret.append(str.substring(currentBegin));
//		}
//		return ret.toString();
//	}

	private static final Pattern QUOTE_PATTERN = Pattern.compile("[\\\"]");
	private static final String QUOTE_REPLACEMENT = "\\\"";
	public static final String nullSafeReplaceQuotations(String str) {
		if (str!=null) {
			return QUOTE_PATTERN.matcher(str).replaceAll(QUOTE_REPLACEMENT);
		} else {
			return null;
		}
	}

	public static final String nullSafeSingleQuotedString(Object o) {
		if (o!=null) {
			return "'"+o.toString()+"'";
		} else {
			return null;
		}
	}
	public static final String nullSafeQuotedString(Object o) {
		if (o!=null) {
			return "\""+o.toString()+"\"";
		} else {
			return "";
		}
	}
	public static final String nullSafeNonQuotedObject(Object obj) {
		if (obj!=null) {
			return ""+obj.toString()+"";
		} else {
			return "";
		}
	}

	public static final String trimBothEnds(String line, char characterToTrim) {
		if (line!=null) {
			String trimmedLine = line.trim();
			if (trimmedLine.length()>1) {
				if (trimmedLine.charAt(0)==characterToTrim&&trimmedLine.charAt(trimmedLine.length()-1)==characterToTrim) {
					return trimmedLine.substring(1,trimmedLine.length()-1);
				} else {
					return trimmedLine;
				}
			} else {
				return trimmedLine;
			}
		} else {
			return null;
		}
	}
//
//	public static final String trimEtherEnd(String line, final char characterToTrim) {
//		return trimEtherEnd(line, new CharTrimmer(){
//
//			@Override
//			public boolean trim(char c) {
//				return characterToTrim==c;
//			}});
//	}
//
//	public static final String trimEtherEnd(String line, CharTrimmer trimmer) {
//		if (line!=null) {
//			String trimmedLine = line.trim();
//			if (trimmedLine.length()>0) {
//				int indexFrom = 0;
//				while (indexFrom < trimmedLine.length() && trimmer.trim(trimmedLine.charAt(indexFrom))) {
//					indexFrom++;
//				}
//				int indexTo = trimmedLine.length()-1;
//				while (indexTo >= indexFrom && trimmer.trim(trimmedLine.charAt(indexTo))) {
//					indexTo--;
//				}
//				trimmedLine = trimmedLine.substring(indexFrom,indexTo+1);
//				if (!line.equals(trimmedLine)) {
//					return trimEtherEnd(trimmedLine, trimmer);
//				} else {
//					return trimmedLine;
//				}
//			} else {
//				return trimmedLine;
//			}
//		} else {
//			return null;
//		}
//	}
//
//	public static final String trimEtherEnd(String line, char ... charactersToTrim ) {
//		if (line!=null) {
//			if (charactersToTrim!=null && charactersToTrim.length>0) {
//				String beforeTrimming = line;
//				String afterTrimming = line;
//				for (int index=0;index<charactersToTrim.length;index++) {
//					afterTrimming = trimEtherEnd(afterTrimming, charactersToTrim[index]);
//				}
//				while (beforeTrimming.length()!=afterTrimming.length()) {
//					beforeTrimming = afterTrimming;
//					for (int index=0;index<charactersToTrim.length;index++) {
//						afterTrimming = trimEtherEnd(afterTrimming, charactersToTrim[index]);
//					}
//				}
//				return afterTrimming;
//			} else {
//				return line.trim();
//			}
//		} else {
//			return null;
//		}
//	}
//
//
//	public static final String trimBothEnds(String line, char ... charactersToTrim ) {
//		if (line!=null) {
//			String beforeTrimming = line;
//			String afterTrimming = line;
//			for (int index=0;index<charactersToTrim.length;index++) {
//				afterTrimming = trimBothEnds(afterTrimming, charactersToTrim[index]);
//			}
//			while (beforeTrimming.length()!=afterTrimming.length()) {
//				beforeTrimming = afterTrimming;
//				for (int index=0;index<charactersToTrim.length;index++) {
//					afterTrimming = trimBothEnds(afterTrimming, charactersToTrim[index]);
//				}
//			}
//			return afterTrimming;
//		} else {
//			return null;
//		}
//	}

//	private static boolean isaEscapes(char c, char[] escapes) {
//		for (char cx:escapes) {
//			if (c==cx) {
//				return true;
//			}
//		}
//		return false;
//	}

//	public static final List<String> parseDelimitedLine(String line, Function<Character,Boolean> isDelimiter, char[] escapes) {
//		List<String> ret = new ArrayList<String>();
//		if (line!=null) {
//			List<Integer> splitPositions = new ArrayList<Integer>();
//			char escapedBy=' ';
//			boolean isEscaped=false;
//			for (int index=0;index<line.length();index++) {
//				char ch = line.charAt(index);
//				if (isEscaped) {
//					if (ch==escapedBy) {
//						isEscaped=false;
//					}
//				} else {
//					if (isaEscapes(ch, escapes)) {
//						escapedBy=ch;
//						isEscaped=true;
//					} else if (isDelimiter.apply(ch)) {
//						splitPositions.add(index);
//					}
//				}
//			}
//			if (!splitPositions.isEmpty()) {
//				int start = 0;
//				for (int pos:splitPositions) {
//					ret.add(trimBothEnds(line.substring(start,pos),escapes));
//					start=pos+1;
//				}
//				if (start>line.length()) {
//					ret.add("");
//				} else {
//					ret.add(trimBothEnds(line.substring(start),escapes));
//				}
//			} else {
//				ret.add(trimBothEnds(line ,escapes));
//			}
//		}
//		return ret;
//	}
//	private static final char[] DEFAULT_ESCAPES = {'\'','"'};
//
//	public static final List<String> parseDelimitedLine(String line, Function<Character,Boolean> isDelimiter) {
//		return parseDelimitedLine(line,isDelimiter,DEFAULT_ESCAPES);
//	}
//	
//	public static final List<String> parseDelimitedLine(String line, char delimiter) {
//		return parseDelimitedLine(line,c->delimiter==c,DEFAULT_ESCAPES);
//	}
//	public static final List<String> parseDelimitedLine(String line, char delimiter, char[] escapes) {
//		return parseDelimitedLine(line,c->delimiter==c,escapes);
//	}

	private static final Pattern NAME_VALUE_PAIR = Pattern.compile("\\s*(\\w+)\\s*=\\s*(.*)");
	public static final KeyValue<String, String> parseNameValuePair(String pair) {
		if (pair!=null) {
			Matcher m = NAME_VALUE_PAIR.matcher(pair);
			if (m.matches()) {
				return new KeyValue<String, String>(m.group(1), m.group(2));
			} else {
				return new KeyValue<String, String>(pair, null);
			}
		} else {
			return null;
		}
	}
	
	public static final Integer nullSafeToInteger(String str) throws NumberFormatException {
		return nullSafeToInteger(str,null);
	}
	public static final Integer nullSafeToInteger(String str, Integer defaultValue) throws NumberFormatException {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			return Integer.valueOf(trimmed);
		} else {
			return defaultValue;
		}
	}
	public static final Long nullSafeToLong(String str) throws NumberFormatException {
		return nullSafeToLong(str,null);
	}
	public static final Long nullSafeToLong(String str, Long defaultValue) throws NumberFormatException {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			return Long.valueOf(trimmed);
		} else {
			return defaultValue;
		}
	}
	public static final Long nullSafeToLongIgnoreNumberFormatExceptions(String str, Long nullValue, Long numberFormatValue) {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			try {
				return Long.valueOf(trimmed);
			} catch (NumberFormatException e) {
				return numberFormatValue;
			}
		} else {
			return nullValue;
		}
	}
	public static final double nullSafeToDouble(String str) throws NumberFormatException {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			return Double.valueOf(trimmed);
		} else {
			return Double.NaN;
		}
	}
	public static final double nullSafeToDouble(Object obj) throws NumberFormatException {
		if (obj!=null) {
			Class<?> objClass = obj.getClass();
			if (Double.class.isAssignableFrom(objClass)) {
				return ((Double)obj).doubleValue();
			} else if (double.class.isAssignableFrom(objClass)) {
				return (double)obj;
			} else {
				return Double.valueOf(obj.toString());
			}
		} else {
			return Double.NaN;
		}
	}
	public static final float nullSafeToFloat(String str) throws NumberFormatException {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			return Float.valueOf(trimmed);
		} else {
			return Float.NaN;
		}
	}
	public static final boolean nullSafeToBoolean(String str, boolean defaultValue) {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			return Boolean.valueOf(trimmed);
		} else {
			return defaultValue;
		}
	}
	public static final boolean nullSafeToBoolean(Object obj, boolean defaultValue) {
		if (obj!=null) {
			Class<?> objClass = obj.getClass();
			if (Boolean.class.isAssignableFrom(objClass)) {
				return ((Boolean)obj).booleanValue();
			} else if (boolean.class.isAssignableFrom(objClass)) {
				return (boolean)obj;
			} else {
				return Boolean.valueOf(obj.toString());
			}
		} else {
			return defaultValue;
		}
	}

	public static final Boolean nullSafeToBooleanExtended(String str, Boolean defaultValue) {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			switch (trimmed.toLowerCase()) {
			case "true":
			case "1":
			case "yes":
			case "y":
			case "t":
				return true;
			default:
				return false;
			}
		} else {
			return defaultValue;
		}
	}
	public static final boolean nullSafeToBooleanExtended(String str, boolean defaultValue) {
		String trimmed = nullSafeTrim(str);
		if (!isNullOrEmpty(trimmed)) {
			switch (trimmed.toLowerCase()) {
			case "true":
			case "1":
			case "yes":
			case "y":
			case "t":
				return true;
			default:
				return false;
			}
		} else {
			return defaultValue;
		}
	}
	public static final String nullSafeToString(Iterable<String> iterable) {
		if(iterable == null) return null;
		return String.join(",", iterable);
	}
	public static final String nullSafeToString(Object str) {
		if (str!=null) {
			return str.toString();
		} else {
			return null;
		}
	}
	public static final String nullIfEmpty(String value) {
		if (!isNullOrEmpty(value)) {
			return value;
		} else {
			return null;
		}
	}
	public static final <T> String nullSafeComputeIfNullOrEmpty(String value, T str, Function<T, String> toString) {
		if (!isNullOrEmpty(value)) {
			return value;
		} else {
			return nullSafeToString(str,toString);
		}
	}
	public static final <T> String nullSafeToString(T str, Function<T, String> toString) {
		if (str!=null) {
			try {
				return toString.apply(str);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}
	public static final <T> String nullSafeToString(T str, Function<T, String> toString, String ifNull) {
		if (str!=null) {
			return toString.apply(str);
		} else {
			return ifNull;
		}
	}
	public static final <T> String nullSafeToStringDefaultNull(T obj, String defaultValue) {
		if (obj!=null) {
			return obj.toString();
		} else {
			return defaultValue;
		}
	}
	public static final <T> String nullSafeToStringDefaultNullOrEmpty(T obj, String defaultValue) {
		if (obj!=null) {
			String ret = obj.toString();
			if (ret.isEmpty()) {
				return defaultValue;
			} else {
				return ret;
			}
		} else {
			return defaultValue;
		}
	}
	public static final <T> T nullSafeToObject(String str, Function<String, T> toObject, T ifNull) {
		if (str!=null) {
			return toObject.apply(str);
		} else {
			return ifNull;
		}
	}

	public static final int compareLong(long l1, long l2) {
		if (l1>l2) {
			return 1;
		} else if (l2>l1) {
			return -1;
		} else {
			return 0;
		}
	}

	public static final Throwable getRootCause(Throwable t) {
		Throwable cause = t.getCause();
		if (cause!=null && !cause.equals(t)) {
			return getRootCause(cause);
		} else {
			return t;
		}
	}

	private static final Pattern DETAG_PATTERN = Pattern.compile("\\<[^\\<]+\\>|\\n|\\r", Pattern.CASE_INSENSITIVE);

	public static final String removeAllMarkups(String str) {
		Matcher m = DETAG_PATTERN.matcher(str);
		return m.replaceAll("");
	}

	public static final class TextHelperMapEntry<K,V> {
		private K key;
		private V value;
		public TextHelperMapEntry(K key, V value) {
			this.key=key;
			this.value=value;
		}
		public TextHelperMapEntry(Map.Entry<K, V> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}
		public K getKey() {
			return key;
		}
		public V getValue() {
			return value;
		}
		@Override
		public String toString() {
			return key + "=" + (value!=null?value.toString():"<<null>>");
		}
	}

	public static final String renderMapAsSeparatedString(Map<String,?> map, char separator) {
		List<TextHelperMapEntry<String, ?>> entries = new ArrayList<>();
		for (Map.Entry<String, ?> entry:map.entrySet()) {
			entries.add(new TextHelperMapEntry<>(entry));
		}
		Collections.sort(entries, new Comparator<TextHelperMapEntry<String, ?>>(){
			@Override
			public int compare(TextHelperMapEntry<String, ?> o1, TextHelperMapEntry<String, ?> o2) {
				return TextHelper.nullSafeCompare(o1.getKey(), o2.getKey());
			}
		});
		return renderCollectionAsSeparatedString(entries, separator);
	}

	public static final String renderCollectionAsSeparatedString(Collection<?> collection, char separator) {
		return renderCollectionAsSeparatedString(collection,separator,(char)0,(char)0);
	}
	public static final String renderCollectionAsSeparatedString(Object first, Collection<?> other, char separator) {
		List<Object> collection = new ArrayList<>();
		collection.add(first);
		collection.addAll(other);
		return renderCollectionAsSeparatedString(collection,separator,(char)0,(char)0);
	}

	public static final String renderCollectionAsSeparatedString(Collection<?> collection, char separator, char escapeLeft, char escapeRight) {
		Iterator<?> it = collection.iterator();
		StringBuilder ret = new StringBuilder();
		while (it.hasNext()) {
			if (escapeLeft>0) {
				ret.append(escapeLeft);
			}
			ret.append(TextHelper.nullSafeTrim(TextHelper.nullSafeToString(it.next())));
			if (escapeRight>0) {
				ret.append(escapeRight);
			}
			if (separator>0 && it.hasNext()) {
				ret.append(separator);
			}
		}
		return ret.toString();
	}

	public static final String renderArrayAsSeparatedString(char separator, Object... arr) {
		return renderArrayAsSeparatedString(separator, (char)0, (char)0, arr);
	}

	public static final String renderArrayAsSeparatedString(char separator, char escapeLeft, char escapeRight, Object... arr) {
		StringBuilder ret = new StringBuilder();
		for (int i=0;i<arr.length;i++) {
			if (escapeLeft>0) {
				ret.append(escapeLeft);
			}
			ret.append(TextHelper.nullSafeTrim(TextHelper.nullSafeToString(arr[i])));
			if (escapeRight>0) {
				ret.append(escapeRight);
			}
			if (separator>0 && i<arr.length-1) {
				ret.append(separator);
			}
		}
		return ret.toString();
	}

//	public static final LineParser grep(final String pattern, final LineParser next) {
//		return grep(Pattern.compile(pattern),next);
//	}
//
//	public static final LineParser grep(final Pattern pattern, final LineParser next) {
//		return new LineParser() {
//			@Override
//			public void parseLine(String line) throws ParseException {
//				Matcher matcher = pattern.matcher(line);
//				if (matcher.matches()) {
//					next.parseLine(line);
//				}
//			}
//		};
//	}
//
	public static final Pattern[] compileAll(String[] patterns) {
		return compileAll(patterns,0);
	}

	public static final Pattern[] compileAll(String[] patterns, int flags) {
		if (patterns!=null) {
			List<Pattern> ret = new ArrayList<>();
			for (String pattern:patterns) {
				ret.add(Pattern.compile(pattern, flags));
			}
			return ret.toArray(new Pattern[ret.size()]);
		} else {
			return null;
		}
	}

	public static final Pattern[] compileAll(Collection<String> patterns) {
		return compileAll(patterns,0);
	}

	public static final Pattern[] compileAll(Collection<String> patterns, int flags) {
		if (patterns!=null) {
			return compileAll(patterns.toArray(new String[patterns.size()]),flags);
		} else {
			return null;
		}
	}

	public static final boolean matchAny(Pattern[] patterns, String str) {
		for (Pattern pattern:patterns) {
			Matcher m = pattern.matcher(str);
			if (m.matches()) {
				return true;
			}
		}
		return false;
	}
	public static final boolean matchAll(Pattern[] patterns, String str) {
		for (Pattern pattern:patterns) {
			Matcher m = pattern.matcher(str);
			if (!m.matches()) {
				return false;
			}
		}
		return true;
	}

	public static final boolean findAny(Pattern[] patterns, String str) {
		for (Pattern pattern:patterns) {
			Matcher m = pattern.matcher(str);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}
	public static final boolean findAll(Pattern[] patterns, String str) {
		for (Pattern pattern:patterns) {
			Matcher m = pattern.matcher(str);
			if (!m.find()) {
				return false;
			}
		}
		return true;
	}

	public static final String nullSafeCollectionToString(Collection<?> elements) {
		return nullSafeCollectionToString(elements, ',');
	}
	public static final String nullSafeIntegerToString(Integer integer) {
		if(integer == null)
			return "0";
		else
			return String.valueOf(integer);
	}
	public static final String nullSafeCollectionToString(Collection<?> elements, char separator) {
		StringBuilder ret = null;
		if (elements!=null) {
			ret = new StringBuilder();
			Iterator<?> it = elements.iterator();
			while (it.hasNext()) {
				Object element = it.next();
				if (element!=null) {
					if (separator>0 && ret.length()>0) {
						ret.append(separator);
					}
					ret.append(nullSafeToString(element));
				}
			}
		}
		return ret!=null?ret.toString():null;
	}

	public static final String nullSafeStringToCollection(Collection<?> elements, char separator) {
		StringBuilder ret = null;
		if (elements!=null) {
			Iterator<?> it = elements.iterator();
			while (it.hasNext()) {
				Object element = it.next();
				if (element!=null) {
					if (ret==null) {
						ret = new StringBuilder();
					}
					if (separator>0 && ret.length()>0) {
						ret.append(separator);
					}
					ret.append(nullSafeToString(element));
				}
			}
		}
		return ret!=null?ret.toString():null;
	}


	public static final <K,V> String nullSafeMapToString(Map<K,V> source) {
		return nullSafeMapToString(source, ',');
	}

	public static final <K,V> String nullSafeMapToString(Map<K,V> source, char separator) {
		StringBuilder ret = null;
		if (source!=null) {
			List<Map.Entry<K, V>> entries = new ArrayList<>();
			entries.addAll(source.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<K, V>>(){

				@SuppressWarnings("unchecked")
				@Override
				public int compare(Entry<K, V> o1, Entry<K, V> o2) {
					K key1 = o1.getKey();
					K key2 = o2.getKey();
					if (Comparable.class.isAssignableFrom(key1.getClass())) {
						return ((Comparable<K>)key1).compareTo(key2);
					} else {
						return nullSafeCompare(nullSafeToString(key1),nullSafeToString(key2));
					}
				}});
			Iterator<Map.Entry<K, V>> it = entries.iterator();
			while (it.hasNext()) {
				Map.Entry<K, V> entry = it.next();
				if (entry!=null) {
					if (ret==null) {
						ret = new StringBuilder();
					}
					if (separator>0 && ret.length()>0) {
						ret.append(separator);
					}
					ret.append(nullSafeToString(entry.getKey()));
					ret.append('=');
					ret.append(nullSafeToString(entry.getValue()));
				}
			}
		}
		return ret!=null?ret.toString():null;
	}


	public static final String nullSafeReplaceAll(String str, String regex, String replacement) {
		if (str!=null) {
			return str.replaceAll(regex, replacement);
		} else {
			return null;
		}
	}

	public static String maxLength(String str, int length) {
		if (str==null) {
			return null;
		} else if (str.length()<=length) {
			return str;
		} else {
			return str.substring(0,length)+" ...";
		}
	}

	public static final String nullSafeReplaceAll(String str, Pattern pattern, String replacement) {
		if (!isNullOrEmpty(str)) {
			StringBuilder ret = new StringBuilder();
			Matcher m = pattern.matcher(str);
			int current = 0;
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				ret.append(str.substring(current,start));
				ret.append(replacement);
				current=end;
			}
			ret.append(str.substring(current));
			return ret.toString();
		} else {
			return str;
		}
	}

	public static final String nullSafeSubstring(String str, String prefix, String suffix) {
		if (!isNullOrEmpty(str)) {
			int begin;
			if (!TextHelper.isNullOrEmpty(prefix)) {
				begin=str.indexOf(prefix);
			} else {
				begin=0;
			}
			if (begin>=0) {
				int end;
				if (!TextHelper.isNullOrEmpty(suffix)) {
					end=str.indexOf(suffix, begin+prefix.length());
				} else {
					end=str.length();
				}
				if (end>=0) {
					return str.substring(begin+prefix.length(), end);
				} else {
					return "";
				}
			} else {
				return "";
			}
		} else {
			return str;
		}
	}

	public static final boolean nullSafeContains(String where, String what) {
		if (where!=null) {
			return where.contains(what);
		} else {
			return false;
		}
	}

	public static final boolean nullSafeContainsIgnoreCase(String where, String what) {
		if (where!=null && what!=null) {
			return where.toLowerCase().contains(what.toLowerCase());
		} else {
			return false;
		}
	}

	public static final boolean nullSafeContainsSplitIgnoreCase(String where, String what, Pattern splitPattern) {
		if (where!=null && what!=null) {
			for (String str:TextHelper.nullSafeSplit(where, splitPattern)) {
				if (str.equalsIgnoreCase(what)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public static final boolean nullSafeContainsSplitIgnoreCase(String where, String what) {
		return nullSafeContainsSplitIgnoreCase(where,what,SPLIT_PATTERN);
	}

	public static final String firstLetterUpperCaseAllLowerCase(String input) {
		if (!isNullOrEmpty(input)) {
			StringBuilder ret = new StringBuilder();
			ret.append(input.substring(0, 1).toUpperCase());
			ret.append(input.substring(1).toLowerCase());
			return ret.toString();
		} else {
			return input;
		}
	}

	public static final Collection<String> nullSafeStringToCollection(String value, String separator, Collection<String> ret) {
		String[] values = value.split(separator);
		for (String v:values) {
			if (v!=null) {
				ret.add(v);
			}
		}
		return ret;
	}

	private static final Pattern ALL_NUMBERS = Pattern.compile("\\d+");
	private static final class StringOrNumber {
		private String str=null;
		private Long num=null;

		private StringOrNumber(String str) {
			this.str=str;
			this.num=null;
		}
		private StringOrNumber(Long num) {
			this.str=null;
			this.num=num;
		}

		private boolean isNumber() {
			return str==null;
		}
		public String toString() {
			if (str!=null) {
				return str;
			} else {
				return num.toString();
			}
		}

		private int compareTo(StringOrNumber other) {
			if (isNumber() && other.isNumber()) {
				return num.compareTo(other.num);
			} else {
				return toString().compareTo(other.toString());
			}
		}
	}
	private static final List<StringOrNumber> splitStringsAndNumbers(String s1) {
		List<StringOrNumber> ret = new ArrayList<>();
		if (!isNullOrEmpty(s1)) {
			Matcher m = ALL_NUMBERS.matcher(s1);
			int begin = 0;
			while (m.find()) {
				int start = m.start(0);
				int end = m.end(0);
				if (start>begin) {
					String sub = s1.substring(begin,m.start(0));
					ret.add(new StringOrNumber(sub));
				} 
				Long num = Long.valueOf(s1.substring(start, end));
				ret.add(new StringOrNumber(num));
				begin = end;
			}		
			if (begin<s1.length()) {
				ret.add(new StringOrNumber(s1.substring(begin)));
			}
		}
		return ret;
	}

	public static final int compareStringAndNumbers(String s1, String s2) {
		List<StringOrNumber> l1 = TextHelper.splitStringsAndNumbers(s1);
		List<StringOrNumber> l2 = TextHelper.splitStringsAndNumbers(s2);
		int minSize = Math.min(l1.size(), l2.size());
		for (int i=0;i<minSize;i++) {
			StringOrNumber c1 = l1.get(i);
			StringOrNumber c2 = l2.get(i);
			int ret = c1.compareTo(c2);
			if (ret!=0) {
				return ret;
			}
		}
		if (l1.size()>l2.size()) {
			return 1;
		} else if (l1.size()<l2.size()) {
			return -1;
		} else {
			return 0;
		}
	}

	public static final String quoted(String str) {
		return quoted(str,'"');
	}

	public static final String quoted(String str, char quote) {
		StringBuilder ret = new StringBuilder();
		ret.append(quote);
		if (str!=null) {
			for (char c:str.toCharArray()) {
				if (quote==c) {
					ret.append('\\');
					ret.append(c);
				} else {
					ret.append(c);
				}
			}
		}
		ret.append(quote);
		return ret.toString();
	}

	public static final String cutInTheMiddle(String text, int toLength, String middle) {
		if (text.length()+middle.length() > toLength) {
			int cut = (toLength - middle.length()) / 2;
			String left = text.substring(0, cut);
			String right = text.substring(text.length()-cut,text.length());
			return left+middle+right;
		} else {
			return text;
		}
	}

	public static final String[] splitStringToFixedLengths(String in, int length) {
		if (in!=null) {
			List<String> ret = new ArrayList<>();
			int p = 0;
			while (p<in.length()) {
				if (p+length<in.length()) {
					ret.add(in.substring(p,p+length));
				} else {
					ret.add(in.substring(p));
				}
				p+=length;
			}
			return CollectionsHelper.asArray(String.class, ret);
		} else {
			return new String[0];
		}
	}

	public static final String nullSafeToLowerCase(String str) {
		if (str!=null) {
			return str.toLowerCase();
		} else {
			return null;
		}
	}

	public static final String nullSafeToUpperCase(String str) {
		if (str!=null) {
			return str.toUpperCase();
		} else {
			return null;
		}
	}

	public static final String nullSafeSubstring(String str, int beginIndex, int endIndex) {
		if (str!=null) {
			if (endIndex>str.length()) {
				return str.substring(beginIndex);
			} else {
				return str.substring(beginIndex, endIndex);
			}
		} else {
			return null;
		}
	}

	public static final boolean nullSafeMatches(Pattern pattern, String str) {
		return PatternHelper.nullSafeMatches(pattern, str);
	}

	public static final boolean nullSafeNotMatches(Pattern pattern, String str) {
		return PatternHelper.nullSafeNotMatches(pattern, str);
	}

	public static final Character[] toCharacterArray(String str) {
		if (str==null) {
			return null;
		} else {
			Character[] ret = new Character[str.length()];
			for (int i=0;i<str.length();i++) {
				ret[i] = Character.valueOf(str.charAt(i));
			}
			return ret;
		}
	}

	public static final String firstNotNull(String... strings) {
		for (String s:strings) {
			if (s!=null) {
				return s;
			}
		}
		return null;
	}

	private static final Pattern JS_STRINGS_ESCAPE = Pattern.compile("\\'");
	public static final String escapeJsString(String str) {
		if (!isNullOrEmpty(str)) {
			return JS_STRINGS_ESCAPE.matcher(str).replaceAll("\\\\'");
		} else {
			return str;
		}
	}

	public static final Pattern COMMA_PATTERN = Pattern.compile("[\\,]+");
	public static final Pattern COMMA_OR_TAB_PATTERN = Pattern.compile("[\\,\\t]+");
	public static final Pattern SPLIT_PATTERN = Pattern.compile("[\\s\\.\\,\\;]+");
	public static final Pattern LINES_SPLIT_PATTERN = Pattern.compile("[\\n\\r]+",Pattern.MULTILINE|Pattern.DOTALL);

	public static final String[] nullSafeSplit(String input, Pattern splitPattern, boolean defaultToNull) {
		if (input==null) {
			if (defaultToNull) {
				return null;
			} else {
				return new String[0];
			}
		} else {
			return splitPattern.split(input);
		}
	}

	public static final String[] nullSafeSplit(String input, Pattern splitPattern) {
		return nullSafeSplit(input, splitPattern, true);
	}

	public static final String[] nullSafeSplit(String input) {
		return nullSafeSplit(input, SPLIT_PATTERN);
	}

	public static final <T> Collection<T> splitAndParceToCollection(Collection<T> coll, String str, Pattern splitPattern, Function<String,T> parce) {
		for (String s:nullSafeSplit(str,splitPattern,false)) {
			if (!TextHelper.isNullOrEmpty(s)) {
				T item = parce.apply(s);
				if (item!=null) {
					coll.add(item);
				}
			}
		}
		return coll;
	}
	public static final <T> Collection<T> splitAndParceToCollection(Collection<T> coll, String str, Function<String,T> parce) {
		return splitAndParceToCollection(coll, str, SPLIT_PATTERN, parce);
	}
	public static final List<Integer> splitAndParceToIntegerList(String str) {
		return (List<Integer>)splitAndParceToCollection(new ArrayList<Integer>(), str, SPLIT_PATTERN, s->Integer.valueOf(s));
	}

	public static final <T> String nullSafeFormat(T obj, Formatter<T> formatter, String defaultValue) {
		if (obj!=null) {
			return formatter.format(obj);
		} else {
			return defaultValue;
		}
	}

	public static String generateNSeparatedChars(int size, char separator, char value) {
		StringBuilder ret = new StringBuilder();
		for (int i=0;i<size;i++) {
			ret.append(value);
			if (i<size-1) {
				ret.append(separator);
			}
		}
		return ret.toString();
	}

	public static final String escape(String str) {
		StringBuilder ret = new StringBuilder();
		ret.append('"');
		if (!isNullOrEmpty(str)) {
			ret.append(str);
		}
		ret.append('"');
		return ret.toString();
	}

	public static final String escapeForCsv(String str) {
		StringBuilder ret = new StringBuilder();
		if (!isNullOrEmpty(str)) {
			for (char c:str.toCharArray()) {
				if (c=='"') {
					ret.append('\\');
				}
				ret.append(c);
			}
		}
		return ret.toString();
	}


	private static final Pattern SPLIT_BY_SPACES = Pattern.compile("\\S+");
	public static final String nullSafeShortenStringBySpaces(String longString, int partsCount, String delimitor) {
		if (!isNullOrEmpty(longString)) {
			Matcher matcher = SPLIT_BY_SPACES.matcher(longString);
			int count=0;
			List<String> ret = new ArrayList<>();
			while(count<partsCount && matcher.find()) {
				ret.add(matcher.group(0));
				count++;
			}
			return ret.stream().collect(Collectors.joining(delimitor));
		} else {
			return longString;
		}
	}
	public static final String nullSafeShortenStringBySpaces(String longString, int partsCount) {
		return nullSafeShortenStringBySpaces(longString,partsCount," ");
	}

	public static final boolean nullSafeIn(String compareTo, String... options) {
		if (compareTo==null || options==null || options.length==0) {
			return false;
		} else {
			for (String o:options) {
				if (compareTo.equals(o)) {
					return true;
				}
			}
			return false;
		}
	}

	public static final String orElse(String str, String orElse) {
		if (str!=null) {
			return str;
		} else {
			return orElse;
		}
	}

	public static final String orElse(String str) {
		return orElse(str,"");
	}

	public static final boolean nullSafeEqualsSplit(String matchTo, String matchFrom, Pattern split, Equals<String> check) {
		if (matchTo!=null && matchFrom!=null) {
			String[] to = split.split(matchTo);
			String[] from = split.split(matchFrom);
			if (to.length==from.length) {
				for (int i=0;i<to.length;i++) {
					if (!check.check(from[i],to[i])) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static final String toHexDump(String str) {
		if (str==null) {
			return null;
		} else {
			StringBuilder ret = new StringBuilder();
			char[] chars = str.toCharArray();
			for (int i=0;i<chars.length;i++) {
				char c = chars[i];
				if (c>0) {
					ret.append(String.format("0x%02x(%c)", (int)chars[i],chars[i]));
				} else {
					ret.append(String.format("0x%02x", (int)chars[i]));
				}
				if (i<chars.length-1) {
					ret.append(' ');
				}
			}
			return ret.toString();
		}
	}

	public static final String toHexString(byte[] in) {
		if (in==null) {
			return null;
		} else {
			StringBuilder ret = new StringBuilder();
			for (int i=0;i<in.length;i++) {
				ret.append(String.format("%02x", in[i] & 0xff));
			}
			return ret.toString();
		}
	}

	public static final String urlDecode(String in, Charset charset) {
		try {
			if (in!=null) {
				return URLDecoder.decode(in, charset.name());
			} else {
				return null;
			}
		} catch (UnsupportedEncodingException e) { 
			throw new RuntimeException(e);
		}

	}
	public static final String urlDecode(String in) {
		return urlDecode(in, StandardCharsets.UTF_8);
	}

	public static final boolean applyFilter(Pattern filter, Object... any) {
		if (any!=null) {
			for (Object o:any) {
				if (o!=null) {
					if (filter.matcher(o.toString()).find()) {
						return true;
					}
				}
			}
			return false;
		} else {
			return true;
		}
	}

	public static final Pattern MATCH_ANY = Pattern.compile(".*",Pattern.MULTILINE|Pattern.DOTALL);

	public static final String lineOf(char c, int length) {
		char[] buf = new char[length];
		Arrays.fill(buf, c);
		return String.valueOf(buf);
	}

	public static final String lineOf(char c, int... lengths) {
		return lineOf(c, ' ', lengths);
	}
	
	public static final String lineOf(char line, char delimiter, int... lengths) {
		if (lengths!=null && lengths.length>0) {
			StringBuilder ret = new StringBuilder();
			for (int i=0;i<lengths.length;i++) {
				ret.append(lineOf(line,lengths[i]));
				if (i<lengths.length-1) {
					ret.append(delimiter);
				}
			}
			return ret.toString();
		} else {
			return null;
		}

	}

	public static final char[] nullSafeToCharArray(String str) {
		if (str!=null) {
			return str.toCharArray();
		} else {
			return null;
		}
	}

	public static final String centered(String str, int length) {
		int delta = length-str.length();
		if (delta>0) {
			int left = delta/2;
			int right = delta-left;
			return lineOf(' ', left)+str+lineOf(' ',right);
		} else {
			return str;
		}
	}
	public static final String self(String s) {
		return s;
	}

	//	&amp; → & (ampersand, U+0026)
	//	&lt; → < (less-than sign, U+003C)
	//	&gt; → > (greater-than sign, U+003E)
	//	&quot; → " (quotation mark, U+0022)
	//	&apos; → ' (apostrophe, U+0027)
	private static final EscapeEntity[] ESCAPE_ENTITIES = {
			new EscapeEntity("&", "&amp;"),
			new EscapeEntity("<", "&lt;"),
			new EscapeEntity(">", "&gt;"),
			new EscapeEntity("\"", "&quot;"),
			new EscapeEntity("'", "&apos;"),
	}; 

	public static final String escapeHtmlEntities(String input) {
		String ret = input;
		for (EscapeEntity escape:ESCAPE_ENTITIES) {
			ret = escape.pattern.matcher(ret).replaceAll(escape.replacement);
		}
		return ret;
	}

	public static final boolean isNullOrValue(String str, String value) {
		return str==null || value.equals(str);
	}

	public static final String getByIndexOrException(String[] record, int index) {
		if (record!=null) {
			if (record.length>index) {
				return record[index];
			} else {
				throw new IllegalArgumentException(String.format("Expected to have element by index %d", index));
			}
		} else {
			throw new IllegalArgumentException("Record is null");
		}
	}
	public static final String getByIndexOrElse(String[] record, int index, String orElse) {
		if (record!=null) {
			if (record.length>index) {
				return record[index];
			} else {
				return orElse;
			}
		} else {
			return orElse;
		}
	}
	public static final String getByIndexOrNull(String[] record, int index) {
		return getByIndexOrElse(record, index, null);
	}
}

