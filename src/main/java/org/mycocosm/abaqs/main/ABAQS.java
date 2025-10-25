package org.mycocosm.abaqs.main;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.mycocosm.framework.cli.BatchRunnableCli;
import org.mycocosm.framework.cli.CliHelper;
import org.mycocosm.framework.cli.CliOption;
import org.mycocosm.framework.logger.LogHelper;
import org.mycocosm.framework.logger.LoggerHelper;

public class ABAQS implements BatchRunnableCli {

	public static final CliOption<Boolean> VERBOSE = CliOption.optionalBooleanNoArgument("v", "verbose", "produce verbose output");
	public static final CliOption<Path> INPUT_GFF3 = CliOption.requiredPathWithArgument("i", "input-gff", "input gff3 file path");
	public static final CliOption<Path> OUTPUT = CliOption.optionalPathWithArgument("o", "output", "output path, optional, default to stdout");

	private final Logger logger = LogHelper.getLogger(ABAQS.class);

	public static final String VERSION = "0.1 preview"; 
	
	public static void main(String[] args) {
		CliHelper.runCli(ABAQS.class, args);
	}

	@Override
	public Options buildOptions() {
		return CliOption.buildCliOptions(VERBOSE,INPUT_GFF3,OUTPUT);
	}
	
	@Override
	public void runBatch(CommandLine options) {
		boolean verbose = VERBOSE.getOptionValue(options);
		Path input = INPUT_GFF3.getOptionValue(options);
		Path output = OUTPUT.getOptionValue(options);
		processInput(logger, input, output, verbose);
	}

	private void processInput(Logger logger, Path input, Path output, boolean verbose) {
		LoggerHelper.log(logger, Level.INFO, "Starting ABAQS version '%s'",VERSION);
		if (verbose) {
			showJava(logger);
		}
		LoggerHelper.log(logger, Level.INFO, "All done");
	}
	
	private static void showJava(Logger logger) {
		String runtimeName = System.getProperty("java.runtime.name","N/S");
		String runtimeVersion = System.getProperty("java.runtime.version","N/S");
		String vmName = System.getProperty("java.vm.name","N/S");
		String vmVersion = System.getProperty("java.version","N/S");
		String vmInfo = System.getProperty("java.vm.info","N/S");
		String javaVendor = System.getProperty("java.vendor","N/S");
		LoggerHelper.log(logger, Level.INFO,"Java Runtime: %s %s", runtimeName, runtimeVersion);
		LoggerHelper.log(logger, Level.INFO,"Java VM: %s %s %s", vmName, vmVersion, vmInfo);
		LoggerHelper.log(logger, Level.INFO,"Java Vendor: %s", javaVendor);
		long maxMemory = Runtime.getRuntime().maxMemory();
		if (Long.MAX_VALUE>=maxMemory) {
			LoggerHelper.log(logger, Level.INFO,"Java Memory: %,dM",maxMemory/(1024*1024));
		} else {
			LoggerHelper.log(logger, Level.INFO,"Java Memory: UNLIMITED");
		}
	}

}
