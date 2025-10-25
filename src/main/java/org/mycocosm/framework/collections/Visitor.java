package org.mycocosm.framework.collections;

public interface Visitor<T> {
	void first(T o);
	void each(T o);
	void last(T o);
}
