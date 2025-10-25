package org.mycocosm.framework.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class PlainFormatter extends Formatter {

	private static final char DELIM = '|';
	private Format dateFormat = new SimpleDateFormat("yyyy-MM-dd"+DELIM+"HH:mm:ss.SSS");

	/* (non-Javadoc)
	 * @see gov.doe.jgi.portal.framework.logger.FormatterInterface#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		try {
			StringBuffer buf = new StringBuffer();
			buf.append(record.getLevel().getName());
			buf.append(DELIM);
			buf.append(dateFormat.format(new Date(record.getMillis())));
			buf.append(DELIM);
			buf.append(shorterName(record.getLoggerName()));
			buf.append(DELIM);
			buf.append(record.getLongThreadID());
			buf.append(DELIM);
			String message = record.getMessage();
			if (message!=null && message.length()>0) {
				Object[] parameters = record.getParameters();
				if (parameters!=null && parameters.length>0) {
					buf.append(MessageFormat.format(message,parameters));
				} else {
					buf.append(message);
				}
			}
			if (record.getThrown()!=null) {
				buf.append("\n");
				formatTrowable(buf, record.getThrown());
			}
			return buf.toString();
		} catch (Exception e) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter pr = new PrintWriter(os);
			pr.println("Exception during log formatting:");
			e.printStackTrace(pr);
			pr.close();
			return os.toString();
		}
	}
	private static final String shorterName(String name) {
		if (name!=null) {
			int pos = name.lastIndexOf('.');
			if (pos>=0) {
				if (pos==name.length()-1) {
					return ".";
				} else {
					return name.substring(pos+1);
				}
			} else {
				return name;
			}
		} else {
			return "";
		}
	}

	private void formatTrowable(StringBuffer buf, Throwable t) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(os);
		t.printStackTrace(pw);
		pw.close();
		buf.append(os.toString());
	}

}
