package org.mycocosm.framework.collections;

import java.util.ArrayList;
import java.util.Collection;

public final class BinnedCollection<T> {
	public final double fromBinValue;
	public final double toBinValue;
	public final Collection<T> entries = new ArrayList<>();
	BinnedCollection(double fromBinValue, double toBinValue) {
		this.fromBinValue = fromBinValue;
		this.toBinValue = toBinValue;
	}
	void add(T entry) {
		entries.add(entry);
	}
}
