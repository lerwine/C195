package scheduler.model.ui;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.ModelHelper;
import scheduler.observables.DerivedBooleanProperty;
import scheduler.observables.DerivedObjectProperty;
import scheduler.observables.DerivedStringProperty;
import scheduler.view.ModelFilter;
import scheduler.model.CustomerCountry;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryModel extends FxRecordModel<CountryDAO> implements CountryItem<CountryDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    public CountryModel(CountryDAO dao) {
        super(dao);
        
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#isValid
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#validProperty
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#nameProperty
    }

    @Override
    public ZoneId getZoneId() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#getZoneId
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#zoneIdProperty
    }

    @Override
    public String getDefaultTimeZoneDisplay() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#getDefaultTimeZoneDisplay
    }

    @Override
    public ReadOnlyStringProperty defaultTimeZoneDisplayProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#defaultTimeZoneDisplayProperty
    }

    @Override
    public String getLanguage() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#getLanguage
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#languageProperty
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#getName
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#getLocale
    }

    public final static class Factory extends FxRecordModel.ModelFactory<CountryDAO, CountryModel> {

        // Singleton
        private Factory() {
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
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
        public CountryDAO updateDAO(CountryModel item) {
            CountryDAO dataObject = item.getDataObject();
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#isValid
        }

        @Override
        protected void updateItemProperties(CountryModel item, CountryDAO dao) {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#isValid
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
