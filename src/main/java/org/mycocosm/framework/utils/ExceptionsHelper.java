package org.mycocosm.framework.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionsHelper {
	public static final Throwable getRootCause(Throwable e) {
		Throwable r = e.getCause();
		if (r!=null && !r.equals(e)) {
			return getRootCause(r);
		} else {
			return e;
		}
	}

	public static String getStackTrace(StackTraceElement stack[]) {
		StringBuffer sb = new StringBuffer();
		for (StackTraceElement element : stack) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();		
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}	

	public static final String getExceptionBody(Throwable t) {
		if (t!=null) {
			Throwable rootCause = getRootCause(t);
			StringWriter buffer = new StringWriter();
			PrintWriter writer = new PrintWriter(buffer); 
			if (rootCause==null) {
				writer.println("Root cause:");
				t.printStackTrace(writer);
				writer.println();
			} else {
				writer.println("Root cause:");
				rootCause.printStackTrace(writer);
				writer.println();
				if (!t.equals(rootCause)) {
					writer.println("Failure cause:");
					t.printStackTrace(writer);
					writer.println();
				}
			}
			writer.close();
			return buffer.toString();
		} else {
			return null;
		}
	}
	
	public static final String getRootCauseMessageOrClassName(Throwable t) {
		Throwable rootCause = getRootCause(t);
		if (rootCause.getMessage()!=null) {
			return rootCause.getMessage();
		} else {
			return rootCause.getClass().getName();
		}
	}

	public static final RuntimeException newRuntimeException(Throwable t, String format, Object... args) {
		return new RuntimeException(String.format(format, args),t);
	}
	public static final RuntimeException newRuntimeException(String format, Object... args) {
		return new RuntimeException(String.format(format, args));
	}
}
