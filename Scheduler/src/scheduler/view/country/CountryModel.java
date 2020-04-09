package scheduler.view.country;

import java.util.Objects;
import javafx.beans.property.StringProperty;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_ALLCOUNTRIES;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.observables.NonNullableStringProperty;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.ModelFilter;
import scheduler.view.model.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public final class CountryModel extends ItemModel<CountryDAO> implements CityCountryModel<CountryDAO> {

    private final NonNullableStringProperty name;

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

    public CountryModel(CountryDAO dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
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
            return Objects.hashCode(this.name);
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
                return name.isEqualTo(other.name).get();
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
            item.setName(dao.getName());
        }

        @Override
        public CountryDAO updateDAO(CountryModel item) {
            CountryDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED)
                throw new IllegalArgumentException("Country has been deleted");
            String name = item.name.get();
            if (name.trim().isEmpty())
                throw new IllegalArgumentException("Country name empty");
            dao.setName(name);
            return dao;
        }

        public ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> getAllItemsFilter() {
            return new ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>>() {
                private final String headingText = AppResources.getProperty(RESOURCEKEY_ALLCOUNTRIES);
                private final DaoFilter<CountryDAO> daoFilter = DaoFilter.all(AppResources.getProperty(RESOURCEKEY_READINGFROMDB),
                            AppResources.getProperty(RESOURCEKEY_LOADINGCOUNTRIES));
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

    }

}
