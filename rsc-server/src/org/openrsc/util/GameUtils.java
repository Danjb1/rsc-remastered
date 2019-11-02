package org.openrsc.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameUtils {

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    private static final char VALID_CHARS[] = { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', '{', ']', '}',
            ';', ':', '\'', '"', ',', '<', '.', '>', '/', '?', ' ', '|', '`', '~', '\\' };

	public enum OperatingSystem {
		WINDOWS, LINUX, MAC;
	}

	public GameUtils() {
		// ..
	}

	/**
	 * @return The operating system of the local machine.
	 */
	public static OperatingSystem getOperatingSystem() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().contains("windows")) {
			// Logger.getLogger(Platform.class.getName()).log(Level.INFO, "User is running " + osName);
			return OperatingSystem.WINDOWS;
		}
		if (osName.toLowerCase().contains("mac") || osName.toLowerCase().contains("os x") || (osName.toLowerCase().contains("darwin"))) {
			// Logger.getLogger(Platform.class.getName()).log(Level.INFO, "User is running " + osName);
			return OperatingSystem.MAC;
		}
		try {
			// Logger.getLogger(Platform.class.getName()).log(Level.INFO, "User is running " + execute("lsb_release -si") + "");
			return OperatingSystem.LINUX;
		} catch (Exception e) {
			throw new RuntimeException("Unable to determine operating system.", e);
		}
	}

	/**
	 * @return The current calendar date, formatted as <code>Month ##, ####</code>
	 */
	public static String getCalendarDate() {
		return Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * @return The current calendar time, formatted as <code>HH:MM.SS</code>
	 */
	public static String getCalendarTime() {
		return Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
	}

	/**
	 * @return The current time in milliseconds (using nanosecond precision).
	 */
	public static long getCurrentTimeMillis() {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
	}

	/**
	 * @return The total amount of memory on the local machine.
	 */
	public static String getSystemMemory() {
		try {
			// Try to access memory size using reflection.
			OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
			Method method = bean.getClass().getMethod("getTotalPhysicalMemorySize", new Class<?>[0]);
			method.setAccessible(true);
			return formatSize((Long) method.invoke(bean, new Object[0]));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		// return Runtime.getRuntime().totalMemory();
		return "NaN";
	}

	/**
	 * The amount of memory being used by the application.
	 */
	public static String getMemoryUsage() {
		//return (MemoryUtils.getDirectMemoryUsage() / 1000000) + "MB";
		return formatSize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	}
	/**
	 * Request a garbage collection, to free unallocated memory.
	 */
	public static String gc() {
		long usage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().gc();
		long cleanup = usage - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		if (cleanup > 0) {
			return formatSize(cleanup);
		}
		return "NaN";
	}

	/**
	 * aA-zZ, 0-9, and VALID_CHARS[] are THE ONLY usable chat characters.
	 */
	public static boolean isValidCharacter(char letterOrDigit) {
		if (Character.isLetterOrDigit(letterOrDigit)) {
			return true;
		}
		for (int i = 0; i < VALID_CHARS.length; i++) {
			if (letterOrDigit == VALID_CHARS[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the given String of text, by enabling a backspace feature, and by
	 * clipping the said text at the given maximum length.
	 */
	public static String formatInputString(String input, int keyCode, char keyChar, int clipLength, int backspaceKey) {
		if (keyCode == backspaceKey && input.length() > 0) {
			return input.substring(0, input.length() - 1);
		}
		if (isValidCharacter(keyChar) && input.length() + 1 <= clipLength) {
			return input += keyChar;
		}
		return input;
	}

	/**
	 * Replaces the input string with asterisks.
	 */
	public static String replaceWithAsterisks(String string) {
		StringBuilder asterisks = new StringBuilder();
		int l = string.length();
		for (int i = 0; i < l; i++) {
			asterisks.append("*");
		}
		return asterisks.toString();
	}

	/**
	 * Formats the given number with a more readable output, example: 9001 becomes
	 * 9,001.
	 */
	public static String formatNumber(int number) {
		return NumberFormat.getInstance().format(number);
	}

	/**
	 * @param millis
	 *            The millisecond time to be formatted.
	 * @return Returns the given millisecond time value as HH:MM:SS
	 */
	public static String formatTimeMillis(long millis) {
		if (millis < 1000) {
			return millis + "ms";
		}
		int s = (int) (millis / 1000) % 60;
		int m = (int) ((millis / (1000 * 60)) % 60);
		int h = (int) ((millis / (1000 * 60 * 60)) % 24);
		return h + ":" + m + ":" + s;
	}

	/**
	 * Formats the supplied size value from bytes to a more readable value.
	 */
	public static String formatSize(long bytes) {
		double kb = bytes / 1024.0;
		double mb = (kb / 1024.0);
		double gb = (mb / 1024.0);
		if (gb > 1) {
			return DECIMAL_FORMAT.format(gb).concat("GB");
		} else if (mb > 1) {
			return DECIMAL_FORMAT.format(mb).concat("MB");
		} else if (kb > 1) {
			return DECIMAL_FORMAT.format(kb).concat("KB");
		} else {
			return DECIMAL_FORMAT.format(bytes).concat("B");
		}
	}

	/**
	 * @see #formatSize(long)
	 */
	public static String formatSize(File file) {
		return formatSize(file.length());
	}

	/**
	 * String to Binary.
	 */
	public static String binaryEncode(String s) {
		byte[] sdata = s.getBytes(StandardCharsets.UTF_8);
		StringBuilder sb = new StringBuilder(sdata.length * (Byte.SIZE + 1));
		for (int i = 0; i < sdata.length; i++) {
			if (i != 0) {
				sb.append(' ');
			}
			byte b = sdata[i];
			for (int j = 0; j < Byte.SIZE; j++) {
				sb.append('0' + ((b >> (Byte.SIZE - j - 1))) & 1);
			}
		}
		return sb.toString();
	}

	/**
	 * Binary to String.
	 */
	public static String binaryDecode(String bs) {
		byte[] sdata = new byte[(bs.length() + 1) / (Byte.SIZE + 1)];
		Pattern bytegets = Pattern.compile("([01]{8})(?: |$)");
		Matcher bytegetsFinder = bytegets.matcher(bs);
		int offset = 0, i = 0;
		while (bytegetsFinder.find()) {
			if (bytegetsFinder.start() != offset) {
				throw new IllegalArgumentException();
			}
			sdata[i++] = (byte) Integer.parseInt(bytegetsFinder.group(1), 2);

			offset = bytegetsFinder.end();
		}
		if (offset != bs.length()) {
			throw new IllegalArgumentException();
		}
		return new String(sdata, StandardCharsets.UTF_8);
	}

}
