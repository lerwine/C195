package scheduler.view.city;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.CityDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.model.ItemModel;
import scheduler.view.country.CityCountryModelImpl;
import scheduler.view.country.CityCountryModel;
import scheduler.dao.CountryElement;
import scheduler.dao.DataRowState;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class CityModelImpl extends ItemModel<CityDAO> implements CityModel<CityDAO> {

    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<CityCountryModel<? extends CountryElement>> country;
    private final ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryName;

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
    public CityCountryModel<? extends CountryElement> getCountry() {
        return country.get();
    }

    public void setCountry(CityCountryModel<? extends CountryElement> value) {
        country.set(value);
    }

    @Override
    public ObjectProperty<CityCountryModel<? extends CountryElement>> countryProperty() {
        return country;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryNameProperty() {
        return countryName;
    }

    public CityModelImpl(CityDAO dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
        CountryElement c = dao.getCountry();
        country = new SimpleObjectProperty<>(this, "country", (null == c) ? null : new CityCountryModelImpl(c));
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
        if (null != obj && obj instanceof CityModelImpl) {
            final CityModelImpl other = (CityModelImpl) obj;
            if (isNewItem()) {
                return name.isEqualTo(other.name).get() && country.isEqualTo(other.country).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
    public final static class Factory extends ItemModel.ModelFactory<CityDAO, CityModelImpl> {

        private Factory() { }
        
        @Override
        public DaoFactory<CityDAO> getDaoFactory() {
            return CityDAO.getFactory();
        }

        @Override
        public CityModelImpl createNew(CityDAO dao) {
            return new CityModelImpl(dao);
        }

        @Override
        public void updateItem(CityModelImpl item, CityDAO dao) {
            super.updateItem(item, dao);
            item.name.set(dao.getName());
            CountryElement countryDAO = dao.getCountry();
            item.setCountry((null == countryDAO) ? null : new CityCountryModelImpl(countryDAO));
        }

        @Override
        public CityDAO updateDAO(CityModelImpl item) {
            CityDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED)
                throw new IllegalArgumentException("City has been deleted");
            String name = item.name.get();
            if (name.trim().isEmpty())
                throw new IllegalArgumentException("City name empty");
            CityCountryModel<? extends CountryElement> countryModel = item.country.get();
            if (null == countryModel)
                throw new IllegalArgumentException("No associated country");
            CountryElement countryDAO = countryModel.getDataObject();
            switch (countryDAO.getRowState()) {
                case DELETED:
                    throw new IllegalArgumentException("Associated country has been deleted");
                case NEW:
                    throw new IllegalArgumentException("Associated country has never been saved");
                default:
                    dao.setCountry(countryDAO);
                    break;
            }
            dao.setName(name);
            return dao;
        }

    }
}
