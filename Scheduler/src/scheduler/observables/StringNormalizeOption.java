package scheduler.observables;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum StringNormalizeOption {
    REPLACE_NULL_ONLY(StringNormalizeOption::replaceNull, StringNormalizeOption::isNullOrEmpty),
    TRIM(StringNormalizeOption::trim, StringNormalizeOption::isNullOrWhiteSpace),
    NORMALIZE_WHITESPACE(StringNormalizeOption::normalizeWhiteSpace, StringNormalizeOption::isNullOrWhiteSpace);
    public static final Pattern WS_PATTERN = Pattern.compile("^\\s+$");
    public static final Pattern NORMALIZE_WS_PATTERN = Pattern.compile(" \\s+|(?! )\\s+");

    public static String replaceNull(String value) {
        return (null == value) ? "" : value;
    }

    public static String trim(String value) {
        return (null == value || value.isEmpty()) ? "" : value.trim();
    }

    public static String normalizeWhiteSpace(String value) {
        if (null == value || (value = value.trim()).isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = NORMALIZE_WS_PATTERN.matcher(value);
        if (matcher.find()) {
            do {
                matcher.appendReplacement(sb, " ");
            } while (matcher.find());
            matcher.appendTail(sb);
            return sb.toString();
        }
        return value;
    }

    public static boolean isNullOrEmpty(String value) {
        return null == value || value.isEmpty();
    }
    
    public static boolean isNullOrWhiteSpace(String value) {
        return null == value || value.isEmpty() || WS_PATTERN.matcher(value).find();
    }
    
    private final UnaryOperator<String> operator;
    private final Predicate<String> emptyPredicate;

    private StringNormalizeOption(UnaryOperator<String> operator, Predicate<String> emptyPredicate) {
        this.operator = operator;
        this.emptyPredicate = emptyPredicate;
    }

    public UnaryOperator<String> getOperator() {
        return operator;
    }

    public Predicate<String> getEmptyPredicate() {
        return emptyPredicate;
    }

    public String apply(String value) {
        return operator.apply(value);
    }
    
    public boolean testEmpty(String value) {
        return emptyPredicate.test(value);
    }

}
