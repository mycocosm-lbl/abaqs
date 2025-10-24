package org.mycocosm.famework.collections;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArraysHelper {
	public static final <T> T[] newArray(Class<T> clasz, int length, T initialValue) {
		@SuppressWarnings("unchecked")
		T[] ret = (T[])Array.newInstance(clasz, length);
		for (int i=0;i<length;i++) {
			ret[i] = initialValue;
		}
		return ret;
	}
	public static final boolean isNullOrEmpty(Object[] arr) {
		return arr==null || arr.length==0;
	}

	public static final <T> T[] merge(T[] a, T[] b) {
		assert a!=null;
		assert b!=null;
		T[] ret = Arrays.copyOf(a, a.length+b.length);
		for (int i=0;i<b.length;i++) {
			ret[i+a.length]=b[i];
		}
		return ret;
	}

	public static final <T> T[] nullSafeReverse(T[] in) {
		if (in!=null) {
			T[] ret = Arrays.copyOf(in,in.length);
			for (int i=0;i<in.length;i++) {
				ret[i]=in[in.length-i-1];
			}
			return ret;
		} else {
			return null;
		}
	}

	public static final char[] nullSafeReverse(char[] in) {
		if (in!=null) {
			char[] ret = Arrays.copyOf(in,in.length);
			for (int i=0;i<in.length;i++) {
				ret[i]=in[in.length-i-1];
			}
			return ret;
		} else {
			return null;
		}
	}
	public static final <T> boolean arraysEqualsNullEqualToNull(T[] a, T[] b) {
		if (a==null && b==null) {
			return true;
		} else {
			return Arrays.equals(a, b);
		}
	}
}
