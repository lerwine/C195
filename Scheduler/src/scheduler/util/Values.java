package scheduler.util;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Utility class for validating and normalizing values.
 *
 * @author lerwi
 */
public class Values {

    public static final Pattern REGEX_NON_NORMAL_WHITESPACES = Pattern.compile(" \\s+|(?! )\\s+");

    /**
     * Ensures a {@link String} value is not null.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNull(String value) {
        return (null == value) ? "" : value;
    }

    /**
     * Composes a {@link Supplier} that returns the non-null value result of the source {@link Supplier} or an empty string.
     *
     * @param valueSupplier The source {@link Supplier}.
     * @return A non-null {@link String} value derived from the source {@code valueSupplier}.
     */
    public static Supplier<String> asNonNull(Supplier<String> valueSupplier) {
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
        if (value == null) {
            return "";
        }
        String[] ws;
        if ((value = value.trim()).isEmpty() || (ws = REGEX_NON_NORMAL_WHITESPACES.split(value)).length == 1) {
            return value;
        }
        return String.join(" ", ws);
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
        assert !Objects.requireNonNull(value, message).isEmpty()
                && value.codePoints().anyMatch((c) -> !Character.isWhitespace(c)) : message;
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
        assert !Objects.requireNonNull(value, messageSupplier).isEmpty()
                && value.codePoints().anyMatch((c) -> !Character.isWhitespace(c)) : messageSupplier.get();
        return value;
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
            return asNonNull(baseSupplier.get());
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
