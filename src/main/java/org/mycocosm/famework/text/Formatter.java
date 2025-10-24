package org.mycocosm.famework.text;

@FunctionalInterface
public interface Formatter<T> {
	public String format(T o);
}
