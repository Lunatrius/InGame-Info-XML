package lunatrius.ingameinfo;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static String escapeValue(String str) {
		str = str.replace("\u00a7", "$");
		str = str.replaceAll("([<>\\[/\\]\\\\])", "\\\\$1");
		return str;
	}

	public static String unescapeValue(String str) {
		str = str.replaceAll("\\$(?=[0-9a-fk-or])", "\u00a7");
		str = str.replaceAll("\\\\([<>\\[/\\]\\\\])", "$1");
		return str;
	}

	public static String getPosition(String str) {
		Pattern pattern = Pattern.compile("(?i)(top|mid|bot).*?(left|center|right)");
		Matcher matcher = pattern.matcher(str);

		if (matcher.find()) {
			return String.format(Locale.ENGLISH, "%s%s", matcher.group(1), matcher.group(2)).toLowerCase();
		}

		return null;
	}
}
