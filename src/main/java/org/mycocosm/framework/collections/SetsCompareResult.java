package org.mycocosm.framework.collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SetsCompareResult<T> {
	public final Set<T> uniqueToA;
	public final Set<T> uniqueToB;
	public final Set<T> common;
	private SetsCompareResult(Set<T> uniqueToA, Set<T> uniqueToB, Set<T> common) {
		this.uniqueToA =   uniqueToA;
		this.uniqueToB = uniqueToB;
		this.common = common;
	}
	protected static final <P,K> SetsCompareResult<P> compareTwoSets(Set<P> setA, Set<P> setB, ToKey<K,P> toKey) {
		Set<P> uniqueToA = new HashSet<>();
		Set<P> uniqueToB = new HashSet<>();
		Set<P> common = new HashSet<>();
		Map<K,P> keysA = CollectionsHelper.asMapLastKeyWin(setA, toKey);
		Map<K,P> keysB = CollectionsHelper.asMapLastKeyWin(setB, toKey);
		
		for (Map.Entry<K,P> element:keysA.entrySet()) {
			if (keysB.containsKey(element.getKey())) {
				common.add(element.getValue());
			} else {
				uniqueToA.add(element.getValue());
			}
		}
		for (Map.Entry<K,P> element:keysB.entrySet()) {
			if (!keysA.containsKey(element.getKey())) {
				uniqueToB.add(element.getValue());
			}
		}
		return new SetsCompareResult<P>(uniqueToA, uniqueToB, common);
	}
	public boolean isEqual() {
		return uniqueToA.isEmpty() && uniqueToB.isEmpty();
	}
}
