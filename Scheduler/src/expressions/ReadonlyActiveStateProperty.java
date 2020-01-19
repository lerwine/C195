/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import scheduler.dao.User;

/**
 *
 * @author Leonard T. Erwine
 */
public class ReadonlyActiveStateProperty extends ReadOnlyIntegerWrapper {

    public ReadonlyActiveStateProperty() { super(User.STATUS_USER); }

    public ReadonlyActiveStateProperty(int initialValue) { super(ActiveStateProperty.asValidValue(initialValue)); }

    public ReadonlyActiveStateProperty(Object bean, String name) { super(bean, name, User.STATUS_USER); }

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
        super.set(User.STATUS_INACTIVE);
    }
}
