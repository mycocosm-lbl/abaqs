package org.mycocosm.framework.io;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/*
 * This class designed to be used to wrap System.out or System.err in try with resource to prevent automatic
 * close of standard outputs.
 */
public class WrappedPrintStreamIgnoreClose extends PrintStream {

	public WrappedPrintStreamIgnoreClose(PrintStream delegate) throws FileNotFoundException {
		super(delegate);
	}

	@Override
	public void close() {
		// do not close delegate
	}

	
}
