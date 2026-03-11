package org.mycocosm.framework.utils;

public class IllegalEnumValueException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6855691875246188440L;

	protected IllegalEnumValueException() {
		super();
	}
	
	public IllegalEnumValueException(String s) {
		super(String.format("Illegal enum value: '%s'", s));
	}

}
