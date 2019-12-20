/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * An integer property that only stores specific integer values that represent active status for users.
 * @author Leonard T. Erwine
 */
public class ActiveStateProperty extends SimpleIntegerProperty {
    /**
     * 
     */
    public ActiveStateProperty() {
        super(UserRow.STATE_USER);
    }

    /**
     * 
     * @param initialValue
     */
    public ActiveStateProperty(int initialValue) {
        super((initialValue < UserRow.STATE_INACTIVE) ? UserRow.STATE_INACTIVE : ((initialValue > UserRow.STATE_ADMIN) ? UserRow.STATE_ADMIN : initialValue));
    }

    /**
     * 
     * @param bean
     * @param name
     */
    public ActiveStateProperty(Object bean, String name) {
        super(bean, name, UserRow.STATE_USER);
    }

    /**
     * 
     * @param bean
     * @param name
     * @param initialValue
     */
    public ActiveStateProperty(Object bean, String name, int initialValue) {
        super(bean, name, (initialValue < UserRow.STATE_INACTIVE) ? UserRow.STATE_INACTIVE : ((initialValue > UserRow.STATE_ADMIN) ? UserRow.STATE_ADMIN : initialValue));
    }

    
    @Override
    public void set(int newValue) {
        super.set((newValue < UserRow.STATE_INACTIVE) ? UserRow.STATE_INACTIVE : ((newValue > UserRow.STATE_ADMIN) ? UserRow.STATE_ADMIN : newValue));
    }

    @Override
    public void setValue(Number v) {
        super.set((v != null && v instanceof Integer) ?
                (((int)v < UserRow.STATE_INACTIVE) ? UserRow.STATE_INACTIVE : (((int)v > UserRow.STATE_ADMIN) ? UserRow.STATE_ADMIN : (int)v)) :
                UserRow.STATE_INACTIVE);
    }
}
