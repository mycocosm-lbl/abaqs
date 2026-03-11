package org.mycocosm.framework.fasta;

public class Scaffold extends ScaffoldDef {
	private byte[] data;

	public Scaffold(String def) {
		super(def);
	}

	public byte[] getData() {
		return this.data;
	}

	public int length() {
		if (this.data!=null) {
			return this.data.length;
		} else {
			return 0;
		}
	}

	protected void setData(byte[] data) {
		this.data = data;
	}

	public String subSequence(int start, int end) {
		//Note: dna indices are 1-based
		try {
			return new String(data, start-1, end - start + 1);
		} catch(IndexOutOfBoundsException e) {
			throw new RuntimeException("Error in Scaffold.subSequence: start = " + start + ". end = " + end + ". len = " + data.length + "."); 
		}
	}

	public String getSequence() {
		if (data!=null) {
			return new String(data);
		} else {
			return null;
		}
	}
}

