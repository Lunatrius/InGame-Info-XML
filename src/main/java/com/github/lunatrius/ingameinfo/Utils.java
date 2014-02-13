package com.github.lunatrius.ingameinfo;

public class Utils {
	public static String escapeValue(String str, boolean isText) {
		str = str.replace("\u00a7", "$");
		if (isText) {
			str = str.replaceAll("([<>\\[/\\]\\\\])", "\\\\$1");
		}
		return str;
	}

	public static String unescapeValue(String str, boolean isText) {
		str = str.replaceAll("\\$(?=[0-9a-fk-or])", "\u00a7");
		if (isText) {
			str = str.replaceAll("\\\\([<>\\[/\\]\\\\])", "$1");
		}
		return str;
	}
}
