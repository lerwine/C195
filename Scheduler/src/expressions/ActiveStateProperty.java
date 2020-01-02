/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import javafx.beans.property.SimpleIntegerProperty;
import model.db.UserRow;

/**
 * An integer property that only stores specific integer values that represent active status for users.
 * @author Leonard T. Erwine
 */
public class ActiveStateProperty extends SimpleIntegerProperty {
    public static int asValidValue(int value) {
        return (value < UserRow.STATE_INACTIVE) ? UserRow.STATE_INACTIVE : ((value > UserRow.STATE_ADMIN) ? UserRow.STATE_ADMIN : value);
    }
    
    /**
     * 
     */
    public ActiveStateProperty() { super(UserRow.STATE_USER); }

    /**
     * 
     * @param initialValue
     */
    public ActiveStateProperty(int initialValue) { super(asValidValue(initialValue)); }

    /**
     * 
     * @param bean
     * @param name
     */
    public ActiveStateProperty(Object bean, String name) { super(bean, name, UserRow.STATE_USER); }

    /**
     * 
     * @param bean
     * @param name
     * @param initialValue
     */
    public ActiveStateProperty(Object bean, String name, int initialValue) { super(bean, name, asValidValue(initialValue)); }

    
    @Override
    public void set(int newValue) { super.set(asValidValue(newValue)); }

    @Override
    public void setValue(Number v) {
        if (v != null) {
            if (v instanceof Integer) {
                super.set(asValidValue((int)v));
                return;
            }
            try {
                super.set(asValidValue(v.intValue()));
                return;
            } catch (Exception ex) { }
        }
        super.set(UserRow.STATE_INACTIVE);
    }
}
