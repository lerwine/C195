package scheduler.model.ui;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.observables.DerivedBooleanProperty;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryModel extends FxRecordModel<CountryDAO> implements CountryItem<CountryDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final ObjectProperty<CountryDAO.PredefinedCountryElement> predefinedElement;
    private final ReadOnlyStringWrapper name;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;
    private final DerivedBooleanProperty<CountryDAO.PredefinedCountryElement> valid;

    public CountryModel(CountryDAO dao) {
        super(dao);
        predefinedElement = new SimpleObjectProperty<>(this, "predefinedData", dao.getPredefinedElement());
        name = new ReadOnlyStringWrapper(this, "name", dao.getName());
        zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId", Country.getZoneIdOf(dao));
        language = new ReadOnlyStringWrapper(this, "language", Country.getLanguageOf(dao));
        valid = new DerivedBooleanProperty<>(this, "valid", predefinedElement, Objects::nonNull);
        predefinedElement.addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                Locale locale = newValue.getLocale();
                name.set(locale.getDisplayCountry());
                language.set(locale.getDisplayLanguage());
                zoneId.set(ZoneId.of(newValue.getDefaultZoneId()));
            } else {
                name.set("");
                language.set("");
                zoneId.set(ZoneId.systemDefault());
            }
        });
    }

    @Override
    public CountryDAO.PredefinedCountryElement getPredefinedElement() {
        return predefinedElement.get();
    }

    public void setPredefinedElement(CountryDAO.PredefinedCountryElement value) {
        predefinedElement.set(value);
    }

    @Override
    public ObjectProperty<CountryDAO.PredefinedCountryElement> predefinedElementProperty() {
        return predefinedElement;
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
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyProperty();
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyProperty();
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public int hashCode() {
        if (isNewRow()) {
            return Objects.hashCode(name.get());
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
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
            dataObject.setPredefinedElement(item.predefinedElement.get());
            return dataObject;
        }

        @Override
        protected void updateItemProperties(CountryModel item, CountryDAO dao) {
            item.setPredefinedElement(dao.getPredefinedElement());
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
