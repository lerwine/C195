/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import javafx.beans.property.ReadOnlyIntegerWrapper;

/**
 *
 * @author Leonard T. Erwine
 */
public class ReadonlyActiveStateProperty extends ReadOnlyIntegerWrapper {

    public ReadonlyActiveStateProperty() { super(UserRow.STATE_USER); }

    public ReadonlyActiveStateProperty(int initialValue) { super(ActiveStateProperty.asValidValue(initialValue)); }

    public ReadonlyActiveStateProperty(Object bean, String name) { super(bean, name, UserRow.STATE_USER); }

    public ReadonlyActiveStateProperty(Object bean, String name, int initialValue) { super(bean, name, ActiveStateProperty.asValidValue(initialValue)); }

    @Override
    public void set(int newValue) { super.set(ActiveStateProperty.asValidValue(newValue)); }

    @Override
    public void setValue(Number v) {
        if (v != null) {
            if (v instanceof Integer) {
                super.set(ActiveStateProperty.asValidValue((int)v));
                return;
            }
            try {
                super.set(ActiveStateProperty.asValidValue(v.intValue()));
                return;
            } catch (Exception ex) { }
        }
        super.set(UserRow.STATE_INACTIVE);
    }
}
