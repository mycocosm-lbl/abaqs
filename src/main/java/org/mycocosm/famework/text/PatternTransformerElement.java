package org.mycocosm.famework.text;

import java.io.Serializable;
import java.util.regex.Matcher;

public interface PatternTransformerElement extends Serializable {
	String getElement(Matcher matcher);
}
