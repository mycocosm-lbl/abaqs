package org.mycocosm.framework.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public final class IoHelper {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    public static final byte[] readInputStream(InputStream inx) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream(); InputStream in = inx) {
			copyStream(in, os, new byte[DEFAULT_BUFFER_SIZE]);
			return os.toByteArray();
		}
	}


    public static long copyStream(final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
	
	public static final String asUTF8String(byte[] buf) {
		return new String(buf,StandardCharsets.UTF_8);
	}

	public static final String readFile(String path) throws IOException {
		return loadFileToString(path);
	}

	public static final String loadStreamToString(InputStream in) throws IOException {
		return asUTF8String(readInputStream(in));
	}

	public static final String loadFileToString(String path) throws IOException {
		return loadFileToString(new File(path));
	}

	public static final String loadFileToString(File file) throws IOException {
		return asUTF8String(readInputStream(Files.newInputStream(file.toPath())));
	}

	public static final String loadPathToString(Path path) throws IOException {
		return asUTF8String(readInputStream(Files.newInputStream(path)));
	}

	public static final byte[] loadPath(Path path) throws IOException {
		return readInputStream(Files.newInputStream(path));
	}

	public static final void saveStringToFile(File file, String data) throws IOException {
		try (OutputStream os = Files.newOutputStream(file.toPath())) {
			os.write(data.getBytes(StandardCharsets.UTF_8));
		}
	}

	public static final void saveStringToFile(Path path, String data) throws IOException {
		try (OutputStream os = Files.newOutputStream(path)) {
			os.write(data.getBytes(StandardCharsets.UTF_8));
		}
	}

	private static final int COPY_BUFFER_SIZE = 10_000;
	public static void copy(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[COPY_BUFFER_SIZE];
		int read = is.read(buf);
		while (read>0) {
			os.write(buf, 0, read);
			read = is.read(buf);
		}
	}

	public static void copyToPath(InputStream is, Path out) throws IOException {
		try (OutputStream os = FilesHelper.newOutputStream(out)) {
			copy(is,os);
		}
	}

	public static void copyFromPath(Path in, OutputStream os) throws IOException {
		try (InputStream is = FilesHelper.newInputStream(in)) {
			copy(is,os);
		}
	}

	public static void copyToPathOptionallyGzipped(InputStream is, Path out) throws IOException {
		try (OutputStream os = FilesHelper.newOutputStreamOptionallyGzipped(out)) {
			copy(is,os);
		}
	}
	
	public static final byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
			oos.writeObject(object);
		}
		return os.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public static final <T> T bytesToObject(Class<T> clasz, byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		try (ObjectInputStream ois = new ObjectInputStream(is)) {
			return (T) ois.readObject();
		}
	}

	public static final int bytesPerIntArrayEntry(int size) {
		if ((size & ~0x000000ff) == 0) {
			return 1;
		} else if ((size & ~0x0000ffff) == 0) {
			return 2;
		} else if ((size & ~0x00ffffff) == 0) {
			return 3;
		} else {
			return 4;
		}
	}

	public static final int bytesPerLongArrayEntry(long size) {
		if ((size & ~0x00000000000000ffL) == 0) {
			return 1;
		} else if ((size & ~0x000000000000ffffL) == 0) {
			return 2;
		} else if ((size & ~0x0000000000ffffffL) == 0) {
			return 3;
		} else if ((size & ~0x00000000ffffffffL) == 0) {
			return 4;
		} else if ((size & ~0x000000ffffffffffL) == 0) {
			return 5;
		} else if ((size & ~0x0000ffffffffffffL) == 0) {
			return 6;
		} else if ((size & ~0x00ffffffffffffffL) == 0) {
			return 7;
		} else {
			return 8;
		}
	}


	public static final void intToBytes(int i, ByteBuffer buf) {
		byte[] conv = buf.array();
		switch (conv.length) {
		case 1:
			conv[0] = (byte) i;
			return;
		case 2:
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		case 3:
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		default:
			conv[3] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		}
	}

	public static final byte[] intToBytesFixed(int i) {
		byte[] conv = new byte[Integer.BYTES];
		conv[3] = (byte) (i & 0x0ff);
		i >>= 8;
		conv[2] = (byte) (i & 0x0ff);
		i >>= 8;
		conv[1] = (byte) (i & 0x0ff);
		i >>= 8;
		conv[0] = (byte) i;
		return conv;
	}

	public static final void longToBytes(long i, ByteBuffer buf) {
		byte[] conv = buf.array();
		switch (conv.length) {
		case 1:
			conv[0] = (byte) i;
			return;
		case 2:
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		case 3:
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		case 4:
			conv[3] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		case 5:
			conv[4] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[3] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		case 6:
			conv[5] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[4] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[3] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		case 7:
			conv[6] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[5] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[4] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[3] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		default:
			conv[7] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[6] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[5] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[4] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[3] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[2] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[1] = (byte) (i & 0x0ff);
			i >>= 8;
			conv[0] = (byte) i;
			return;
		}
	}

	public static final int bytesToInt(ByteBuffer buf) {
		return bytesToInt(buf.array());
	}

	public static final int bytesToInt(byte[] conv) {
		switch (conv.length) {
		case 1:
			return conv[0] & 0xFF;
		case 2:
			return (conv[0] << 8 | (conv[1] & 0xff)) & 0xFFFF;
		case 3:
			return (conv[0] << 16 | (conv[1] & 0xff) << 8 | (conv[2] & 0xff)) & 0xFFFFFF;
		default:
			return conv[0] << 24 | (conv[1] & 0xff) << 16 | (conv[2] & 0xff) << 8 | (conv[3] & 0xff);
		}
	}
	
	public static final long bytesToLong(ByteBuffer buf) {
		byte[] conv = buf.array();
		switch (conv.length) {
		case 1:
			return conv[0] & 0xffL;
		case 2:
			return (conv[0] << 8 | (conv[1] & 0xffL)) & 0xffffL;
		case 3:
			return (conv[0] << 16 | (conv[1] & 0xffL) << 8 | (conv[2] & 0xffL)) & 0xffffffL;
		case 4:
			return (conv[0] << 24 | (conv[1] & 0xffL) << 16 | (conv[2] & 0xffL) << 8 | (conv[3] & 0xffL)) & 0xffffffffL;
		case 5:
			return ((long)conv[0] << 32 | (conv[1] & 0xffL) << 24 | (conv[2] & 0xffL) << 16 | (conv[3] & 0xffL) << 8 | (conv[4] & 0xffL)) & 0xffffffffffL;
		case 6:
			return ((long)conv[0] << 40 | (conv[1] & 0xffL) << 32 | (conv[2] & 0xffL) << 24 | (conv[3] & 0xffL) << 16 | (conv[4] & 0xffL) << 8 | (conv[5] & 0xffL)) & 0xffffffffffffL;
		case 7:
			return ((long)conv[0] << 48 | (conv[1] & 0xffL) << 40 | (conv[2] & 0xffL) << 32 | (conv[3] & 0xffL) << 24 | (conv[4] & 0xffL) << 16 | (conv[5] & 0xffL) << 8 | (conv[6] & 0xffL)) & 0xffffffffffffffL;
		default:
			return ((long)conv[0] << 56 | (conv[1] & 0xffL) << 48 | (conv[2] & 0xffL) << 40 | (conv[3] & 0xffL) << 32 | (conv[4] & 0xffL) << 24 | (conv[5] & 0xffL) << 16 | (conv[6] & 0xffL) << 8 | (conv[7] & 0xffL)) & 0xffffffffffffffffL;
		}
	}

	public static final void gzip(Path path) throws IOException {
		String outputName = path.getFileName().toString()+".gz";
		String tempName = path.getFileName().toString()+".gz~";
		Path tempPath = path.resolveSibling(tempName);
		try (InputStream is = FilesHelper.newInputStream(path); OutputStream os = new GZIPOutputStream(FilesHelper.newOutputStream(tempPath, StandardOpenOption.CREATE_NEW,StandardOpenOption.TRUNCATE_EXISTING))) {
			copy(is, os);
		}
		Path outputPath = path.resolveSibling(outputName);
		Files.move(tempPath,outputPath,StandardCopyOption.REPLACE_EXISTING);
		Files.delete(path);
	}



	public static void copy(FileChannel input, FileChannel output) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);
		int read = input.read(buffer);
		while (read>0) {
			buffer.flip();
			output.write(buffer);
			buffer.rewind();
			read = input.read(buffer);
		}
	}

	public static final void write(OutputStream out, String str) throws IOException {
		if (str!=null && str.length()>0) {
			out.write(str.getBytes());
		}
	}
	public static final void write(OutputStream out, String str, Charset cs) throws IOException {
		if (str!=null && str.length()>0) {
			out.write(str.getBytes(cs));
		}
	}
	public static final void write(OutputStream out, char chr) throws IOException {
		out.write(chr);
	}
	public static final void printf(OutputStream out, Charset cs, String format, Object... args) throws IOException {
		out.write(String.format(format, args).getBytes(cs));
	}
	public static final void printf(OutputStream out, String format, Object... args) throws IOException {
		out.write(String.format(format, args).getBytes());
	}

	public static final void writeIntToByteChannel(int i, ByteChannel ch) throws IOException {
		ch.write(ByteBuffer.wrap(intToBytesFixed(i)));
	}
	public static final int readIntFromByteChannel(ByteChannel ch) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
		ch.read(buf);
		return bytesToInt(buf);
	}

	public static final String readFirstLine(Path input) throws IOException {
		try (BufferedReader rd = new BufferedReader(FilesHelper.newReader(input))) {
			return rd.readLine();
		}
	}
	
	public static final InputStream openResourceStream(Path resource) throws IOException {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource.toString());
	}

	public static final void eachLine(Path path, Consumer<String> consumer) throws IOException {
		eachLine(FilesHelper.newReader(path), consumer);
	}

	public static final void eachLine(Reader r, Consumer<String> consumer) throws IOException {
		try (BufferedReader reader = new BufferedReader(r)) {
			String line = reader.readLine();
			while(line!=null) {
				consumer.accept(line);
				line = reader.readLine();
			}
		}
	}

	public static final void parseEachLine(Path path, Pattern pattern, Consumer<Matcher> consumer) throws IOException {
		parseEachLine(FilesHelper.newReader(path), pattern, consumer);
	}
	public static final void parseEachLine(Reader r, Pattern pattern, Consumer<Matcher> consumer) throws IOException {
		try (BufferedReader reader = new BufferedReader(r)) {
			String line = reader.readLine();
			while(line!=null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.matches()) {
					consumer.accept(matcher);
				}
				line = reader.readLine();
			}
		}
	}

}

