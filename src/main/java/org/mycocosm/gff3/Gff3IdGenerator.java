package org.mycocosm.gff3;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;

public class Gff3IdGenerator {
	private Map<Gff3Type,MutableInt> idByType = new HashMap<>();
	protected String nextId(Gff3Type type) {
		return String.format("%s_%d", type.name(),idByType.computeIfAbsent(type, k->new MutableInt(0)).incrementAndGet());
	}
}
