/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import expressions.NonNullableStringProperty;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author erwinel
 */
public class AddressImpl extends DataObjectImpl implements Address {

    private final NonNullableStringProperty address1;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress1() {
        return address1.get();
    }

    public ReadOnlyStringProperty address1Property() {
        return address1.getReadOnlyProperty();
    }
    
    private final NonNullableStringProperty address2;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress2() {
        return address2.get();
    }

    public ReadOnlyStringProperty address2Property() {
        return address2.getReadOnlyProperty();
    }
    
    private final ReadOnlyObjectWrapper<City> city;

    /**
     * {@inheritDoc}
     */
    @Override
    public City getCity() {
        return city.get();
    }

    public ReadOnlyObjectProperty<City> cityProperty() {
        return city.getReadOnlyProperty();
    }
    
    private final NonNullableStringProperty postalCode;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    public ReadOnlyStringProperty postalCodeProperty() {
        return postalCode.getReadOnlyProperty();
    }
    
    private final NonNullableStringProperty phone;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhone() {
        return phone.get();
    }

    public ReadOnlyStringProperty phoneProperty() {
        return phone.getReadOnlyProperty();
    }

    public AddressImpl() {
        this.address1 = new NonNullableStringProperty();
        this.address2 = new NonNullableStringProperty();
        this.city = new ReadOnlyObjectWrapper<>();
        this.postalCode = new NonNullableStringProperty();
        this.phone = new NonNullableStringProperty();
    }

    public Editable createEditable() { return new Editable(); }
    
    public class Editable extends EditableBase implements Address {

        private final NonNullableStringProperty address1;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAddress1() {
            return address1.get();
        }

        public void setAddress1(String value) {
            address1.set(value);
        }

        public StringProperty address1Property() {
            return address1;
        }
        private final NonNullableStringProperty address2;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAddress2() {
            return address2.get();
        }

        public void setAddress2(String value) {
            address2.set(value);
        }

        public StringProperty address2Property() {
            return address2;
        }
        
        private final ObjectProperty<City> city;

        /**
         * {@inheritDoc}
         */
        @Override
        public City getCity() {
            return city.get();
        }

        public void setCity(City value) {
            city.set(value);
        }

        public ObjectProperty cityProperty() {
            return city;
        }
        
        private final NonNullableStringProperty postalCode;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPostalCode() {
            return postalCode.get();
        }

        public void setPostalCode(String value) {
            postalCode.set(value);
        }

        public StringProperty postalCodeProperty() {
            return postalCode;
        }
        
        private final NonNullableStringProperty phone;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPhone() {
            return phone.get();
        }

        public void setPhone(String value) {
            phone.set(value);
        }

        public StringProperty phoneProperty() {
            return phone;
        }
        
        public Editable() {
            this.address1 = new NonNullableStringProperty();
            this.address2 = new NonNullableStringProperty();
            this.city = new SimpleObjectProperty<>();
            this.postalCode = new NonNullableStringProperty();
            this.phone = new NonNullableStringProperty();
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
