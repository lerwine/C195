package scheduler.dao;

import expressions.NonNullableStringProperty;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author erwinel
 */
public class CityImpl extends DataObjectImpl implements City {

    private final NonNullableStringProperty name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() { return name.get(); }

    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<Country> country;

    /**
     * {@inheritDoc}
     */
    @Override
    public Country getCountry() { return country.get(); }

    public ReadOnlyObjectProperty<Country> countryProperty() { return country.getReadOnlyProperty(); }
    
    public CityImpl() {
        super();
        name = new NonNullableStringProperty();
        country = new ReadOnlyObjectWrapper<>();
    }
    
    public Editable createEditable() { return new Editable(); }
    
    public class Editable extends EditableBase implements City {

        private final NonNullableStringProperty name;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() { return name.get(); }

        public void setName(String value) { name.set(value); }

        public StringProperty nameProperty() { return name; }

        private final SimpleObjectProperty<Country> country;

        /**
         * {@inheritDoc}
         */
        @Override
        public Country getCountry() {
            return country.get();
        }

        public void setCountry(Country value) {
            country.set(value);
        }

        public ObjectProperty countryProperty() {
            return country;
        }

        public Editable() {
            super();
            name = new NonNullableStringProperty(CityImpl.this.getName());
            country = new SimpleObjectProperty<>(CityImpl.this.getCountry());
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
