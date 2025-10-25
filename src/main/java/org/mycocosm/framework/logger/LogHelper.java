package org.mycocosm.framework.logger;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {

	
//	private final static UncaughtExceptionHandler EH = new UncaughtExceptionHandler() {
//
//		@Override
//		public void uncaughtException(Thread t, Throwable e) {
//			LogHelper.getLogger(LogHelper.class).log(Level.WARNING, "Uncaught error found", e);
//		}
//	};
//
//	public static final void log2(Logger logger1, Logger logger2, Level level, String format, Object... parameters) {
//		if (logger1!=null) {
//			log(logger1,level,format,parameters);
//		}
//		if (logger2!=null) {
//			log(logger2,level,format,parameters);
//		}
//	}
//
//	public static final void log(Logger logger, Level level, String format, Object... parameters) {
//		if (logger!=null) {
//			LoggerHelper.log(logger, level, format, parameters);
//		} else {
//			LoggerHelper.log(LogHelper.getLogger(LogHelper.class), level, format, parameters);
//		}
//	}
//
//	public static final void logException2(Logger logger1, Logger logger2, Level level, Throwable t, String format, Object... parameters) {
//		if (logger1!=null) {
//			logException(logger1,level,t,format,parameters);
//		}
//		if (logger2!=null) {
//			logException(logger2,level,t,format,parameters);
//		}
//	}
//
//	public static final void logException(Logger logger, Level level, Throwable t, String format, Object... parameters) {
//		if (logger!=null) {
//			LoggerHelper.logException(logger, level, t, format, parameters);
//		} else {
//			LoggerHelper.logException(LogHelper.getLogger(LogHelper.class), level, t, format, parameters);
//		}
//	}
//
//	public static final void log(Class<?> clasz, Level level, String format, Object... parameters) {
//		LoggerHelper.log(getLogger(clasz), level, format, parameters);
//	}
//
//	public static final void logException(Class<?> clasz, Level level, Throwable t, String format, Object... parameters) {
//		LoggerHelper.logException(getLogger(clasz), level, t, format, parameters);
//	}
//
//	public static final UncaughtExceptionHandler getLoggingUncaughtExceptionhandler() {
//		return EH;
//	}

//	public static class LoggingThreadGroup extends ThreadGroup {
//
//		public LoggingThreadGroup(String name) {
//			super(name);
//		}
//
//		public void uncaughtException(Thread t, Throwable e) {
//			getLogger(getName()).log(Level.WARNING, t.getName(), e);
//		}
//	}
//
//	public static ThreadGroup getLoggingThreadGroup(String name) {
//		return new LoggingThreadGroup(name);
//	}

	public static final Logger getLogger(Class<?> clasz) {
		return getLogger(clasz.getName());
	}

	public static final Logger getLogger(Object object) {
		return getLogger(object.getClass());
	}

	public static final Logger getLogger(String logName) {
		return setupLog(Logger.getLogger(logName));
	}

	private static Logger setupLog(Logger logger) {
		Handler logHandler = new PlainHandler();
		Logger ourLogger = Logger.getLogger(LogHelper.class.getName());
		Level level;
		if (ourLogger!=null) {
			level = inferLevelFromThis(ourLogger);
		} else {
			level = DEFAULT_LEVEL;
		}
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		logger.addHandler(logHandler);
		return logger;
	}

	private static final Level DEFAULT_LEVEL = Level.SEVERE;
	private static final Level inferLevelFromThis(Logger logger) {
		Level level = logger.getLevel();
		if (level!=null) {
			return level;
		} else {
			Logger parent = logger.getParent();
			if (parent!=null) {
				return inferLevelFromThis(parent);
			} else {
				return DEFAULT_LEVEL;
			}
		}
	}

	public static final Logger getAnonymousLogger(Path logFile) {
		Handler logHandler = new PlainHandler(logFile);
		Logger ourLogger = Logger.getLogger(LogHelper.class.getName());
		Level level;
		if (ourLogger!=null) {
			level = inferLevelFromThis(ourLogger);
		} else {
			level = DEFAULT_LEVEL;
		}
		Logger logger = Logger.getAnonymousLogger();
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		logger.addHandler(logHandler);
		return logger;
	}
	public static final Logger getAnonymousLogger(File logFile) {
		return getAnonymousLogger(logFile.toPath());
	}

	public static final Logger getAnonymousLogger(Path logFile, Level level, Formatter formatter) {
		Handler logHandler = new PlainHandler(logFile);
		logHandler.setFormatter(formatter);
		logHandler.setLevel(level);
		Logger logger = Logger.getAnonymousLogger();
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		logger.addHandler(logHandler);
		return logger;
	}

}
