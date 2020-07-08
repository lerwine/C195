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
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.AnyTrueSet;
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

    private final AnyTrueSet changeIndicator;
    private final AnyTrueSet validityIndicator;
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper changed;
    private final StringProperty name;
    private final ObjectProperty<TimeZone> timeZone;
    private final ObjectProperty<CountryItem<? extends ICountryDAO>> country;
    private final ReadOnlyStringBindingProperty timeZoneDisplay;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty language;
    private final AnyTrueSet.Node nameChanged;
    private final AnyTrueSet.Node nameValid;
    private final AnyTrueSet.Node timeZoneChanged;
    private final AnyTrueSet.Node timeZoneValid;
    private final AnyTrueSet.Node countryValid;
    private final AnyTrueSet.Node countryChanged;

    public CityModel(CityDAO dao) {
        super(dao);
        changeIndicator = new AnyTrueSet();
        validityIndicator = new AnyTrueSet();
        name = new SimpleStringProperty(this, PROP_NAME);
        nameChanged = changeIndicator.add(false);
        nameValid = validityIndicator.add(Values.isNotNullWhiteSpaceOrEmpty(name.get()));
        name.addListener((observable, oldValue, newValue) -> {
            nameValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(newValue));
            String n = dao.getName();
            nameChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        timeZone = new SimpleObjectProperty<>();
        timeZoneChanged = changeIndicator.add(false);
        timeZoneValid = validityIndicator.add(null != timeZone.get());
        timeZone.addListener((observable, oldValue, newValue) -> {
            timeZoneValid.setValid(null != newValue);
            TimeZone t = dao.getTimeZone();
            timeZoneChanged.setValid((null == newValue) ? null == t : null != t && newValue.getID().equals(t.getID()));
        });
        country = new SimpleObjectProperty<>();
        countryValid = validityIndicator.add(null != country.get());
        countryChanged = changeIndicator.add(false);
        country.addListener((observable, oldValue, newValue) -> {
            countryValid.setValid(null != newValue);
            countryChanged.setValid(!ModelHelper.areSameRecord(newValue, dao.getCountry()));
        });
        timeZoneDisplay = new ReadOnlyStringBindingProperty(this, PROP_TIMEZONEDISPLAY, () -> {
            return CityProperties.getTimeZoneDisplayText(timeZone.get());
        }, timeZone);
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(country, Country.PROP_NAME));
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(country, CountryItem.PROP_LANGUAGE));
        name.set(dao.getName());
        timeZone.set(dao.getTimeZone());
        country.set(CountryItem.createModel(dao.getCountry()));
        
        dao.addPropertyChangeListener((evt) -> {
            switch (evt.getPropertyName()) {
                case PROP_NAME:
                    // FIXME: update validity and change
                    break;
                case PROP_TIMEZONE:
                    // FIXME: update validity and change
                    break;
                case PROP_COUNTRY:
                    // FIXME: update validity and change
                    break;
            }
        });
        
        valid = new ReadOnlyBooleanWrapper(this, PROP_VALID, validityIndicator.isValid());
        validityIndicator.validProperty().addListener((observable, oldValue, newValue) -> {
            valid.set(newValue);
        });
        changed = new ReadOnlyBooleanWrapper(this, PROP_CHANGED, changeIndicator.isValid());
        changeIndicator.validProperty().addListener((observable, oldValue, newValue) -> {
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
