package org.mycocosm.abaqs.famework.logger;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mycocosm.famework.text.StandardFormatters;
import org.mycocosm.famework.text.TextHelper;

public class LoggerHelper {
	public static final void logIfLoggerExists(Logger logger, Level level, String format, Object... parameters) {
		if (logger!=null) {
			logger.log(level, new LoggerFormattedMessageSupplier(format, parameters));
		}
	}
	public static final void logOrSystemOut(Logger logger, Level level, String format, Object... parameters) {
		if (logger!=null) {
			logger.log(level, new LoggerFormattedMessageSupplier(format, parameters));
		} else {
			System.out.printf(format, parameters);
		}
	}
	public static final void log(Logger logger, Level level, String format, Object... parameters) {
		logger.log(level, new LoggerFormattedMessageSupplier(format, parameters));
	}
	public static final void log2(Logger logger1, Logger logger2, Level level, String format, Object... parameters) {
		log(logger1,level,format,parameters);
		log(logger2,level,format,parameters);
	}
	public static final void logExceptionIfLoggerExists(Logger logger, Level level, Throwable t, String format, Object... parameters) {
		if (logger!=null) {
			logger.log(level, String.format(format, parameters), t);
		}
	}
	public static final void logException(Logger logger, Level level, Throwable t, String format, Object... parameters) {
		logger.log(level, String.format(format, parameters), t);
	}
	public static final void logException2(Logger logger1, Logger logger2, Level level, Throwable t, String format, Object... parameters) {
		logException(logger1,level,t,format,parameters);
		logException(logger2,level,t,format,parameters);
	}
	public static final Path getLogFileNameBasedOnLocalDateTime() {
		return getLogFileNameBasedOnLocalDateTime(".log");
	}
	public static final Path getLogFileNameBasedOnLocalDateTime(String suffix) {
		return Path.of(String.format("%s%s",
				StandardFormatters.LongSortableDate.format(LocalDateTime.now()),
				TextHelper.orElse(suffix)
			)
		);
	}
	public static final Path getLogFileNameBasedOnLocalDate() {
		return getLogFileNameBasedOnLocalDate("log");
	}
	public static final Path getLogFileNameBasedOnLocalDate(String suffix) {
		return Path.of(String.format("%s%s",
				StandardFormatters.ShortSortableDate.format(LocalDateTime.now()),
				TextHelper.orElse(suffix)
			)
		);
	}

}
