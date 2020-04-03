package scheduler.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.DataAccessObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class ValueBindings {

    /**
     * Creates a new {@link javafx.beans.binding.StringBinding} that returns an string value with leading and trailing whitespace removed or an empty
     * string if the source value was null.
     *
     * @param stringProperty The {@link javafx.beans.property.StringProperty} to trim.
     * @return The new {@link javafx.beans.binding.StringBinding}.
     * @throws NullPointerException The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static StringBinding asTrimmedAndNotNull(final StringProperty stringProperty) {
        return new StringBinding() {
            {
                super.bind(Objects.requireNonNull(stringProperty));
            }

            @Override
            protected String computeValue() {
                return Values.asNonNullAndTrimmed(stringProperty.get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(stringProperty);
            }

            @Override
            public void dispose() {
                super.unbind(stringProperty);
            }
        };
    }

    public static StringBinding asNormalized(final StringProperty stringProperty) {
        return new StringBinding() {
            {
                super.bind(Objects.requireNonNull(stringProperty));
            }

            @Override
            protected String computeValue() {
                return Values.asNonNullAndWsNormalized(stringProperty.get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(stringProperty);
            }

            @Override
            public void dispose() {
                super.unbind(stringProperty);
            }
        };
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if a given {@link StringProperty} is not null and contains at least one
     * non-whitespace character.
     *
     * @param stringProperty The {@link javafx.beans.property.StringProperty} to test.
     * @return The new {@link javafx.beans.binding.BooleanBinding}.
     * @throws NullPointerException The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static BooleanBinding notNullOrWhiteSpace(final StringProperty stringProperty) {
        Objects.requireNonNull(stringProperty, "String property cannot be null");
        return new BooleanBinding() {
            {
                super.bind(stringProperty);
            }

            @Override
            protected boolean computeValue() {
                return !Values.isNullWhiteSpaceOrEmpty(stringProperty.get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(stringProperty);
            }

            @Override
            public void dispose() {
                super.unbind(stringProperty);
            }
        };
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if a given {@link StringProperty} is null, empty or contains all whitespace
     * characters.
     *
     * @param stringProperty The {@link javafx.beans.property.StringProperty} to test.
     * @return The new {@link javafx.beans.binding.BooleanBinding}.
     * @throws NullPointerException The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static BooleanBinding isNullOrWhiteSpace(final StringProperty stringProperty) {
        Objects.requireNonNull(stringProperty, "String property cannot be null");
        return new BooleanBinding() {
            {
                super.bind(stringProperty);
            }

            @Override
            protected boolean computeValue() {
                return Values.isNullWhiteSpaceOrEmpty(stringProperty.get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(stringProperty);
            }

            @Override
            public void dispose() {
                super.unbind(stringProperty);
            }
        };
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if a given {@link ObjectExpression} of {@link LocalDateTime} is not null and start
     * is not greater than end.
     *
     * @param start The start {@link ObjectExpression} of {@link LocalDateTime} to test.
     * @param end The end {@link ObjectExpression} of {@link LocalDateTime} to test.
     * @return The new {@link BooleanBinding}.
     * @throws NullPointerException if either {@link ObjectExpression} was {@code null}.
     */
    public static BooleanBinding isRangeUndefinedOrValid(final ObjectExpression<LocalDateTime> start, final ObjectExpression<LocalDateTime> end) {
        if (start == null || end == null) {
            throw new NullPointerException("LocalDateTime binding cannot be null.");
        }

        return new BooleanBinding() {
            {
                super.bind(start, end);
            }

            @Override
            protected boolean computeValue() {
                LocalDateTime s = start.get();
                LocalDateTime e = end.get();
                return s == null || e == null || s.compareTo(e) <= 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.observableArrayList(start, end);
            }

            @Override
            public void dispose() {
                super.unbind(start, end);
            }
        };
    }

    public static BooleanBinding isDateUndefinedOrValid(final ObjectExpression<LocalDateTime> value) {
        if (value == null) {
            throw new NullPointerException("LocalDateTime binding cannot be null.");
        }

        return new BooleanBinding() {
            {
                super.bind(value);
            }

            @Override
            protected boolean computeValue() {
                LocalDateTime d = value.get();
                return d == null || d.compareTo(LocalDateTime.now()) >= 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }

            @Override
            public void dispose() {
                super.unbind(value);
            }
        };
    }

    public static ObjectBinding<LocalDateTime> asLocalDateTime(final ObjectExpression<LocalDate> date, final ObjectExpression<Integer> hour,
            final ObjectExpression<Integer> minute) {
        if (date == null || hour == null || minute == null) {
            throw new NullPointerException("Binding dependency cannot be null.");
        }

        return new ObjectBinding<LocalDateTime>() {
            {
                super.bind(date, hour, minute);
            }

            @Override
            protected LocalDateTime computeValue() {
                LocalDate d = date.get();
                Integer h = hour.get();
                Integer m = minute.get();
                return (d == null || h == null || m == null) ? null : LocalDateTime.of(d, LocalTime.of(h, m, 0));
            }

            @Override
            public ObservableList<Observable> getDependencies() {
                return FXCollections.observableArrayList(date, hour, minute);
            }

            @Override
            public void dispose() {
                super.unbind(date, hour, minute);
            }
        };
    }

    public static <R extends DataAccessObject> IntegerBinding primaryKeyBinding(final ObjectProperty<R> recordProperty) {
        if (recordProperty == null) {
            throw new NullPointerException("Record binding cannot be null.");
        }

        return new IntegerBinding() {
            {
                super.bind(recordProperty);
            }

            @Override
            protected int computeValue() {
                DataAccessObject record = recordProperty.get();
                return (record == null) ? 0 : record.getPrimaryKey();
            }
        };
    }

}
