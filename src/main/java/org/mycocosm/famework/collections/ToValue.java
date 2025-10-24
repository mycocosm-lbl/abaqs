package org.mycocosm.famework.collections;

@FunctionalInterface
public interface ToValue<R,V> {
	R toValue(V v); 
}
