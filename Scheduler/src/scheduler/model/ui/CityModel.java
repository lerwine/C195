package scheduler.model.ui;

import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventType;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.events.CityEvent;
import scheduler.events.CityOpRequestEvent;
import scheduler.events.CountryEvent;
import scheduler.events.CountryFailedEvent;
import scheduler.model.City;
import scheduler.model.CityProperties;
import static scheduler.model.CityProperties.MAX_LENGTH_NAME;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.RecordModelContext;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.LogHelper;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModel extends FxRecordModel<CityDAO> implements CityItem<CityDAO> {

    public static final Factory FACTORY = new Factory();

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

    public final static class Factory extends FxRecordModel.FxModelFactory<CityDAO, CityModel, CityEvent> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Factory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(Factory.class.getName());

        // Singleton
        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<CityDAO, CityEvent> getDaoFactory() {
            return CityDAO.FACTORY;
        }

        @Override
        public CityModel createNew(CityDAO dao) {
            return new CityModel(dao);
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
        public DataAccessObject.SaveDaoTask<CityDAO, CityModel, CityEvent> createSaveTask(RecordModelContext<CityDAO, CityModel> model) {
            return new CityDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<CityDAO, CityModel, CityEvent> createDeleteTask(RecordModelContext<CityDAO, CityModel> model) {
            return new CityDAO.DeleteTask(model, false);
        }

        @Override
        public CityEvent validateForSave(RecordModelContext<CityDAO, CityModel> target) {
            CityDAO dao = target.getDataAccessObject();
            String message;
            if (dao.getRowState() == DataRowState.DELETED) {
                message = "City has already been deleted";
            } else {
                String name = dao.getName();
                if (name.isEmpty()) {
                    message = "City name not defined";
                } else {
                    TimeZone zoneId = dao.getTimeZone();
                    if (null == zoneId) {
                        message = "Zone Id not defined";
                    } else if ((name.length() + zoneId.toZoneId().getId().length() + 1) > MAX_LENGTH_NAME) {
                        message = "Name too long";
                    } else {
                        ICountryDAO country = dao.getCountry();
                        if (null == country) {
                            message = "Country not specified";
                        } else {
                            CityModel fxRecordModel = target.getFxRecordModel();
                            CountryEvent event;
                            if (null != fxRecordModel) {
                                CountryItem<? extends ICountryDAO> c = fxRecordModel.getCountry();
                                if (null != c) {
                                    if (c instanceof CountryModel) {
                                        if (null == (event = CountryModel.FACTORY.validateForSave(RecordModelContext.of((CountryModel) c)))) {
                                            return null;
                                        }
                                    } else {
                                        return null;
                                    }
                                } else {
                                    event = null;
                                }
                            } else {
                                ICountryDAO c = dao.getCountry();
                                if (null != c) {
                                    if (c instanceof CountryDAO) {
                                        if (null == (event = CountryModel.FACTORY.validateForSave(RecordModelContext.of((CountryDAO) c)))) {
                                            return null;
                                        }
                                    } else {
                                        return null;
                                    }
                                } else {
                                    event = null;
                                }
                            }
                            if (null != event) {
                                if (event instanceof CountryFailedEvent) {
                                    if (dao.getRowState() == DataRowState.NEW) {
                                        return CityEvent.createInsertInvalidEvent(target, this, (CountryFailedEvent) event);
                                    }
                                    return CityEvent.createUpdateInvalidEvent(target, this, (CountryFailedEvent) event);
                                }
                                return null;
                            }

                            message = "City not specified.";
                        }
                    }
                }
            }

            if (dao.getRowState() == DataRowState.NEW) {
                return CityEvent.createInsertInvalidEvent(target, this, message);
            }
            return CityEvent.createUpdateInvalidEvent(target, this, message);
        }

        @Override
        public CityOpRequestEvent createEditRequestEvent(CityModel model, Object source) {
            return new CityOpRequestEvent(model, source, false);
        }

        @Override
        public CityOpRequestEvent createDeleteRequestEvent(CityModel model, Object source) {
            return new CityOpRequestEvent(model, source, true);
        }

        @Override
        public EventType<CityOpRequestEvent> getBaseRequestEventType() {
            return CityOpRequestEvent.CITY_OP_REQUEST;
        }

        @Override
        public EventType<CityOpRequestEvent> getEditRequestEventType() {
            return CityOpRequestEvent.EDIT_REQUEST;
        }

        @Override
        public EventType<CityOpRequestEvent> getDeleteRequestEventType() {
            return CityOpRequestEvent.DELETE_REQUEST;
        }

    }
}
