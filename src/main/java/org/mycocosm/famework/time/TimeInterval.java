package org.mycocosm.famework.time;

import java.text.ParseException;
import java.time.Duration;
import java.util.Date;

public final class TimeInterval {
	public static final long ZERO = 0L;
	public static final TimeInterval ZERO_INTERVAL = new TimeInterval(ZERO);

	public static final long MSEC = 1L;
	public static final TimeInterval MSEC_INTERVAL = new TimeInterval(MSEC);

	public static final long SEC = 1000L * MSEC;
	public static final TimeInterval SEC_INTERVAL = new TimeInterval(SEC);

	public static final long MIN = 60L * SEC;
	public static final long MIN_IN_SECONDS = 60L;
	public static final TimeInterval MIN_INTERVAL = new TimeInterval(MIN);
	public static final long HOUR = 60L * MIN;
	public static final long HOUR_IN_SECONDS = 60L * MIN_IN_SECONDS;
	public static final TimeInterval HOUR_INTERVAL = new TimeInterval(HOUR);
	public static final long DAY = 24L * HOUR;
	public static final long DAY_IN_SECONDS = 24L*HOUR_IN_SECONDS;
	public static final TimeInterval DAY_INTERVAL = new TimeInterval(DAY);
	public static final double DAY_DOUBLE = (double) DAY;

	public static final long WEEK = 7L * DAY;
	public static final TimeInterval WEEK_INTERVAL = new TimeInterval(WEEK);

	public static final long DECADE = 10L * DAY;
	public static final TimeInterval DECADE_INTERVAL = new TimeInterval(DECADE);

	public static final long QUATER = 90L * DAY;
	public static final TimeInterval QUATER_INTERVAL = new TimeInterval(QUATER);

	public static final long MAX_VALUE = Long.MAX_VALUE/2;
	public static final TimeInterval MAX_VALUE_INTERVAL = new TimeInterval(MAX_VALUE);

	public static final long MS_NANO=1_000_000;
	
	public static final double MS_NANO_DOUBLE=(double)MS_NANO;

	public static final double SEC_PER_HOUR=3600.0;

	public static final TimeInterval of(Duration d) {
		if (d!=null) {
			return new TimeInterval(d.toMillis());
		} else {
			return null;
		}
	}

	private final long interval;

	private TimeInterval(long interval) {
		this.interval = interval;
	}

	public long longValue() {
		return this.interval;
	}

	public int intValue() {
		return (int) this.interval;
	}

	public long milliseconds() {
		return longValue();
	}

	public int seconds() {
		return (int)(longValue()/1000);
	}
	
	public static final TimeInterval valueOf(String value) {
		if (value != null) {
			try {
				return new TimeInterval((Long) TimeIntervalFormat.getInstance().parseObject(value));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}

	public static final TimeInterval valueOf(Long value) {
		if (value != null) {
			return new TimeInterval(value);
		} else {
			return null;
		}
	}
	
	public static final Long nullSafeToLong(TimeInterval interval) {
		if (interval!=null) {
			return interval.milliseconds();
		} else { 
			return null;
		}
	}
	
	public static final TimeInterval interval(Date from, Date to) {
		if (from!=null && to!=null) {
			return new TimeInterval(to.getTime()-from.getTime());
		} else {
			return null;
		}
	}
	public static final TimeInterval intervalToNow(Date from) {
		return interval(from,new Date());
	}
	public String toString() {
		TimeIntervalFormat fmt = new TimeIntervalFormat();
		return fmt.format(this);
	}

	public long nanoseconds() {
		return longValue()*1000000;
	}

	public TimeInterval multiply(double d) {
		return new TimeInterval((long)((double)interval*d));
	}
	
	public final Duration toDuration() {
		return Duration.ofMillis(interval);
	}

	public final double toDays() {
		return (double) interval / DAY_DOUBLE;
	}

	public final TimeInterval plus(TimeInterval other) {
		if (other!=null && other.interval!=0) {
			return new TimeInterval(interval+other.interval);
		} else {
			return this;
		}
	}
	public final TimeInterval minus(TimeInterval other) {
		if (other!=null && other.interval!=0) {
			return new TimeInterval(interval-other.interval);
		} else {
			return this;
		}
	}

	public static final Duration nullSafeToDuration(TimeInterval interval) {
		if (interval!=null) {
			return interval.toDuration();
		} else {
			return null;
		}
	}

	public static final TimeInterval nullSafeFromDuration(Duration interval) {
		if (interval!=null) {
			return new TimeInterval(interval.toMillis());
		} else {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (interval ^ (interval >>> 32));
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
		TimeInterval other = (TimeInterval) obj;
		if (interval != other.interval)
			return false;
		return true;
	}
}
