package scheduler.model.ui;

import java.util.Locale;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.Country;
import scheduler.model.CountryProperties;
import static scheduler.model.CountryProperties.MAX_LENGTH_NAME;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.AnyTrueSet;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryModel extends FxRecordModel<CountryDAO> implements CountryItem<CountryDAO> {

    public static final Factory FACTORY = new Factory();

    private final AnyTrueSet changeIndicator;
    private final AnyTrueSet validityIndicator;
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper changed;
    private final ObjectProperty<Locale> locale;
    private final ReadOnlyStringBindingProperty name;
    private final ReadOnlyStringBindingProperty language;
    private final AnyTrueSet.Node localeChanged;
    private final AnyTrueSet.Node localeValid;

    public CountryModel(CountryDAO dao) {
        super(dao);
        changeIndicator = new AnyTrueSet();
        validityIndicator = new AnyTrueSet();
        locale = new SimpleObjectProperty<>(this, PROP_LOCALE, dao.getLocale());
        localeChanged = changeIndicator.add(false);
        localeValid = validityIndicator.add(null != locale.get());
        locale.addListener((observable, oldValue, newValue) -> {
            localeValid.setValid(null != newValue);
            Locale l = dao.getLocale();
            localeChanged.setValid((null == newValue) ? null == l : null != l && l.toLanguageTag().equals(newValue.toLanguageTag()));
        });
        name = new ReadOnlyStringBindingProperty(this, PROP_NAME, () -> CountryProperties.getCountryDisplayText(locale.get()), locale);
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, () -> CountryProperties.getLanguageDisplayText(locale.get()), locale);

        dao.addPropertyChangeListener((evt) -> {
            if (evt.getPropertyName() == PROP_LOCALE) {
                // FIXME: update validity and change
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

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public Locale getLocale() {
        return locale.get();
    }

    public void setLocale(Locale value) {
        locale.set(value);
    }

    @Override
    public ObjectProperty<Locale> localeProperty() {
        return locale;
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
        return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
    }

    @Override
    public int hashCode() {
        if (getRowState() == DataRowState.NEW) {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(name.get());
            hash = 67 * hash + Objects.hashCode(locale.get());
            return hash;
        }
        return getPrimaryKey();
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
                .addLocale(locale)
                .addLocalDateTime(createDateProperty())
                .addString(createdByProperty())
                .addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty())
                .addBoolean(valid);
    }

    public final static class Factory extends FxRecordModel.FxModelFactory<CountryDAO, CountryModel> {

        // Singleton
        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<CountryDAO> getDaoFactory() {
            return CountryDAO.FACTORY;
        }

        @Override
        public CountryModel createNew(CountryDAO dao) {
            return new CountryModel(dao);
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

        @Override
        public DataAccessObject.SaveDaoTask<CountryDAO, CountryModel> createSaveTask(CountryModel model, boolean force) {
            return new CountryDAO.SaveTask(model, force);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<CountryDAO, CountryModel> createDeleteTask(CountryModel model) {
            return new CountryDAO.DeleteTask(model);
        }

        @Override
        public String validateProperties(CountryModel target) {
            CountryDAO dao = target.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                return "Country has already been deleted";
            }
            String name = dao.getName();
            if (name.isEmpty()) {
                return "Country name not defined";
            }
            if (name.length() > MAX_LENGTH_NAME) {
                return "Name too long";
            }
            Locale locale = dao.getLocale();
            if (null == locale) {
                return "Locale not defined";
            }
            if (locale.getDisplayCountry().isEmpty()) {
                return "Locale does not specify a country";
            }
            if (locale.getDisplayLanguage().isEmpty()) {
                return "Locale does not specify a language";
            }
            return null;
        }

    }

}
