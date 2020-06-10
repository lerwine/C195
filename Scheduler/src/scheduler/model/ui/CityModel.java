package scheduler.model.ui;

import java.util.Objects;
import java.util.TimeZone;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventTarget;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.City;
import scheduler.model.CityProperties;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.ModelFilter;
import scheduler.view.event.CityEvent;
import scheduler.view.event.ModelItemEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModel extends FxRecordModel<CityDAO> implements CityItem<CityDAO> {

    public static final Factory FACTORY = new Factory();

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
        name = new SimpleStringProperty(this, PROP_NAME);
        timeZone = new SimpleObjectProperty<>();
        country = new SimpleObjectProperty<>();
        timeZoneDisplay = new ReadOnlyStringBindingProperty(this, PROP_TIMEZONEDISPLAY, () -> {
            return CityProperties.getTimeZoneDisplayText(timeZone.get());
        }, timeZone);
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(country, Country.PROP_NAME));
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(country, CountryItem.PROP_LANGUAGE));
        name.set(dao.getName());
        timeZone.set(dao.getTimeZone());
        country.set(CountryItem.createModel(dao.getCountry()));
        valid = new ReadOnlyBooleanBindingProperty(this, PROP_VALID,
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(name.get()), name)
                        .and(timeZoneDisplay.isNotEmpty()).and(Bindings.selectBoolean(country, PROP_VALID)));
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

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
    }

    @Override
    public int hashCode() {
        if (getRowState() != DataRowState.NEW) {
            return getPrimaryKey();
        }
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(name.get());
        hash = 53 * hash + Objects.hashCode(country.get());
        return hash;
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(primaryKeyProperty());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(name)
                .addDataObject(country)
                .addTimeZone(timeZone)
                .addLocalDateTime(createDateProperty())
                .addString(createdByProperty())
                .addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty())
                .addBoolean(valid);
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
        public CityEvent createInsertEvent(CityModel model, Object source, EventTarget target) {
            return new CityEvent(model, source, target, CityEvent.CITY_INSERTING_EVENT);
        }

        @Override
        public CityEvent createUpdateEvent(CityModel model, Object source, EventTarget target) {
            return new CityEvent(model, source, target, CityEvent.CITY_UPDATING_EVENT);
        }

        @Override
        public CityEvent createDeleteEvent(CityModel model, Object source, EventTarget target) {
            return new CityEvent(model, source, target, CityEvent.CITY_DELETING_EVENT);
        }

        @Override
        public ModelItemEvent<CityModel, CityDAO> createEditRequestEvent(CityModel model, Object source, EventTarget target) {
            return new CityEvent(model, source, target, CityEvent.CITY_EDIT_REQUEST_EVENT);
        }

        @Override
        public ModelItemEvent<CityModel, CityDAO> createDeleteRequestEvent(CityModel model, Object source, EventTarget target) {
            return new CityEvent(model, source, target, CityEvent.CITY_DELETE_REQUEST_EVENT);
        }

    }
}
