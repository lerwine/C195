package scheduler.observables;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class BindingHelper {
    /**
     * Creates a new {@link javafx.beans.binding.StringBinding} that returns an string value with leading and trailing whitespace removed or an empty
     * string if the source value was null.
     *
     * @param stringProperty The {@link javafx.beans.property.StringProperty} to trim.
     * @return The new {@link javafx.beans.binding.StringBinding}.
     * @throws NullPointerException The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static StringBinding asTrimmedAndNotNull(final StringProperty stringProperty) {
        return Bindings.createStringBinding(() -> Values.asNonNullAndTrimmed(stringProperty.get()), stringProperty);
    }

    public static StringBinding asNormalized(final StringProperty stringProperty) {
        return Bindings.createStringBinding(() -> Values.asNonNullAndWsNormalized(stringProperty.get()), stringProperty);
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
        return Bindings.createBooleanBinding(() -> {
            String s = stringProperty.get();
            return null != s && !s.trim().isEmpty();
        }, stringProperty);
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
        return Bindings.createBooleanBinding(() -> {
            String s = stringProperty.get();
            return null == s || s.trim().isEmpty();
        }, stringProperty);
    }

    public static ObjectBinding<LocalDateTime> asLocalDateTime(final ObjectExpression<LocalDate> date, final ObjectExpression<Integer> hour,
            final ObjectExpression<Integer> minute) {
        return Bindings.createObjectBinding(() -> {
            LocalDate d = date.get();
            Integer h = hour.get();
            Integer m = minute.get();
            return (d == null || h == null || m == null) ? null : LocalDateTime.of(d, LocalTime.of(h, m, 0));
        }, date, hour, minute);
    }

    public static <R extends DataAccessObject> IntegerBinding primaryKeyBinding(final ObjectProperty<R> recordProperty) {
        return Bindings.createIntegerBinding(() -> {
            DataAccessObject record = recordProperty.get();
            return (record == null || record.getRowState() == DataRowState.NEW) ? Integer.MIN_VALUE : record.getPrimaryKey();
        }, recordProperty);
    }

}
