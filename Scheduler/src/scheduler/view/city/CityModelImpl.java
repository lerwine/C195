package scheduler.view.city;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.dao.CityDAO;
import scheduler.dao.CityElement;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.model.ItemModel;
import scheduler.view.country.CityCountryModelImpl;
import scheduler.view.country.CityCountryModel;
import scheduler.dao.CountryElement;
import scheduler.dao.DataRowState;
import scheduler.view.country.CityOptionModel;
import scheduler.view.country.CountryModel;
import scheduler.view.country.CountryOptionModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModelImpl extends ItemModel<CityDAO> implements CityModel<CityDAO> {

    public static ZoneId getZoneId(CityModel<? extends CityElement> city) {
        if (null != city) {
            CityOptionModel optionModel = city.getOptionModel();
            if (null != optionModel) {
                return optionModel.getZoneId();
            }
            return CountryModel.getZoneId(city.getCountry());
        }
        return ZoneId.systemDefault();
    }

    private final ReadOnlyStringWrapper name;
    private final SimpleObjectProperty<CityCountryModel<? extends CountryElement>> country;
    private final ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryName;
    private final ObjectProperty<CityOptionModel> optionModel;

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
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

    @Override
    public CityOptionModel getOptionModel() {
        return optionModel.get();
    }

    public void setOptionModel(CityOptionModel value) {
        optionModel.set(value);
    }

    public ObjectProperty<CityOptionModel> optionModelProperty() {
        return optionModel;
    }

    public CityModelImpl(CityDAO dao) {
        super(dao);

        optionModel = new SimpleObjectProperty<>(CityOptionModel.getCityOption(dao.getName()));
        if (null == optionModel.get()) {
            throw new IllegalArgumentException("Data access object does not map to an option model");
        }
        name = new ReadOnlyStringWrapper(this, "name");
        name.bind(optionModel.get().nameProperty());
        CountryElement c = dao.getCountry();
        country = new SimpleObjectProperty<>(this, "country", (null == c) ? null : new CityCountryModelImpl(c));
        countryName = new ChildPropertyWrapper<>(this, "countryName", country, (t) -> t.nameProperty());
        optionModel.addListener(this::onOptionModelChanged);
    }

    @SuppressWarnings("unchecked")
    private void onOptionModelChanged(Observable observable) {
        name.unbind();
        CityOptionModel model = ((SimpleObjectProperty<CityOptionModel>) observable).get();
        if (null != model) {
            name.bind(model.nameProperty());
        }
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

        private Factory() {
        }

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
            CityOptionModel m = CityOptionModel.getCityOption(dao.getName());
            if (null == m) {
                throw new IllegalArgumentException("Data access object does not map to an option model");
            }
            item.setOptionModel(m);
            CountryElement countryDAO = dao.getCountry();
            item.setCountry((null == countryDAO) ? null : new CityCountryModelImpl(countryDAO));
        }

        @Override
        public CityDAO updateDAO(CityModelImpl item) {
            CityDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("City has been deleted");
            }
            CityOptionModel cityOption = item.optionModel.get();
            if (null == cityOption) {
                throw new IllegalArgumentException("City does not have an option model");
            }
            CityCountryModel<? extends CountryElement> countryModel = item.country.get();
            if (null == countryModel) {
                throw new IllegalArgumentException("No associated country");
            }
            CountryOptionModel countryOption = countryModel.getOptionModel();
            if (countryOption == null) {
                throw new IllegalArgumentException("Associated country does not have an option model");
            }
            if (!countryOption.equals(cityOption.getCountry())) {
                throw new IllegalArgumentException("City option model does not belong to the associated country option model");
            }
            CountryElement countryDAO = countryModel.getDataObject();

            if (countryDAO.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated country has been deleted");
            }
            dao.setCountry(countryDAO);
            dao.setName(cityOption.getResourceKey());
            return dao;
        }

    }
}
