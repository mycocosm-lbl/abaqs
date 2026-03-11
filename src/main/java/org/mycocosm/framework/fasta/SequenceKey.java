package org.mycocosm.framework.fasta;

import java.io.Serializable;

public abstract class SequenceKey<T extends SequenceKey<?>> implements Serializable,Comparable<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2919780051922618557L;
	public final String portalId;

	protected SequenceKey(String portalId) {
		this.portalId = portalId;
	}

	@Override
	public int compareTo(T o) {
		return portalId.compareTo(o.portalId);
	}

}
