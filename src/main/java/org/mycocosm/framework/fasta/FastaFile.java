package org.mycocosm.framework.fasta;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mycocosm.framework.io.BufferedPushbackReader;
import org.mycocosm.framework.io.FilesHelper;
import org.mycocosm.framework.text.TextHelper;

public class FastaFile implements Closeable {
	private static final Pattern HEADER_LINE_PATTERN = Pattern.compile("^[>;](.*)");

	private final BufferedPushbackReader in;

	public FastaFile(Path file) throws IOException {
		this.in = new BufferedPushbackReader(new InputStreamReader(FilesHelper.newInputStreamOptionallyGzipped(file)));
	}

	public FastaFile(InputStream is) throws IOException {
		this.in = new BufferedPushbackReader(new InputStreamReader(is));
	}
	
	public FastaFile(BufferedReader reader) {
		this.in = new BufferedPushbackReader(reader);
	}

	public void close() throws IOException {
		this.in.close();
	}

	/*
	 * @return null of no more scaffolds
	 */

	public Scaffold getNextScaffold() throws IOException {
		Scaffold scaf = readToNextScaffold();
		if (scaf == null) {
			return null;
		}
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		String line = in.readLine();
		while (line!=null) {
			if (maybeHeader(line)) {
				this.in.unreadLine(line);
				break;
			} else {
				buf.write(line.getBytes());
			}
			line = in.readLine();
		}
		scaf.setData(buf.toByteArray());
		return scaf;
	}

	public boolean hasMoreScaffolds() throws IOException {
		String line = in.readLine();
		while (line!=null) {
			if (maybeHeader(line)) {
				Matcher m = HEADER_LINE_PATTERN.matcher(line);
				if (m.matches()) {
					in.unreadLine(line);
					return true;
				}
			}
			line = in.readLine();
		}
		return false;
	}
	
	/*
	 * @return null of no more scaffolds
	 */
	
	private Scaffold readToNextScaffold() throws IOException {
		String line = in.readLine();
		while (line!=null) {
			if (maybeHeader(line)) {
				Matcher m = HEADER_LINE_PATTERN.matcher(line);
				if (m.matches()) {
					String def = m.group(1);
					return new Scaffold(def);
				}
			}
			line = in.readLine();
		}
		return null;
	}

	private boolean maybeHeader(String line) {
		if (!TextHelper.isNullOrEmpty(line)) {
			switch (line.charAt(0)) {
			case '>':
			case ';': return true;
			default: return false;
			}
		} else {
			return false;
		}
	}

	public Scaffold getScaffoldByName(String name) throws IOException {
		Scaffold scaffold = getNextScaffold();
		while(scaffold != null) {
			if (scaffold.name.equals(name))
				return scaffold;
			scaffold = getNextScaffold();
		}
		return null;
	}
		
	public List<String> getScaffoldNames() throws IOException {
		List<String> ret = new ArrayList<String>();
		Scaffold scaffold = getNextScaffold();
		while (scaffold != null) {
			ret.add(scaffold.name);
			scaffold = getNextScaffold();
		}
		return ret;
	}
}
