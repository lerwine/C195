package scheduler.model.ui;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
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
import scheduler.dao.PartialCountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.events.CityEvent;
import scheduler.events.CityOpRequestEvent;
import scheduler.events.CitySuccessEvent;
import scheduler.events.CountryEvent;
import scheduler.events.CountryFailedEvent;
import scheduler.events.ModelEvent;
import scheduler.model.City;
import scheduler.model.CityEntity;
import static scheduler.model.CityProperties.MAX_LENGTH_NAME;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.LogHelper;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModel extends EntityModel<CityDAO> implements PartialCityModel<CityDAO>, CityEntity<LocalDateTime> {

    public static final Factory FACTORY = new Factory();

    private final StringProperty name;
    private final ObjectProperty<PartialCountryModel<? extends PartialCountryDAO>> country;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty language;

    private CityModel(CityDAO dao) {
        super(dao);
        name = new SimpleStringProperty(this, PROP_NAME, dao.getName());
        country = new SimpleObjectProperty<>(this, PROP_COUNTRY, PartialCountryModel.createModel(dao.getCountry()));
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(country, Country.PROP_NAME));
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(country, PartialCountryModel.PROP_LANGUAGE));
    }

    @Override
    protected void onModelSaved(ModelEvent<CityDAO, ? extends EntityModel<CityDAO>> event) {
        CityDAO dao = event.getDataAccessObject();
        name.set(dao.getName());
        PartialCountryModel<? extends PartialCountryDAO> currentCountry = country.get();
        PartialCountryDAO newCountry = dao.getCountry();
        if (null == currentCountry || null == newCountry) {
            country.set(PartialCountryModel.createModel(dao.getCountry()));
        } else {
            PartialCountryDAO currentDao = currentCountry.dataObject();
            if (currentDao != newCountry && !(ModelHelper.areSameRecord(currentDao, newCountry) && currentDao instanceof CountryDAO)) {
                country.set(PartialCountryModel.createModel(dao.getCountry()));
            }
        }
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
    public PartialCountryModel<? extends PartialCountryDAO> getCountry() {
        return country.get();
    }

    public void setCountry(PartialCountryModel<? extends PartialCountryDAO> value) {
        country.set(value);
    }

    @Override
    public ObjectProperty<PartialCountryModel<? extends PartialCountryDAO>> countryProperty() {
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
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language;
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
                .addLocalDateTime(createDateProperty())
                .addString(createdByProperty())
                .addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty());
    }

    public final static class Factory extends EntityModel.EntityModelFactory<CityDAO, CityModel, CityEvent, CitySuccessEvent> {

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
        protected CityModel onCreateNew(CityDAO dao) {
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
        public DataAccessObject.SaveDaoTask<CityDAO, CityModel, CityEvent> createSaveTask(CityModel model) {
            return new CityDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<CityDAO, CityModel, CityEvent> createDeleteTask(CityModel model) {
            return new CityDAO.DeleteTask(model, false);
        }

        @Override
        public CityEvent validateForSave(CityModel fxRecordModel) {
            CityDAO dao = fxRecordModel.dataObject();
            String message;
            if (dao.getRowState() == DataRowState.DELETED) {
                message = "City has already been deleted";
            } else {
                String name = dao.getName();
                if (name.isEmpty()) {
                    message = "City name not defined";
                } else if (name.length() > MAX_LENGTH_NAME) {
                    message = "Name too long";
                } else {
                    PartialCountryDAO country = dao.getCountry();
                    if (null == country) {
                        message = "Country not specified";
                    } else {
                        CountryEvent event;
                        PartialCountryModel<? extends PartialCountryDAO> c = fxRecordModel.getCountry();
                        if (null != c) {
                            if (c instanceof CountryModel) {
                                if (null == (event = CountryModel.FACTORY.validateForSave((CountryModel) c))) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        } else {
                            event = null;
                        }
                        if (null != event) {
                            if (event instanceof CountryFailedEvent) {
                                if (dao.getRowState() == DataRowState.NEW) {
                                    return CityEvent.createInsertInvalidEvent(fxRecordModel, this, (CountryFailedEvent) event);
                                }
                                return CityEvent.createUpdateInvalidEvent(fxRecordModel, this, (CountryFailedEvent) event);
                            }
                            return null;
                        }

                        message = "City not specified.";
                    }
                }
            }

            if (dao.getRowState() == DataRowState.NEW) {
                return CityEvent.createInsertInvalidEvent(fxRecordModel, this, message);
            }
            return CityEvent.createUpdateInvalidEvent(fxRecordModel, this, message);
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
        public Class<CityEvent> getModelEventClass() {
            return CityEvent.class;
        }

        @Override
        public EventType<CitySuccessEvent> getSuccessEventType() {
            return CitySuccessEvent.SUCCESS_EVENT_TYPE;
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
