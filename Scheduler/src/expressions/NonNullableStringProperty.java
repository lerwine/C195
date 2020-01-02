/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Leonard T. Erwine
 */
public class NonNullableStringProperty extends SimpleStringProperty {
    private boolean trim;
    
    public NonNullableStringProperty(boolean trim) {
        super("");
        this.trim = trim;
    }

    public NonNullableStringProperty() { this(false); }

    public NonNullableStringProperty(String initialValue, boolean trim) {
        super((initialValue == null) ? "" : ((trim) ? initialValue.trim() : initialValue));
        this.trim = trim;
    }

    public NonNullableStringProperty(String initialValue) { this(initialValue, false); }

    public NonNullableStringProperty(Object bean, String name, boolean trim) {
        super(bean, name, "");
        this.trim = trim;
    }

    public NonNullableStringProperty(Object bean, String name) { this(bean, name, false); }

    public NonNullableStringProperty(Object bean, String name, String initialValue, boolean trim) {
        super(bean, name, (initialValue == null) ? "" : ((trim) ? initialValue.trim() : initialValue));
        this.trim = trim;
    }

    public NonNullableStringProperty(Object bean, String name, String initialValue) { this(bean, name, initialValue, false); }

    @Override
    public void set(String newValue) { super.set((newValue == null) ? "" : ((trim) ? newValue.trim() : newValue)); }

    @Override
    public void setValue(String v) { super.set((v == null) ? "" : ((trim) ? v.trim() : v)); }
}
