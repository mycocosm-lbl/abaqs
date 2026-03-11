package org.mycocosm.framework.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;

public class BufferedPushbackReader extends BufferedReader {

	private final Deque<String> pushedStrings = new LinkedList<String>();

	public BufferedPushbackReader(Reader in) {
		super(in);
	}

	@Override
	public String readLine() throws IOException {
		if (!pushedStrings.isEmpty()) {
			return pushedStrings.pop();
		}
		return super.readLine();
	}

	public void unreadLine(String line) {
		pushedStrings.push(line);
	}

}
