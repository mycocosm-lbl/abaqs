package org.mycocosm.ncbi;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mycocosm.framework.io.IoHelper;

public class GeneCodeHelper {

//	public static final Path GC_FILE_LOCATION = Path.of("/entrez/misc/data/gc.prt");
//
//	public static final String getGcFile() throws SocketException, IOException {
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		try {
//			NcbiHelper.getFTPFile(GC_FILE_LOCATION, os);
//		} finally {
//			os.close();
//		}
//		return os.toString();
//	}

	private static final Pattern GC_SPLIT = Pattern.compile("\\{([^\\}]+)\\}",Pattern.CASE_INSENSITIVE|Pattern.DOTALL|Pattern.MULTILINE); 

	public static final Map<Integer,GeneCode> loadAndParseGeneCodes(Logger logger, InputStream in) throws SocketException, IOException {
		String gcFile = IoHelper.loadStreamToString(in);
		Map<Integer,GeneCode> ret = new HashMap<>();
		Matcher matcher  = GC_SPLIT.matcher(gcFile);
		while (matcher.find()) {
			String section = matcher.group(1);
			GeneCode code = GeneCode.parseGeneCodeFromNcbiGCPrint(section); 
			ret.put(code.id,code);
		}
		return ret;
	}
}
