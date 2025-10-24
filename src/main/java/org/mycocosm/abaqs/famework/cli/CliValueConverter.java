package org.mycocosm.abaqs.famework.cli;

@FunctionalInterface
public interface CliValueConverter<V> {
	V convertToValue(String str, V defaultValue);
}
