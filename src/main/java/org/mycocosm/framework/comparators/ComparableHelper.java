package org.mycocosm.framework.comparators;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

public class ComparableHelper {

	public static final <T extends Comparable<T>> int nullSafeCompare(T o1, T o2) {
		if (o1!=null && o2!=null) {
			return o1.compareTo(o2);
		} else if (o1!=null && o2==null) {
			return 1;
		} else if (o1==null && o2!=null) {
			return -1;
		} else {
			return 0;
		}
	}

	public static final <T> int nullSafeCompare(T o1, T o2, Comparator<T> comp) {
		if (o1!=null && o2!=null) {
			return comp.compare(o1, o2);
		} else if (o1!=null && o2==null) {
			return 1;
		} else if (o1==null && o2!=null) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public static final <T extends Comparable<T>,V> int nullSafeCompare(V o1, V o2, Function<V, T> func) {
		if (o1!=null && o2!=null) {
			return nullSafeCompare(func.apply(o1), func.apply(o2));
		} else if (o1!=null && o2==null) {
			return 1;
		} else if (o1==null && o2!=null) {
			return -1;
		} else {
			return 0;
		}
	}

	public static final <T,V> int nullSafeCompare(V o1, V o2, Function<V, T> func, Comparator<T> comp) {
		if (o1!=null && o2!=null) {
			return comp.compare(func.apply(o1), func.apply(o2));
		} else if (o1!=null && o2==null) {
			return 1;
		} else if (o1==null && o2!=null) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public static final <T> int nullSafeCompareStack(Collection<Comparator<T>> list,T o1, T o2) {
		for (Comparator<T> c:list) {
			int ret = nullSafeCompare(o1, o2, c);
			if (ret!=0) {
				return ret;
			}
		}
		return 0;
	}
	
	@SafeVarargs
	public static final <T> int nullSafeCompareStack(T o1, T o2, Comparator<T>... stack) {
		for (Comparator<T> c:stack) {
			int ret = nullSafeCompare(o1, o2, c);
			if (ret!=0) {
				return ret;
			}
		}
		return 0;
	}

	public static final int compare2LevelInt(int level_1_a, int level_1_b, int level_2_a, int level_2_b) {
		int ret = Integer.compare(level_1_a, level_1_b);
		if (ret==0) {
			return Integer.compare(level_2_a, level_2_b);
		} else {
			return ret;
		}
	}

}
