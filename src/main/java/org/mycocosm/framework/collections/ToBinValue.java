package org.mycocosm.framework.collections;

@FunctionalInterface
public interface ToBinValue<T> {
	double toBinValue(T data);
}
