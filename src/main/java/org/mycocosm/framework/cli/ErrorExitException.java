package org.mycocosm.framework.cli;

import org.mycocosm.framework.text.TextHelper;

public final class ErrorExitException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7387777633580456011L;

	public final int errorCode;
	public final String message;
	public static final int DEFAULT_ERROR_CODE=-1;
	private static final Object[] EMPTY = {};
	
	private static final String message(String format, Object... args) {
		if (!TextHelper.isNullOrEmpty(format)) {
			return String.format(format, args);
		} else {
			return format;
		}
	}
	public ErrorExitException() {
		this(null,DEFAULT_ERROR_CODE,EMPTY);
	}
	public ErrorExitException(String format, Object... args) {
		this(message(format,args),DEFAULT_ERROR_CODE,EMPTY);
	}
	public ErrorExitException(String format, int errorCode, Object... args) {
		super(message(format,args));
		this.errorCode = errorCode;
		this.message = getMessage();
	}
	public ErrorExitException(int errorCode, String format, Object... args) {
		super(message(format,args));
		this.errorCode = errorCode;
		this.message = getMessage();
	}
	public static final ErrorExitException ofMessage(String format, Object... args) {
		return new ErrorExitException(DEFAULT_ERROR_CODE,format,args);
	}

}
