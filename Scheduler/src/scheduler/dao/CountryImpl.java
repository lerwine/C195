/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import expressions.NonNullableStringProperty;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author erwinel
 */
public class CountryImpl extends DataObjectImpl implements Country {

    private final NonNullableStringProperty name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() { return name.get(); }

    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }

    public CountryImpl() {
        super();
        name = new NonNullableStringProperty();
    }
    
    public Editable createEditable() { return new Editable(); }
    
    public class Editable extends EditableBase implements Country {

        private final NonNullableStringProperty name;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() { return name.get(); }

        public void setName(String value) { name.set(value); }

        public StringProperty nameProperty() { return name; }

        public Editable() {
            super();
            name = new NonNullableStringProperty(CountryImpl.this.getName());
        }

        @Override
        public BooleanBinding isValid() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void applyChanges() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void undoChanges() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
