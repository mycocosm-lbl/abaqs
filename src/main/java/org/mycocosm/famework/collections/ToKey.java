package org.mycocosm.famework.collections;

@FunctionalInterface
public interface ToKey<K,V> {
	K toKey(V value);
}
