package org.mycocosm.famework.text;

@FunctionalInterface
public interface Equals<T> {
	boolean check(T a, T b);
}
