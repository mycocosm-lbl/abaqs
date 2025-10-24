package org.mycocosm.famework.collections;

public interface AggregationOperation<T,V> {
	T aggregate(Iterable<V> data);
}
