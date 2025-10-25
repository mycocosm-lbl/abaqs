package org.mycocosm.framework.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mycocosm.framework.comparators.StackableComparator;
import org.mycocosm.framework.utils.KeyValue;

public final class CollectionsHelper {

	@SafeVarargs
	public static final <T> void addAll(Collection<T> collection, T... array) {
		if (array!=null) {
			for (T e:array) {
				collection.add(e);
			}
		}
	}
	@SafeVarargs
	public static final <T,V> void addAll(Collection<V> collection, Function<T,V> convertor, T... array) {
		if (array!=null) {
			for (T e:array) {
				collection.add(convertor.apply(e));
			}
		}
	}

	public static final <T,V> void addAllIgnoreNulls(Collection<V> collection, Function<T,V> convertor, Collection<T> from) {
		if (from!=null) {
			for (T e:from) {
				V a = convertor.apply(e);
				if (a!=null) {
					collection.add(a);
				}
			}
		}
	}

	public static final <T> Collection<T> addAllIgnoreNulls(Collection<T> collection, Collection<T> from) {
		if (from!=null) {
			for (T e:from) {
				if (e!=null) {
					collection.add(e);
				}
			}
		}
		return collection;
	}

	@SafeVarargs
	public static final <T> Collection<T> addAllIgnoreNulls(Collection<T> collection, T... array) {
		if (array!=null) {
			for (T e:array) {
				if (e!=null) {
					collection.add(e);
				}
			}
		}
		return collection;
	}

	@SafeVarargs
	public static final <T> List<T> asList(T... array) {
		List<T> ret = new ArrayList<>();
		addAll(ret, array);
		return ret;
	}

	@SafeVarargs
	public static final <T> List<T> asList(Collection<T>... array) {
		List<T> ret = new ArrayList<>();
		if (array!=null) {
			for	(Collection<T> c:array) {
				if (c!=null) {
					ret.addAll(c);
				}
			}
		}
		return ret;
	}

	@SafeVarargs
	public static final <T> List<T> asList(T first, Collection<T>... array) {
		List<T> ret = new ArrayList<>();
		if (first!=null) {
			ret.add(first);
		}
		if (array!=null) {
			for	(Collection<T> c:array) {
				if (c!=null) {
					ret.addAll(c);
				}
			}
		}
		return ret;
	}

	@SafeVarargs
	public static final <T> List<T> asListIgnoreNulls(T... array) {
		List<T> ret = new ArrayList<>();
		addAllIgnoreNulls(ret, array);
		return ret;
	}

	@SafeVarargs
	public static final <T> List<T> asListOrNull(T... array) {
		if (array!=null) {
			List<T> ret = new ArrayList<>();
			addAll(ret, array);
			return ret;
		} else {
			return null;
		}
	}

	@SafeVarargs
	public static final <T> Set<T> asSet(T... array) {
		Set<T> ret = new HashSet<>();
		addAll(ret, array);
		return ret;
	}
	@SafeVarargs
	public static final <T,V> Set<V> asSet(Function<T, V> converter, T... array) {
		Set<V> ret = new HashSet<>();
		addAll(ret, converter, array);
		return ret;
	}

	@SafeVarargs
	public static final <T> Set<T> asSetOrNull(T... array) {
		if (array!=null) {
			Set<T> ret = new HashSet<>();
			addAll(ret, array);
			return ret;
		} else {
			return null;
		}
	}

	@SafeVarargs
	public static final <T,V> Set<V> asSetOrNull(Function<T, V> converter, T... array) {
		if (array!=null) {
			Set<V> ret = new HashSet<>();
			addAll(ret, converter, array);
			return ret;
		} else {
			return null;
		}
	}

	public static final <T> Set<T> asSet(Collection<T> collection) {
		Set<T> ret = new HashSet<>(collection.size());
		ret.addAll(collection);
		return ret;
	}

	@SafeVarargs
	public static final <T> Set<T> asSet(Collection<T>... collections) {
		Set<T> ret = new HashSet<>();
		if (collections!=null) {
			Arrays.stream(collections).forEach(ret::addAll);
		} else {
			return null;
		}
		return ret;
	}

	public static final <T> Set<T> asSetOrNull(Collection<T> collection) {
		if (collection!=null) {
			Set<T> ret = new HashSet<>(collection.size());
			ret.addAll(collection);
			return ret;
		} else {
			return null;
		}
	}

	public static final <T> Set<T> asSetOrEmptySet(Collection<T> collection) {
		if (collection!=null) {
			Set<T> ret = new HashSet<>(collection.size());
			ret.addAll(collection);
			return ret;
		} else {
			return new HashSet<>();
		}
	}
	
	public static final Predicate<?> FILTER_NO_NULLS = o->o!=null;

	public static final <T> Collection<T> filter(Iterable<T> source, Collection<T> destination, Predicate<T> filter) {
		destination.clear();
		for (T object:source) {
			if (filter.test(object)) {
				destination.add(object);
			}
		}
		return destination;
	}

	public static final <S,D> Collection<D> filterAndTransform(Iterable<S> source, Collection<D> destination, Predicate<S> filter, Function<S,D> transformer) {
		destination.clear();
		for (S object:source) {
			if (filter.test(object)) {
				destination.add(transformer.apply(object));
			}
		}
		return destination;
	}
	public static final <S,D> Collection<D> transform(Iterable<S> source, Collection<D> destination, Function<S,D> transformer) {
		destination.clear();
		for (S object:source) {
			destination.add(transformer.apply(object));
		}
		return destination;
	}


	public static final <T> List<T> asList(Collection<T> collection) {
		List<T> ret = new ArrayList<T>();
		if (!isNullOrEmpty(collection)) {
			ret.addAll(collection);
		}
		return ret;
	}

	public static final <K,T> Map<K,T> asMapLastKeyWin(Collection<T> collection, ToKey<K,T> toKey) {
		Map<K,T> ret = new HashMap<>();
		for (T o:collection) {
			ret.put(toKey.toKey(o),o);
		}
		return ret;
	}

	public static final <K,T> Map<K,T> asMap(Map<K,T> map) {
		Map<K,T> ret = new HashMap<>();
		for (Map.Entry<K, T> e:map.entrySet()) {
			ret.put(e.getKey(),e.getValue());
		}
		return ret;
	}

	public static final <S> List<S> nullSafeAsList(Collection<S> collection) {
		List<S> ret = new ArrayList<>();
		if (collection!=null) {
			ret.addAll(collection);
		}
		return ret;
	}

	public static final <S,D> List<D> asList(Collection<S> collection, Function<S, D> transforner) {
		List<D> ret = new ArrayList<D>();
		for (S entry:collection) {
			ret.add(transforner.apply(entry));
		}
		return ret;
	}

	public static final <S,D> List<D> asList(Collection<S> collection, Function<S, D> transforner, Comparator<D> comparator) {
		List<D> ret = new ArrayList<D>();
		for (S entry:collection) {
			ret.add(transforner.apply(entry));
		}
		Collections.sort(ret,comparator);
		return ret;
	}

	public static final <T> List<T> asList(Collection<T> collection, Comparator<T> comparator) {
		List<T> ret = new ArrayList<>();
		ret.addAll(collection);
		Collections.sort(ret,comparator);
		return ret;
	}

	public static final <T> List<T> asList(Collection<T> collection, Predicate<T> filter) {
		List<T> ret = new ArrayList<>();
		for (T entry:collection) {
			if (filter.test(entry)) {
				ret.add(entry);
			}
		}
		return ret;
	}

	public static final <T> Set<T> emptySet(Class<T> T) {
		return new HashSet<>();
	}

	public static final class GroupByEntry<K,V> {
		private K key;
		private V value;
		private GroupByEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		public K getKey() {
			return key;
		}
		public V getValue() {
			return value;
		}
	}

	private static final <K,V,R> Map<K,List<R>> mapToGroups(Collection<V> values, ToKey<K,V> toKey, ToValue<R, V> toValue) {
		Map<K,List<R>> groups = new HashMap<>();
		for (V v:values) {
			K key = toKey.toKey(v);
			groups.computeIfAbsent(key, k->new ArrayList<>()).add(toValue.toValue(v));
		}
		return groups;
	}

	public static final <K,V> List<GroupByEntry<K, Double>> groupByDouble(Collection<V> values, AggregationOperation<Double,V> aggregation, ToKey<K,V> toKey, final Comparator<K> comparator) {
		return mapToGroups(values, toKey, v->v).entrySet().stream()
				.map(e->new GroupByEntry<K, Double>(e.getKey(), aggregation.aggregate(e.getValue())))
				.sorted((o1,o2)->comparator.compare(o1.getKey(), o2.getKey()))
				.collect(Collectors.toList());
	}


	public static final <K,V,T,R> List<GroupByEntry<K, T>> groupBy(Collection<V> values, AggregationOperation<T,R> aggregation, ToKey<K,V> toKey, ToValue<R,V> toValue, final Comparator<K> comparator) {
		return mapToGroups(values, toKey, toValue).entrySet().stream()
				.map(e->new GroupByEntry<K, T>(e.getKey(), aggregation.aggregate(e.getValue())))
				.sorted((o1,o2)->comparator.compare(o1.getKey(), o2.getKey()))
				.collect(Collectors.toList());
	}

	public static final <K,V> Map<K, List<V>> groupBy(Collection<V> values, ToKey<K,V> toKey) {
		return mapToGroups(values, toKey, v->v);
	}

	public static final <K,V> List<Map.Entry<K,V>> asEntriesList(Map<K,V> map) {
		List<Map.Entry<K,V>> ret = new ArrayList<>();
		ret.addAll(map.entrySet());
		return ret;
	}
	public static final <K,V> List<Map.Entry<K,V>> asEntriesList(Map<K,V> map, Comparator<Map.Entry<K,V>> comparator) {
		List<Map.Entry<K,V>> ret = asEntriesList(map);
		Collections.sort(ret, comparator);
		return ret;
	}
	public static final <K,V> Stream<Map.Entry<K,V>> asEntriesStream(Map<K,V> map, Comparator<Map.Entry<K,V>> comparator) {
		return asEntriesList(map).stream().sorted(comparator);
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] asArray(Class<T> clasz, Collection<T> collection) {
		T[] ret = (T[])Array.newInstance(clasz, collection.size());
		return collection.toArray(ret);
	}

	public static final <T> T[] collectionAsArray(Class<T> clasz, Collection<T> collection) {
		return asArray(clasz, collection);
	}

	@SafeVarargs
	public static final <T> T[] asArray(T... arr) {
		return arr;
	}

	@SuppressWarnings("unchecked")
	public static final <S,D> D[] asTransformedArray(Class<D> clasz, Collection<S> collection, Function<S, D> transformer) {
		D[] ret = (D[])Array.newInstance(clasz, collection.size());
		int i=0;
		for (S e:collection) {
			ret[i] = transformer.apply(e);
			i++;
		}
		return ret;
	}

	public static final <T extends Comparable<T>> List<T> sorted(List<T> aList) {
		List<T> ret = new ArrayList<>();
		ret.addAll(aList);
		Collections.sort(ret);
		return ret;
	}

	public static final <T> List<T> sorted(List<T> aList, Comparator<T> comparator) {
		List<T> ret = new ArrayList<>();
		ret.addAll(aList);
		Collections.sort(ret,comparator);
		return ret;
	}

	public static final <T> void sort(List<T> list, final StackableComparator<T> comparator) {
		Collections.sort(list, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return comparator.compare(o1, o2).toComparatorResult();
			}
		});
	}

	public static final <T> void forEach(Collection<T> collection, Visitor<T> visitor) {
		Iterator<T> it = collection.iterator();
		boolean hasFirst = false;
		while (it.hasNext()) {
			T o = it.next();
			if (it.hasNext()) {
				if (!hasFirst) {
					visitor.first(o);
					hasFirst = true;
				} else {
					visitor.each(o);
				}
			} else {
				visitor.last(o);
			}
		}
	}

	public static final <T> List<T> createImmutableEmptyList(Class<T> clasz) {
		return new List<T>() {

			private Object[] emptyArray = new Object[0];

			private ListIterator<T> emptyIterator = new ListIterator<T>() {

				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public T next() {
					return null;
				}

				@Override
				public boolean hasPrevious() {
					return false;
				}

				@Override
				public T previous() {
					return null;
				}

				@Override
				public int nextIndex() {
					return 0;
				}

				@Override
				public int previousIndex() {
					return 0;
				}

				@Override
				public void remove() {
				}

				@Override
				public void set(T e) {
				}

				@Override
				public void add(T e) {
				}
			};

			@Override
			public int size() {
				return 0;
			}

			@Override
			public boolean isEmpty() {
				return true;
			}

			@Override
			public boolean contains(Object o) {
				return false;
			}

			@Override
			public Iterator<T> iterator() {
				return emptyIterator;
			}

			@Override
			public Object[] toArray() {
				return emptyArray;
			}

			@SuppressWarnings({ "hiding", "unchecked" })
			@Override
			public <T> T[] toArray(T[] a) {
				return (T[])emptyArray;
			}

			@Override
			public boolean add(T e) {
				return false;
			}

			@Override
			public boolean remove(Object o) {
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				return false;
			}

			@Override
			public boolean addAll(Collection<? extends T> c) {
				return false;
			}

			@Override
			public boolean addAll(int index, Collection<? extends T> c) {
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				return false;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				return false;
			}

			@Override
			public void clear() {
			}

			@Override
			public T get(int index) {
				return null;
			}

			@Override
			public T set(int index, T element) {
				return null;
			}

			@Override
			public void add(int index, T element) {
			}

			@Override
			public T remove(int index) {
				return null;
			}

			@Override
			public int indexOf(Object o) {
				return -1;
			}

			@Override
			public int lastIndexOf(Object o) {
				return -1;
			}

			@Override
			public ListIterator<T> listIterator() {
				return emptyIterator;
			}

			@Override
			public ListIterator<T> listIterator(int index) {
				return emptyIterator;
			}

			@Override
			public List<T> subList(int fromIndex, int toIndex) {
				return this;
			}
		};
	}

//	private static final class BinEntry<T> {
//		private double binValue;
//		private T entry;
//		private BinEntry(double binValue, T entry) {
//			this.binValue = binValue;
//			this.entry = entry;
//		}
//
//	}

//	public static final <T> BinnedCollection<T>[] binCollectionWithPercentOfDataElementsCutoff(Collection<T> data, ToBinValue<T> toBinValue, int numberOfBins, double percentCutOff) {
//		List<BinEntry<T>> allEntries = toSortedBinentryList(data, toBinValue);
//		int numberEntriesToIgnore = (int)Math.round((double)data.size() * (1.0-percentCutOff));
//		int start = numberEntriesToIgnore/2;
//		int end = allEntries.size()-numberEntriesToIgnore/2;
//		return binCollectionFromStartToEnd(allEntries, numberOfBins, start, end);
//	}
//
//	public static final <T> BinnedCollection<T>[] binCollectionWithPercentOfDataRangeFromMaxCutoff(Collection<T> data, ToBinValue<T> toBinValue, int numberOfBins, double percentCutOff) {
//		List<BinEntry<T>> allEntries = toSortedBinentryList(data, toBinValue);
//		double cutoffValue = allEntries.get(allEntries.size()-1).binValue * percentCutOff;
//		int numberEntriesToIgnore = 0;
//		for (;numberEntriesToIgnore<allEntries.size() && allEntries.get(allEntries.size()-1-numberEntriesToIgnore).binValue>cutoffValue;numberEntriesToIgnore++);
//		int start = 0;
//		int end = allEntries.size()-numberEntriesToIgnore;
//		return binCollectionFromStartToEnd(allEntries, numberOfBins, start, end);
//	}
//
//	private static final <T> BinnedCollection<T>[] binCollectionFromStartToEnd(List<BinEntry<T>> allEntries, int numberOfBins, int start, int end) {
//		double minBinValue = allEntries.get(start).binValue;
//		double maxBinValue = allEntries.get(end-1).binValue;
//		double binStep = (maxBinValue - minBinValue) / numberOfBins;
//		@SuppressWarnings("unchecked")
//		BinnedCollection<T>[] ret = new BinnedCollection[numberOfBins];
//		for (int i=0;i<ret.length;i++) {
//			double startBinValue = minBinValue + i * binStep;
//			double endBinValue = startBinValue + binStep;
//			ret[i] = new BinnedCollection<T>(startBinValue, endBinValue);
//		}
//		for (int i=start;i<end;i++) {
//			int binIndex = (int)Math.floor((allEntries.get(i).binValue - minBinValue) / binStep);
//			if (binIndex>=0 && binIndex<numberOfBins) {
//				ret[binIndex].add(allEntries.get(i).entry);
//			} else if (binIndex<0) {
//				ret[0].add(allEntries.get(i).entry);
//			} else if (binIndex>=numberOfBins) {
//				ret[numberOfBins-1].add(allEntries.get(i).entry);
//			}
//		}
//		return ret;
//	}
//
//	public static final <T> BinnedCollection<T>[] binCollectionWithMaxValueCutoff(Collection<T> data, ToBinValue<T> toBinValue, int numberOfBins, double valueCutOff) {
//		List<BinEntry<T>> allEntries = toSortedBinentryList(data, toBinValue);
//		double minBinValue = allEntries.get(0).binValue;
//		if (valueCutOff>allEntries.get(allEntries.size()-1).binValue) {
//			valueCutOff=allEntries.get(allEntries.size()-1).binValue;
//		}
//		double maxBinValue = valueCutOff;
//		double binStep = (maxBinValue - minBinValue) / numberOfBins;
//		@SuppressWarnings("unchecked")
//		BinnedCollection<T>[] ret = new BinnedCollection[numberOfBins+1]; 
//		for (int i=0;i<numberOfBins;i++) {
//			double startBinValue = minBinValue + i * binStep;
//			double endBinValue = startBinValue + binStep;
//			ret[i] = new BinnedCollection<T>(startBinValue, endBinValue);
//		}
//		double startBinValue = valueCutOff;
//		double endBinValue = allEntries.get(allEntries.size()-1).binValue;;
//		ret[numberOfBins] = new BinnedCollection<T>(startBinValue, endBinValue);
//		for (BinEntry<T> e:allEntries) {
//			int binIndex;
//			if (e.binValue<valueCutOff) {
//				binIndex = (int)Math.floor((e.binValue - minBinValue) / binStep);
//			} else {
//				binIndex = numberOfBins;
//			}
//			if (binIndex>=0 && binIndex<numberOfBins+1) {
//				ret[binIndex].add(e.entry);
//			}
//		}
//		return ret;
//	}
//
//	private static final <T> List<BinEntry<T>> toSortedBinentryList(Collection<T> data, ToBinValue<T> toBinValue) {
//		List<BinEntry<T>> ret = new ArrayList<>();
//		for (T entry:data) {
//			double binValue = toBinValue.toBinValue(entry);
//			ret.add(new BinEntry<T>(binValue, entry));
//		}
//		Collections.sort(ret, new Comparator<BinEntry<T>>() {
//
//			@Override
//			public int compare(BinEntry<T> o1, BinEntry<T> o2) {
//				return Double.compare(o1.binValue, o2.binValue);
//			}
//		});
//		return ret;
//	}

	public static final boolean isNullOrEmpty(Collection<?> collection) {
		return collection==null || collection.isEmpty();
	}

	public static final boolean isNullOrEmpty(Map<?,?> map) {
		return map==null || map.isEmpty();
	}

	public static final <T> void addIfNotNull(Collection<T> collection, T value) {
		if (value!=null) {
			collection.add(value);
		}
	}

	public static final <K,V> V nullSafeGet(Map<K,V> map, K key) {
		if(map == null || map.isEmpty()) {
			return null;
		}
		return map.get(key);
	}

	public static final <K1,K2,V> V nullSafeGetGet(Map<K1,Map<K2,V>> map, K1 key1, K2 key2) {
		return nullSafeGet(nullSafeGet(map, key1),key2);
	}

	
	public static final <T> T firstOrNull(Collection<T> list) {
		return firstOrElse(list, null);
	}

	public static final <T> T firstOrElse(Collection<T> list, T defaultValue) {
		if (list!=null && !list.isEmpty()) {
			return list.iterator().next();
		} else {
			return defaultValue;
		}
	}

	public static final <T> T anyOrNull(Set<T> set) {
		if (set!=null && !set.isEmpty()) {
			return set.iterator().next();
		} else {
			return null;
		}
	}

	public static final <T> Set<T> emptySetOf(Class<T> clasz) {
		return new HashSet<>();
	}

	public static final <K,V> Map<V,List<K>> reverseMap(Map<K,V> map) {
		Map<V,List<K>> ret = new HashMap<>();
		map.entrySet().stream().forEach(e->{
			ret.computeIfAbsent(e.getValue(), v->new ArrayList<>()).add(e.getKey());
		});
		return ret;
	}

	public static final <T> void nullSafeForEach(Collection<T> c, Consumer<? super T> action) {
		if (c!=null) {
			c.stream().forEach(action);
		}
	}

	public static final <R,T> Stream<R> nullSafeMap(Collection<T> c, Function<? super T, ? extends R> mapper) {
		if (c!=null) {
			return c.stream().map(mapper);
		} else {
			return Stream.empty();
		}
	}
	public static final <R,T> Stream<R> nullSafeMapArray(T[] a, Function<? super T, ? extends R> mapper) {
		if (a!=null) {
			return Arrays.stream(a).map(mapper);
		} else {
			return Stream.empty();
		}
	}

	public static <T,C extends Collection<T>> Integer nullSafeSize(C collection) {
		if (collection!=null) {
			return collection.size();
		} else {
			return null;
		}
	}

	public static final <T> T[] arrayOfOne(Class<T> classz, T member) {
		@SuppressWarnings("unchecked")
		T[] array = (T[])Array.newInstance(classz, 1);
		array[0] = member;
		return array;
	}

	public static final <K,V> V orElse(Map<K, V> map, K key, Supplier<V> orEelseValue) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return orEelseValue.get();
		}
	}


	public static final <T extends Comparable<T>> int compare(Collection<T> a, Collection<T> b) {
		return compare(a,b,(c1,c2)->c1.compareTo(c2));
	}

	public static final <T> int compare(Collection<T> a, Collection<T> b, Comparator<T> comparator) {
		if (a!=null && b!=null) {
			if (a.isEmpty() && b.isEmpty()) {
				return 0;
			} else {
				Iterator<T> itA = a.iterator();
				Iterator<T> itB = b.iterator();
				T ea;
				T eb;
				Comparator<T> cmp = null;
				while (itA.hasNext() && itB.hasNext()) {
					ea = itA.next();
					eb = itB.next();
					int result;
					if (comparator!=null) {
						result = comparator.compare(ea, eb);
					} else if (cmp!=null) {
						result = cmp.compare(ea, eb);
					} else if (Comparator.class.isAssignableFrom(ea.getClass())) {
						cmp = new Comparator<T>() {

							@SuppressWarnings("unchecked")
							@Override
							public int compare(T o1, T o2) {
								return ((Comparable<T>)o1).compareTo(o2);
							}
						};
						result = cmp.compare(ea, eb);
					} else {
						throw new NullPointerException("Need instans of comparator here");
					}
					if (result!=0) {
						return result;
					}
				}
				if (!itA.hasNext() && !itB.hasNext()) {
					return 0;
				} else if (itA.hasNext()) {
					return 1;
				} else {
					return -1;
				}
			}
		} else if (a==null && b==null) {
			return 0;
		} else if (a!=null) {
			return 1;
		} else {
			return -1;
		}
	}

	public static final <T> int compareArrays(T[] a1, T[] a2, Comparator<T> comparator) {
		if (a1!=null && a2!=null) {
			int max = Math.min(a1.length, a2.length);
			for (int index=0;index<max;index++) {
				int ret = comparator.compare(a1[index], a2[index]);
				if (ret!=0) {
					return ret;
				}
			}
			return Integer.compare(a1.length, a2.length);
		} else if (a1==null && a2==null) {
			return 0;
		} else if (a1!=null) {
			return 1;
		} else {
			return -1;
		}
	}


	@SafeVarargs
	public static final <T> boolean nullSafeContainsAny(Set<T> set, T... items) {
		if (isNullOrEmpty(set)) {
			return false;
		} else {
			if (items!=null) {
				for (T item:items) {
					if (item!=null && set.contains(item)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public static final <K,V> Map<K,V> mapOf(Map<K,V> map, K key1, V value1) {
		map.put(key1, value1);
		return Collections.unmodifiableMap(map);
	}
	public static final <K,V> Map<K,V> mapOf(Map<K,V> map, K key1, V value1, K key2, V value2) {
		map.put(key1, value1);
		map.put(key2, value2);
		return Collections.unmodifiableMap(map);
	}
	public static final <K,V> Map<K,V> mapOf(K key1, V value1) {
		return mapOf(new HashMap<K,V>(), key1, value1);
	}
	public static final <K,V> Map<K,V> mapOf(K key1, V value1, K key2, V value2) {
		return mapOf(new HashMap<K,V>(), key1, value1, key2, value2);
	}
	public static final <K,V> V computeIfAbsent(Map<K,V> map, K key, Function<K, V> producer) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			V ret = producer.apply(key);
			map.put(key, ret);
			return ret;
		}
	}
	public static final <K,V> V computeIfAbsentIgnoreNull(Map<K,V> map, K key, Function<K, V> producer) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			V ret = producer.apply(key);
			if (ret!=null) {
				map.put(key, ret);
			}
			return ret;
		}
	}
	public static final <T> List<List<T>> splitToBatches(Collection<T> input, int batchSize) {
		List<List<T>> ret = new ArrayList<>();
		List<T> current = new ArrayList<>();
		for (T r:input) {
			if (current.size()>=batchSize) {
				ret.add(current);
				current = new ArrayList<>();
			}
			current.add(r);
		}
		ret.add(current);
		return ret;
	}

	private static final List<?> EMPTY_LIST = new ArrayList<>();
	@SuppressWarnings("unchecked")
	public static final <C> List<C> emptyList() {
		return (List<C>)EMPTY_LIST;
	}

//	public static final <T> SetsCompareResult<T> compareTwoSets(Set<T> setA, Set<T> setB) {
//		return SetsCompareResult.compareTwoSets(setA, setB, p->p);
//	}
//
//	public static final <T,K> SetsCompareResult<T> compareTwoSets(Set<T> setA, Set<T> setB, ToKey<K,T> toKey) {
//		return SetsCompareResult.compareTwoSets(setA, setB, toKey);
//	}
//
//	public static final <T> SetsCompareResult<T> compareTwoSetsWithStringKeys(Set<T> setA, Set<T> setB, ToKey<String,T> toKey) {
//		return SetsCompareResult.compareTwoSets(setA, setB, toKey);
//	}

	public static final <T> Iterable<T> copyIterable(Collection<T> collection) {
		return asList(collection);
	}

	public static final <T> void addAllIfNotSet(Collection<T> addTo, Set<T> unique, Collection<T> addFrom) {
		assert addTo!=null;
		assert unique!=null;
		if (!isNullOrEmpty(addFrom)) {
			for (T o:addFrom) {
				if (o!=null && unique.add(o)) {
					addTo.add(o);
				}
			}
		}
	}
	public static final <T> T nullSafeGetByIndexOrElse(List<T> lst, int index, Supplier<T> orElse) {
		if (lst!=null) {
			if (lst.size()>index) {
				return lst.get(index);
			} else {
				return orElse.get();
			}
		} else {
			return orElse.get();
		}
	}
	public static final <T> T nullSafeGetByIndexOrOther(List<T> lst, int index, T orElse) {
		return nullSafeGetByIndexOrElse(lst, index, ()->orElse);
	}
	public static final <T> T nullSafeGetByIndexOrNull(List<T> lst, int index) {
		return nullSafeGetByIndexOrElse(lst, index, ()->null);
	}

	@SafeVarargs
	public static final <T> List<T> mergeToList(Collection<T>... cls) {
		List<T> ret = new ArrayList<>();
		if (cls!=null) {
			for (Collection<T> c:cls) {
				if (c!=null) {
					ret.addAll(c);
				}
			}
		}
		return ret;
	}

	public static final <K,V> Map<K,V> asMapOfEntries(Map<K,V> map, Collection<Map.Entry<K, V>> entries) {
		if (!isNullOrEmpty(entries)) {
			for (Map.Entry<K, V> e:entries) {
				map.put(e.getKey(), e.getValue());
			}
		}
		return map;
	}
	public static final <K,V> Map<K,V> asMapOfEntries(Collection<Map.Entry<K, V>> entries) {
		return asMapOfEntries(new HashMap<>(), entries);
	}

	public static final <K,V> Map<K,V> asMapOfKeyValues(Map<K,V> map, Collection<KeyValue<K, V>> entries) {
		if (!isNullOrEmpty(entries)) {
			for (KeyValue<K, V> e:entries) {
				map.put(e.key, e.value);
			}
		}
		return map;
	}
	public static final <K,V> Map<K,V> asMapOfKeyValues(Collection<KeyValue<K, V>> entries) {
		return asMapOfKeyValues(new HashMap<>(), entries);
	}
	@SafeVarargs
	public static final <K,V> Map<K,V> asMapOfKeyValues(KeyValue<K, V>... entries) {
		return asMapOfKeyValues(new HashMap<>(), asList(entries));
	}

	
	public static final class IntersectionResult<T>  {
		public final Set<T> common;
		public final Set<T> uniqueA;
		public final Set<T> uniqueB;
		public IntersectionResult(Set<T> common, Set<T> uniqueA, Set<T> uniqueB) {
			this.common = common;
			this.uniqueA = uniqueA;
			this.uniqueB = uniqueB;
		}
	}
	public static final <T> IntersectionResult<T> intercection(Collection<? extends T> a, Collection<? extends T> b) {
		final Set<T> common = new HashSet<>();
		final Set<T> uniqueA = new HashSet<>();
		final Set<T> uniqueB = new HashSet<>(b);
		a.forEach(e->{
			if (b.contains(e)) {
				common.add(e);
				uniqueB.remove(e);
			} else {
				uniqueA.add(e);
			}
		});
		return new IntersectionResult<T>(common,uniqueA,uniqueB);
	}

	public static final <K extends Comparable<K>> int compareMapsByKey(Map.Entry<K,?> e1, Map.Entry<K,?> e2) {
		return e1.getKey().compareTo(e2.getKey());
	}

	public static final <K,V,R> R getAndMapOrElse(Map<K,V> map, K key, Function<V,R> mapFunction, R orElse)  {
		V value = map.get(key);
		if (value!=null) {
			return mapFunction.apply(value);
		} else {
			return orElse;
		}
	}
	public static final <T> Collection<T> reverseCollection(Collection<T> col) {
		if (col==null) {
			return null;
		} else if (col.isEmpty()){
			return col;
		} else {
			LinkedList<T> stack = new LinkedList<>();
			col.forEach(stack::push);
			return stack;
		}
	}
	public static <T,I> Collection<T> nullSafeParseCollection(Collection<T> out, I[] data, Function<I,T> parser) {
		if (!ArraysHelper.isNullOrEmpty(data)) {
			for (I d:data) {
				if (d!=null) {
					out.add(parser.apply(d));
				}
			}
		}
		return out;
	}

	public static final <T> boolean nullSafeEqualSets(Set<T> a, Set<T> b) {
		if (a!=null && b!=null) {
			if (a.size()==b.size()) {
				Set<T> copyB = new HashSet<>(b);
				for (T e:a) {
					if (!copyB.remove(e)) {
						return false;
					}
				}
				return copyB.isEmpty();
			} else {
				return false;
			}
		} else {
			return false; // null is not equal to null
		}
	}
	
	public static final <K,V> MapBuilder<K,V> map(Map<K,V> map) {
		return new MapBuilder<K, V>(map);
	}

	public static final <T> List<T> nullSafeSortedList(Collection<T> collection) {
		return nullSafeSortedList(collection, null);
	}
	
	public static final <T> List<T> nullSafeSortedList(Collection<T> collection, Comparator<T> comp) {
		if (collection!=null) {
			if (comp!=null) {
				return collection.stream().sorted(comp).collect(Collectors.toList());
			} else {
				return collection.stream().sorted().collect(Collectors.toList());
			}
		} else {
			return null;
		}
	}
	
	public static final <T> Stream<T> nullSafeStream(Collection<T> col) {
		if (!isNullOrEmpty(col)) {
			return col.stream();
		} else {
			return Stream.empty();
		}
	}
	
	public static final <K,V> void nullSafeForEach(Map<K,V> map, BiConsumer<? super K, ? super V> action) {
		if (!isNullOrEmpty(map)) {
			map.forEach(action);
		}
	}

}
