package scheduler.model.ui;

import java.util.TimeZone;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.CityProperties;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.Values;
import scheduler.view.ModelFilter;
import scheduler.view.event.CityMutateEvent;
import scheduler.view.event.ItemMutateEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModel extends FxRecordModel<CityDAO> implements CityItem<CityDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final StringProperty name;
    private final ObjectProperty<TimeZone> timeZone;
    private final ObjectProperty<CountryItem<? extends ICountryDAO>> country;
    private final ReadOnlyStringBindingProperty timeZoneDisplay;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty language;
    private final ReadOnlyBooleanBindingProperty valid;

    public CityModel(CityDAO dao) {
        super(dao);
        name = new SimpleStringProperty(this, "name");
        timeZone = new SimpleObjectProperty<>();
        country = new SimpleObjectProperty<>();
        timeZoneDisplay = new ReadOnlyStringBindingProperty(this, "timeZoneDisplay", () -> {
            return CityProperties.getTimeZoneDisplayText(timeZone.get());
        }, timeZone);
        countryName = new ReadOnlyStringBindingProperty(this, "countryName", Bindings.selectString(country, "name"));
        language = new ReadOnlyStringBindingProperty(this, "language", Bindings.selectString(country, "language"));
        name.set(dao.getName());
        timeZone.set(dao.getTimeZone());
        country.set(CountryItem.createModel(dao.getCountry()));
        valid = new ReadOnlyBooleanBindingProperty(this, "valid",
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(name.get()), name)
                        .and(timeZoneDisplay.isNotEmpty()).and(Bindings.selectBoolean(country, "valid")));
    }

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public CountryItem<? extends ICountryDAO> getCountry() {
        return country.get();
    }

    public void setCountry(CountryItem<? extends ICountryDAO> value) {
        country.set(value);
    }

    @Override
    public ObjectProperty<CountryItem<? extends ICountryDAO>> countryProperty() {
        return country;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName;
    }

    @Override
    public TimeZone getTimeZone() {
        return timeZone.get();
    }

    public void setTimeZone(TimeZone value) {
        timeZone.set(value);
    }

    @Override
    public ObjectProperty<TimeZone> timeZoneProperty() {
        return timeZone;
    }

    @Override
    public String getTimeZoneDisplay() {
        return timeZoneDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty timeZoneDisplayProperty() {
        return timeZoneDisplay;
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    public final static class Factory extends FxRecordModel.ModelFactory<CityDAO, CityModel> {

        // Singleton
        private Factory() {
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DaoFactory<CityDAO> getDaoFactory() {
            return CityDAO.FACTORY;
        }

        @Override
        public CityModel createNew(CityDAO dao) {
            return new CityModel(dao);
        }

        @Override
        public CityDAO updateDAO(CityModel item) {
            CityDAO dao = item.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("City has been deleted");
            }
            dao.setName(item.getName());
            CountryItem<? extends ICountryDAO> c = item.getCountry();
            dao.setCountry((null == c) ? null : c.dataObject());
            dao.setTimeZone(item.getTimeZone());
            return dao;
        }

        @Override
        protected void updateItemProperties(CityModel item, CityDAO dao) {
            item.setName(dao.getName());
            item.setCountry(CountryItem.createModel(dao.getCountry()));
            item.setTimeZone(dao.getTimeZone());
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

        @Override
        public ItemMutateEvent<CityModel> createInsertEvent(CityModel source, Event fxEvent) {
            return new CityMutateEvent(source, CityMutateEvent.CITY_INSERT_EVENT, fxEvent);
        }

        @Override
        public ItemMutateEvent<CityModel> createUpdateEvent(CityModel source, Event fxEvent) {
            return new CityMutateEvent(source, CityMutateEvent.CITY_UPDATE_EVENT, fxEvent);
        }

        @Override
        public ItemMutateEvent<CityModel> createDeleteEvent(CityModel source, Event fxEvent) {
            return new CityMutateEvent(source, CityMutateEvent.CITY_DELETE_EVENT, fxEvent);
        }

    }
}
