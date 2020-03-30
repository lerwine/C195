package scheduler.view.city;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.CityImpl;
import scheduler.dao.Country;
import scheduler.dao.DataObjectImpl.DaoFactory;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.ItemModel;
import scheduler.view.ModelFilter;
import scheduler.view.country.CountryReferenceModel;
import scheduler.view.country.CountryReferenceModelImpl;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class CityModel extends scheduler.view.ItemModel<CityImpl> implements CityReferenceModel<CityImpl> {

    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<CountryReferenceModel<? extends Country>> country;
    private final ChildPropertyWrapper<String, CountryReferenceModel<? extends Country>> countryName;

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public CountryReferenceModel<? extends Country> getCountry() {
        return country.get();
    }

    public void setCountry(CountryReferenceModel<? extends Country> value) {
        country.set(value);
    }

    @Override
    public ObjectProperty<CountryReferenceModel<? extends Country>> countryProperty() {
        return country;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CountryReferenceModel<? extends Country>> countryNameProperty() {
        return countryName;
    }

    public CityModel(CityImpl dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
        Country c = dao.getCountry();
        country = new SimpleObjectProperty<>(this, "country", (null == c) ? null : new CountryReferenceModelImpl(c));
        countryName = new ChildPropertyWrapper<>(this, "countryName", country, (t) -> t.nameProperty());
    }

    @Override
    public String toString() {
        return name.get();
    }

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    @Override
    public int hashCode() {
        if (isNewItem()) {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.name);
            hash = 23 * hash + Objects.hashCode(this.country);
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CityModel) {
            final CityModel other = (CityModel) obj;
            if (isNewItem()) {
                return name.isEqualTo(other.name).get() && country.isEqualTo(other.country).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
    public final static class Factory extends ItemModel.ModelFactory<CityImpl, CityModel> {

        private Factory() { }
        
        @Override
        public DaoFactory<CityImpl> getDaoFactory() {
            return CityImpl.getFactory();
        }

        @Override
        public CityModel createNew(CityImpl dao) {
            return new CityModel(dao);
        }

        @Override
        protected void updateItem(CityModel item, CityImpl dao) {
            super.updateItem(item, dao);
            // TODO: Implement this
        }

        @Override
        public ModelFilter<CityImpl, CityModel> getAllItemsFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public ModelFilter<CityImpl, CityModel> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public CityImpl applyChanges(CityModel item) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
