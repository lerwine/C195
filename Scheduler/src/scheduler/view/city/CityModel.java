package scheduler.view.city;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.City;
import scheduler.model.db.CountryRowData;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.ui.CityDbItem;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.view.ModelFilter;
import scheduler.view.country.RelatedCountry;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModel extends FxRecordModel<CityDAO> implements CityDbItem<CityDAO> {

    private static final Factory FACTORY = new Factory();

    public static ZoneId getZoneId(City city) {
        city.asPredefinedData().getZoneId();
//        if (null != city) {
//            CityOptionModel optionModel = city.getOptionModel();
//            if (null != optionModel) {
//                return optionModel.getZoneId();
//            }
//            return CountryModel.getZoneId(city.getCountry());
//        }
        return ZoneId.systemDefault();
    }

    public static final Factory getFactory() {
        return FACTORY;
    }

    private PredefinedCity predefinedData;
    private final ReadOnlyStringWrapper name;
    private final SimpleObjectProperty<CountryItem> country;
    private final NestedStringBindingProperty<CountryItem> countryName;

    public CityModel(CityDAO dao) {
        super(dao);
        predefinedData = dao.asPredefinedData();
        name = new ReadOnlyStringWrapper(this, "name", dao.getName());
        CountryRowData c = dao.getCountry();
        country = new SimpleObjectProperty<>(this, "country", (null == c) ? null : new RelatedCountry(c));
        countryName = new NestedStringBindingProperty<>(this, "countryName", country, (t) -> t.nameProperty());
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public CountryItem getCountry() {
        return country.get();
    }

    public void setCountry(CountryItem value) {
        country.set(value);
    }

    @Override
    public ObjectProperty<CountryItem> countryProperty() {
        return country;
    }

    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public NestedStringBindingProperty<CountryItem> countryNameProperty() {
        return countryName;
    }

    @Override
    public String toString() {
        return name.get();
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

    @Override
    public PredefinedCity asPredefinedData() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.city.CityModel#asPredefinedData
    }

    public final static class Factory extends FxRecordModel.ModelFactory<CityDAO, CityModel> {

        private Factory() {
        }

        @Override
        public DaoFactory<CityDAO> getDaoFactory() {
            return CityDAO.getFactory();
        }

        @Override
        public CityModel createNew(CityDAO dao) {
            return new CityModel(dao);
        }

        @Override
        public void updateItem(CityModel item, CityDAO dao) {
            super.updateItem(item, dao);
            CountryRowData countryDAO = dao.getCountry();
            PredefinedCity p = dao.asPredefinedData();
            if (countryDAO.asPredefinedData() != p.getCountry()) {
                throw new IllegalArgumentException("Invalid country assignment");
            }
            item.predefinedData = p;
            item.setCountry(new RelatedCountry(countryDAO));
        }

        @Override
        public CityDAO updateDAO(CityModel item) {
            CityDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("City has been deleted");
            }
            CountryItem country = item.getCountry();
            if (item.asPredefinedData().getCountry() != country.asPredefinedData()) {
                throw new IllegalArgumentException("Invalid country assignment");
            }
            dao.setPredefinedCity(item.asPredefinedData());
            return dao;
        }

        @Override
        public ModelFilter<CityDAO, CityModel, ? extends DaoFilter<CityDAO>> getAllItemsFilter() {
            return new ModelFilter<CityDAO, CityModel, DaoFilter<CityDAO>>() {
                private final String headingText = AppResources.getResourceString(RESOURCEKEY_ALLCITIES);
                private final DaoFilter<CityDAO> daoFilter = DaoFilter.all(AppResources.getResourceString(RESOURCEKEY_READINGFROMDB),
                        AppResources.getResourceString(RESOURCEKEY_LOADINGCITIES));

                @Override
                public String getHeadingText() {
                    return headingText;
                }

                @Override
                public DaoFilter<CityDAO> getDaoFilter() {
                    return daoFilter;
                }

                @Override
                public boolean test(CityModel t) {
                    return null != t;
                }

            };
        }

        @Override
        public ModelFilter<CityDAO, CityModel, ? extends DaoFilter<CityDAO>> getDefaultFilter() {
            return getAllItemsFilter();
        }

    }
}
