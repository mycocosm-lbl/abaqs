package org.mycocosm.famework.utils;

import java.util.Map;

public class KeyValue<K,V> implements Map.Entry<K, V> {
	public final K key;
	public final V value;
	@SuppressWarnings("unchecked")
	public static final <K,V> KeyValue<K,V> fromArray(Object... values) {
		if (values!=null) {
			if (values.length>=2) {
				return new KeyValue<K, V>((K) values[0], (V) values[1]);
			} else if (values.length==1){
				return new KeyValue<K, V>((K) values[0], null);
			} else { // ==0
				return new KeyValue<K, V>(null, null);
			}
		} else {
			return null;
		}
	}
	public static final <K,V> KeyValue<K,V> of(K key, V value) {
		return new KeyValue<>(key, value);
	}
	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}
	@Override
	public K getKey() {
		return key;
	}
	@Override
	public V getValue() {
		return value;
	}
	@Override
	public V setValue(V value) {
		throw new IllegalArgumentException("KeyValue is immutable");
	}
}
