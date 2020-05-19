package scheduler.view.country;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
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
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.ui.CountryDbItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.CalculatedBooleanProperty;
import scheduler.observables.NestedObjectValueProperty;
import scheduler.observables.NestedStringProperty;
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

    private final ObjectProperty<PredefinedCountry> predefinedData;
    private final NestedStringProperty<PredefinedCountry> name;
    private final NestedStringProperty<PredefinedCountry> language;
    private final NestedObjectValueProperty<PredefinedCountry, ZoneId> zoneId;
    private final CalculatedBooleanProperty<PredefinedCountry> valid;

    public CountryModel(CountryDAO dao) {
        super(dao);
        predefinedData = new SimpleObjectProperty<>(this, "predefinedData", dao.getPredefinedData());
        name = new NestedStringProperty<>(this, "name", predefinedData, (t) -> t.nameProperty());
        zoneId = new NestedObjectValueProperty<>(this, "zoneId", predefinedData, (t) -> t.zoneIdProperty());
        language = new NestedStringProperty<>(this, "language", predefinedData, (t) -> t.languageProperty());
        valid = new CalculatedBooleanProperty<>(this, "valid", predefinedData, Objects::nonNull);
    }

    @Override
    protected void onDaoPropertyChanged(CountryDAO dao, String propertyName) {
        if (propertyName.equals(CountryDAO.PROP_PREDEFINEDCOUNTRY)) {
            onDataObjectChanged(dao);
        }
    }

    @Override
    protected void onDataObjectChanged(CountryDAO dao) {
        predefinedData.set(dao.getPredefinedCountry());
    }

    @Override
    public PredefinedCountry getPredefinedData() {
        return predefinedData.get();
    }

    public void setPredefinedData(PredefinedCountry value) {
        predefinedData.set(value);
    }

    @Override
    public ObjectProperty<PredefinedCountry> predefinedDataProperty() {
        return predefinedData;
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyStringProperty();
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyObjectProperty();
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyStringProperty();
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
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
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
            dataObject.setPredefinedCountry(item.predefinedData.get());
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
