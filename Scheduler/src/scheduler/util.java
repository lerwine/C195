/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;

/**
 *
 * @author Leonard T. Erwine
 */
public class util {
    public static void collapseLabeledVertical(Labeled labeled) {
        labeled.setText("");
        collapseControlVertical(labeled);
    }
    
    public static void collapseControlVertical(Control control) {
        control.setMinHeight(0);
        control.setPrefHeight(0);
        control.setMaxHeight(0);
        control.setVisible(false);
    }
    
    public static void restoreLabeledVertical(Labeled labeled, String text) {
        labeled.setText(text);
        restoreControlVertical(labeled);
    }
    
    public static void restoreControlVertical(Control control) {
        restoreControlVertical(control, Control.USE_COMPUTED_SIZE);
    }
    
    public static void restoreLabeledVertical(Labeled labeled, String text, double prefHeight) {
        labeled.setText(text);
        restoreControlVertical(labeled, prefHeight);
    }
    
    public static void restoreControlVertical(Control control, double prefHeight) {
        restoreControlVertical(control, Control.USE_COMPUTED_SIZE, prefHeight, Control.USE_COMPUTED_SIZE);
    }
    
    public static void restoreLabeledVertical(Labeled labeled, String text, double minHeight, double prefHeight, double maxHeight) {
        labeled.setText(text);
        restoreControlVertical(labeled, minHeight, prefHeight, maxHeight);
    }
    
    public static void restoreControlVertical(Control control, double minHeight, double prefHeight, double maxHeight) {
        control.setMaxHeight(maxHeight);
        control.setPrefHeight(prefHeight);
        control.setMinHeight(minHeight);
        control.setVisible(true);
    }
    
    /**
     * Creates a new {@link javafx.beans.binding.StringBinding} that returns an string value with leading and trailing whitespace removed or
     * an empty string if the source value was null.
     *
     * @param stringProperty
     *         The {@link javafx.beans.property.StringProperty} to trim.
     * @return
     *         The new {@link javafx.beans.binding.StringBinding}.
     * @throws NullPointerException
     *         The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static StringBinding asTrimmedAndNotNull(final StringProperty stringProperty) {
        if (stringProperty == null)
            throw new NullPointerException("String binding cannot be null.");
        
        return new StringBinding() {
            { super.bind(stringProperty); }

            @Override
            protected String computeValue() {
                String s = stringProperty.get();
                return (s != null) ? s.trim() : "";
            }

            @Override
            public ObservableList<?> getDependencies() { return FXCollections.singletonObservableList(stringProperty); }

            @Override
            public void dispose() { super.unbind(stringProperty); }
        };
    }

    /**
     * Creates a new {@link javafx.beans.binding.BooleanBinding} that holds {@code true} if a given
     * {@link javafx.beans.property.StringProperty} is not null and contains at least one non-whitespace character.
     *
     * @param stringProperty
     *         The {@link javafx.beans.property.StringProperty} to test.
     * @return
     *         The new {@link javafx.beans.binding.BooleanBinding}.
     * @throws NullPointerException
     *         The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static BooleanBinding notNullOrWhiteSpace(final StringProperty stringProperty) {
        if (stringProperty == null)
            throw new NullPointerException("String binding cannot be null.");

        return new BooleanBinding() {
            { super.bind(stringProperty); }

            @Override
            protected boolean computeValue() {
                String s = stringProperty.get();
                return s != null && !s.trim().isEmpty();
            }

            @Override
            public ObservableList<?> getDependencies() { return FXCollections.singletonObservableList(stringProperty); }

            @Override
            public void dispose() { super.unbind(stringProperty); }
        };
    }

    /**
     * Creates a new {@link javafx.beans.binding.BooleanBinding} that holds {@code true} if a given
     * {@link javafx.beans.property.NonNullableLocalDateTimeProperty} is not null and contains at least one non-whitespace character.
     *
     * @param start
     *         The start {@link javafx.beans.property.ObjectProperty} to test.
     * @param end
     *         The end {@link javafx.beans.property.ObjectProperty} to test.
     * @return
     *         The new {@link javafx.beans.binding.BooleanBinding}.
     * @throws NullPointerException
     *         A source {@link javafx.beans.property.ObjectProperty} was {@code null}.
     */
    public static BooleanBinding isRangeUndefinedOrValid(final ObjectExpression<LocalDateTime> start, final ObjectExpression<LocalDateTime> end) {
        if (start == null || end == null)
            throw new NullPointerException("LocalDateTime binding cannot be null.");

        return new BooleanBinding() {
            { super.bind(start, end); }

            @Override
            protected boolean computeValue() {
                LocalDateTime s = start.get();
                if (s == null)
                    return true;
                LocalDateTime e = end.get();
                return e == null || s.compareTo(e) < 0;
            }

            @Override
            public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(start, end); }

            @Override
            public void dispose() { super.unbind(start, end); }
        };
    }

    public static BooleanBinding isDateUndefinedOrValid(final ObjectExpression<LocalDateTime> value) {
        if (value == null)
            throw new NullPointerException("LocalDateTime binding cannot be null.");

        return new BooleanBinding() {
            { super.bind(value); }

            @Override
            protected boolean computeValue() {
                LocalDateTime d = value.get();
                return d == null || d.compareTo(LocalDateTime.now()) >= 0;
            }

            @Override
            public ObservableList<?> getDependencies() { return FXCollections.singletonObservableList(value); }

            @Override
            public void dispose() { super.unbind(value); }
        };
    }

    public static ObjectBinding<LocalDateTime> asLocalDateTime(final ObjectExpression<LocalDate> date, final ObjectExpression<Integer> hour,
            final ObjectExpression<Integer> minute) {
        if (date == null || hour == null || minute == null)
            throw new NullPointerException("Binding dependency cannot be null.");

        return new ObjectBinding<LocalDateTime>() {
            { super.bind(date); }

            @Override
            protected LocalDateTime computeValue() {
                LocalDate d = date.get();
                if (d == null)
                    return null;
                Integer h = hour.get();
                if (h == null)
                    return null;
                Integer m = minute.get();
                if (m == null)
                    return null;
                return LocalDateTime.of(d, LocalTime.of(h, m, 0));
            }

            @Override
            public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(date, hour, minute); }

            @Override
            public void dispose() { super.unbind(date, hour, minute); }
        };
    }

    public static <R extends model.Record> IntegerBinding primaryKeyBinding(final ObjectProperty<R> recordProperty) {
        if (recordProperty == null)
            throw new NullPointerException("Record binding cannot be null.");
        
        return new IntegerBinding() {
            { super.bind(recordProperty); }

            @Override
            protected int computeValue() {
                model.Record record = recordProperty.get();
                return (record == null) ? 0 : record.getPrimaryKey();
            }
        };
    }
    
    public static String resultStringOrDefault(ResultSet rs, String columnLabel, String defaultValue) throws SQLException {
        String result = rs.getString(columnLabel);
        return (rs.wasNull()) ? defaultValue : result;
    }
    
    public static short resultShortOrDefault(ResultSet rs, String columnLabel, short defaultValue) throws SQLException {
        short result = rs.getShort(columnLabel);
        return (rs.wasNull()) ? defaultValue : result;
    }
}
