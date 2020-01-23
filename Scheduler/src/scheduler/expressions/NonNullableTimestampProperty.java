/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.expressions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author erwinel
 */
public class NonNullableTimestampProperty extends SimpleObjectProperty<Timestamp> {
    ReadOnlyObjectProperty<Timestamp> readOnlyProperty;
    
    /**
     * Returns the readonly property, that is synchronized with this {@code RowStateProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyObjectProperty<Timestamp> getReadOnlyProperty() {
        if (readOnlyProperty == null)
            readOnlyProperty = new NonNullableTimestampProperty.ReadOnlyPropertyImpl();
        return readOnlyProperty;
    }
    public NonNullableTimestampProperty() {
        super(Timestamp.valueOf(LocalDateTime.now()));
    }

    public NonNullableTimestampProperty(Timestamp initialValue) {
        super((initialValue == null) ? Timestamp.valueOf(LocalDateTime.now()) : initialValue);
    }

    public NonNullableTimestampProperty(Object bean, String name) {
        super(bean, name, Timestamp.valueOf(LocalDateTime.now()));
    }

    public NonNullableTimestampProperty(Object bean, String name, Timestamp initialValue) {
        super(bean, name, (initialValue == null) ? Timestamp.valueOf(LocalDateTime.now()) : initialValue);
    }

    @Override
    public void set(Timestamp newValue) {
        super.set((newValue == null) ? Timestamp.valueOf(LocalDateTime.now()) : newValue);
    }
    
    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<Timestamp> {

        @Override
        public Timestamp get() { return NonNullableTimestampProperty.this.get(); }

        @Override
        public Object getBean() { return NonNullableTimestampProperty.this.getBean(); }

        @Override
        public String getName() { return NonNullableTimestampProperty.this.getName(); }

        private ReadOnlyPropertyImpl() {
            NonNullableTimestampProperty.this.addListener((observable, oldValue, newValue) -> {
                super.fireValueChangedEvent();
            });
        }
    };
}
