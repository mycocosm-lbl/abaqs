package org.mycocosm.framework.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mycocosm.framework.collections.ArraysHelper;
import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.text.TextHelper;

public class CliHelper {
	public static final String BATCH_PROPERTY="gov.doe.jgi.portal.framework.Batch";
	public static final int UNLIMITED_NUMBER_OF_ARGUMENTS = Option.UNLIMITED_VALUES;
	public static Option createOption(String shortName, String fullName, boolean hasArgument, String description, boolean required) {
		Option ret = new Option(shortName, fullName, hasArgument, description);
		ret.setRequired(required);
		return ret;
	}

	public static OptionWithKey<Integer> createOptionWithKey(String shortName, String fullName, boolean hasArgument, String description, boolean required, Integer key) {
		OptionWithKey<Integer> ret = new OptionWithKey<>(shortName, fullName, hasArgument, description, key);
		ret.setRequired(required);
		return ret;
	}

	public static Option createOption(String shortName, String fullName, String description, int nArg) {
		Option ret = new Option(shortName, fullName, false, description);
		ret.setRequired(false);
		ret.setArgs(nArg);
		return ret;
	}

	public static final Options buildCliOptions(CliOption<?>... options) {
		return buildCliOptions(null, options);
	}

	public static final Options buildCliOptions(CliOption<?>[] base, CliOption<?>... options) {
		Options ret = new Options();
		int index=0;
		if (!ArraysHelper.isNullOrEmpty(base)) {
			for (CliOption<?> o:base) {
				ret.addOption(createOptionWithKey(o.name, o.longName, o.hasArgument, o.description, o.required, index++));
			}
		}
		if (options!=null) {
			for (CliOption<?> o:options) {
				ret.addOption(createOptionWithKey(o.name, o.longName, o.hasArgument, o.description, o.required, index++));
			}
		}
		return ret;
	}

	public static final Options buildCliOptions(Option... options) {
		Options ret = new Options();
		if (options!=null) {
			for (Option o:options) {
				ret.addOption(o);
			}
		}
		return ret;
	}

	public static final Options emptyCliOptions() {
		return new Options();
	}

	@SuppressWarnings("unchecked")
	public static final <T> List<T> getOptionValues(CommandLine cmd, CliOption<T> option) {
		List<T> ret = new ArrayList<>();
		if (option.hasArgument) {
			String[] valStr = cmd.getOptionValues(option.name);
			if (ArraysHelper.isNullOrEmpty(valStr)) {
				if (option.defaultValue!=null) {
					ret.add(option.defaultValue);
				}
			} else {
				for (String val:valStr) {
					if (val!=null) {
						if (option.converter==null) {
							ret.add((T)val);
						} else {
							ret.add(option.converter.convertToValue(val, null));
						}
					} else if (option.defaultValue!=null) {
						ret.add(option.defaultValue);
					}
				}
			}
		} else {
			if (cmd.hasOption(option.name)) {
				ret.add(option.valueIfPresent);
			} else {
				ret.add(option.defaultValue);
			}
		}
		if (option.validator!=null) {
			ret.forEach(val->{
				if (!option.validator.test(val)) {
					throw new IllegalArgumentException(String.format("Illegal value for argument %s:'%s'", option.longName,val));
				}
			});
		}
		return ret;
	}

	public static final <T> T getOptionValue(CommandLine cmd, CliOption<T> option) {
		return CollectionsHelper.firstOrNull(getOptionValues(cmd, option));
	}

	public static final boolean getBooleanOptionValue(CommandLine cmd, CliOption<String> option) {
		boolean defaultValue = TextHelper.nullSafeToBooleanExtended(option.defaultValue,false);
		return TextHelper.nullSafeToBooleanExtended(getOptionValue(cmd, option),defaultValue);
	}

	public static final CommandLine parseCli(RunnableCli process, String[] args) {
		Options options = process.buildOptions();
		if (options==null) {
			options = new Options();
		}
		DefaultParser parser = new DefaultParser();
		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			System.err.printf("Error parsing command line: %s%n", e.getMessage());
			printHelp(process, options);
			System.exit(-1);
			return null;
		}
	}

	public static final void printHelp(RunnableCli process, Options options) {
		try {
			HelpFormatter formatter = HelpFormatter.builder()
					.setComparator(new Comparator<Option>() {

						@SuppressWarnings("unchecked")
						@Override
						public int compare(Option o1, Option o2) {
							if (o1 instanceof OptionWithKey<?> && o2 instanceof OptionWithKey<?>) {
								Comparable<Object> key1 = ((OptionWithKey<? extends Comparable<Object>>)o1).getComparableKey();
								Comparable<Object> key2 = ((OptionWithKey<? extends Comparable<Object>>)o2).getComparableKey();
								return key1.compareTo(key2);
							} else {
								return o1.getKey().compareToIgnoreCase(o2.getKey());
							}
						}})
					.setShowSince(false)
					.get();
			String command = String.format("%n%s [options]%n", process.getClass().getCanonicalName());
			formatter.printHelp(command,process.getHelpHeader(),options,process.getHelpFooter(), process.autoUsage());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
