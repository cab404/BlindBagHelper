package me.katefiore.mlpblindbaghelper.base;

/**
 * Некоторые утилиты, скопированные из Moonlight
 * <p/>
 * <a href="https://github.com/cab404/moonlight/blob/dev/src/com/cab404/moonlight/util/SU.java">(отсюда)</a>
 *
 * @author cab404
 */
public class StringUtils {

	public static int count(CharSequence seq, char ch) {
		int counter = 0;
		for (int i = 0; i < seq.length(); i++)
			if (seq.charAt(i) == ch)
				counter++;
		return counter;
	}


	public static boolean fast_match(String regex, String data) {
		int count = count(regex, '*');

		if (count == 0)
			return data.equals(regex);

		String[] strings = splitToArray(regex, '*');

		if (!(data.startsWith(strings[0])) && data.endsWith(strings[strings.length - 1]))
			return false;

		int s, f = 0;

		for (String str : strings) {
			s = data.indexOf(str, f);
			f = s + str.length();

			if (s == -1)
				return false;
		}

		return true;
	}

	public static String[] splitToArray(String source, char ch) {
		return splitToArray(source, source.length() + 1, ch);
	}


	public static String[] splitToArray(String source, int limit, char ch) {
		int occurences = 0;

		occurences += count(source, ch);

		String[] out = new String[(occurences + 1) > limit ? limit : (occurences + 1)];
		int last = 0;
		int array_index = 0;

		for (int i = 0; i < source.length(); i++) {
			if (ch == source.charAt(i)) {
				out[array_index++] = source.substring(last, i);
				last = i + 1;

				if (array_index + 1 == limit)
					break;

			}
		}

		out[array_index] = source.substring(last);

		return out;
	}
}
