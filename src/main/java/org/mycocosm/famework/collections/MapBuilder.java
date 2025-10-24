package org.mycocosm.famework.collections;

import java.util.Map;

public class MapBuilder<K,V> {
	private final Map<K,V> map;
	
	MapBuilder(Map<K,V> map) {
		this.map=map;
	}
	public MapBuilder<K,V> put(K key, V value) {
		map.put(key,value);
		return this;
	}
	public Map<K,V> build() {
		return map;
	}
	
}
