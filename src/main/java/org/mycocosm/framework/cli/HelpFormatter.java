package org.mycocosm.framework.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.help.AbstractHelpFormatter;
import org.apache.commons.cli.help.OptionFormatter;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextStyle;

public class HelpFormatter extends AbstractHelpFormatter {

	/**
	 * A builder for the HelpFormatter. Intended to make more complex uses of the HelpFormatter class easier. Default values are:
	 * <ul>
	 * <li>showSince = true</li>
	 * <li>helpAppendable = a {@link TextHelpAppendable} writing to {@code System.out}</li>
	 * <li>optionFormatter.Builder = the default {@link OptionFormatter.Builder}</li>
	 * </ul>
	 */
	public static class Builder extends AbstractHelpFormatter.Builder<Builder, HelpFormatter> {

		/** If {@code true} show the "Since" column, otherwise ignore it. */
		private boolean showSince = true;

		/**
		 * Constructs a new instace.
		 * <p>
		 * Sets {@code showSince} to {@code true}.
		 * </p>
		 */
		protected Builder() {
			// empty
		}

		@Override
		public HelpFormatter get() {
			return new HelpFormatter(this);
		}

		/**
		 * Sets the showSince flag.
		 *
		 * @param showSince the desired value of the showSince flag.
		 * @return {@code this} instance.
		 */
		public Builder setShowSince(final boolean showSince) {
			this.showSince = showSince;
			return this;
		}
	}

	/**
	 * Default number of characters per line: {@value}.
	 */
	public static final int DEFAULT_WIDTH = 74;

	/**
	 * Default padding to the left of each line: {@value}.
	 */
	public static final int DEFAULT_LEFT_PAD = 1;

	/**
	 * The default number of spaces between columns in the options table: {@value}.
	 */
	public static final int DEFAULT_COLUMN_SPACING = 5;

	/**
	 * Constructs a new builder.
	 *
	 * @return a new builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/** If {@code true} show the "Since" column, otherwise ignore it. */
	private final boolean showSince;

	/**
	 * Constructs the Help formatter.
	 *
	 * @param builder the Builder to build from.
	 */
	protected HelpFormatter(final Builder builder) {
		super(builder);
		this.showSince = builder.showSince;
	}

	/**
	 * Gets the table definition for the options.
	 *
	 * @param options the collection of {@link Option} instances to create the table from.
	 * @return A {@link TableDefinition} to display the options.
	 */
	@Override
	public TableDefinition getTableDefinition(final Iterable<Option> options) {
		// set up the base TextStyle for the columns configured for the Option opt and arg values.
		final TextStyle.Builder builder = TextStyle.builder().setAlignment(TextStyle.Alignment.LEFT).setIndent(DEFAULT_LEFT_PAD).setLeftPad(DEFAULT_LEFT_PAD).setScalable(false);
		final List<TextStyle> columnStyles = new ArrayList<>();
		columnStyles.add(builder.get()); // short
		columnStyles.add(builder.get()); // long
		// set up showSince column
//		builder.setScalable(false).setLeftPad(DEFAULT_COLUMN_SPACING);
//		if (showSince) {
//			builder.setAlignment(TextStyle.Alignment.CENTER);
//			columnStyles.add(builder.get());
//		}
//		// set up the description column.
//		builder.setAlignment(TextStyle.Alignment.LEFT);
		columnStyles.add(builder.get()); // description
		// setup the rows for the table.
		final List<List<String>> rows = new ArrayList<>();
		final StringBuilder sb = new StringBuilder();
		options.forEach(option -> {
			final List<String> row = new ArrayList<>();
			// create an option formatter to correctly format the parts of the option
			final OptionFormatter formatter = getOptionFormatBuilder().build(option);
			row.add(formatter.getOpt());
			sb.setLength(0);
			// append the opt values.
			sb.append(formatter.getLongOpt());
			// append the arg name if it exists.
			if (option.hasArg()) {
				sb.append(" ").append(formatter.getArgName());
			}
			row.add(sb.toString());
			// append the "since" value if desired.
			if (showSince) {
				row.add(formatter.getSince());
			}
			// add the option description
			row.add(formatter.getDescription());
			rows.add(row);
		});
		// return the TableDefinition with the proper column headers.
		return TableDefinition.from("", columnStyles, showSince ? Arrays.asList("","Options", "Since", "Description") : Arrays.asList("","Options", "Description"), rows);
	}
}
