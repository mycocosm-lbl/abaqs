package org.mycocosm.framework.comparators;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mycocosm.framework.text.TextHelper;

public class StandardComparatorsHelper {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final StackableComparatorResult universalCompare(Object o1, Object o2) {
		if (o1!=null && o2!=null) {
			if (o1.equals(o2)) {
				return StackableComparatorResult.equal;
			} else if (o1.getClass().equals(o2.getClass())) {
				Class<?> cls = o1.getClass();
				if (String.class.isAssignableFrom(cls)) {
					return  StackableComparatorResult.fromComparatorResult(((String)o1).compareTo((String)o2));
				} else if (Comparable.class.isAssignableFrom(cls)) {
					return StackableComparatorResult.fromComparatorResult(((Comparable)o1).compareTo(o2));
				} else {
					return StackableComparatorResult.fromComparatorResult(o1.toString().compareTo(o2.toString()));
				}
			} else {
				return StackableComparatorResult.fromComparatorResult(o1.toString().compareTo(o2.toString()));
			}
		} else if (o1==null && o2==null) {
			return StackableComparatorResult.equal;
		} else if (o1!=null){
			return StackableComparatorResult.greater;
		} else {
			return StackableComparatorResult.smaller;
		}
	}
	
	public static final StackableComparatorResult stringAndNumbersCompareIgnoreCase(String s1, String s2) {
		return StackableComparatorResult.fromComparatorResult(TextHelper.compareStringAndNumbers(s1.toLowerCase(), s2.toLowerCase()));
	}
	
	public static final StackableComparator<Object> NULL_SAFE_STRING_AND_NUMBER_IGNORE_CASE = new StackableComparator<Object>() {
		
		@Override
		public StackableComparatorResult compare(Object o1, Object o2) {
			if (o1!=null && o2!=null) {
				return StackableComparatorResult.fromComparatorResult(TextHelper.compareStringAndNumbers(o1.toString().toLowerCase(), o2.toString().toLowerCase()));
			} else if (o1==null && o2==null) {
				return StackableComparatorResult.undecided;
			} else if (o1!=null && o2==null) {
				return StackableComparatorResult.greater;
			} else {
				return StackableComparatorResult.smaller;
			}
		}
	};
	
	public static final StackableComparator<Object> NULL_SAFE_TO_STRING_IGNORE_CASE = new NullSafeToStringIgnoreCase<>();

	public static final Comparator<Object> UNIVERSAL_COMPARATOR = new Comparator<Object> () {

		@Override
		public int compare(Object o1, Object o2) {
			return StackableComparatorResult.toComparatorResult(universalCompare(o1, o2));
		}

	};

	@SafeVarargs
	public static final <V> Comparator<V> createComparatorsStack(final StackableComparator<V>... comparators) {
		return new Comparator<V>() {

			@Override
			public int compare(V o1, V o2) {
				for (StackableComparator<V> c:comparators) {
					StackableComparatorResult result = c.compare(o1, o2);
					switch (result) {
						case undecided: continue;
						default: return StackableComparatorResult.toComparatorResult(result);
					}
				}
				throw new StackableComparatorException("Open ended stackable comparators stack");
			}
		};
	}
	
	public static final <V> Comparator<V> createOneObjectOnTopComparator(V objectOnTop) {
		return createComparatorsStack(new OneObjectOnTop<V>(objectOnTop), new Generic<V>());
	}

	public static final <V> Comparator<V> createOneObjectOnBottomComparator(V objectOnBottom) {
		return createComparatorsStack(new OneObjectOnBottom<V>(objectOnBottom), new Generic<V>());
	}
	
	public static final <V> Comparator<V> createDefinedOrderOnTopComparator(List<V> definedOrder) {
		return createComparatorsStack(new DefinedOrderOnTop<V>(definedOrder), new Generic<V>());
	}

	public static final <V> Comparator<V> createOneObjectOnTopDefinedOrderNextComparator(V objectOnTop, List<V> definedOrder) {
		return createComparatorsStack(new OneObjectOnTop<V>(objectOnTop), new DefinedOrderOnTop<V>(definedOrder), new Generic<V>());
	}
	
	public static final class OneObjectOnTop<V> implements StackableComparator<V> {
		public OneObjectOnTop(V objectOnTop) {
			this.objectOnTop = objectOnTop;
		}

		private V objectOnTop;

		@Override
		public StackableComparatorResult compare(V o1, V o2) {
			if (o1!=null && o1.equals(objectOnTop) && o2!=null && o2.equals(objectOnTop)) {
				return StackableComparatorResult.equal;
			} else if (o1!=null && o1.equals(objectOnTop)) {
				return StackableComparatorResult.smaller;
			} else if (o2!=null && o2.equals(objectOnTop)) {
				return StackableComparatorResult.greater;
			} else {
				return StackableComparatorResult.undecided;
			}
		}
	}

	public static final class OneObjectOnBottom<V> implements StackableComparator<V> {
		public OneObjectOnBottom(V objectOnBottom) {
			this.objectOnBottom=objectOnBottom;
		}

		private V objectOnBottom;
		
		@Override
		public StackableComparatorResult compare(V o1, V o2) {
			if (o1!=null && o1.equals(objectOnBottom) && o2!=null && o2.equals(objectOnBottom)) {
				return StackableComparatorResult.equal;
			} else if (o1!=null && o1.equals(objectOnBottom)) {
				return StackableComparatorResult.greater;
			} else if (o2!=null && o2.equals(objectOnBottom)) {
				return StackableComparatorResult.smaller;
			} else {
				return StackableComparatorResult.undecided;
			}
		}
		
	}
	
	public static final class DefinedOrderOnTop<V> implements StackableComparator<V> {

		private Map<V,Integer> definedOrder;
		
		public DefinedOrderOnTop(List<V> definedOrder) {
			this.definedOrder = new HashMap<>(definedOrder.size());
			for (int i=0;i<definedOrder.size();i++) {
				V value = definedOrder.get(i);
				if (value!=null) {
					this.definedOrder.put(value, i);
				}
			}
		}
		
		@Override
		public StackableComparatorResult compare(V o1, V o2) {
			Integer i1 = null;
			Integer i2 = null;
			if (o1!=null) {
				i1 = definedOrder.get(o1);
			}
			if (o2!=null) {
				i2 = definedOrder.get(o2);
			}
			if (i1!=null && i2!=null) {
				return StackableComparatorResult.fromComparatorResult(i1.compareTo(i2));
			} else if (i1!=null && i2==null) {
				return StackableComparatorResult.smaller;
			} else if (i1==null && i2!=null) {
				return StackableComparatorResult.greater;
			} else {
				return StackableComparatorResult.undecided;
			}
		}
	}

	public static final class NullSafeToStringIgnoreCase<V> implements StackableComparator<V> {

		@Override
		public StackableComparatorResult compare(V o1, V o2) {
			if (o1!=null&&o2!=null) {
				return StackableComparatorResult.fromComparatorResult( o1.toString().compareToIgnoreCase(o2.toString()));
			} else if (o1==null&&o2!=null) {
				return StackableComparatorResult.smaller;
			} else if (o1!=null&&o2==null) {
				return StackableComparatorResult.greater;
			} else {
				return StackableComparatorResult.equal;
			}
		}
	}
	
	public static final class FromJavaComparator<V> implements StackableComparator<V> {
		private Comparator<V> comparator;

		public FromJavaComparator(Comparator<V> comparator) {
			this.comparator = comparator;
		}

		@Override
		public StackableComparatorResult compare(V o1, V o2) {
			return StackableComparatorResult.fromComparatorResult(comparator.compare(o1, o2));
		}
	}
	
	public static final class FixedResult<V> implements StackableComparator<V> {
		private StackableComparatorResult result;

		private FixedResult(StackableComparatorResult result) {
			this.result = result;
		}

		@Override
		public StackableComparatorResult compare(V o1, V o2) {
			return result;
		}
	}

	public static final class Generic<V> implements StackableComparator<V> {

		private Generic() {
		}

		@Override
		public StackableComparatorResult compare(V o1, V o2) {
			return universalCompare(o1,o2);
		}
	}

	public static final <T> StackableComparator<T> inversed(final StackableComparator<T> c) {
		return new StackableComparator<T>() {

			@Override
			public StackableComparatorResult compare(T o1, T o2) {
				return c.compare(o1, o2);
			}
		};
	}
	
	public static final StackableComparator<Integer> INTEGER_STACKABLE_COMPARATOR = new StackableComparator<Integer>() {

		@Override
		public StackableComparatorResult compare(Integer o1, Integer o2) {
			return StackableComparatorResult.fromComparatorResult(o1.compareTo(o2));
		}
	};
	
}
