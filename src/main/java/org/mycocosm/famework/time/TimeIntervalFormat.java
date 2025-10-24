package org.mycocosm.famework.time;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.Duration;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeIntervalFormat extends Format {

	private static final long serialVersionUID = -2604045854413578823L;

	private static final TimeIntervalFormat INSTANCE = new TimeIntervalFormat();

	public static final TimeIntervalFormat getInstance() {
		return INSTANCE;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		long input;
		boolean hasValue = false;
		if (obj!=null) {
			Class<?> clasz = obj.getClass();
			if (TimeInterval.class.isAssignableFrom(clasz)) {
				input = ((TimeInterval)obj).longValue();
			} else if (Duration.class.isAssignableFrom(clasz)) {
				input = ((Duration)obj).toMillis();
			} else if (Number.class.isAssignableFrom(clasz)) {
				input = ((Number)obj).longValue();
			} else {
				throw new IllegalArgumentException("Time Interval format accept only TimeInterval or Number arguments, passed: '"+clasz.toString()+"'");
			}
			if (hasField(input, TimeInterval.DAY)) {
				input = appendField(input, 'd', TimeInterval.DAY, toAppendTo);
				hasValue=true;
			}
			if (hasField(input, TimeInterval.HOUR)) {
				if (toAppendTo.length()>0) {
					toAppendTo.append(' ');
				}
				input = appendField(input, 'h', TimeInterval.HOUR, toAppendTo);
				hasValue=true;
			}
			if (hasField(input, TimeInterval.MIN)) {
				if (toAppendTo.length()>0) {
					toAppendTo.append(' ');
				}
				input = appendField(input, 'm', TimeInterval.MIN, toAppendTo);
				hasValue=true;
			}
			if (hasField(input, TimeInterval.SEC) || !hasValue) {
				if (toAppendTo.length()>0) {
					toAppendTo.append(' ');
				}
				input = appendField(input, (char)0, TimeInterval.SEC, toAppendTo, !hasValue);
				if (input>0) { // if we have milliseconds
					toAppendTo.append(String.format(".%ds", input));
				} else {
					toAppendTo.append("s");
				}
			}
		} else {
			return toAppendTo.append("null");
		}
		return toAppendTo;
	}

	private long appendField(long input, char chr, long unit, StringBuffer buf) {
		return appendField(input, chr, unit, buf, false);
	}

	private long appendField(long input, char chr, long unit, StringBuffer buf, boolean force) {
		long val = input / unit;
		long ret = input % unit;
		if (force || val > 0) {
			buf.append(val);
			if (chr>0) {
				buf.append(chr);
			}
		}
		return ret;
	}

	private boolean hasField(long input, long unit) {
		long val = input / unit;
		return val > 0;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		long ret = 0L;
		int startingPos = 0;
		if (pos != null) {
			startingPos = pos.getIndex();
			pos.setIndex(source.length());
		}
		StringTokenizer t = new StringTokenizer(source.toLowerCase().substring(startingPos), " :,+");
		while (t.hasMoreTokens()) {
			String s = t.nextToken();
			if (s != null) {
				if (isValidDef(s)) {
					ret = ret + parseDef(s);
				} else if (t.hasMoreTokens()) {
					String ss = t.nextToken();
					ret = ret + parseDef(s, ss);
				} else {
					throw new IllegalStateException(String.format("Unrecognized time unit:'%s' source:'%s'",s,source));
				}
			}
		}
		return ret;
	}

	private static final class IntervalData {
		private final Pattern defWithNumber;
		private final Pattern defWithoutNumber;
		private final double valueMilliseconds;

		protected IntervalData(String defWithNumber, String defWithoutNumber, double value) {
			this.defWithNumber = Pattern.compile(defWithNumber, Pattern.CASE_INSENSITIVE);
			this.defWithoutNumber = Pattern.compile(defWithoutNumber, Pattern.CASE_INSENSITIVE);
			this.valueMilliseconds = value;
		}
	}

	private static final IntervalData INTERVALS_TABLE[] = { 
		new IntervalData("([\\d\\.]+)\\s*(ms|msec|millisec.*)\\.?","(ms|msec|millisec.*)\\.?",TimeInterval.MSEC),
		new IntervalData("([\\d\\.]+)\\s*(s|sec.*)\\.?","(s|sec.*)\\.?",TimeInterval.SEC),
		new IntervalData("([\\d\\.]+)\\s*(m|min.*)\\.?","(m|min.*)\\.?",TimeInterval.MIN),
		new IntervalData("([\\d\\.]+)\\s*(h|hs|hour.*)\\.?","(h|hs|hour.*)\\.?",TimeInterval.HOUR),
		new IntervalData("([\\d\\.]+)\\s*(d|day.*)\\.?","(d|day.*)\\.?",TimeInterval.DAY),
		new IntervalData("([\\d\\.]+)\\s*(w|week.*)\\.?","(w|week.*)\\.?",TimeInterval.WEEK),
		new IntervalData("([\\d\\.]+)\\s*(mo|month.*)\\.?","(mo|month.*)\\.?",TimeInterval.DAY*30),
		new IntervalData("([\\d\\.]+)\\s*(y|year.*)\\.?","(y|year.*)\\.?",TimeInterval.DAY*365)
	};

	private static boolean isValidNumber(String def) {
		for (char c : def.toCharArray()) {
			if (!Character.isDigit(c) && '.' != c) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidDef(String def) {
		if (def != null) {
			for (IntervalData t:INTERVALS_TABLE) {
				if (t.defWithNumber.matcher(def).matches()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isValidDef(String def, String param) {
		if (def != null && param != null && isValidNumber(def)) {
			for (IntervalData t:INTERVALS_TABLE) {
				if (t.defWithoutNumber.matcher(def).matches()) {
					return true;
				}
			}
		}
		return false;
	}

	public static long parseDef(String def) {
		assert def != null;
		for (IntervalData t:INTERVALS_TABLE) {
			Matcher matcher = t.defWithNumber.matcher(def);
			if (matcher.matches()) {
				double val = Double.parseDouble(matcher.group(1));
				val = val * t.valueMilliseconds;
				return Math.round(val);
			}
		}
		return 0L;
	}

	public static long parseDef(String value, String timeunit) {
		for (IntervalData t:INTERVALS_TABLE) {
			if (t.defWithoutNumber.matcher(timeunit).matches()) {
				double val = Double.parseDouble(value);
				val = val * t.valueMilliseconds;
				return Math.round(val);
			}
		}
		throw new IllegalStateException("Unrecognized time unit:" + timeunit);
	}
}
