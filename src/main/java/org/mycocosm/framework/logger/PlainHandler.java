package org.mycocosm.framework.logger;

/*
 * This class is intentionally cop[ied into 2 projects: portal-apps-bootstrap and portal-framework; this is needed 
 * to handle several execution classpath cases
 * Also almost same but not same copy exists in portal-common, this is for reason. 
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class PlainHandler extends Handler {

	private Path logFile=null;

	public PlainHandler() {
		super();
	}
	public PlainHandler(Path logFile) {
		super();
		this.logFile=logFile;
	}

	@Override
	public void close() throws SecurityException {
	}
	@Override
	public void flush() {
	}

	private AtomicReference<Formatter> cachedFormatter = new AtomicReference<>();
	private Formatter getOrCreateFormatter() {

		Formatter ret = cachedFormatter.get();
		if (ret!=null) {
			return ret;
		} else {
			ret = getFormatter();
			if (ret==null) {
				ret = new PlainFormatter();
			}
			cachedFormatter.set(ret);
		}
		return ret;
	}

	private void printLogRecord(String message) {
		if (logFile!=null) {
			try (PrintWriter wr = new PrintWriter(Files.newOutputStream(logFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
				wr.println(message);
			} catch (IOException e) {
				System.err.println("exception in logger: "+e.getMessage());
				System.out.println(message);
			}
		} else {
			System.out.println(message);
		}
	}


	@Override
	public void publish(LogRecord record) {
		String message = getOrCreateFormatter().format(record);
		printLogRecord(message);
	}
}
