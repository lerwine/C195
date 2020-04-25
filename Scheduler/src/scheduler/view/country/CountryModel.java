package scheduler.view.country;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.view.ModelFilter;
import scheduler.view.city.SupportedLocale;
import scheduler.view.model.ItemModel;
import scheduler.model.db.CountryRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryModel extends ItemModel<CountryDAO> implements CityCountryModel<CountryDAO> {

    public static ZoneId getZoneId(CityCountryModel<? extends CountryRowData> country) {
        if (null != country) {
            CountryOptionModel optionModel = country.getOptionModel();
            if (null != optionModel) {
                Optional<SupportedLocale> sl = SupportedLocale.fromRegionCode(optionModel.getRegionCode());
                if (sl.isPresent()) {
                    CityOptionModel cityOption = CityOptionModel.getCityOption(sl.get().getHomeOfficeKey());
                    if (null != cityOption)
                        return cityOption.getZoneId();
                }
            }
        }
        return ZoneId.systemDefault();
    }

    private final ReadOnlyStringWrapper name;
    private final ObjectProperty<CountryOptionModel> optionModel;

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public CountryOptionModel getOptionModel() {
        return optionModel.get();
    }

    public void setOptionModel(CountryOptionModel value) {
        optionModel.set(value);
    }

    public ObjectProperty<CountryOptionModel> optionModelProperty() {
        return optionModel;
    }
    
    public CountryModel(CountryDAO dao) {
        super(dao);
        optionModel = new SimpleObjectProperty<>(CountryOptionModel.getCountryOption(dao.getName()));
        if (null == optionModel.get())
            throw new IllegalArgumentException("Data access object does not map to an option model");
        name = new ReadOnlyStringWrapper(this, "name");
        name.bind(optionModel.get().nameProperty());
        optionModel.addListener(this::onOptionModelChanged);
    }

    @SuppressWarnings("unchecked")
    private void onOptionModelChanged(Observable observable) {
        name.unbind();
        CountryOptionModel model = ((SimpleObjectProperty<CountryOptionModel>)observable).get();
        if (null != model)
            name.bind(model.nameProperty());
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
            return Objects.hashCode(name.get());
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CountryModel) {
            final CountryModel other = (CountryModel) obj;
            if (isNewItem()) {
                return Objects.equals(optionModel.get(), other.optionModel.get());
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
    public final static class Factory extends ItemModel.ModelFactory<CountryDAO, CountryModel> {

        private Factory() { }
        
        @Override
        public DaoFactory<CountryDAO> getDaoFactory() {
            return CountryDAO.getFactory();
        }

        @Override
        public CountryModel createNew(CountryDAO dao) {
            return new CountryModel(dao);
        }

        @Override
        public void updateItem(CountryModel item, CountryDAO dao) {
            super.updateItem(item, dao);
            CountryOptionModel m = CountryOptionModel.getCountryOption(dao.getName());
            if (null == m)
                throw new IllegalArgumentException("Data access object does not map to an option model");
            item.setOptionModel(m);
        }

        @Override
        public CountryDAO updateDAO(CountryModel item) {
            CountryDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED)
                throw new IllegalArgumentException("Country has been deleted");
             CountryOptionModel m = item.optionModel.get();
             if (null == m)
                throw new IllegalArgumentException("Country does not have an option model");
            dao.setName(m.getRegionCode());
            return dao;
        }

        @Override
        public ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> getAllItemsFilter() {
            return new ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>>() {
                private final String headingText = AppResources.getResourceString(RESOURCEKEY_ALLCOUNTRIES);
                private final DaoFilter<CountryDAO> daoFilter = DaoFilter.all(AppResources.getResourceString(RESOURCEKEY_READINGFROMDB),
                            AppResources.getResourceString(RESOURCEKEY_LOADINGCOUNTRIES));
                @Override
                public String getHeadingText() {
                    return headingText;
                }

                @Override
                public DaoFilter<CountryDAO> getDaoFilter() {
                    return daoFilter;
                }

                @Override
                public boolean test(CountryModel t) {
                    return null != t;
                }
                
            };
        }

        @Override
        public ModelFilter<CountryDAO, CountryModel, ? extends DaoFilter<CountryDAO>> getDefaultFilter() {
            return getAllItemsFilter();
        }

    }

}
