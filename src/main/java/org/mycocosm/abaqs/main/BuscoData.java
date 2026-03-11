package org.mycocosm.abaqs.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableInt;
import org.mycocosm.framework.io.FilesHelper;
import org.mycocosm.framework.text.PatternHelper;
import org.mycocosm.framework.utils.ExceptionsHelper;


public class BuscoData {
	// C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758
	public final double complete; // 0.0 to 1.0
	public final double singleCopy; // 0.0 to 1.0
	public final double duplicated; // 0.0 to 1.0
	public final double fragmented; // 0.0 to 1.0
	public final double missing; // 0.0 to 1.0
	public final int totalGroupsCount;
	private BuscoData(double complete, double singleCopy, double duplicated, double fragmented, double missing, int totalGroupsCount) {
		this.complete = complete;
		this.singleCopy = singleCopy;
		this.duplicated = duplicated;
		this.fragmented = fragmented;
		this.missing = missing;
		this.totalGroupsCount = totalGroupsCount;
	}
	
	/*
	 
	# BUSCO version is: 5.0.0 
	# The lineage dataset is: fungi_odb10 (Creation date: 2021-06-28, number of species: 549, number of BUSCOs: 758)
	# Summarized benchmarking in BUSCO notation for file /pscratch/sd/s/sharidas/genomeAssemblyStats/BUSCO_prot/P1/Aalte1.ExternalModels.prot.fasta
	# BUSCO was run in mode: proteins

	        ***** Results: *****

	        C:99.2%[S:99.1%,D:0.1%],F:0.5%,M:0.3%,n:758        
	        752     Complete BUSCOs (C)                        
	        751     Complete and single-copy BUSCOs (S)        
	        1       Complete and duplicated BUSCOs (D)         
	        4       Fragmented BUSCOs (F)                      
	        2       Missing BUSCOs (M)                         
	        758     Total BUSCO groups searched     
	*/

	private static final Pattern BUSCO_LINE = Pattern.compile( 	"\\s*(\\d+)\\s+(.+)");
	protected static final BuscoData ofFile(Path file) throws IOException {
		MutableInt totalGroupsCount = new MutableInt();
		MutableInt complete = new MutableInt();
		MutableInt singleCopy = new MutableInt();
		MutableInt duplicated = new MutableInt();
		MutableInt fragmented = new MutableInt();
		MutableInt missing = new MutableInt();

		try (BufferedReader reader=FilesHelper.newBufferedReaderOptionallyGzipped(file)) {
			reader.lines().forEach(line->{
				if (!PatternHelper.COMMENT_LINE.matcher(line).matches()) {
					Matcher matcher = BUSCO_LINE.matcher(line);
					if (matcher.matches()) {
						int value = Integer.valueOf(matcher.group(1));
						String category = matcher.group(2).toLowerCase();
						if (category.contains(" (c)")) {
							complete.setValue(value);
						} else if (category.contains(" (s)")) {
							singleCopy.setValue(value);
						} else if (category.contains(" (d)")) {
							duplicated.setValue(value);
						} else if (category.contains(" (f)")) {
							fragmented.setValue(value);
						} else if (category.contains(" (m)")) {
							missing.setValue(value);
						} else if (category.contains("total busco ")) {
							totalGroupsCount.setValue(value);
						}
					}
				}
			});
		}
		return new BuscoData(
				complete.doubleValue()/totalGroupsCount.doubleValue(),
				singleCopy.doubleValue()/totalGroupsCount.doubleValue(),
				duplicated.doubleValue()/totalGroupsCount.doubleValue(),
				fragmented.doubleValue()/totalGroupsCount.doubleValue(),
				missing.doubleValue()/totalGroupsCount.doubleValue(),
				totalGroupsCount.intValue());
	}

	private static final Pattern BUSCO_CATEGORY = Pattern.compile("([CSDFMn]):([\\d\\.]+)(%?)",Pattern.CASE_INSENSITIVE);
	protected static final BuscoData of(String string) {
		int totalCount = 0;
		double complete = 0.0;
		double singleCopy = 0.0;
		double duplicated = 0.0;
		double fragmented = 0.0;
		double missing = 0.0;
		
		// Lets do the total number first
		Matcher matcher = BUSCO_CATEGORY.matcher(string);
		while (matcher.find()) {
			switch (matcher.group(1)) {
			case "N":
			case "n":
				totalCount = Integer.valueOf(matcher.group(2));
				break;
			}
		}
		// Now will do the rest
		matcher = BUSCO_CATEGORY.matcher(string);
		while (matcher.find()) {
			switch (matcher.group(1)) {
			case "C":
			case "c":
				complete = loadBuscoValue(matcher.group(2),matcher.group(3),totalCount,string);
				break;
			case "S":
			case "s":
				singleCopy = loadBuscoValue(matcher.group(2),matcher.group(3),totalCount,string);
				break;
			case "D":
			case "d":
				duplicated = loadBuscoValue(matcher.group(2),matcher.group(3),totalCount,string);
				break;
			case "F":
			case "f":
				fragmented = loadBuscoValue(matcher.group(2),matcher.group(3),totalCount,string);
				break;
			case "M":
			case "m":
				missing = loadBuscoValue(matcher.group(2),matcher.group(3),totalCount,string);
				break;
			}
		}
		return new BuscoData(complete, singleCopy, duplicated, fragmented, missing, totalCount);
	}
	private static double loadBuscoValue(String val, String pct, long totalCount, String str) {
		if (pct!=null) {
			return Double.valueOf(val) / 100.0;
		} else if (totalCount>0){
			return Double.valueOf(val) / (double) totalCount;
		} else {
			throw ExceptionsHelper.newRuntimeException("Illegal BUSCO input: '%s' - missing total count", str);
		}
	}
	
	
}
