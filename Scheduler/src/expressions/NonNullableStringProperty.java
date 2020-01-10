/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Leonard T. Erwine
 */
public class NonNullableStringProperty extends SimpleStringProperty {
    ReadOnlyStringProperty readOnlyProperty;
    
    /**
     * Returns the readonly property, that is synchronized with this {@code RowStateProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyStringProperty getReadOnlyProperty() {
        if (readOnlyProperty == null)
            readOnlyProperty = new NonNullableStringProperty.ReadOnlyPropertyImpl();
        return readOnlyProperty;
    }

    private final BooleanBinding whiteSpaceOrEmpty;
    
    public BooleanBinding isWhiteSpaceOrEmpty() { return whiteSpaceOrEmpty; }
    
    private boolean trim;
    
    private BooleanBinding getBinding(boolean trim) {
        return (trim) ? new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return NonNullableStringProperty.this.get().isEmpty();
            }
        } : new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return NonNullableStringProperty.this.get().trim().isEmpty();
            }
        };
    }
    public NonNullableStringProperty(boolean trim) {
        super("");
        this.trim = trim;
        whiteSpaceOrEmpty = getBinding(trim);
    }

    public NonNullableStringProperty() { this(false); }

    public NonNullableStringProperty(String initialValue, boolean trim) {
        super((initialValue == null) ? "" : ((trim) ? initialValue.trim() : initialValue));
        this.trim = trim;
        whiteSpaceOrEmpty = getBinding(trim);
    }

    public NonNullableStringProperty(String initialValue) { this(initialValue, false); }

    public NonNullableStringProperty(Object bean, String name, boolean trim) {
        super(bean, name, "");
        this.trim = trim;
        whiteSpaceOrEmpty = getBinding(trim);
    }

    public NonNullableStringProperty(Object bean, String name) { this(bean, name, false); }

    public NonNullableStringProperty(Object bean, String name, String initialValue, boolean trim) {
        super(bean, name, (initialValue == null) ? "" : ((trim) ? initialValue.trim() : initialValue));
        this.trim = trim;
        whiteSpaceOrEmpty = getBinding(trim);
    }

    public NonNullableStringProperty(Object bean, String name, String initialValue) { this(bean, name, initialValue, false); }

    @Override
    public void set(String newValue) { super.set((newValue == null) ? "" : ((trim) ? newValue.trim() : newValue)); }

    @Override
    public void setValue(String v) { super.set((v == null) ? "" : ((trim) ? v.trim() : v)); }

    private class ReadOnlyPropertyImpl extends ReadOnlyStringPropertyBase {

        @Override
        public String get() { return NonNullableStringProperty.this.get(); }

        @Override
        public Object getBean() { return NonNullableStringProperty.this.getBean(); }

        @Override
        public String getName() { return NonNullableStringProperty.this.getName(); }

        private ReadOnlyPropertyImpl() {
            NonNullableStringProperty.this.addListener((observable, oldValue, newValue) -> {
                super.fireValueChangedEvent();
            });
        }
    };
}
