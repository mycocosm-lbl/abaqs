package org.mycocosm.abaqs.famework.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.mycocosm.famework.text.TextHelper;
import org.mycocosm.famework.time.DateHelper;

//import gov.doe.jgi.portal.framework.text.TextHelper;
//import gov.doe.jgi.portal.framework.time.DateHelper;
//import gov.doe.jgi.portal.framework.time.TimeInterval;

public class CliOption<T>  {
	public final String name;
	public final String longName;
	public final boolean hasArgument;
	public final String description;
	public final boolean required;
	public final T defaultValue;
	public final T valueIfPresent;
	public final CliValueConverter<T> converter;
	public final Predicate<T> validator;
	
	public static final CliOption<String> requiredWithArgument(String name, String longName, String description) {
		return new CliOption<>(name, longName, true, description, true, null, (CliValueConverter<String>)null,null,null);
	}
	public static final CliOption<String> optionalWithArgument(String name, String longName, String description, String defaultValue) {
		return new CliOption<>(name, longName, true, description, false, defaultValue, (CliValueConverter<String>)null,null,null);
	}
	public static final CliOption<Boolean> optionalNoArgument(String name, String longName, String description) {
		return new CliOption<>(name, longName, false, description, false, Boolean.TRUE, (s,d)->s!=null,null,null);
	}
	public static final CliOption<Boolean> requiredBooleanWithArgument(String name, String longName, String description) {
		return new CliOption<Boolean>(name, longName, true, description, true, (Boolean)null, TextHelper::nullSafeToBooleanExtended,null,null);
	}
	public static final CliOption<Boolean> optionalBooleanWithArgument(String name, String longName, String description, Boolean defaultValue) {
		return new CliOption<Boolean>(name, longName, true, description, false, defaultValue, TextHelper::nullSafeToBooleanExtended,null,null);
	}
	public static final CliOption<Boolean> optionalBooleanNoArgument(String name, String longName, String description) {
		return new CliOption<Boolean>(name, longName, false, description, false, false, TextHelper::nullSafeToBooleanExtended,true,null);
	}
	public static final CliOption<String> requiredStringWithArgument(String name, String longName, String description) {
		return new CliOption<String>(name, longName, true, description, true, (String)null, CliOption::stringToStringValue,null,null);
	}
	
	public static final CliOption<String> optionalStringWithArgument(String name, String longName, String description, String defaultValue) {
		return new CliOption<String>(name, longName, true, description, false, defaultValue, CliOption::stringToStringValue,null,null);
	}
	public static final CliOption<String> optionalStringWithArgument(String name, String longName, String description) {
		return new CliOption<String>(name, longName, true, description, false, (String)null, CliOption::stringToStringValue,null,null);
	}
	
	public static final CliOption<Double> requiredDoubleWithArgument(String name, String longName, String description) {
		return new CliOption<Double>(Double::valueOf, name, longName, true, description, true, (Double)null,null);
	}

	public static final CliOption<Double> requiredDoubleWithArgument(String name, String longName, String description, Predicate<Double> validator) {
		return new CliOption<Double>(Double::valueOf, name, longName, true, description, true, (Double)null,validator);
	}

	public static final CliOption<Double> optionalDoubleWithArgument(String name, String longName, String description, Double defaultValue) {
		return new CliOption<Double>(Double::valueOf, name, longName, true, description, false, defaultValue,null);
	}

	public static final CliOption<Double> optionalDoubleWithArgument(String name, String longName, String description, Double defaultValue, Predicate<Double> validator) {
		return new CliOption<Double>(Double::valueOf, name, longName, true, description, false, defaultValue,validator);
	}

	public static final CliOption<Long> requiredLongWithArgument(String name, String longName, String description) {
		return new CliOption<Long>(Long::valueOf, name, longName, true, description, true, (Long)null,null);
	}

	public static final CliOption<Long> requiredLongWithArgument(String name, String longName, String description, Predicate<Long> validator) {
		return new CliOption<Long>(Long::valueOf, name, longName, true, description, true, (Long)null,validator);
	}

	public static final CliOption<Long> optionalLongWithArgument(String name, String longName, String description, Long defaultValue) {
		return new CliOption<Long>(Long::valueOf, name, longName, true, description, false, defaultValue,null);
	}

	public static final CliOption<Long> optionalLongWithArgument(String name, String longName, String description, Long defaultValue, Predicate<Long> validator) {
		return new CliOption<Long>(Long::valueOf, name, longName, true, description, false, defaultValue,validator);
	}

	public static final CliOption<Integer> requiredIntegerWithArgument(String name, String longName, String description) {
		return new CliOption<Integer>(Integer::valueOf, name, longName, true, description, true, (Integer)null,null);
	}

	public static final CliOption<Integer> requiredIntegerWithArgument(String name, String longName, String description, Predicate<Integer> validator) {
		return new CliOption<Integer>(Integer::valueOf, name, longName, true, description, true, (Integer)null,validator);
	}

	public static final CliOption<Integer> optionalIntegerWithArgument(String name, String longName, String description, Integer defaultValue) {
		return new CliOption<Integer>(Integer::valueOf, name, longName, true, description, false, defaultValue,null);
	}

	public static final CliOption<Integer> optionalIntegerWithArgument(String name, String longName, String description, Integer defaultValue, Predicate<Integer> validator) {
		return new CliOption<Integer>(Integer::valueOf, name, longName, true, description, false, defaultValue,validator);
	}

	public static final CliOption<Path> requiredPathWithArgument(String name, String longName, String description) {
		return new CliOption<Path>(Paths::get, name, longName, true, description, true, (Path)null,null);
	}
	
	public static final CliOption<Path> optionalPathWithArgument(String name, String longName, String description, Path defaultValue) {
		return new CliOption<Path>(Paths::get, name, longName, true, description, false, defaultValue,null);
	}

	public static final CliOption<Path> optionalPathWithArgument(String name, String longName, String description) {
		return new CliOption<Path>(Paths::get, name, longName, true, description, false, (Path)null,null);
	}

	public static final CliOption<LocalDate> optionalLocalDateWithArgument(String name, String longName, String description) {
		return new CliOption<LocalDate>(LocalDate::parse, name, longName, true, description, false, (LocalDate)null,null);
	}

	public static final CliOption<Pattern> requiredPatternWithArgument(String name, String longName, String description, int patternFlags) {
		return new CliOption<Pattern>(s->Pattern.compile(s,patternFlags), name, longName, true, description, true, null,null);
	}

	public static final CliOption<Pattern> requiredPatternWithArgument(String name, String longName, String description) {
		return new CliOption<Pattern>(s->Pattern.compile(s), name, longName, true, description, true, null,null);
	}

	public static final CliOption<Pattern> optionalPatternWithArgument(String name, String longName, String description, Pattern defaultValue, int patternFlags) {
		return new CliOption<Pattern>(s->Pattern.compile(s,patternFlags), name, longName, true, description, false, defaultValue,null);
	}
	
	public static final CliOption<Pattern> optionalPatternWithArgument(String name, String longName, String description, Pattern defaultValue) {
		return new CliOption<Pattern>(Pattern::compile, name, longName, true, description, false, defaultValue,null);
	}

	public static final CliOption<Pattern> optionalPatternWithArgument(String name, String longName, String description) {
		return new CliOption<Pattern>(Pattern::compile, name, longName, true, description, false, (Pattern)null,null);
	}
	
	public static final CliOption<Duration> optionalDurationWithArgument(String name, String longName, String description, Duration defaultValue) {
		return new CliOption<Duration>(DateHelper::nullSafeStringToDuration, name, longName, true, description, false, defaultValue,null);
	}
	
	public static final CliOption<LocalDateTime> optionalDateTimeWithArgument(String name, String longName, String description, LocalDateTime defaultValue) {
		return new CliOption<LocalDateTime>(s->LocalDateTime.from(DateHelper.nullSafeParceDateOptionalTime(s)), name, longName, true, description, false, defaultValue,null);
	}

	public static final Options buildCliOptions(CliOption<?>... options) {
		return CliHelper.buildCliOptions(options);
	}	
	
	private static final String stringToStringValue(String str, String defaultValue) {
		if (str==null) {
			return defaultValue;
		} else {
			return str;
		}
	}

	private static final <V> V stringToValue(String str, V defaultValue, Function<String, V> valueOf) {
		if (str==null) {
			return defaultValue;
		} else {
			return valueOf.apply(str);
		}
	}
	
	protected CliOption(String name, String longName, boolean hasArgument, String description, boolean required, T defaultValue, CliValueConverter<T> converter, T valueIfPresent, Predicate<T> validator) {
		this.name = name;
		this.longName = longName;
		this.hasArgument = hasArgument;
		this.description = description;
		this.required = required;
		this.defaultValue = defaultValue;
		this.converter = converter;
		this.valueIfPresent = valueIfPresent;
		this.validator = validator;
	}

	protected CliOption(Function<String,T> valueOf, String name, String longName, boolean hasArgument, String description, boolean required, T defaultValue, Predicate<T> validator) {
		this(name,longName,hasArgument,description,required,defaultValue,(s,d)->stringToValue(s, d, valueOf),null,validator);
	}
	
	
	public String optionString() {
		return "--"+longName;
	}
	
	public T getOptionValue(CommandLine options) {
		return CliHelper.getOptionValue(options, this); 
	}

	public List<T> getOptionValues(CommandLine options) {
		return CliHelper.getOptionValues(options, this); 
	}

}
