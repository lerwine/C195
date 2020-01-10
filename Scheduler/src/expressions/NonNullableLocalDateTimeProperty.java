/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Leonard T. Erwine
 */
public class NonNullableLocalDateTimeProperty extends SimpleObjectProperty<LocalDateTime> {
    ReadOnlyObjectProperty<LocalDateTime> readOnlyProperty;
    
    /**
     * Returns the readonly property, that is synchronized with this {@code RowStateProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyObjectProperty<LocalDateTime> getReadOnlyProperty() {
        if (readOnlyProperty == null)
            readOnlyProperty = new NonNullableLocalDateTimeProperty.ReadOnlyPropertyImpl();
        return readOnlyProperty;
    }
    
    public NonNullableLocalDateTimeProperty() {
        super(LocalDateTime.now());
    }

    public NonNullableLocalDateTimeProperty(LocalDateTime initialValue) {
        super((initialValue == null) ? LocalDateTime.MIN : initialValue);
    }

    public NonNullableLocalDateTimeProperty(Object bean, String name) {
        super(bean, name, LocalDateTime.now());
    }

    public NonNullableLocalDateTimeProperty(Object bean, String name, LocalDateTime initialValue) {
        super(bean, name, (initialValue == null) ? LocalDateTime.MIN : initialValue);
    }

    @Override
    public void set(LocalDateTime newValue) {
        super.set((newValue == null) ? LocalDateTime.MIN : newValue);
    }
    
    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<LocalDateTime> {

        @Override
        public LocalDateTime get() { return NonNullableLocalDateTimeProperty.this.get(); }

        @Override
        public Object getBean() { return NonNullableLocalDateTimeProperty.this.getBean(); }

        @Override
        public String getName() { return NonNullableLocalDateTimeProperty.this.getName(); }

        private ReadOnlyPropertyImpl() {
            NonNullableLocalDateTimeProperty.this.addListener((observable, oldValue, newValue) -> {
                super.fireValueChangedEvent();
            });
        }
    };
}
