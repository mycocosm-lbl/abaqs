package org.mycocosm.framework.comparators;

import java.util.Comparator;

public enum StandardComparators implements Comparator<Object>{
	Universal {
		@Override
		public int compare(Object o1, Object o2) {
			return StandardComparatorsHelper.UNIVERSAL_COMPARATOR.compare(o1, o2);
		}
	},
	NullSafeToStringIgnoreCase {
		@Override
		public int compare(Object o1, Object o2) {
			return StackableComparatorResult.toComparatorResult(StandardComparatorsHelper.NULL_SAFE_TO_STRING_IGNORE_CASE.compare(o1, o2));
		}
	},
	StringsAndNumbersIgnoreCase {
		@Override
		public int compare(Object o1, Object o2) {
			return StackableComparatorResult.toComparatorResult(StandardComparatorsHelper.NULL_SAFE_STRING_AND_NUMBER_IGNORE_CASE.compare(o1, o2));
		}
	};

}
