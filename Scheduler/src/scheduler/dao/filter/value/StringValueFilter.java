package scheduler.dao.filter.value;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import scheduler.dao.filter.ComparisonOperator;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface StringValueFilter extends ValueFilter<String>, Predicate<String> {
    
    static final Pattern PATTERN_REPL = Pattern.compile("[%\\_]");
    static final Pattern PATTERN_WC = Pattern.compile("([%_])|([^%_a-zA-Z\\d]+)");
    
    @Override
    public default void accept(PreparedStatement ps, int index) throws SQLException {
        ps.setString(index, get());
    }

    static boolean areEqual(StringValueFilter a, StringValueFilter b) {
        return (null == a) ? null == b : (null != b && a.get().equals(b.get()) && a.getOperator() == b.getOperator());
    }

    public static StringValueFilter ofEmptyString(boolean negate) {
        if (negate)
            return new StringValueFilter() {
                @Override
                public ComparisonOperator getOperator() { return ComparisonOperator.NOT_EQUALS; }
                @Override
                public boolean test(String t) { return null != t && !t.isEmpty(); }
                @Override
                public String get() { return ""; }
                @Override
                public int hashCode() { return (get().hashCode() << 4) & 2; }
                @Override
                public boolean equals(Object obj) {
                    return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                }
            };
        return new StringValueFilter() {
            @Override
            public ComparisonOperator getOperator() { return ComparisonOperator.EQUALS; }
            @Override
            public boolean test(String t) { return null != t && !t.isEmpty(); }
            @Override
            public String get() { return ""; }
            @Override
            public int hashCode() { return get().hashCode() << 4; }
            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
            }
        };
    }
    
    public static StringValueFilter ofWcPattern(String value, boolean negate) {
        if (value.isEmpty())
            return ofEmptyString(negate);
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_WC.matcher(value);
        while (matcher.find()) {
            if (null != matcher.group(2))
                matcher.appendReplacement(sb, Matcher.quoteReplacement("\\" + Pattern.quote(matcher.group(2))));
            else if (matcher.group(1).equals("*"))
                matcher.appendReplacement(sb, Matcher.quoteReplacement(".*?"));
            else
                matcher.appendReplacement(sb, Matcher.quoteReplacement("."));
        }
        matcher.appendTail(sb);
        if (negate)
            return new StringValueFilter() {
                private final Pattern pattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                @Override
                public ComparisonOperator getOperator() { return ComparisonOperator.NOT_LIKE; }
                @Override
                public boolean test(String t) { return null != t && pattern.matcher(t).find(); }
                @Override
                public String get() { return value; }
                @Override
                public int hashCode() { return (value.hashCode() << 4) & 12; }
                @Override
                public boolean equals(Object obj) {
                    return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                }
            };
        return new StringValueFilter() {
            private final Pattern pattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            @Override
            public ComparisonOperator getOperator() { return ComparisonOperator.LIKE; }
            @Override
            public boolean test(String t) { return null != t && pattern.matcher(t).find(); }
            @Override
            public String get() { return value; }
            @Override
            public int hashCode() { return (value.hashCode() << 4) & 11; }
            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
            }
        };
    }
    
    public static StringValueFilter of(String value) {
        if (value.isEmpty())
            return ofEmptyString(false);
        return new StringValueFilter() {
            @Override
            public ComparisonOperator getOperator() { return ComparisonOperator.EQUALS; }
            @Override
            public boolean test(String t) { return null != t && value.equals(t); }
            @Override
            public String get() { return value; }
            @Override
            public int hashCode() { return value.hashCode() << 4; }
            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
            }
        };
    }
    
    public static StringValueFilter ofNot(String value) {
        if (value.isEmpty())
            return ofEmptyString(false);
        return new StringValueFilter() {
            @Override
            public ComparisonOperator getOperator() { return ComparisonOperator.NOT_EQUALS; }
            @Override
            public boolean test(String t) { return null != t && !value.equals(t); }
            @Override
            public String get() { return value; }
            @Override
            public int hashCode() { return (value.hashCode() << 4) & 2; }
            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
            }
        };
    }
    
    public static StringValueFilter of(String value, ComparisonOperator operator) {
        String str = value.trim();
        if (str.isEmpty()) {
            switch (operator) {
                case LIKE:
                case EQUALS:
                    return ofEmptyString(false);
                case NOT_EQUALS:
                case NOT_LIKE:
                case GREATER_THAN:
                    return ofEmptyString(true);
                default:
                    throw new IllegalArgumentException(String.format("Operator %s cannot be applied to empty string values", operator));
            }
        }
        int h = 0;
        Objects.requireNonNull(operator);
        for (ComparisonOperator op : ComparisonOperator.values()) {
            if (op == operator) {
                break;
            }
            h++;
        }
        final int hashcode = (str.hashCode() << 4) | h;
        StringBuffer sb;
        switch (operator) {
            case LIKE:
                return ofWcPattern(str, false);
            case NOT_LIKE:
                return ofWcPattern(str, true);
            case EQUALS:
                return of(str);
            case NOT_EQUALS:
                return ofNot(str);
            case GREATER_THAN:
                return new StringValueFilter() {
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && str.compareTo(t) > 0; }
                    @Override
                    public String get() { return str; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            case NOT_LESS_THAN:
                return new StringValueFilter() {
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && str.compareTo(t) >= 0; }
                    @Override
                    public String get() { return str; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            case LESS_THAN:
                return new StringValueFilter() {
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && str.compareTo(t) < 0; }
                    @Override
                    public String get() { return str; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            case NOT_GREATER_THAN:
                return new StringValueFilter() {
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && str.compareTo(t) <= 0; }
                    @Override
                    public String get() { return str; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            case CONTAINS:
                sb = new StringBuffer();
                sb.append("%");
            case ENDS_WITH:
                sb = new StringBuffer();
                sb.append("%");
            default:
                sb = new StringBuffer();
                break;
        }
        
        Matcher matcher = PATTERN_REPL.matcher(str);
        while (matcher.find()) {
             matcher.appendReplacement(sb, Matcher.quoteReplacement("\\" + matcher.group()));
        }
        matcher.appendTail(sb);
        if (operator != ComparisonOperator.ENDS_WITH && operator != ComparisonOperator.EQUALS_CASE_INSENSITIVE)
            sb.append("%");
        String whereParameter = sb.toString();
        
        switch (operator) {
            case EQUALS_CASE_INSENSITIVE:
                return new StringValueFilter() {
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && t.equalsIgnoreCase(str); }
                    @Override
                    public String get() { return str; }
                    @Override
                    public void accept(PreparedStatement ps, int index) throws SQLException { ps.setString(index, whereParameter); }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            case NOT_EQUALS_CASE_INSENSITIVE:
                return new StringValueFilter() {
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && !t.equalsIgnoreCase(str); }
                    @Override
                    public String get() { return str; }
                    @Override
                    public void accept(PreparedStatement ps, int index) throws SQLException { ps.setString(index, whereParameter); }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            case STARTS_WITH:
                return new StringValueFilter() {
                    private final String lcValue = str.toLowerCase();
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && t.toLowerCase().startsWith(lcValue); }
                    @Override
                    public String get() { return str; }
                    @Override
                    public void accept(PreparedStatement ps, int index) throws SQLException { ps.setString(index, whereParameter); }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            case ENDS_WITH:
                return new StringValueFilter() {
                    private final String lcValue = str.toLowerCase();
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && t.toLowerCase().endsWith(lcValue); }
                    @Override
                    public String get() { return str; }
                    @Override
                    public void accept(PreparedStatement ps, int index) throws SQLException { ps.setString(index, whereParameter); }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
            default:
                return new StringValueFilter() {
                    private final String lcValue = str.toLowerCase();
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(String t) { return null != t && t.toLowerCase().contains(lcValue); }
                    @Override
                    public String get() { return str; }
                    @Override
                    public void accept(PreparedStatement ps, int index) throws SQLException { ps.setString(index, whereParameter); }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof StringValueFilter && areEqual(this, ((StringValueFilter)obj));
                    }
                };
        }
    }
       
    public static String encodeLikeString(String toEncode) {
        if (toEncode.trim().isEmpty())
            return toEncode;
        
        Matcher matcher = PATTERN_REPL.matcher(toEncode);
        if (!matcher.find())
            return toEncode;
        
        StringBuffer sb = new StringBuffer();
        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\" + matcher.group()));
        while (matcher.find()) {
             matcher.appendReplacement(sb, Matcher.quoteReplacement("\\" + matcher.group()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
