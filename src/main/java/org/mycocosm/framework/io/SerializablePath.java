package org.mycocosm.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

import org.mycocosm.framework.text.TextHelper;

public class SerializablePath implements Path, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7996801605856694881L;
	
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		String s = stream.readUTF();
		if (!TextHelper.isNullOrEmpty(s)) {
			delegate = Paths.get(s);
		} else {
			delegate = null;
		}
	}
	private void writeObject(ObjectOutputStream stream) throws IOException {
		if (delegate!=null) {
			stream.writeUTF(delegate.toString());
		} else {
			stream.writeUTF("");
		}
	}
	
	private Path delegate;
	
	public static final SerializablePath of(Path path) {
		if (path!=null) {
			return new SerializablePath(path);
		} else {
			return null;
		}
	}
	
	private Path toRealPath(Path other) {
		if (other!=null) {
			Class<? extends Path> cl = other.getClass();
			if (cl.isAssignableFrom(SerializablePath.class)) {
				return ((SerializablePath)other).delegate;
			} else {
				return other;
			}
		} else {
			return null;
		}
	}	
	public Path delegate() {
		return delegate;
	}
	
	public SerializablePath(Path delegate) {
		this.delegate = delegate;
	}

	public FileSystem getFileSystem() {
		return delegate.getFileSystem();
	}

	public boolean isAbsolute() {
		return delegate.isAbsolute();
	}

	public Path getRoot() {
		return delegate.getRoot();
	}

	public Path getFileName() {
		return delegate.getFileName();
	}

	public Path getParent() {
		return delegate.getParent();
	}

	public int getNameCount() {
		return delegate.getNameCount();
	}

	public Path getName(int index) {
		return delegate.getName(index);
	}

	public Path subpath(int beginIndex, int endIndex) {
		return delegate.subpath(beginIndex, endIndex);
	}

	public boolean startsWith(Path other) {
		return delegate.startsWith(toRealPath(other));
	}

	public boolean startsWith(String other) {
		return delegate.startsWith(other);
	}

	public boolean endsWith(Path other) {
		return delegate.endsWith(toRealPath(other));
	}

	public boolean endsWith(String other) {
		return delegate.endsWith(other);
	}

	public Path normalize() {
		return delegate.normalize();
	}

	public Path resolve(Path other) {
		return delegate.resolve(toRealPath(other));
	}

	public Path resolve(String other) {
		return delegate.resolve(other);
	}

	public Path resolveSibling(Path other) {
		return delegate.resolveSibling(toRealPath(other));
	}

	public Path resolveSibling(String other) {
		return delegate.resolveSibling(other);
	}

	public Path relativize(Path other) {
		return delegate.relativize(toRealPath(other));
	}

	public URI toUri() {
		return delegate.toUri();
	}

	public Path toAbsolutePath() {
		return delegate.toAbsolutePath();
	}

	public Path toRealPath(LinkOption... options) throws IOException {
		return delegate.toRealPath(options);
	}

	public File toFile() {
		return delegate.toFile();
	}

	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
		return delegate.register(watcher, events, modifiers);
	}

	public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
		return delegate.register(watcher, events);
	}

	public Iterator<Path> iterator() {
		return delegate.iterator();
	}

	public int compareTo(Path other) {
		return delegate.compareTo(toRealPath(other));
	}

	public boolean equals(Object other) {
		return delegate.toString().equals(other.toString());
	}

	public int hashCode() {
		return delegate.toString().hashCode();
	}

	public String toString() {
		return delegate.toString();
	}
	
	
}
