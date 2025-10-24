package org.mycocosm.abaqs.famework.logger;

import java.util.function.Supplier;

public class LoggerFormattedMessageSupplier implements Supplier<String> {

	private String format;
	private Object[] parameters;
	public LoggerFormattedMessageSupplier(String format, Object... parameters) {
		this.format=format;
		this.parameters=parameters;
	}
	
	@Override
	public String get() {
		return String.format(format, parameters);
	}

}
