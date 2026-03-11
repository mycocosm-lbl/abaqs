package org.mycocosm.framework.biology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mycocosm.framework.collections.ArraysHelper;
import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.text.TextHelper;

public class SequenceSegment {
	public final int start; // inclusive, begin from 1
	public final int end; // inclusive

	private static final Pattern EXONS_SPLIT = Pattern.compile("\\:");
	private static final Pattern PARSER = Pattern.compile("(\\d+)(\\,(\\d+))?");


	// Format start,end:start,end....
	public static final SequenceSegment[] parseExons(String exons) {
		List<SequenceSegment> ret = new ArrayList<>();
		for (String exon:EXONS_SPLIT.split(exons)) {
			SequenceSegment s = parseExon(exon);
			// ignore empty sfExons
			if (s!=null) {
				ret.add(s);
			}
		}
		return CollectionsHelper.asArray(SequenceSegment.class, ret);
	}


	private static final Pattern SEGMENTS_SPLIT = Pattern.compile("\\,");
	private static final Pattern SEGMENT_PARSE = Pattern.compile("(\\d+)\\-(\\d+)");
	// Format start-end,start-end....
	public static SequenceSegment[] parseSegments(String segments) {
		List<SequenceSegment> ret = new ArrayList<>();
		if (!TextHelper.isNullOrEmpty(segments)) {
			for (String segment:SEGMENTS_SPLIT.split(segments)) {
				if (!TextHelper.isNullOrEmpty(segment)) {
					Matcher matcher = SEGMENT_PARSE.matcher(segment);
					if (matcher.matches()) {
						int start = Integer.valueOf(matcher.group(1));
						int end = Integer.valueOf(matcher.group(2));
						ret.add(new SequenceSegment(start, end));
					} else {
						throw new IllegalArgumentException(String.format("Illegal segments line '%s'", segments));
					}
				} else {
					ret.add(null);
				}
			}
			return CollectionsHelper.asArray(SequenceSegment.class, ret);
		} else {
			return null;
		}
	}	

	private static final SequenceSegment parseExon (String str) {
		Matcher m = PARSER.matcher(str);
		if (!TextHelper.isNullOrEmpty(str)) {
			if (m.matches()) {
				if (m.group(3)!=null) {
					return new SequenceSegment(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(3)));
				} else {
					return new SequenceSegment(0, Integer.valueOf(m.group(1)));
				}
			} else {
				throw new IllegalArgumentException(String.format("Illegal sf exons segments line '%s'", str));
			}
		} else {
			return null;
		}
	}

	public SequenceSegment(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getLength() {
		return end-start+1;
	}

	public static final int sortedForStrandPlus(SequenceSegment s1, SequenceSegment s2) {
		int ret = Integer.compare(s1.start, s2.start);
		if (ret==0) {
			return Integer.compare(s1.end, s2.end);
		} else {
			return ret;
		}
	}
	public static final int sortedForStrandMinus(SequenceSegment s1, SequenceSegment s2) {
		int ret = Integer.compare(s2.end, s1.end);
		if (ret==0) {
			return Integer.compare(s2.start, s1.start);
		} else {
			return ret;
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequenceSegment other = (SequenceSegment) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}

	public static String arrayToString(SequenceSegment[] sfExons) {
		if (!ArraysHelper.isNullOrEmpty(sfExons)) {
			return Arrays.stream(sfExons).map(ex->String.format("%d-%d", ex.start, ex.end)).collect(Collectors.joining(","));
		} else {
			return "";
		}
	}

	public static String collectionToString(Collection<SequenceSegment> sfExons) {
		if (!CollectionsHelper.isNullOrEmpty(sfExons)) {
			return sfExons.stream().map(ex->String.format("%d-%d", ex.start, ex.end)).collect(Collectors.joining(","));
		} else {
			return "";
		}
	}

	private static final Pattern STARTS_ENDS_SPLIT = Pattern.compile("\\D+");

	public static SequenceSegment[] fromStartsAndEnds(String startsStr, String endsStr) {
		String[] starts = TextHelper.nullSafeSplit(startsStr, STARTS_ENDS_SPLIT,false);
		String[] ends = TextHelper.nullSafeSplit(endsStr, STARTS_ENDS_SPLIT,false);
		if (starts.length==ends.length) {
			SequenceSegment[] ret = new SequenceSegment[starts.length];
			for (int index=0;index<starts.length;index++) {
				ret[index] = new SequenceSegment(Integer.valueOf(starts[index]), Integer.valueOf(ends[index]));
			}
			return ret;
		} else {
			throw new IllegalArgumentException(String.format("Unable to parse sequence segments, starts:'%s', ends:'%s'", startsStr,endsStr));
		}

	}
}
