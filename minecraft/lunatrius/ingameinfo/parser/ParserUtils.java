package lunatrius.ingameinfo.parser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
	public static String getPosition(String str) {
		Pattern pattern = Pattern.compile("(?i)(top|mid|bot).*?(left|center|right)");
		Matcher matcher = pattern.matcher(str);

		if (matcher.find()) {
			return String.format(Locale.ENGLISH, "%s%s", matcher.group(1), matcher.group(2)).toLowerCase();
		}

		return null;
	}
}
