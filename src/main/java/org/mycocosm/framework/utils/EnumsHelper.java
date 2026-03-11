package org.mycocosm.framework.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumsHelper {

	public static final <E extends Enum<E>> E nullSafeValueOfOrNull(String name, E[] values) {
		return nullSafeValueOfOrDefaultValue(name, values, null);
	}

	public static final <E extends Enum<E>> E nullSafeValueOfOrNullFromOrdinal(int ordinal, E[] values) {
		return nullSafeValueOfOrDefaultValueFromOrdinal(ordinal, values, null);
	}
	
	public static final <E extends Enum<E>> E nullSafeValueOfOrDefaultValue(String name, E[] values, E defaultValue) {
		if (name!=null) {
			for (E v:values) {
				if (v.name().equals(name)) {
					return v;
				}
			}
			return defaultValue;
		} else {
			return defaultValue;
		}
	}

	public static final <E extends Enum<E>> E nullSafeValueOfOrException(String name, E[] values) throws IllegalEnumValueException {
		if (name!=null) {
			for (E v:values) {
				if (v.name().equals(name)) {
					return v;
				}
			}
			throw new IllegalEnumValueException(name);
		} else {
			return null;
		}
	}

	public static final <E extends Enum<E>> E nullSafeValueOfOrDefaultValueFromOrdinal(int ordinal, E[] values, E defaultValue) {
		if (ordinal>=0 && ordinal<values.length) {
			return values[ordinal];
		} else {
			return defaultValue;
		}
	}

	public static final <E extends Enum<E>> String nullSafeName(E en) {
		if (en!=null) {
			return en.name();
		} else {
			return null;
		}
	}
	public static final <E extends Enum<E>> String nullSafeValue(E en, Function<E, String> func) {
		if (en!=null) {
			return func.apply(en);
		} else {
			return null;
		}
	}
	
	public static final <E extends Enum<E>> int nullSafeCompareByOrdinal(E e1, E e2) {
		if (e1!=null&&e2!=null) {
			return Integer.compare(e1.ordinal(),e2.ordinal());
		} else if (e1==null&&e2!=null) {
			return -1;
		} else if (e1!=null&&e2==null) {
			return 1;
		} else {
			return 0;
		}
	}

	public static final <E extends Enum<E>> int nullSafeCompareByOrdinalReverseNulls(E e1, E e2) {
		if (e1!=null&&e2!=null) {
			return Integer.compare(e1.ordinal(),e2.ordinal());
		} else if (e1==null&&e2!=null) {
			return 1;
		} else if (e1!=null&&e2==null) {
			return -1;
		} else {
			return 0;
		}
	}

	public static final <E extends Enum<E>> int nullSafeCompareByName(E e1, E e2) {
		if (e1!=null&&e2!=null) {
			return e1.name().compareTo(e2.name());
		} else if (e1==null&&e2!=null) {
			return -1;
		} else if (e1!=null&&e2==null) {
			return 1;
		} else {
			return 0;
		}
	}

	public static final <E extends Enum<E>> int nullSafeCompareByNameReverseNulls(E e1, E e2) {
		if (e1!=null&&e2!=null) {
			return e1.name().compareTo(e2.name());
		} else if (e1==null&&e2!=null) {
			return 1;
		} else if (e1!=null&&e2==null) {
			return -1;
		} else {
			return 0;
		}
	}

	public static final <E extends Enum<E>> String nullSafeToStringJoining(Set<E> st, CharSequence delimiter) {
		if (st!=null) {
			return st.stream().map(r->r.name()).collect(Collectors.joining(delimiter));
		} else {
			return null;
		}
	}

	public static final <E extends Enum<E>> E orElse(E value, E orElse) {
		return value!=null?value:orElse;
	}

	public static final <E extends Enum<E>> boolean nullSafeEquals(E v1, E v2) {
		if (v1!=null && v2!=null) {
			return v1.equals(v2);
		} else {
			return false; // let assume that null is NOT equal to the other null
		}
	}
	
	public static final <T extends Enum<T>> String joining(Class<T> en, String delimiter) {
		return Arrays.stream(en.getEnumConstants()).map(e->e.name()).collect(Collectors.joining(delimiter));
	}
	public static final <T extends Enum<T>> String joining(Class<T> en) {
		return joining(en,",");
	}

}
