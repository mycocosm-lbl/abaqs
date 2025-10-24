package org.mycocosm.abaqs.famework.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface RunnableCli {
	void runBatch(CommandLine options);
	default Options buildOptions() {
		return new Options();
	};
	default String getHelpHeader() {
		return "options:";
	}
	default String getHelpFooter() {
		return null;
	}
	default boolean autoUsage() {
		return false;
	}
}
