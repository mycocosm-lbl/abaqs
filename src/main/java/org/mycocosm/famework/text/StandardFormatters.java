package org.mycocosm.famework.text;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.Format;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.mycocosm.famework.time.TimeIntervalFormat;

public enum StandardFormatters implements Formatter<Object>  {
	Default,
	NullSafeDefaultEmptyNull {
		@Override
		public String format(Object o) {
			return DEFAULT_NULL_SAFE_EMPTY_NULL.format(o);
		}
	},
	NullSafeDefault {
		@Override
		public String format(Object o) {
			return DEFAULT_NULL_SAFE.format(o);
		}
	},
	ShortDate {
		@Override
		public String format(Object o) {
			if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
				return StandardFormatters.DEFAULT_SHORT_DATE_TIME.format((TemporalAccessor)o);
			} else {
				return preferredFormatFallToDefault(Date.class, DEFAULT_SHORT_DATE, o);
			}
		}
	},
	ShorterDateTime {
		@Override
		public String format(Object o) {
			if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
				return StandardFormatters.DEFAULT_SHORTER_DATE_TIME.format((TemporalAccessor)o);
			} else {
				return preferredFormatFallToDefault(Date.class, DEFAULT_SHORTER_DATE, o);
			}
		}
	},
	ShorterDateTimeEmptyNull {
		@Override
		public String format(Object o) {
			if (o!=null) {
				if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
					return StandardFormatters.DEFAULT_SHORTER_DATE_TIME.format((TemporalAccessor)o);
				} else {
					return preferredFormatFallToDefault(Date.class, DEFAULT_SHORTER_DATE, o);
				}
			} else {
				return "";
			}
		}
	},
	LongDate {
		@Override
		public String format(Object o) {
			if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
				return StandardFormatters.DEFAULT_LONG_DATE_TIME.format((TemporalAccessor)o);
			} else {
				return preferredFormatFallToDefault(Date.class, DEFAULT_LONG_DATE, o);
			}
		}
	},
	ShortSortableDate {
		@Override
		public String format(Object o) {
			if (o!=null) {
				if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
					return StandardFormatters.DEFAULT_SHORT_SORTABLE_DATE_TIME.format((TemporalAccessor)o);
				} else {
					return preferredFormatFallToDefault(Date.class, DEFAULT_SHORT_SORTABLE_DATE, o);
				}
			} else {
				return "null";
			}
		}
	},
	ShortSortableDateEmptyNull {
		@Override
		public String format(Object o) {
			if (o!=null) {
				if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
					return StandardFormatters.DEFAULT_SHORT_SORTABLE_DATE_TIME.format((TemporalAccessor)o);
				} else {
					return preferredFormatFallToDefault(Date.class, DEFAULT_SHORT_SORTABLE_DATE, o);
				}
			} else {
				return "";
			}
		}
	},
	ShortSortableDateNullSafe {
		@Override
		public String format(Object o) {
			if (o!=null) {
				if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
					return StandardFormatters.DEFAULT_SHORT_SORTABLE_DATE_TIME.format((TemporalAccessor)o);
				} else {
					return preferredFormatFallToDefault(Date.class, DEFAULT_SHORT_SORTABLE_DATE, o);
				}
			} else {
				return null;
			}
		}
	},
	FineSortableDate {
		@Override
		public String format(Object o) {
			if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
				return StandardFormatters.DEFAULT_FINE_SORTABLE_DATE_TIME.format((TemporalAccessor)o);
			} else {
				return preferredFormatFallToDefault(Date.class, DEFAULT_FINE_SORTABLE_DATE, o);
			}
		}
	},
	LongSortableDate {
		@Override
		public String format(Object o) {
			if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
				return StandardFormatters.DEFAULT_LONG_SORTABLE_DATE_TIME.format((TemporalAccessor)o);
			} else {
				return preferredFormatFallToDefault(Date.class, DEFAULT_LONG_SORTABLE_DATE, o);
			}
		}
	},
	LongSortableDateEmptyNull {
		@Override
		public String format(Object o) {
			if (o!=null) {
				if (TemporalAccessor.class.isAssignableFrom(o.getClass())) {
					return StandardFormatters.DEFAULT_LONG_SORTABLE_DATE_TIME.format((TemporalAccessor)o);
				} else {
					return preferredFormatFallToDefault(Date.class, DEFAULT_LONG_SORTABLE_DATE, o);
				}
			} else {
				return "";
			}
		}
	},
	Double {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_DOUBLE, o);
		}
	},
	Double4Digits {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_DOUBLE_4_DIGITS, o);
		}
	},
	DoubleOptionalDigits {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_DOUBLE_OPTIONAL_DECIMAL, o);
		}
	},
	Float {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_FLOAT, o);
		}
	},
	Scientific {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_SCIENTIFIC, o);
		}
	},
	Integer {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_INTEGER, o);
		}
	},
	IntegerEmptyNull {
		@Override
		public String format(Object o) {
			if (o!=null) {
				return preferredFormatFallToDefault(Number.class, DEFAULT_INTEGER, o);
			} else {
				return "";
			}
		}
	},
	IntegerNoComma {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_NO_COMMA_INTEGER, o);
		}
	},
	IntegerNoCommaEmptyNull {
		@Override
		public String format(Object o) {
			if (o!=null) {
				return preferredFormatFallToDefault(Number.class, DEFAULT_NO_COMMA_INTEGER, o);
			} else {
				return "";
			}
		}
	},
	Long {
		@Override
		public String format(Object o) {
			return preferredFormatFallToDefault(Number.class, DEFAULT_LONG, o);
		}
	},
	LongEmptyNull {
		@Override
		public String format(Object o) {
			if (o!=null) {
				return preferredFormatFallToDefault(Number.class, DEFAULT_LONG, o);
			} else {
				return "";
			}
		}
	},
	Percent {
		@Override
		public String format(Object o) {
			if (Number.class.isAssignableFrom(o.getClass())) {
				return DEFAULT_PERCENT.format((Number)o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for percent format");
			}
		}
	},
	PercentNoComma {
		@Override
		public String format(Object o) {
			if (Number.class.isAssignableFrom(o.getClass())) {
				return DEFAULT_NO_COMMA_PERCENT.format((Number)o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for percent format");
			}
		}
	},
	PercentEmptyNullOrNaN {
		@Override
		public String format(Object o) {
			if (o!=null) {
				if (Number.class.isAssignableFrom(o.getClass())) {
					double d = ((Number)o).doubleValue();
					if (!java.lang.Double.isNaN(d)) {
						return DEFAULT_PERCENT.format(d);
					} else {
						return "";
					}
				} else {
					throw new IllegalArgumentException(""+o+" is not valid for percent format");
				}
			} else {
				return "";
			}
		}
	},
	Percent100 {
		@Override
		public String format(Object o) {
			if (Number.class.isAssignableFrom(o.getClass())) {
				return DEFAULT_PERCENT_100.format((Number)o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for percent format");
			}
		}
	},
	Color {
		@Override
		public String format(Object o) {
			if (Color.class.isAssignableFrom(o.getClass())) {
				return DEFAULT_COLOR.format((Color)o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for color format");
			}
		}
	},
	TimeInterval {
		@Override
		public String format(Object o) {
			if (org.mycocosm.famework.time.TimeInterval.class.isAssignableFrom(o.getClass()) || Number.class.isAssignableFrom(o.getClass()) || Duration.class.isAssignableFrom(o.getClass())) {
				return TIME_INTERVAL_FORMAT.format(o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for time interval format");
			}
		}
	},
	Duration {
		@Override
		public String format(Object o) {
			if (Duration.class.isAssignableFrom(o.getClass())) {
				return DURATION.format((Duration)o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for duration format");
			}
		}
	},
	SmartSizeEmptyNull {
		@Override
		public String format(Object o) {
			return DEFAULT_SMART_SIZE_EMPTY_NULL.format((Number)o);
		}
	},
	SmartCountEmptyNull {
		@Override
		public String format(Object o) {
			return DEFAULT_SMART_COUNT_EMPTY_NULL.format((Number)o);
		}
	},
	BooleanFormatter {
		@Override
		public String format(Object o) {
			if (Boolean.class.isAssignableFrom(o.getClass())) {
				return DEFAULT_BOOLEAN.format((Boolean)o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for Boolean format");
			}
		}
	},
	BooleanFormatterYNEmptyNull {
		@Override
		public String format(Object o) {
			if (o!=null) {
				if (Boolean.class.isAssignableFrom(o.getClass())) {
					return ((Boolean)o)?"Y":"N";
				} else {
					throw new IllegalArgumentException(""+o+" is not valid for Boolean format");
				}
			} else {
				return "";
			}
		}
	},
	BooleanDefaultFalse {
		@Override
		public String format(Object o) {
			if (Boolean.class.isAssignableFrom(o.getClass())) {
				return DEFAULT_BOOLEAN_FALSE.format((Boolean)o);
			} else {
				throw new IllegalArgumentException(""+o+" is not valid for Boolean format");
			}
		}
	},
	NoFormat {
		@Override
		public String format(Object o) {
			return DEFAULT_NO_FORMAT.format(o);
		}
	};

	@SuppressWarnings("unchecked")
	private static final <T> String preferredFormatFallToDefault(Class<T> conditionClass, Formatter<T> preferredFormatter, Object o) {
		if (o!=null && conditionClass.isAssignableFrom(o.getClass())) {
			return preferredFormatter.format((T)o);
		} else {
			return DEFAULT_NULL_SAFE.format(o);
		}
	}

	private static final class DefaultFormatter implements Formatter<Object> {
		String nullValue = "";

		private DefaultFormatter(String nullValue) {
			this.nullValue = nullValue;
		}

		@Override
		public String format(Object o) {
			if (o==null) {
				return nullValue;
			} else {
				Class<?> objectClass = o.getClass();
				if (Date.class.isAssignableFrom(objectClass)) {
					return DEFAULT_SHORT_DATE.format((Date)o);
				} else if (java.lang.Double.class.isAssignableFrom(objectClass)) {
					return DEFAULT_DOUBLE.format((Double)o);
				} else if (java.lang.Float.class.isAssignableFrom(objectClass)) {
					return DEFAULT_FLOAT.format((Float)o);
				} else if (java.lang.Integer.class.isAssignableFrom(objectClass)) {
					return DEFAULT_INTEGER.format((Integer)o);
				} else if (java.lang.Short.class.isAssignableFrom(objectClass)) {
					return DEFAULT_INTEGER.format((Integer)o);
				} else if (java.lang.Long.class.isAssignableFrom(objectClass)) {
					return DEFAULT_LONG.format((Long)o);
				} else {
					return o.toString();
				}
			}
		}
	}

	public static final Formatter<Object> DEFAULT_NULL_SAFE_EMPTY_NULL = new DefaultFormatter("");

	public static final Formatter<Object> DEFAULT_NULL_SAFE = new DefaultFormatter("null");

	public static final Formatter<String> DEFAULT_STRING = new Formatter<String>() {

		@Override
		public String format(String o) {
			return DEFAULT_NO_FORMAT.format(o);
		}
	};

	public static final Formatter<Boolean> DEFAULT_BOOLEAN_FALSE = new Formatter<Boolean>() {

		@Override
		public String format(Boolean o) {
			if (o!=null) {
				return o.toString();
			} else {
				return Boolean.FALSE.toString();
			}
		}
	};

	public static final Formatter<Boolean> DEFAULT_BOOLEAN = new Formatter<Boolean>() {

		@Override
		public String format(Boolean o) {
			return DEFAULT_NO_FORMAT.format(o);
		}
	};

	public static final Formatter<Object> DEFAULT_NO_FORMAT = new Formatter<Object>() {

		@Override
		public String format(Object o) {
			if (o!=null) {
				return o.toString();
			} else {
				return "null";
			}
		}
	};

	public static final Formatter<Date> DEFAULT_SHORT_SORTABLE_DATE = new Formatter<Date>() {

		@Override
		public String format(Date o) {
			return String.format("%1$tY-%1$tm-%1$td", o);
		}
	};

	public static final Formatter<Date> DEFAULT_LONG_SORTABLE_DATE = new Formatter<Date>() {

		@Override
		public String format(Date o) {
			return String.format("%1$tY-%1$tm-%1$td+%1$tH-%1$tM-%1$tS", o);
		}
	};
	public static final Formatter<Date> DEFAULT_FINE_SORTABLE_DATE = new Formatter<Date>() {

		@Override
		public String format(Date o) {
			return String.format("%1$tY-%1$tm-%1$td+%1$tH-%1$tM-%1$tS.%1$tL", o);
		}
	};

	public static final DateTimeFormatter DEFAULT_FINE_SORTABLE_DATE_TIME = DateTimeFormatter
			.ofPattern("yyyy-MM-dd+HH-mm-ss.SSS")
			.withZone(ZoneId.systemDefault());
	public static final DateTimeFormatter DEFAULT_LONG_SORTABLE_DATE_TIME = DateTimeFormatter
			.ofPattern("yyyy-MM-dd+HH-mm-ss")
			.withZone(ZoneId.systemDefault());
	public static final DateTimeFormatter DEFAULT_SHORTER_DATE_TIME = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm")
			.withZone(ZoneId.systemDefault());
	public static final DateTimeFormatter DEFAULT_SHORT_SORTABLE_DATE_TIME = DateTimeFormatter
			.ofPattern("yyyy-MM-dd")
			.withZone(ZoneId.systemDefault());
	public static final DateTimeFormatter DEFAULT_SHORT_DATE_TIME = DateTimeFormatter
			.ofPattern("dd-MMM-yyyy")
			.withZone(ZoneId.systemDefault());

	public static final DateTimeFormatter DEFAULT_LONG_DATE_TIME = DateTimeFormatter
			.ofPattern("dd-MMM-yyyy HH-mm-ss")
			.withZone(ZoneId.systemDefault());

	public static final Formatter<Date> DEFAULT_SHORT_DATE = new Formatter<>() {

		@Override
		public String format(Date o) {
			return String.format("%1$td-%1$tb-%1$tY", o);
		}
	};

	public static final Formatter<Date> DEFAULT_LONG_DATE = new Formatter<>() {

		@Override
		public String format(Date o) {
			return String.format("%1$td-%1$tb-%1$tY %1$tH:%1$tM:%1$tS", o);
		}
	};
	public static final Formatter<Date> DEFAULT_SHORTER_DATE = new Formatter<>() {

		@Override
		public String format(Date o) {
			return String.format("%1$td-%1$tb-%1$tY %1$tH:%1$tM", o);
		}
	};
	public static final Formatter<Duration> DURATION = new Formatter<>() {
		@Override
		public String format(Duration o) {
			return TIME_INTERVAL_FORMAT.format(org.mycocosm.famework.time.TimeInterval.nullSafeFromDuration(o));
		}
	};


	private static class SmartSizeFormatterOption {
		private char suffix;
		private double multiplier;
		private SmartSizeFormatterOption(char suffix, double multiplier) {
			this.suffix = suffix;
			this.multiplier = multiplier;
		}
	}

	public static final double SIZE_K = 1024.0;
	public static final double SIZE_M = SIZE_K*SIZE_K;
	public static final double SIZE_G = SIZE_M*SIZE_K;
	public static final double SIZE_T = SIZE_G*SIZE_K;
	public static final double SIZE_P = SIZE_T*SIZE_K;

	private static final SmartSizeFormatterOption[] SM_SIZE_OPTIONS = {
			new SmartSizeFormatterOption('K', SIZE_K),
			new SmartSizeFormatterOption('M', SIZE_M),
			new SmartSizeFormatterOption('G', SIZE_G),
			new SmartSizeFormatterOption('T', SIZE_T),
			new SmartSizeFormatterOption('P', SIZE_P)
	};

	public static final long COUNT_K = 1000L;
	public static final long COUNT_M = COUNT_K*COUNT_K;
	public static final long COUNT_G = COUNT_M*COUNT_K;
	public static final long COUNT_T = COUNT_G*COUNT_K;
	public static final long COUNT_P = COUNT_T*COUNT_K;

	public static final Formatter<Number> DEFAULT_SMART_SIZE_EMPTY_NULL = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			if (o!=null) {
				double v = o.doubleValue();
				if (!java.lang.Double.isNaN(v)) {
					if (v<=SIZE_K) {
						return FLOAT_FORMAT.format(v);
					} else {
						for (int i=0;i<SM_SIZE_OPTIONS.length-1;i++) {
							SmartSizeFormatterOption x = SM_SIZE_OPTIONS[i];
							if (v/x.multiplier<SIZE_K) {
								return FLOAT_FORMAT.format(v/x.multiplier)+x.suffix;
							}
						}
						SmartSizeFormatterOption x = SM_SIZE_OPTIONS[SM_SIZE_OPTIONS.length-1];
						return FLOAT_FORMAT.format(v/x.multiplier)+x.suffix;
					}
				} else {
					return "";
				}
			} else {
				return "";
			}
		}
	};

	private static final SmartSizeFormatterOption[] SM_COUNT_OPTIONS = {
			new SmartSizeFormatterOption('k', COUNT_K),
			new SmartSizeFormatterOption('m', COUNT_M),
			new SmartSizeFormatterOption('g', COUNT_G),
			new SmartSizeFormatterOption('t', COUNT_T),
			new SmartSizeFormatterOption('p', COUNT_P)
	};

	public static final Formatter<Number> DEFAULT_SMART_COUNT_EMPTY_NULL = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			if (o!=null) {
				long v = o.longValue();
				if (v<=COUNT_K) {
					return CARDINAL_FORMAT.format(v);
				} else {
					for (int i=0;i<SM_COUNT_OPTIONS.length-1;i++) {
						SmartSizeFormatterOption x = SM_COUNT_OPTIONS[i];
						if (v/x.multiplier<COUNT_K) {
							return CARDINAL_FORMAT.format(v/x.multiplier)+x.suffix;
						}
					}
					SmartSizeFormatterOption x = SM_COUNT_OPTIONS[SM_COUNT_OPTIONS.length-1];
					return CARDINAL_FORMAT.format(v/x.multiplier)+x.suffix;
				}
			} else {
				return "";
			}
		}
	};

	public static final Formatter<Number> DEFAULT_SCIENTIFIC = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return SCIENTIFIC_FORMAT.format(o.doubleValue());
		}
	};

	public static final Formatter<Number> DEFAULT_DOUBLE = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return FLOAT_FORMAT.format(o.doubleValue());
		}
	};
	public static final Formatter<Number> DEFAULT_DOUBLE_4_DIGITS = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return FLOAT_FORMAT_4_DIGITS.format(o.doubleValue());
		}
	};
	public static final Formatter<Number> DEFAULT_DOUBLE_OPTIONAL_DECIMAL = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return FLOAT_FORMAT_OPTIONAL_DECIMAL.format(o.doubleValue());
		}
	};

	public static final Formatter<Number> DEFAULT_DOUBLE_OPTIONAL_ZERO = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return FLOAT_FORMAT_OPTIONAL_ZERO.format(o.doubleValue());
		}
	};

	public static final Formatter<Number> DEFAULT_FLOAT = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return FLOAT_FORMAT.format(o.floatValue());
		}
	};

	public static final Formatter<Number> DEFAULT_PERCENT_100 = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return PERCENT_FORMAT.format(o.floatValue()/100.0);
		}
	};

	public static final Formatter<Number> DEFAULT_PERCENT = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return PERCENT_FORMAT.format(o.floatValue());
		}
	};

	public static final Formatter<Number> DEFAULT_NO_COMMA_PERCENT = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return PERCENT_NO_COMMA_FORMAT.format(o.floatValue());
		}
	};

	public static final Formatter<Number> DEFAULT_INTEGER = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return CARDINAL_FORMAT.format(o.intValue());
		}
	};
	public static final Formatter<Number> DEFAULT_NO_COMMA_INTEGER = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return CARDINAL_NO_COMMA_FORMAT.format(o.intValue());
		}
	};
	public static final Formatter<Number> DEFAULT_LONG = new Formatter<Number>() {

		@Override
		public String format(Number o) {
			return CARDINAL_FORMAT.format(o.longValue());
		}
	};

	public static final Formatter<Color> DEFAULT_COLOR = new Formatter<Color>() {

		@Override
		public String format(Color o) {
			return String.format("#%02X%02X%02X", 0xff&o.getRed(), 0xff&o.getGreen(), 0xff&o.getBlue());
		}
	};

	public static final Formatter<?> TIME_INTERVAL = new Formatter<Object>() {

		@Override
		public String format(Object o) {
			return TIME_INTERVAL_FORMAT.format(o);
		}
	};

	private static final Format PERCENT_FORMAT = new DecimalFormat("#,##0.00%");
	private static final Format PERCENT_NO_COMMA_FORMAT = new DecimalFormat("###0.00%");
	private static final Format FLOAT_FORMAT = new DecimalFormat("#,##0.00");
	private static final Format FLOAT_FORMAT_4_DIGITS = new DecimalFormat("#,##0.0000");
	private static final Format FLOAT_FORMAT_OPTIONAL_DECIMAL = new DecimalFormat("#,##0.##");
	private static final Format FLOAT_FORMAT_OPTIONAL_ZERO = new DecimalFormat("#,###.##");
	private static final Format SCIENTIFIC_FORMAT = new DecimalFormat("0.00E000");
	private static final Format CARDINAL_FORMAT = new DecimalFormat("#,##0");
	private static final Format CARDINAL_NO_COMMA_FORMAT = new DecimalFormat("#0");
	private static final Format TIME_INTERVAL_FORMAT = new TimeIntervalFormat();

	public static final Formatter<?> wrappedFormat(final Format format) {
		return new Formatter<Object>() {

			@Override
			public String format(Object o) {
				return format.format(o);
			}

		};
	}

	public static final Formatter<Object> DEFAULT = new Formatter<Object>() {

		@Override
		public String format(Object o) {
			return o.toString();
		}
	};

	public static final Formatter<String> STRING = new Formatter<String>() {

		@Override
		public String format(String o) {
			return o;
		}
	};

	public static final Formatter<String> NULL_SAFE_STRING = new Formatter<String>() {

		@Override
		public String format(String o) {
			if (o!=null) {
				return o;
			} else {
				return "";
			}
		}
	};

	@Override
	public String format(Object o) {
		return DEFAULT.format(o);
	}

}
