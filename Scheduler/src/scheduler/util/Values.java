package scheduler.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for validating and normalizing values.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class Values {

    public static final Pattern REGEX_NON_NORMAL_WHITESPACES = Pattern.compile(" \\s+|(?! )\\s+");
    public static final Pattern REGEX_LINEBREAK = Pattern.compile("[\\r\\n]+");

    public static int compareTimeZones(TimeZone o1, TimeZone o2) {
        if (null == o1) {
            return (null == o2) ? 0 : 1;
        }
        if (null == o2) {
            return -1;
        }
        if (o1 == o2) {
            return 0;
        }
        int result = o1.getRawOffset() - o2.getRawOffset();
        if (result == 0 && (result = o1.getDisplayName().compareTo(o2.getDisplayName())) == 0) {
            ZoneId z1 = o1.toZoneId();
            ZoneId z2 = o2.toZoneId();
            if (null == z1) {
                return (null == z2) ? 0 : -1;
            }
            if (null == z2) {
                return 1;
            }
            Locale d = Locale.getDefault(Locale.Category.DISPLAY);
            if ((result = z1.getDisplayName(TextStyle.FULL, d).compareTo(z2.getDisplayName(TextStyle.FULL, d))) == 0
                    && (result = z1.getId().compareTo(z2.getId())) == 0) {
                return o1.getID().compareTo(o2.getID());
            }
        }
        return result;
    }
    
    public static int compareLocalDateTimeToTimestamp(LocalDateTime a, Timestamp b) {
        if (null == a) {
            return (null == b) ? 0 : 1;
        }
        if (null == b) {
            return -1;
        }
        return compare(a, DateTimeUtil.toLocalDateTime(b));
    }

    public static int compareTimestampToLocalDateTime(Timestamp a, LocalDateTime b) {
        if (null == a) {
            return (null == b) ? 0 : 1;
        }
        if (null == b) {
            return -1;
        }
        return compare(a, DateTimeUtil.toUtcTimestamp(b));
    }

    public static <T extends Comparable<? super T>> int compare(T a, T b) {
        if (null == a) {
            return (null == b) ? 0 : 1;
        }
        if (null == b) {
            return -1;
        }
        return (a == b) ? 0 : a.compareTo(b);
    }

    public static int compareLocaleCountryFirst(Locale o1, Locale o2) {
        if (null == o1) {
            return (null == o2) ? 0 : 1;
        }
        if (null == o2) {
            return -1;
        }
        if (o1 == o2) {
            return 0;
        }
        int result = o1.getDisplayCountry().compareTo(o2.getDisplayCountry());
        if (result == 0 && (result = o1.getDisplayLanguage().compareTo(o2.getDisplayLanguage())) == 0
                && (result = o1.getDisplayVariant().compareTo(o2.getDisplayVariant())) == 0) {
            return o1.getDisplayScript().compareTo(o2.getDisplayScript());
        }
        return result;
    }

    public static ArrayList<String> splitByChar(String source, char delimiter) {
        ArrayList<String> result = new ArrayList<>();
        if (null == source) {
            return result;
        }
        // ab_cd
        int b = source.indexOf(delimiter);
        if (b < 0) {
            result.add(source);
        } else {
            if (b == 0) {
                result.add("");
            } else {
                result.add(source.substring(0, b));
            }
            int e;
            b++;
            while (b < source.length() && (e = source.indexOf(delimiter, b)) > 0) {
                if (e == b) {
                    result.add("");
                } else {
                    result.add(source.substring(b, e));
                }
                b = e + 1;
            }
            if (b < source.length()) {
                result.add(source.substring(b));
            } else {
                result.add("");
            }
        }
        return result;
    }

    public static ArrayList<String> splitByText(String source, String delimiter) {
        ArrayList<String> result = new ArrayList<>();
        if (null == source) {
            return result;
        }
        // ab_cd
        int b = source.indexOf(delimiter);
        if (b < 0) {
            result.add(source);
        } else {
            if (b == 0) {
                result.add("");
            } else {
                result.add(source.substring(0, b));
            }
            int e;
            int i = delimiter.length();
            b += i;
            while (b < source.length() && (e = source.indexOf(delimiter, b)) > 0) {
                if (e == b) {
                    result.add("");
                } else {
                    result.add(source.substring(b, e));
                }
                b = e + i;
            }
            if (b < source.length()) {
                result.add(source.substring(b));
            } else {
                result.add("");
            }
        }
        return result;
    }

    /**
     * Ensures a {@link String} value is not null.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} if not null; otherwise, an empty {@link String}.
     */
    public static String emptyIfNull(String value) {
        return (null == value) ? "" : value;
    }

    /**
     * Composes a {@link Supplier} that returns the non-null value result of the source {@link Supplier} or an empty string.
     *
     * @param valueSupplier The source {@link Supplier}.
     * @return A non-null {@link String} value derived from the source {@code valueSupplier}.
     */
    public static Supplier<String> ifNull(Supplier<String> valueSupplier) {
        if (Objects.requireNonNull(valueSupplier) instanceof NonNullStringSupplier) {
            return valueSupplier;
        }
        return new NonNullStringSupplier(valueSupplier);
    }

    /**
     * Ensures a {@link String} value is not null and does not contain extraneous white space characters.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with extraneous white space characters removed if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndTrimmed(String value) {
        return (null == value) ? "" : value.trim();
    }

    /**
     * Composes a {@link Supplier} that returns the non-null, trimmed value result of the source {@link Supplier} or an empty string.
     *
     * @param valueSupplier The source {@link Supplier}.
     * @return A non-null, trimmed {@link String} value derived from the source {@code valueSupplier}.
     */
    public static Supplier<String> asNonNullAndTrimmed(Supplier<String> valueSupplier) {
        if (Objects.requireNonNull(valueSupplier) instanceof NonNullAndTrimmedSupplier) {
            return valueSupplier;
        }
        return new NonNullAndTrimmedSupplier(valueSupplier);
    }

    /**
     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive
     * whitespace characters will be replaced with a single space characters. Other whitespace characters will be replaced by a normal space
     * character.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndWsNormalized(String value) {
        if (null == value || (value = value.trim()).isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = REGEX_NON_NORMAL_WHITESPACES.matcher(value);
        if (matcher.find()) {
            do {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(" "));
            } while (matcher.find());
            matcher.appendTail(sb);
            return sb.toString();
        }
        return value;
    }

    /**
     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive
     * whitespace characters will be replaced with a single space characters. Other whitespace characters will be replaced by a normal space
     * character.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndWsNormalizedMultiLine(String value) {
        if (null == value || (value = value.trim()).isEmpty()) {
            return "";
        }

        String[] lines = REGEX_LINEBREAK.split(value);
        if (lines.length < 2)
            return asNonNullAndWsNormalized(value);
        StringBuffer sb = new StringBuffer(asNonNullAndWsNormalized(lines[0]));
        for (int i = 1; i < lines.length; i++) {
            sb.append("\n");
            Matcher matcher = REGEX_NON_NORMAL_WHITESPACES.matcher(value);
            if (matcher.find()) {
                do {
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(" "));
                } while (matcher.find());
                matcher.appendTail(sb);
            }
        }
        return sb.toString();
    }

    /**
     * Composes a {@link Supplier} that returns the non-null, whitespace-normalized result of the source {@link Supplier} or an empty string. Leading
     * and trailing whitespace will be removed. Consecutive whitespace characters will be replaced with a single space characters. Other whitespace
     * characters will be replaced by a normal space character.
     *
     * @param valueSupplier The source {@link Supplier}.
     * @return A non-null, whitespace-normalized {@link String} value derived from the source {@code valueSupplier}.
     */
    public static Supplier<String> asNonNullAndWsNormalized(Supplier<String> valueSupplier) {
        if (Objects.requireNonNull(valueSupplier) instanceof NonNullAndWsNormalizedSupplier) {
            return valueSupplier;
        }
        return new NonNullAndWsNormalizedSupplier(valueSupplier);
    }

    /**
     * Tests whether a string is null, empty or contains only white space characters.
     *
     * @param value The {@link String} to test.
     * @return {@code} true if the {@code value} is null, empty or contains only white space characters; otherwise {@code false} if it contains at
     * least one non-whitespace character.
     */
    public static boolean isNullWhiteSpaceOrEmpty(String value) {
        return (value == null || value.isEmpty() || value.codePoints().allMatch((c) -> Character.isWhitespace(c)));
    }

    /**
     * Tests whether a string is not null and contains at least one white space character.
     *
     * @param value The {@link String} to test.
     * @return {@code} true if the {@code value} is not null and contains at least one white space character; otherwise {@code false} is null, empty
     * or contains only white space characters.
     */
    public static boolean isNotNullWhiteSpaceOrEmpty(String value) {
        return !(value == null || value.isEmpty() || value.codePoints().allMatch((c) -> Character.isWhitespace(c)));
    }

    /**
     * Ensures a string is not null and contains at least one non-whitespace character or else returns a default value.
     *
     * @param sourceValue The source {@link String} value.
     * @param defaultValue The default {@link String} value to return if the {@code sourceValue} is null, empty or contains only whitespace
     * characters.
     * @return {@code sourceValue} if it is not null or empty and contains at least one non-whitespace character; otherwise the {@code defaultValue}
     * is returned.
     */
    public static String nonWhitespaceOrDefault(String sourceValue, String defaultValue) {
        return (isNullWhiteSpaceOrEmpty(sourceValue)) ? defaultValue : sourceValue;
    }

    /**
     * Ensures a string is not null and contains at least one non-whitespace character or else returns a default value.
     *
     * @param sourceValue The source {@link String} value.
     * @param defaultValueSupplier The {@link Supplier} that returns the default value.
     * @return {@code sourceValue} if it is not null or empty and contains at least one non-whitespace character; otherwise the result from the
     * {@code defaultValueSupplier} is returned.
     */
    public static String nonWhitespaceOrDefault(String sourceValue, Supplier<String> defaultValueSupplier) {
        return (isNullWhiteSpaceOrEmpty(sourceValue)) ? defaultValueSupplier.get() : sourceValue;
    }

    /**
     * Composes a {@link Supplier} that returns the non-whitespace result of the source supplier or a supplied default value.
     *
     * @param sourceSupplier The source {@link Supplier}.
     * @param defaultSupplier The {@link Supplier} to use if the {@code sourceSupplier} returns a null value or does not contain any non-whitespace
     * characters.
     * @return A {@link Supplier} that returns the non-whitespace result of the {@code sourceSupplier} or a value supplied by the
     * {@code defaultSupplier}.
     */
    public static Supplier<String> nonWhitespaceOrDefault(Supplier<String> sourceSupplier, Supplier<String> defaultSupplier) {
        Objects.requireNonNull(sourceSupplier);
        Objects.requireNonNull(defaultSupplier);
        return () -> {
            String s = sourceSupplier.get();
            return (isNullWhiteSpaceOrEmpty(s)) ? defaultSupplier.get() : s;
        };
    }

    /**
     * Ensures a string is not null, has extraneous white space removed, and contains at least one non-whitespace character or else returns a default
     * value.
     *
     * @param sourceValue The source {@link String} value.
     * @param defaultValue The default {@link String} value to return if the {@code sourceValue} is null, empty or contains only whitespace
     * characters.
     * @return {@code sourceValue} with extraneous white space removed if it is not null or empty and contains at least one non-whitespace character;
     * otherwise the {@code defaultValue} is returned.
     */
    public static String toNonWhitespaceTrimmedOrDefault(String sourceValue, String defaultValue) {
        return (null == sourceValue || (sourceValue = sourceValue.trim()).isEmpty()) ? defaultValue : sourceValue;
    }

    /**
     * Composes a {@link Supplier} that returns the non-whitespace, trimmed result of the source supplier or a supplied default value.
     *
     * @param sourceSupplier The source {@link Supplier}.
     * @param defaultSupplier The {@link Supplier} to use if the {@code sourceSupplier} returns a null value or does not contain any non-whitespace
     * characters.
     * @return A {@link Supplier} that returns the non-whitespace, trimmed result of the {@code sourceSupplier} or a value supplied by the
     * {@code defaultSupplier}.
     */
    public static Supplier<String> toNonWhitespaceTrimmedOrDefault(Supplier<String> sourceSupplier, Supplier<String> defaultSupplier) {
        Objects.requireNonNull(sourceSupplier);
        Objects.requireNonNull(defaultSupplier);
        return () -> {
            String s = sourceSupplier.get();
            return (null == s || (s = s.trim()).isEmpty()) ? defaultSupplier.get() : s;
        };
    }

    /**
     * Checks that the specified {@link String} is not {@code null} and contains at least one non-whitespace character.
     *
     * @param value The value to check for at least one non-whitespace character.
     * @param message The detail message to be used in the event that a {@code NullPointerException} or {@link AssertionError} is thrown.
     * @return {@code value} if not {@code null} and contains at least one non-whitespace character.
     * @throws NullPointerException if {@code value} is {@code null}.
     * @throws AssertionError if {@code value} does not contain at least one non-whitespace character.
     */
    public static String requireNonWhitespace(String value, String message) {
        if (Objects.requireNonNull(value, message).isEmpty() || !value.codePoints().anyMatch((c) -> !Character.isWhitespace(c))) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Checks that the specified {@link String} is not {@code null} and contains at least one non-whitespace character.
     *
     * @param value The value to check for at least one non-whitespace character.
     * @param messageSupplier The supplier of the detail message to be used in the event that a {@code NullPointerException} or {@link AssertionError}
     * is thrown.
     * @return {@code value} if not {@code null} and contains at least one non-whitespace character.
     * @throws NullPointerException if {@code value} is {@code null}.
     * @throws AssertionError if {@code value} does not contain at least one non-whitespace character.
     */
    public static String requireNonWhitespace(String value, Supplier<String> messageSupplier) {
        if (Objects.requireNonNull(value, messageSupplier).isEmpty() || !value.codePoints().anyMatch((c) -> !Character.isWhitespace(c))) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
    }

    private Values() {
    }

    private static class NonNullStringSupplier implements Supplier<String> {

        private final Supplier<String> baseSupplier;

        protected String getBase() {
            return baseSupplier.get();
        }

        NonNullStringSupplier(Supplier<String> source) {
            this.baseSupplier = Objects.requireNonNull(source);
        }

        @Override
        public String get() {
            return Values.emptyIfNull(baseSupplier.get());
        }
    }

    private static class NonNullAndTrimmedSupplier extends NonNullStringSupplier {

        NonNullAndTrimmedSupplier(Supplier<String> source) {
            super(source);
        }

        @Override
        public String get() {
            return asNonNullAndTrimmed(getBase());
        }
    }

    private static class NonNullAndWsNormalizedSupplier extends NonNullAndTrimmedSupplier {

        NonNullAndWsNormalizedSupplier(Supplier<String> source) {
            super(source);
        }

        @Override
        public String get() {
            String s = super.get();
            String[] ws;
            if (s.isEmpty() || (ws = REGEX_NON_NORMAL_WHITESPACES.split(s)).length == 1) {
                return s;
            }
            return String.join(" ", ws);
        }
    }

}
