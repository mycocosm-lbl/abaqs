package org.mycocosm.framework.text;

@FunctionalInterface
public interface Formatter<T> {
	public String format(T o);
}
