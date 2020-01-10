/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import expressions.NonNullableStringProperty;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author erwinel
 */
public class CustomerImpl extends DataObjectImpl implements Customer {

    private final NonNullableStringProperty name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() { return name.get(); }

    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<Address> address;

    /**
     * {@inheritDoc}
     */
    @Override
    public Address getAddress() { return address.get(); }

    public ReadOnlyObjectProperty<Address> addressProperty() { return address.getReadOnlyProperty(); }
    private final ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() { return active.get(); }

    public ReadOnlyBooleanProperty activeProperty() { return active.getReadOnlyProperty(); }
    
    public CustomerImpl() {
        super();
        name = new NonNullableStringProperty();
        address = new ReadOnlyObjectWrapper<>();
    }
    
    public Editable createEditable() { return new Editable(); }
    
    public class Editable extends EditableBase implements Customer {

        private final NonNullableStringProperty name;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() { return name.get(); }

        public void setName(String value) { name.set(value); }

        public StringProperty nameProperty() { return name; }
        
        private final ObjectProperty<Address> address;

        /**
         * {@inheritDoc}
         */
        @Override
        public Address getAddress() { return address.get(); }

        public void setAddress(Address value) { address.set(value); }

        public ObjectProperty addressProperty() { return address; }
        
        private final BooleanProperty active;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isActive() { return active.get(); }

        public void setActive(boolean value) { active.set(value); }

        public BooleanProperty activeProperty() { return active; }

        public Editable() {
            super();
            name = new NonNullableStringProperty(CustomerImpl.this.getName());
            address = new SimpleObjectProperty<>(CustomerImpl.this.getAddress());
            active = new SimpleBooleanProperty(CustomerImpl.this.isActive());
        }

        @Override
        public void undoChanges() {
            if (Platform.isFxApplicationThread()) {
                name.set(CustomerImpl.this.getName());
                address.set(CustomerImpl.this.getAddress());
                active.set(CustomerImpl.this.isActive());
            } else
                Platform.runLater(() -> {
                    undoChanges();
                });
        }

        @Override
        public void applyChanges() {
            if (Platform.isFxApplicationThread()) {
                CustomerImpl.this.name.set(getName());
                CustomerImpl.this.address.set(getAddress());
                CustomerImpl.this.active.set(isActive());
            } else
                Platform.runLater(() -> {
                    applyChanges();
                });
        }

        @Override
        public BooleanBinding isValid() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
