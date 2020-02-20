package scheduler.dao;

import java.util.function.Supplier;
import scheduler.util.Values;

/**
 *
 * @author lerwi
 */
public interface StringFilter extends Supplier<String> {

    String getOperator();

    public static StringFilter of(String value, String operator) {
        if (null == value) {
            return of("", operator);
        }
        if (null == operator || operator.isEmpty()) {
            return of(value, Values.OPERATOR_NONE);
        }
        switch (operator) {
            case Values.OPERATOR_STARTS_WITH:
            case Values.OPERATOR_ENDS_WITH:
            case Values.OPERATOR_CONTAINS:
            case Values.OPERATOR_LIKE:
                if (value.isEmpty()) {
                    return of(value, Values.OPERATOR_EQUALS);
                }
                break;
            case Values.OPERATOR_GREATER_THAN:
            case Values.OPERATOR_NOT_GREATER_THAN:
            case Values.OPERATOR_LESS_THAN:
            case Values.OPERATOR_NOT_LESS_THAN:
            case Values.OPERATOR_NOT_LIKE:
                if (value.isEmpty()) {
                    return of(value, Values.OPERATOR_NOT_EQUALS);
                }
                break;
            case Values.OPERATOR_NONE:
                if (!value.isEmpty()) {
                    return of(value, Values.OPERATOR_EQUALS);
                }
                break;
            case Values.OPERATOR_EQUALS:
            case Values.OPERATOR_NOT_EQUALS:
                break;
            default:
                throw new AssertionError("Unknown operator");
        }
        return new StringFilter() {
            @Override
            public String get() {
                return value;
            }

            @Override
            public String getOperator() {
                return operator;
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof StringFilter) {
                    StringFilter other = (StringFilter) obj;
                    return getOperator().equals(other.getOperator()) && get().equals(other.get());
                }
                return false;
            }

            @Override
            public int hashCode() {
                return (getOperator() + get()).hashCode();
            }
        };
    }

    public static StringFilter isEqualTo(String value, boolean caseSensitive) {
        return of(value, (null == value || value.isEmpty() || caseSensitive) ? Values.OPERATOR_EQUALS : Values.OPERATOR_LIKE);
    }

    public static StringFilter notEqualTo(String value, boolean caseSensitive) {
        return of(value, (null == value || value.isEmpty() || caseSensitive) ? Values.OPERATOR_NOT_EQUALS : Values.OPERATOR_NOT_LIKE);
    }

    public static StringFilter startsWith(String value) {
        return of(value, Values.OPERATOR_STARTS_WITH);
    }

    public static StringFilter endsWith(String value) {
        return of(value, Values.OPERATOR_ENDS_WITH);
    }

    public static StringFilter contains(String value) {
        return of(value, Values.OPERATOR_CONTAINS);
    }

    public static StringFilter empty() {
        return of("", Values.OPERATOR_NONE);
    }
}
