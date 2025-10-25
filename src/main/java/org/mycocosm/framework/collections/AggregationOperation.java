package org.mycocosm.framework.collections;

public interface AggregationOperation<T,V> {
	T aggregate(Iterable<V> data);
}
