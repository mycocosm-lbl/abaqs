package org.mycocosm.framework.collections;

@FunctionalInterface
public interface ToKey<K,V> {
	K toKey(V value);
}
