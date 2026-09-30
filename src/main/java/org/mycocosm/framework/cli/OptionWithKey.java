package org.mycocosm.framework.cli;

import org.apache.commons.cli.Option;

public class OptionWithKey<T extends Comparable<?>> extends Option {

	public OptionWithKey(String option, String longOption, boolean hasArg, String description) 	throws IllegalArgumentException {
		super(option, longOption, hasArg, description);
		this.key=null;
	}
	public OptionWithKey(String option, String longOption, boolean hasArg, String description, T key) 	throws IllegalArgumentException {
		super(option, longOption, hasArg, description);
		this.key=key;
	}

	private final T key;
	private static final long serialVersionUID = 8946091380747056417L;

	@SuppressWarnings("unchecked")
	public T getComparableKey() {
		if (key!=null) {
			return key;
		} else { // use name
			return (T)getKey();
		}
	}
		
}
