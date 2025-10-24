package org.mycocosm.famework.time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mycocosm.famework.text.TextHelper;

public class DateHelper {
	public static final String fromDate(Date date) {
		return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(date);
	}
	public static final Date toDate(String date) throws ParseException {
		return new SimpleDateFormat("dd/MM/yyyy:hh:mm:ss").parse(date);
	}
	public static final Calendar toCalendar(String date, boolean up) throws ParseException {
		return toCalendar(new SimpleDateFormat("dd/MM/yyyy").parse(date),up);
	}

	public static final String now() {
		return fromDate(Calendar.getInstance().getTime());
	}

	public static final String nowUnderscored() {
		return now().replace("/", "_");
	}

	public static final Calendar toCalendar(Date date, boolean up) {
		Calendar ret = new GregorianCalendar();
		ret.setTime(date);
		if (up) {
			ret.set(Calendar.HOUR_OF_DAY, 23);
			ret.set(Calendar.MINUTE, 59);
			ret.set(Calendar.SECOND, 59);
			ret.set(Calendar.MILLISECOND, 999);

		} else {
			ret.set(Calendar.HOUR_OF_DAY, 0);
			ret.set(Calendar.MINUTE, 0);
			ret.set(Calendar.SECOND, 0);
			ret.set(Calendar.MILLISECOND, 0);
		}
		return ret;
	}

	public static final Calendar today(boolean inclusive) {
		Calendar ret = new GregorianCalendar();
		if (inclusive) {
			ret.set(Calendar.HOUR_OF_DAY, 23);
			ret.set(Calendar.MINUTE, 59);
			ret.set(Calendar.SECOND, 59);
			ret.set(Calendar.MILLISECOND, 999);

		} else {
			ret.set(Calendar.HOUR_OF_DAY, 0);
			ret.set(Calendar.MINUTE, 0);
			ret.set(Calendar.SECOND, 0);
			ret.set(Calendar.MILLISECOND, 0);
		}
		return ret;
	}
	public static Date beginningOfDay(Date date) {
		String dateString = fromDate(date);
		dateString = dateString.split(":")[0] + ":00:00:00";
		try {
			return toDate(dateString);
		} catch (ParseException e) {
			throw(new RuntimeException(e));
		}
	}
	public static Date endOfDay(Date date) {
		String dateString = fromDate(date);
		dateString = dateString.split(":")[0] + ":23:59:59";
		try {
			return toDate(dateString);
		} catch (ParseException e) {
			throw(new RuntimeException(e));
		}
	}

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("y-M-d");
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:m:s[.S]");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
			.append(DATE_FORMATTER)
			.parseLenient()
			.appendOptional(new DateTimeFormatterBuilder()
					.appendLiteral(' ')
					.append(TIME_FORMATTER)
					.toFormatter())
			.toFormatter();

	public static final boolean isValidDateTimeString(String str) {
		try {
			parseDateTime(str);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	public static final TemporalAccessor parseDateTime(String str) throws DateTimeParseException {
		return DATE_TIME_FORMATTER.parse(str);
	}

	public static final TemporalAccessor nullSafeParceDateTime(String str) throws DateTimeParseException {
		if (!TextHelper.isNullOrEmpty(str)) {
			return parseDateTime(str);
		} else {
			return null;
		}
	}

	public static final LocalDateTime nullSafeParceDateOptionalTime(String str) throws DateTimeParseException {
		TemporalAccessor ta = nullSafeParceDateTime(str);
		if (ta!=null) {
			if (ta instanceof LocalDateTime) {
				return (LocalDateTime) ta;
			} else if (ta instanceof ZonedDateTime) {
				return ((ZonedDateTime) ta).toLocalDateTime();
			} else if (ta instanceof OffsetDateTime) {
				return ((OffsetDateTime) ta).toLocalDateTime();
			}
			LocalDate date = LocalDate.from(ta);
			LocalTime time = ta.query(TemporalQueries.localTime());
			if (time!=null) {
				return LocalDateTime.of(date, time);
			} else {
				return LocalDateTime.of(date, LocalTime.of(0,0,0,0));
			}
		} else {
			return null;
		}
	}


	private static final <T> T inner(T a, T b, BiPredicate<T, T> f) {
		if (a==null) {
			return b;
		} else if (b==null) {
			return a;
		} else {
			if (f.test(a, b)) {
				return a;
			} else {
				return b;
			}
		}
	}

	@SafeVarargs
	private static final <T> T innerArray(BiPredicate<T, T> f, T... dates) {
		T r = null;
		for (T d:dates) {
			r = inner(r, d, f);
		}
		return r;
	}

	public static final Instant earliest(Instant... dates) {
		return innerArray((a,b)->a.isBefore(b), dates);
	}

	public static final Instant latest(Instant... dates) {
		return innerArray((a,b)->a.isAfter(b), dates);
	}

	public static final ChronoLocalDate earliest(ChronoLocalDate... dates) {
		return innerArray((a,b)->a.isBefore(b), dates);
	}

	public static final ChronoLocalDate latest(ChronoLocalDate... dates) {
		return innerArray((a,b)->a.isAfter(b), dates);
	}

	public static final LocalDateTime earliest(LocalDateTime... dates) {
		return innerArray((a,b)->a.isBefore(b), dates);
	}

	public static final LocalDateTime latest(LocalDateTime... dates) {
		return innerArray((a,b)->a.isAfter(b), dates);
	}

	public static final Date earliest(Date... dates) {
		return innerArray((a,b)->a.before(b), dates);
	}

	public static final Date latest(Date... dates) {
		return innerArray((a,b)->a.after(b), dates);
	}


	private static final <T> Timestamp nullSafeToTimestampInner(T t, Function<T, Long> f) {
		if (t!=null) {
			return new Timestamp(f.apply(t));
		} else {
			return null;
		}
	}

	public static final Timestamp nullSafeToTimestamp(Date date) {
		return nullSafeToTimestampInner(date, x->x.getTime());
	}

	public static final Timestamp nullSafeToTimestamp(Instant instant) {
		return nullSafeToTimestampInner(instant, x->x.toEpochMilli());
	}

	public static final Timestamp nullSafeToTimestamp(LocalDateTime date) {
		return nullSafeToTimestamp(date.toInstant(getSystemDefaultZoneOffset()));
	}

	public static final Timestamp nullSafeToTimestamp(LocalDate date) {
		return nullSafeToTimestamp(date.atStartOfDay().toInstant(getSystemDefaultZoneOffset()));
	}

	public static final ZoneOffset getSystemDefaultZoneOffset() {
		return OffsetDateTime.now(ZoneId.systemDefault()).getOffset();
	}

	public static final Instant nullSafeToInstant(Timestamp timestamp) {
		if (timestamp!=null) {
			return timestamp.toInstant();
		} else {
			return null;
		}
	}

	public static final Instant nullSafeToInstant(Long timestamp) {
		if (timestamp!=null) {
			return Instant.ofEpochMilli(timestamp);
		} else {
			return null;
		}
	}

	public static Instant nullSafeToInstant(LocalDateTime dateTime) {
		if (dateTime!=null) {
			return dateTime.toInstant(getSystemDefaultZoneOffset());
		} else {
			return null;
		}
	}

	public static final LocalDateTime nullSafeToLocalDateTime(Timestamp timestamp) {
		if (timestamp!=null) {
			return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.systemDefault());
		} else {
			return null;
		}
	}

	public static final LocalDate nullSafeToLocalDate(Timestamp timestamp) {
		if (timestamp!=null) {
			return LocalDate.ofInstant(timestamp.toInstant(), ZoneOffset.systemDefault());
		} else {
			return null;
		}
	}

	public static final LocalDateTime nullSafeToLocalDateTime(Instant instant) {
		if (instant!=null) {
			return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
		} else {
			return null;
		}
	}

	public static final LocalDate nullSafeToLocalDate(Date date) {
		if (date!=null) {
			Calendar cal = toCalendar(date, false);
			return LocalDate.of(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
		} else {
			return null;
		}
	}

	public static final Duration nullSafeStringToDuration(String value) {
		if (value!=null) {
			return TimeInterval.valueOf(value).toDuration();
		} else {
			return null;
		}
	}

	public static final int getFYear(TemporalAccessor dateTime) {
		int year = dateTime.get(ChronoField.YEAR);
		int month = dateTime.get(ChronoField.MONTH_OF_YEAR);
		if (month>=Month.OCTOBER.getValue()) {
			return year+1;
		} else {
			return year;
		}
	}

	public static final int getFQuater(TemporalAccessor dateTime) {
		int month = dateTime.get(ChronoField.MONTH_OF_YEAR);
		if (Month.OCTOBER.getValue()==month || Month.NOVEMBER.getValue()==month || Month.DECEMBER.getValue()==month) {
			return 1;
		} else if (Month.JANUARY.getValue()==month || Month.FEBRUARY.getValue()==month || Month.MARCH.getValue()==month) {
			return 2;
		} else if (Month.APRIL.getValue()==month || Month.MAY.getValue()==month || Month.JUNE.getValue()==month) {
			return 3;
		} else {
			return 4;
		}
	}

	public static final int getCalendarQuater(TemporalAccessor dateTime) {
		int month = dateTime.get(ChronoField.MONTH_OF_YEAR);
		if (Month.OCTOBER.getValue()==month || Month.NOVEMBER.getValue()==month || Month.DECEMBER.getValue()==month) {
			return 4;
		} else if (Month.JANUARY.getValue()==month || Month.FEBRUARY.getValue()==month || Month.MARCH.getValue()==month) {
			return 1;
		} else if (Month.APRIL.getValue()==month || Month.MAY.getValue()==month || Month.JUNE.getValue()==month) {
			return 2;
		} else {
			return 3;
		}
	}

	public static final int getCalendarMonth(TemporalAccessor dateTime) {
		return dateTime.get(ChronoField.MONTH_OF_YEAR);
	}


	@SuppressWarnings("unchecked")
	public static final <T extends Temporal> T nullSafePlus(T base, Duration amount) {
		if (base!=null) {
			if (amount!=null) {
				return (T)base.plus(amount);
			} else {
				return (T)base;
			}
		} else {
			return null;
		}
	}

	public static final <T extends TemporalAmount> Duration nullSafeDurationFrom(T base) {
		if (base!=null) {
			return Duration.from(base);
		} else {
			return null;
		}
	}

	public static final <T extends Temporal> Duration nullSafeDurationTillNow(T base) {
		if (base!=null) {
			return Duration.between(base, Instant.now());
		} else {
			return null;
		}
	}

	public static final <T extends Temporal> Duration nullSafeDurationBetween(T from, T to) {
		if (from!=null && to!=null) {
			return Duration.between(from, to);
		} else {
			return null;
		}
	}

	private static final Pattern UNIX_LS_DATE_TIME = Pattern.compile("(\\w+)\\s+(\\w+)\\s+(\\d+)\\s+([\\d\\:]+)\\s+(\\d+)");

	public static final Month stringToMonth(String str) {
		for (Month m:Month.values()) {
			if (m.name().startsWith(str.toUpperCase())) {
				return m;
			}
		}
		throw new DateTimeException(String.format("Illegal month string: '%s'", str));
	}

	//Fri Jan  9 19:46:44 2015

	public static final LocalDateTime fromUnixLsLocalDateTime(String unixLocalDate) {
		Matcher matcher = UNIX_LS_DATE_TIME.matcher(unixLocalDate);
		if (matcher.matches()) {
			String month = matcher.group(2);
			int dayOfMonth = Integer.valueOf(matcher.group(3));
			String timeStr = matcher.group(4);
			int year = Integer.valueOf(matcher.group(5));
			LocalTime time = LocalTime.parse(timeStr,TIME_FORMATTER);
			return LocalDateTime.of(LocalDate.of(year, stringToMonth(month), dayOfMonth), time);
		} else {
			throw new DateTimeException(String.format("Unparseable unix ls date: '%s'", unixLocalDate));
		}
	}

	public static final LocalDate fromShortSortableDateOrElse(String value, LocalDate orElse) {
		if (!TextHelper.isNullOrEmpty(value)) {
			return LocalDate.parse(value, DATE_FORMATTER);
		} else {
			return orElse;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final int nullSafeCompare(TemporalAccessor b1, TemporalAccessor b2) {
		if (b1!=null&&b2!=null) {
			if (Comparable.class.isAssignableFrom(b1.getClass()) && Comparable.class.isAssignableFrom(b2.getClass())) {
				return ((Comparable)b1).compareTo(b2);
			} else {
				throw new IllegalArgumentException("Both parameters must implement Comparable interface");
			}
		} else if (b1==null&&b2!=null) {
			return -1;
		} else if (b1!=null&&b2==null) {
			return 1;
		} else {
			return 0;
		}
	}

	
	/*
	 * True is d1 is afther d2
	 */
	public static final boolean nullSafeIsAfter(TemporalAccessor d1, TemporalAccessor d2) {
		return nullSafeCompare(d1, d2)>0;
	}



	public static final boolean isTimeoutExceeded(TemporalAccessor from, Duration timeout, boolean defaultValue) {
		if (from!=null) {
			return Instant.from(from).plus(timeout).isBefore(Instant.now());
		} else {
			return defaultValue;
		}
	}

	public static final boolean isTimeoutExceededIfNullYes(TemporalAccessor from, Duration timeout) {
		return isTimeoutExceeded(from, timeout, true);
	}

	public static final LocalDate nullSafeParseLocalDate(String str, DateTimeFormatter formatter) {
		if (!TextHelper.isNullOrEmpty(str)) {
			return LocalDate.parse(str,formatter);
		} else {
			return null;
		}
	}

	public static final LocalDate nullSafeParseLocalDate(String str) {
		if (!org.mycocosm.famework.text.TextHelper.isNullOrEmpty(str)) {
			return LocalDate.parse(str);
		} else {
			return null;
		}
	}

	public static final LocalDate nullSafeParseLocalDateIgnoreExceptions(String str) {
		if (!TextHelper.isNullOrEmpty(str)) {
			try {
				return LocalDate.parse(str);
			} catch (DateTimeParseException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public static final double divideDuration(Duration duration, TemporalUnit unit) {
		return (double)duration.toNanos()/(double)unit.getDuration().toNanos(); 
	}

	public static final java.util.Date nullSafeToDate(LocalDateTime from) {
		if (from!=null) {
			return java.util.Date.from(from.toInstant(getSystemDefaultZoneOffset()));
		} else {
			return null;
		}
	}

	public static final java.util.Date nullSafeToDate(LocalDate from) {
		if (from!=null) {
			return java.util.Date.from(from.atStartOfDay().toInstant(getSystemDefaultZoneOffset()));
		} else {
			return null;
		}
	}
	
	public static final LocalDateTime nullSafeFromDateToLocalDateTime(Date date) {
		if (date!=null) {
			return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		} else {
			return null;
		}
	}

	public static final void main(String[] args) {
		DateTimeFormatter DF = new DateTimeFormatterBuilder()
				.appendPattern("EEE MMM dd HH:mm:ss yyyy")
				.parseLenient()
				.toFormatter();

		System.out.println(DF.format(LocalDateTime.now()));
		String datestr = "Fri Mar  12 23:46:44 2018";
		System.out.println(fromUnixLsLocalDateTime(datestr));

		System.out.println(fromShortSortableDateOrElse("2018-03-21",null));
	}
}
