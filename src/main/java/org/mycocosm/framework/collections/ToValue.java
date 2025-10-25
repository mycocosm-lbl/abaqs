package org.mycocosm.framework.collections;

@FunctionalInterface
public interface ToValue<R,V> {
	R toValue(V v); 
}
