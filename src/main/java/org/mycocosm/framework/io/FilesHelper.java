package org.mycocosm.framework.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FilesHelper {


	private static final Set<OpenOption> toOpenOptionsSetAny(OpenOption... options) throws IOException {
		int len = options.length;
		Set<OpenOption> opts = new HashSet<OpenOption>(len + 1);
		for (OpenOption opt : options) {
			opts.add(opt);
		}
		if (opts.contains(StandardOpenOption.WRITE) || opts.contains(StandardOpenOption.READ)) {
			return opts;
		} else {
			throw new IOException("Ether READ or WRITE OpenOptions is needed");
		}
	}

	private static final Set<OpenOption> toOpenOptionsSetWrite(OpenOption... options) {
		int len = options.length;
		Set<OpenOption> opts = new HashSet<OpenOption>(len + 3);
		if (len == 0) {
			opts.add(StandardOpenOption.CREATE);
			opts.add(StandardOpenOption.TRUNCATE_EXISTING);
		} else {
			for (OpenOption opt : options) {
				if (opt == StandardOpenOption.READ)
					throw new IllegalArgumentException("READ not allowed");
				opts.add(opt);
			}
		}
		opts.add(StandardOpenOption.WRITE);
		return opts;
	}

	private static final Set<OpenOption> toOpenOptionsSetRead(OpenOption... options) {
		int len = options.length;
		Set<OpenOption> opts = new HashSet<OpenOption>(len + 3);
		if (len == 0) {
			opts.add(StandardOpenOption.READ);
		} else {
			for (OpenOption opt : options) {
				if (opt == StandardOpenOption.WRITE)
					throw new IllegalArgumentException("WRITE not allowed");
				opts.add(opt);
			}
		}
		return opts;
	}

	private static final Path delegate(Path original) {
		if (SerializablePath.class.isAssignableFrom(original.getClass())) {
			return ((SerializablePath)original).delegate();
		} else {
			return original;
		}
	}

	public static final FileChannel newFileChannel(Path path, OpenOption... options) throws IOException {
		return FileChannel.open(delegate(path), toOpenOptionsSetAny(options), STANDARD_PERMISSIONS_FILE);
	}

	public static final SeekableByteChannel newByteChannel(Path path, OpenOption... options) throws IOException {
		return newByteChannel(delegate(path), STANDARD_PERMISSIONS_FILE, options);
	}

	public static final SeekableByteChannel newByteChannel(Path path, FileAttribute<?> permissions, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetAny(options), permissions);
	}

	public static final Writer newWriter(Path path, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newWriter(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetWrite(options), STANDARD_PERMISSIONS_FILE), StandardCharsets.UTF_8.newEncoder(),-1);
	}

	public static final Writer newWriter(Path path, Charset charset, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newWriter(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetWrite(options), STANDARD_PERMISSIONS_FILE), charset.newEncoder(),-1);
	}

	public static final Writer newWriter(Path path, FileAttribute<?> permissions, Charset charset, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newWriter(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetWrite(options), permissions), charset.newEncoder(),-1);
	}

	public static final Reader newReader(Path path, FileAttribute<?> permissions, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newReader(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetRead(options), permissions), StandardCharsets.UTF_8.newDecoder(),-1);
	}

	public static final Reader newReader(Path path, FileAttribute<?> permissions, Charset charset, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newReader(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetRead(options), permissions), charset.newDecoder(),-1);
	}

	public static final Reader newReader(Path path, Charset charset, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newReader(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetRead(options), STANDARD_PERMISSIONS_FILE), charset.newDecoder(),-1);
	}

	public static final Reader newReader(Path path, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newReader(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetRead(options), STANDARD_PERMISSIONS_FILE), StandardCharsets.UTF_8.newDecoder(),-1);
	}

	public static final OutputStream newOutputStream(Path path, FileAttribute<?> permissions, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newOutputStream(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetWrite(options), permissions));
	}

	public static final OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
		return newOutputStream(delegate(path), STANDARD_PERMISSIONS_FILE, options);
	}

	public static final GZIPOutputStream newGzipOutputStream(Path path, OpenOption... options) throws IOException {
		return new GZIPOutputStream(newOutputStream(path, options));
	}

	public static final InputStream newInputStream(Path path, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return Channels.newInputStream(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetRead(options), STANDARD_PERMISSIONS_FILE));
	}

	public static final GZIPInputStream newGzipInputStream(Path path, OpenOption... options) throws IOException {
		return new GZIPInputStream(newInputStream(path,options));
	}

	public static final DataOutputStream newDataOutputStreamWithGzip(Path path, OpenOption... options) throws IOException {
		return new DataOutputStream(new GZIPOutputStream(newOutputStream(delegate(path), STANDARD_PERMISSIONS_FILE, options)));
	}

	public static final DataInputStream newDataInputStreamWithGzip(Path path, OpenOption... options) throws IOException {
		Path p = delegate(path);
		return new DataInputStream(new GZIPInputStream(Channels.newInputStream(p.getFileSystem().provider().newByteChannel(p, toOpenOptionsSetRead(options), STANDARD_PERMISSIONS_FILE))));
	}


	public static final String createRandomFolder(int levels, String pathStr) {
		return createRandomFolder(levels, Paths.get(pathStr)).toString();
	}

	public static final Path createRandomFolder(int levels, Path path) {
		Path ret = generateRandomFolder(levels, path);
		if (!createFolder(ret)) {
			throw new RuntimeException("Cannot create folder: " + ret);
		}
		return ret;
	}

	public static final Path generateRandomFolder(int levels, Path path) {
		Random generator = new Random();
		Path generatedPath = delegate(path);
		for (int i = 0; i < levels; i++) {
			int k = generator.nextInt(100);
			generatedPath = generatedPath.resolve(String.valueOf(k));
		}
		return generatedPath;
	}

	public static final boolean createFolder(String path) {
		return createFolder(Paths.get(path));
	}

	public static final FileAttribute<?> STANDARD_PERMISSIONS_FILE = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-rw-r--"));
	public static final FileAttribute<?> ALL_WRITE_PERMISSIONS_FILE = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-rw-rw-"));

	public static final FileAttribute<?> STANDARD_PERMISSIONS = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxr-x"));
	public static final FileAttribute<?> ALL_WRITE_PERMISSIONS = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx"));
	public static final FileAttribute<?> STRICT_PERMISSIONS = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-x---"));

	public static final Path renameFolder(Path oldName, Path newName) throws IOException {
		return Files.move(oldName, newName);
	}


	public static final boolean createFolder(Path dir, FileAttribute<?> permissions) throws FileCreationException {
		Path p = delegate(dir);
		try {
			BasicFileAttributes attributes = Files.readAttributes(p, BasicFileAttributes.class);
			if (attributes.isDirectory()) {
				return true;
			} else {
				throw new FileCreationException(String.format("Unable to create folder '%s' - file (not directory) with such name already exists",p.toString()));
			}
		} catch (IOException e) {
			try {
				Files.createDirectories(p, permissions);
			} catch (IOException ex) {
				throw new FileCreationException(ex);
			}
			if (Files.isDirectory(p)) {
				return true;
			} else {
				throw new FileCreationException(String.format("Unable to create folder '%s'",p.toString()));
			}
		}
	}

	public static final boolean createFolder(Path f) throws FileCreationException {
		return createFolder(f, STANDARD_PERMISSIONS);
	}

	public static final Set<FileVisitOption> EMPTY_FILE_VISIT_OPTIONS = new HashSet<>();
	public static final Set<FileVisitOption> toOptionsSet(FileVisitOption... options) {
		Set<FileVisitOption> optionsSet = new HashSet<>();
		if (options != null) {
			for (FileVisitOption o : options) {
				optionsSet.add(o);
			}
		}
		return optionsSet;
	}
	public static final Set<FileVisitOption> STANDARD_FILE_VISIT_OPTIONS = toOptionsSet(FileVisitOption.FOLLOW_LINKS);
	
	public static final List<Path> loadDirectoryTree(Path dir, boolean includeDirPaths) throws IOException {
		final List<Path> ret = new ArrayList<>();
		Files.walkFileTree(delegate(dir), new SimpleFileVisitor<Path>() {

			@Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
                throws IOException
            {
				if(includeDirPaths && !path.equals(dir)) {
					ret.add(path);
				}
                return FileVisitResult.CONTINUE;
            }
			
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				ret.add(path);
				return FileVisitResult.CONTINUE;
			}

		});
		return ret;
	}

	public static final List<Path> loadDirectory(Path dir, final PathFilter filter, Set<FileVisitOption> options) throws IOException {
		final List<Path> ret = new ArrayList<>();
		Files.walkFileTree(delegate(dir), options, 1, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (filter.accept(file,attrs)) {
					ret.add(file);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return filter.visitFileFailed(file, exc);
			}

		});
		return ret;
	}
//
//	public static final List<PathWithAllAtributes> loadDirectoryWithAttributes(Path dir, final PathFilter filter, Set<FileVisitOption> options) throws IOException {
//		final List<PathWithAllAtributes> ret = new ArrayList<>();
//		Files.walkFileTree(delegate(dir), options, 1, new SimpleFileVisitor<Path>() {
//
//			@Override
//			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//				if (filter.accept(file,attrs)) {
//					ret.add(new PathWithAllAtributes(file, attrs));
//				}
//				return FileVisitResult.CONTINUE;
//			}
//
//			@Override
//			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//				return filter.visitFileFailed(file, exc);
//			}
//
//		});
//		return ret;
//	}
//
//	public static final List<PathWithAllAtributes> loadDirectoryWithAttributes(Path dir, final PathFilter filter) throws IOException {
//		return loadDirectoryWithAttributes(dir, filter, EMPTY_FILE_VISIT_OPTIONS);
//	}
//	
//	public static final List<Path> loadDirectory(Path dir, final PathFilter filter, FileVisitOption... options) throws IOException {
//		return loadDirectory(dir,filter,toOptionsSet(options));
//	}
//
//	public static final List<Path> loadDirectoryAll(Path dir, FileVisitOption... options) throws IOException {
//		return loadDirectory(dir, new AbstractPathFilterignoringErrors() {
//
//			@Override
//			public boolean accept(Path file, BasicFileAttributes attrs) {
//				return true;
//			}
//		}, options);
//	}
//
//	public static final List<Path> loadDirectoryAllNoFollowSimlinks(Path dir) throws IOException {
//		return loadDirectory(dir, new AbstractPathFilterignoringErrors() {
//
//			@Override
//			public boolean accept(Path file, BasicFileAttributes attrs) {
//				return true;
//			}
//		}, EMPTY_FILE_VISIT_OPTIONS);
//	}
//
//	public static final List<Path> loadDirectoryFilesByPrefix(Path dir, final String prefix, FileVisitOption... options) throws IOException {
//		return loadDirectory(dir, new AbstractPathFilterignoringErrors() {
//
//			@Override
//			public boolean accept(Path file, BasicFileAttributes attrs) {
//				return file.getFileName().toString().startsWith(prefix);
//			}
//		}, options);
//	}
//
//	public static final List<Path> loadDirectoryFilesBySuffix(Path dir, final String suffix, FileVisitOption... options) throws IOException {
//		return loadDirectory(dir, new AbstractPathFilterignoringErrors() {
//
//			@Override
//			public boolean accept(Path file, BasicFileAttributes attrs) {
//				return file.getFileName().toString().endsWith(suffix);
//			}
//		}, options);
//	}
//
//	public static final List<Path> loadDirectoryFilesByPattern(Path dir, final Pattern pattern, FileVisitOption... options) throws IOException {
//		return loadDirectory(dir, new AbstractPathFilterignoringErrors() {
//
//			@Override
//			public boolean accept(Path file, BasicFileAttributes attrs) {
//				return pattern.matcher(file.getFileName().toString()).matches();
//			}
//		}, options);
//	}
//
//	public static final Path getPath(Path root, String... p) {
//		if (root != null) {
//			return Paths.get(root.toString(), p);
//		} else
//			throw new NullPointerException("root path is null");
//	}
//
//	public static final void deleteAll(Path f, FileVisitOption... options) throws IOException {
//		Path p = delegate(f);
//		if (Files.isDirectory(p)) {
//			Files.walkFileTree(p, toOptionsSet(options), 100, new SimpleFileVisitor<Path>() {
//
//				@Override
//				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//					Files.delete(file);
//					return FileVisitResult.CONTINUE;
//				}
//
//				@Override
//				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//					Files.delete(dir);
//					return FileVisitResult.CONTINUE;
//				}
//
//			});
//		} else {
//			Files.delete(p);
//		}
//	}
//
//	public static final void deleteAllIfExists(Path path, FileVisitOption... options) throws IOException {
//		if (Files.exists(path)) {
//			deleteAll(path, options);
//		}
//	}
//
//	public static final void deleteAllIfExistsManyAttempts(int maxNumberOfFailedAttempts, Duration attemptInterval, Path path, FileVisitOption... options) throws IOException {
//		int attempt=0;
//		IOException lastError = null;
//		do {
//			try {
//				deleteAllIfExists(path, options);
//				return;
//			} catch (IOException e) {
//				attempt++;
//				lastError=e;
//				if (attempt<=maxNumberOfFailedAttempts && attemptInterval!=null) {
//					try {
//						Thread.sleep(attemptInterval.toMillis());
//					} catch (InterruptedException a) {
//						// ignore it
//					}
//				}
//			}
//		} while (attempt<=maxNumberOfFailedAttempts);
//		if (lastError!=null) {
//			throw lastError;
//		}
//	}
//
//	public static final long copyTree(final Path source, final Path destination,  int maxDepth, final CopyOption... options) throws IOException {
//		Path s = delegate(source);
//		Path d = delegate(destination);
//		if (Files.isDirectory(s)) {
//			final AtomicLong totalBytes = new AtomicLong(); 
//
//			Files.walkFileTree(s, toOptionsSet(), maxDepth, new SimpleFileVisitor<Path>() {
//
//				@Override
//				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//
//					Path deltaPath = file.subpath(s.getNameCount(), file.getNameCount());
//					Path destPath = d.resolve(deltaPath);
//					FilesHelper.createFolder(destPath.getParent());
//					Files.copy(file, destPath, options);
//					totalBytes.addAndGet(attrs.size());
//					return FileVisitResult.CONTINUE;
//				}
//
//			});
//			return totalBytes.get();
//		} else {
//			Files.copy(source, destination, options);
//			return Files.size(source);
//		}
//	}
//
//	public static final void copyFile(Path source, Path destination) throws IOException {
//		try (OutputStream os = newOutputStream(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
//			Files.copy(delegate(source),os);
//		}
//	}
//
//	public static final Path resolveSimlinkRecursively(Path input) throws IOException {
//		Path in = delegate(input);
//		Path link = Files.readSymbolicLink(in);
//		if (link.isAbsolute()) {
//			return link;
//		} else {
//			link = in.resolveSibling(link);
//		}
//		if (Files.isSymbolicLink(link)) {
//			return resolveSimlinkRecursively(link);
//		} else {
//			return link;
//		}
//	}
//
//	public static final Path resolveSimlink(Path input) throws IOException {
//		Path in = delegate(input);
//		Path link = Files.readSymbolicLink(in);
//		if (!link.isAbsolute()) {
//			link = in.resolveSibling(link);
//		}
//		return link;
//	}
//
//	public static final Path nullSafeToPath(String path) {
//		if (path!=null) {
//			return Paths.get(path);
//		} else {
//			return null;
//		}
//	}
//
//	public static final boolean isSameFileSafe(Path a, Path b) throws IOException {
//		try {
//			return Files.isSameFile(delegate(a), delegate(b));
//		} catch (NoSuchFileException e) {
//			return false;
//		}
//	}
//
//	public static final boolean isWritableRecursive(Path dir) {
//		Path d = delegate(dir);
//		if (Files.exists(d)) {
//			return Files.isWritable(d);
//		} else {
//			return isWritableRecursive(d.getParent());
//		}
//	}
//
//	public static final void filteredDirectoryStreamEach(Path directory,  DirectoryStream.Filter<? super Path> filter, Consumer<? super Path> action) throws IOException {
//		try (DirectoryStream<Path> sid = Files.newDirectoryStream(directory,filter)) {
//			sid.forEach(action);
//		}
//	}
//
//	public static void main(String[] args) {
//		Path a = Paths.get("/scratch/zxc");
//		SerializablePath b = new SerializablePath(a);
//		boolean ret = createFolder(b);
//		System.out.printf("ret=%b%n",ret);
//	}
//
//	public static final int compareFilesByLastModifiedTimeAscending(Path p1, Path p2) {
//		try {
//			return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//	public static final int compareFilesByLastModifiedTimeDescending(Path p1, Path p2) {
//		return compareFilesByLastModifiedTimeAscending(p2,p1);
//	}
//
//	public static final void eraseAllButNLatestFilesByFilter(Path folder, int limit, Predicate<Path> filter) throws IOException {
//		List<Path> filesToDelete = loadDirectory(folder, (p,a)->filter.test(p)).stream().sorted(FilesHelper::compareFilesByLastModifiedTimeDescending).collect(Collectors.toList());
//		for (int index=limit;index<filesToDelete.size();index++) {
//			Files.delete(filesToDelete.get(index));
//		}
//	}
//
//	public static final Map<PosixFileAttributes,Object> readAllPosixFileAttributesMap(Path file,  LinkOption... options) throws IOException {
//		Map<String,Object> attributes = Files.readAttributes(delegate(file), "posix:*", options);
//		Map<PosixFileAttributes,Object> ret = new HashMap<>();
//		attributes.entrySet().forEach(e->{
//			PosixFileAttributes key = EnumsHelper.nullSafeValueOfOrNull(e.getKey(), PosixFileAttributes.values());
//			if (key!=null) {
//				ret.put(key, e.getValue());
//			}
//		});
//		return ret;
//	}
//	public static final PosixFileAttributesData readAllPosixFileAttributesData(Path file,  LinkOption... options) throws IOException {
//		return new PosixFileAttributesData(readAllPosixFileAttributesMap(file, options));
//	}
//
//	
	public static final InputStream newInputStreamOptionallyGzipped(Path path, OpenOption... options) throws IOException {
		if (isGzip(path)) {
			return newGzipInputStream(path, options);
		} else {
			return newInputStream(path,options);
		}
	}

	public static final InputStream newInputStreamOptionallyGzippedOrStdIn(Path path, OpenOption... options) throws IOException {
		if (path!=null) {
			return newInputStreamOptionallyGzipped(path,options);
		} else {
			return System.in;
		}
	}

	public static final BufferedReader newBufferedReaderOptionallyGzipped(Path path, OpenOption... options) throws IOException {
		return new BufferedReader(new InputStreamReader(newInputStreamOptionallyGzipped(path, options)));
	}

	public static final OutputStream newOutputStreamOptionallyGzipped(Path path, OpenOption... options) throws IOException {
		if (isGzip(path)) {
			return newGzipOutputStream(path, options);
		} else {
			return newOutputStream(path,options);
		}
	}
	
	public static final BufferedWriter newBufferedWriterOptionallyGzipped(Path path, OpenOption... options) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(newOutputStreamOptionallyGzipped(path, options)));
	}

	public static final PrintWriter openPrintWriterOrStdOutput(Path output) throws IOException {
		return newPrintWriterOrStdOutput(output);
	}

	public static final PrintWriter openPrintWriterOrNull(Path output) throws IOException {
		return newPrintWriterOrNull(output);
	}
	
	public static final PrintWriter newPrintWriterOrStdOutput(Path output) throws IOException {
		if (output!=null) {
			return new PrintWriter(FilesHelper.newBufferedWriterOptionallyGzipped(output));
		} else {
			return new PrintWriter(new WrappedPrintStreamIgnoreClose(System.out));
		}
	}

	public static final Pattern GZIP = Pattern.compile("(.+)\\.gz",Pattern.CASE_INSENSITIVE);

	public static final PrintWriter newPrintWriterOrNull(Path output) throws IOException {
		if (output!=null) {
			return new PrintWriter(FilesHelper.newBufferedWriterOptionallyGzipped(output));
		} else {
			return null;
		}
	}

	public static final boolean isGzip(Path path) {
		return GZIP.matcher(path.toString()).matches();
	}
	
	public static final Pattern FILE_EXTENTION = Pattern.compile("(.*)\\.[^\\.]*$"); 
	public static final Path replaceExtention(Path path, String newExtentionWithDot) {
		String fileName = path.getFileName().toString();
		Matcher matcher  = FILE_EXTENTION.matcher(fileName);
		if (matcher.matches()) {
			String nameWithoutExtention = matcher.group(1);
			return path.resolveSibling(nameWithoutExtention+newExtentionWithDot);
		} else {
			return path.resolveSibling(fileName+newExtentionWithDot);
		}
	}
}
