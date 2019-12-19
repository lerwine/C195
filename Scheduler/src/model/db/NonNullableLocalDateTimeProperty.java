/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import java.time.LocalDateTime;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Leonard T. Erwine
 */
public class NonNullableLocalDateTimeProperty extends SimpleObjectProperty<LocalDateTime> {
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

    @Override
    public void setValue(LocalDateTime v) {
        super.setValue((v == null) ? LocalDateTime.MIN : v);
    }
}
