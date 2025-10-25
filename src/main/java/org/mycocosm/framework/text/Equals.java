package org.mycocosm.framework.text;

@FunctionalInterface
public interface Equals<T> {
	boolean check(T a, T b);
}
