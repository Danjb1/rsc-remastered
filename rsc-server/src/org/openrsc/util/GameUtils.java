package org.openrsc.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    private static final char VALID_CHARS[] = { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+',
            '[', '{', ']', '}', ';', ':', '\'', '"', ',', '<', '.', '>', '/', '?', ' ', '|', '`', '~', '\\' };

    public GameUtils() {
        // ..
    }

    /**
     * @return The current calendar date, formatted as <code>Month ##, ####</code>
     */
    public static String getCalendarDate() {
        return Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " "
                + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * @return The current calendar time, formatted as <code>HH:MM.SS</code>
     */
    public static String getCalendarTime() {
        return Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + "."
                + (Calendar.getInstance().get(Calendar.SECOND) + 1);
    }

    /**
     * @return The current time in milliseconds (using nanosecond precision).
     */
    public static long getCurrentTimeMillis() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
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
        return "0";
    }

    /**
     * aA-zZ, 0-9, and VALID_CHARS[] are THE ONLY usable chat characters.
     */
    public static boolean isValidCharacter(char character) {
        if (Character.isLetterOrDigit(character)) {
            return true;
        }
        for (int i = 0; i < VALID_CHARS.length; i++) {
            if (character == VALID_CHARS[i]) {
                return true;
            }
        }
        return false;
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

}