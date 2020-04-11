package scheduler.dao.filter.value;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.function.Predicate;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.util.DB;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface TimestampValueFilter<T extends Temporal> extends ValueFilter<Timestamp>, Predicate<Timestamp> {

    T toTemporalValue();

    @Override
    public default void accept(PreparedStatement ps, int index) throws SQLException {
        ps.setTimestamp(index, get());
    }

    boolean testTemporal(T value);

    static boolean areEqual(TimestampValueFilter<? extends Temporal> a, TimestampValueFilter<? extends Temporal> b) {
        return (null == a) ? null == b : (null != b && a.get().equals(b.get()) && a.getOperator() == b.getOperator());
    }

    public static TimestampValueFilter<LocalDateTime> of(Timestamp value, ComparisonOperator operator) {
        int h = 0;
        Objects.requireNonNull(operator);
        for (ComparisonOperator op : ComparisonOperator.values()) {
            if (op == operator) {
                break;
            }
            h++;
        }
        final int hashcode = (value.hashCode() << 4) | h;
        switch (operator) {
            case EQUALS:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return value;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return DB.fromUtcTimestamp(value);
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && lValue.equals(value);
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && test(DB.toUtcTimestamp(lValue));
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<LocalDateTime>) obj);
                    }
                };
            case NOT_EQUALS:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return value;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return DB.fromUtcTimestamp(value);
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && !lValue.equals(value);
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && test(DB.toUtcTimestamp(lValue));
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<LocalDateTime>) obj);
                    }
                };
            case GREATER_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return value;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return DB.fromUtcTimestamp(value);
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && lValue.compareTo(value) > 0;
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && test(DB.toUtcTimestamp(lValue));
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<LocalDateTime>) obj);
                    }
                };
            case NOT_LESS_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return value;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return DB.fromUtcTimestamp(value);
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && lValue.compareTo(value) >= 0;
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && test(DB.toUtcTimestamp(lValue));
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<LocalDateTime>) obj);
                    }
                };
            case LESS_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return value;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return DB.fromUtcTimestamp(value);
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && lValue.compareTo(value) < 0;
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && test(DB.toUtcTimestamp(lValue));
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<LocalDateTime>) obj);
                    }
                };
            case NOT_GREATER_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return value;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return DB.fromUtcTimestamp(value);
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && lValue.compareTo(value) <= 0;
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && test(DB.toUtcTimestamp(lValue));
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<LocalDateTime>) obj);
                    }
                };
            default:
                throw new IllegalArgumentException(String.format("Operator %s cannot be applied to integer values", operator));
        }
    }

    public static TimestampValueFilter<LocalDate> of(LocalDate value, ComparisonOperator operator) {
        int h = 0;
        Objects.requireNonNull(operator);
        for (ComparisonOperator op : ComparisonOperator.values()) {
            if (op == operator) {
                break;
            }
            h++;
        }
        Timestamp timestamp = DB.toUtcTimestamp(value.atStartOfDay());
        final int hashcode = (timestamp.hashCode() << 4) | h;
        switch (operator) {
            case EQUALS:
                return new TimestampValueFilter<LocalDate>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDate toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue).toLocalDate());
                    }

                    @Override
                    public boolean testTemporal(LocalDate lValue) {
                        return null != lValue && lValue.equals(value);
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case NOT_EQUALS:
                return new TimestampValueFilter<LocalDate>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDate toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue).toLocalDate());
                    }

                    @Override
                    public boolean testTemporal(LocalDate lValue) {
                        return null != lValue && !lValue.equals(value);
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case GREATER_THAN:
                return new TimestampValueFilter<LocalDate>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDate toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue).toLocalDate());
                    }

                    @Override
                    public boolean testTemporal(LocalDate lValue) {
                        return null != lValue && lValue.compareTo(value) > 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case NOT_LESS_THAN:
                return new TimestampValueFilter<LocalDate>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDate toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue).toLocalDate());
                    }

                    @Override
                    public boolean testTemporal(LocalDate lValue) {
                        return null != lValue && lValue.compareTo(value) >= 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case LESS_THAN:
                return new TimestampValueFilter<LocalDate>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDate toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue).toLocalDate());
                    }

                    @Override
                    public boolean testTemporal(LocalDate lValue) {
                        return null != lValue && lValue.compareTo(value) < 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case NOT_GREATER_THAN:
                return new TimestampValueFilter<LocalDate>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDate toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue).toLocalDate());
                    }

                    @Override
                    public boolean testTemporal(LocalDate lValue) {
                        return null != lValue && lValue.compareTo(value) <= 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            default:
                throw new IllegalArgumentException(String.format("Operator %s cannot be applied to integer values", operator));
        }
    }

    public static TimestampValueFilter<LocalDateTime> of(LocalDateTime value, ComparisonOperator operator) {
        int h = 0;
        Objects.requireNonNull(operator);
        for (ComparisonOperator op : ComparisonOperator.values()) {
            if (op == operator) {
                break;
            }
            h++;
        }
        Timestamp timestamp = DB.toUtcTimestamp(value);
        final int hashcode = (timestamp.hashCode() << 4) | h;
        switch (operator) {
            case EQUALS:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue));
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && lValue.equals(value);
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case NOT_EQUALS:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue));
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && !lValue.equals(value);
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case GREATER_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue));
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && lValue.compareTo(value) > 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case NOT_LESS_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue));
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && lValue.compareTo(value) >= 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case LESS_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue));
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && lValue.compareTo(value) < 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            case NOT_GREATER_THAN:
                return new TimestampValueFilter<LocalDateTime>() {
                    @Override
                    public Timestamp get() {
                        return timestamp;
                    }

                    @Override
                    public LocalDateTime toTemporalValue() {
                        return value;
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        return operator;
                    }

                    @Override
                    public boolean test(Timestamp lValue) {
                        return null != lValue && testTemporal(DB.fromUtcTimestamp(lValue));
                    }

                    @Override
                    public boolean testTemporal(LocalDateTime lValue) {
                        return null != lValue && lValue.compareTo(value) <= 0;
                    }

                    @Override
                    public int hashCode() {
                        return hashcode;
                    }

                    @Override
                    public boolean equals(Object obj) {
                        return null != obj && obj instanceof TimestampValueFilter && areEqual(this, (TimestampValueFilter<? extends Temporal>) obj);
                    }
                };
            default:
                throw new IllegalArgumentException(String.format("Operator %s cannot be applied to integer values", operator));
        }
    }

}
