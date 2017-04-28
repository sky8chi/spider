package com.skyzd.framework.regex;

import java.util.regex.Matcher;

public interface ReplaceCallback {
	String replace(String txt, int index, Matcher matcher);
}
