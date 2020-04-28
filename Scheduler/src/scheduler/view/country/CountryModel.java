package scheduler.view.country;

import java.util.Objects;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.ui.CountryDbItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryModel extends FxRecordModel<CountryDAO> implements CountryDbItem<CountryDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final ReadOnlyStringWrapper name;
    private PredefinedCountry predefinedData;

    public CountryModel(CountryDAO dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "name", dao.getName());
        predefinedData = dao.asPredefinedData();
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        return name.get();
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
        return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
    }

    @Override
    public PredefinedCountry asPredefinedData() {
        return predefinedData;
    }

    public final static class Factory extends FxRecordModel.ModelFactory<CountryDAO, CountryModel> {

        private Factory() {
        }

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
            item.predefinedData = dao.getPredefinedCountry();
            item.name.set(item.getName());
        }

        @Override
        public CountryDAO updateDAO(CountryModel item) {
            CountryDAO dataObject = item.getDataObject();
            dataObject.setPredefinedCountry(item.predefinedData);
            return dataObject;
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
