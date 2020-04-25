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
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.model.ItemModel;
import scheduler.view.country.CityCountryModelImpl;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.view.ModelFilter;
import scheduler.view.country.CityOptionModel;
import scheduler.model.db.CityRowData;
import scheduler.model.db.CountryRowData;
import scheduler.model.ui.CountryDbItem;
import scheduler.model.ui.CityDbItem;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModelImpl extends ItemModel<CityDAO> implements CityDbItem<CityDAO> {

    public static ZoneId getZoneId(CityDbItem<? extends CityRowData> city) {
        // CURRENT: Implement CityModelImpl#getZoneId
//        if (null != city) {
//            CityOptionModel optionModel = city.getOptionModel();
//            if (null != optionModel) {
//                return optionModel.getZoneId();
//            }
//            return CountryModel.getZoneId(city.getCountry());
//        }
        return ZoneId.systemDefault();
    }

    private final ReadOnlyStringWrapper name;
    private final SimpleObjectProperty<CountryDbItem<? extends CountryRowData>> country;
    private final ChildPropertyWrapper<String, CountryDbItem<? extends CountryRowData>> countryName;
//    private final ObjectProperty<CityOptionModel> optionModel;

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public CountryDbItem<? extends CountryRowData> getCountry() {
        return country.get();
    }

    public void setCountry(CountryDbItem<? extends CountryRowData> value) {
        country.set(value);
    }

    @Override
    public ObjectProperty<CountryDbItem<? extends CountryRowData>> countryProperty() {
        return country;
    }

    public String getCountryName() {
        return countryName.get();
    }

    public ChildPropertyWrapper<String, CountryDbItem<? extends CountryRowData>> countryNameProperty() {
        return countryName;
    }

//    @Override
//    public CityOptionModel getOptionModel() {
//        return optionModel.get();
//    }
//
//    public void setOptionModel(CityOptionModel value) {
//        optionModel.set(value);
//    }
//
//    public ObjectProperty<CityOptionModel> optionModelProperty() {
//        return optionModel;
//    }

    public CityModelImpl(CityDAO dao) {
        super(dao);

//        optionModel = new SimpleObjectProperty<>(CityOptionModel.getCityOption(dao.getName()));
//        if (null == optionModel.get()) {
//            throw new IllegalArgumentException("Data access object does not map to an option model");
//        }
        name = new ReadOnlyStringWrapper(this, "name");
        // CURRENT: Initialize name
//        name.bind(optionModel.get().nameProperty());
        CountryRowData c = dao.getCountry();
        country = new SimpleObjectProperty<>(this, "country", (null == c) ? null : new CityCountryModelImpl(c));
        countryName = new ChildPropertyWrapper<>(this, "countryName", country, (t) -> t.nameProperty());
//        optionModel.addListener(this::onOptionModelChanged);
    }

//    @SuppressWarnings("unchecked")
//    private void onOptionModelChanged(Observable observable) {
//        name.unbind();
//        CityOptionModel model = ((SimpleObjectProperty<CityOptionModel>) observable).get();
//        if (null != model) {
//            name.bind(model.nameProperty());
//        }
//    }

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
//            item.setOptionModel(m);
            CountryRowData countryDAO = dao.getCountry();
            item.setCountry((null == countryDAO) ? null : new CityCountryModelImpl(countryDAO));
        }

        @Override
        public CityDAO updateDAO(CityModelImpl item) {
            CityDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("City has been deleted");
            }
            // CURRENT: Implement CityModelImpl.Factory#updateDAO
//            CityOptionModel cityOption = item.optionModel.get();
//            if (null == cityOption) {
//                throw new IllegalArgumentException("City does not have an option model");
//            }
//            CountryItem<? extends CountryRowData> countryModel = item.country.get();
//            if (null == countryModel) {
//                throw new IllegalArgumentException("No associated country");
//            }
//            CountryOptionModel countryOption = countryModel.getOptionModel();
//            if (countryOption == null) {
//                throw new IllegalArgumentException("Associated country does not have an option model");
//            }
//            if (!countryOption.equals(cityOption.getCountry())) {
//                throw new IllegalArgumentException("City option model does not belong to the associated country option model");
//            }
//            CountryRowData countryDAO = countryModel.getDataObject();
//
//            if (countryDAO.getRowState() == DataRowState.DELETED) {
//                throw new IllegalArgumentException("Associated country has been deleted");
//            }
//            dao.setCountry(countryDAO);
//            dao.setName(cityOption.getResourceKey());
            return dao;
        }

        @Override
        public ModelFilter<CityDAO, CityModelImpl, ? extends DaoFilter<CityDAO>> getAllItemsFilter() {
            return new ModelFilter<CityDAO, CityModelImpl, DaoFilter<CityDAO>>() {
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
                public boolean test(CityModelImpl t) {
                    return null != t;
                }
                
            };
        }

        @Override
        public ModelFilter<CityDAO, CityModelImpl, ? extends DaoFilter<CityDAO>> getDefaultFilter() {
            return getAllItemsFilter();
        }

    }
}
