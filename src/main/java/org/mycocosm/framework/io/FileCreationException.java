package org.mycocosm.framework.io;


public class FileCreationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5443621576564580137L;

	public FileCreationException(Throwable cause) {
		super(cause);
	}
	
	public FileCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileCreationException(String message) {
		super(message);
	}

}
