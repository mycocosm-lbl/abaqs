package org.mycocosm.framework.cli;

@FunctionalInterface
public interface CliValueConverter<V> {
	V convertToValue(String str, V defaultValue);
}
