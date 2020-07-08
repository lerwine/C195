package scheduler.model.ui;

import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.City;
import scheduler.model.CityProperties;
import static scheduler.model.CityProperties.MAX_LENGTH_NAME;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.BooleanAggregate;
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

    private final BooleanAggregate unmodifiedIndicator;
    private final BooleanAggregate validityIndicator;
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper changed;
    private final StringProperty name;
    private final ObjectProperty<TimeZone> timeZone;
    private final ObjectProperty<CountryItem<? extends ICountryDAO>> country;
    private final ReadOnlyStringBindingProperty timeZoneDisplay;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty language;

    public CityModel(CityDAO dao) {
        super(dao);
        unmodifiedIndicator = new BooleanAggregate();
        validityIndicator = new BooleanAggregate();
        name = new SimpleStringProperty(this, PROP_NAME, dao.getName());
        unmodifiedIndicator.register(name, dao, PROP_NAME, (t, u) -> {
            return Objects.equals(u.getNewValue(), Values.asNonNullAndTrimmed(t));
        });
        validityIndicator.register(name, (t) -> Values.isNotNullWhiteSpaceOrEmpty(t) && t.length() <= MAX_LENGTH_NAME);
        timeZone = new SimpleObjectProperty<>(this, PROP_TIMEZONE, dao.getTimeZone());
        unmodifiedIndicator.register(timeZone, dao, PROP_TIMEZONE, (t, u) -> {
            Object obj = u.getNewValue();
            if (null != obj && obj instanceof TimeZone) {
                return null != t && ((TimeZone) obj).toZoneId().getId().equals(t.toZoneId().getId());
            }
            return null == t;
        });
        country = new SimpleObjectProperty<>(this, PROP_COUNTRY, CountryItem.createModel(dao.getCountry()));
        unmodifiedIndicator.register(country, dao, PROP_COUNTRY, (t, u) -> {
            Object v = u.getNewValue();
            return (null == v || v instanceof ICountryDAO) && ModelHelper.areSameRecord((ICountryDAO) v, t);
        });
        validityIndicator.register(country, (t) -> null != t);
        unmodifiedIndicator.register(dao, PROP_ROWSTATE, (e) -> Objects.equals(DataRowState.UNMODIFIED, e.getNewValue()));
        timeZoneDisplay = new ReadOnlyStringBindingProperty(this, PROP_TIMEZONEDISPLAY, () -> {
            return CityProperties.getTimeZoneDisplayText(timeZone.get());
        }, timeZone);
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(country, Country.PROP_NAME));
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(country, CountryItem.PROP_LANGUAGE));

        valid = new ReadOnlyBooleanWrapper(this, PROP_VALID, validityIndicator.isAnyTrue());
        validityIndicator.anyTrueProperty().addListener((observable, oldValue, newValue) -> {
            valid.set(newValue);
        });
        changed = new ReadOnlyBooleanWrapper(this, PROP_CHANGED, unmodifiedIndicator.isAnyFalse());
        unmodifiedIndicator.anyFalseProperty().addListener((observable, oldValue, newValue) -> {
            changed.set(newValue);
        });
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
    public boolean isChanged() {
        return changed.get();
    }

    @Override
    public ReadOnlyBooleanProperty changedProperty() {
        return changed;
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

    public final static class Factory extends FxRecordModel.FxModelFactory<CityDAO, CityModel> {

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
        public DataAccessObject.DaoFactory<CityDAO> getDaoFactory() {
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
        public DataAccessObject.SaveDaoTask<CityDAO, CityModel> createSaveTask(CityModel model, boolean force) {
            return new CityDAO.SaveTask(model, force);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<CityDAO, CityModel> createDeleteTask(CityModel model) {
            return new CityDAO.DeleteTask(model);
        }

        @Override
        public String validateProperties(CityModel fxRecordModel) {
            CityDAO dao = fxRecordModel.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                return "City has already been deleted";
            }
            String name = dao.getName();
            if (name.isEmpty()) {
                return "City name not defined";
            }
            TimeZone zoneId = dao.getTimeZone();
            if (null == zoneId) {
                return "Zone Id not defined";
            }
            if ((name.length() + zoneId.toZoneId().getId().length() + 1) > MAX_LENGTH_NAME) {
                return "Name too long";
            }
            CountryItem<? extends ICountryDAO> c = fxRecordModel.getCountry();
            if (null == c) {
                return "Country not specified";
            }
            if (c instanceof CountryModel) {
                String message = CountryModel.FACTORY.validateProperties((CountryModel) c);
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
            }

            return null;
        }

    }
}
