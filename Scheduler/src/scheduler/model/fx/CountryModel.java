package scheduler.model.fx;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventType;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.events.CountryEvent;
import scheduler.events.CountryOpRequestEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.events.ModelEvent;
import scheduler.model.Country;
import scheduler.model.CountryEntity;
import static scheduler.model.CountryProperties.MAX_LENGTH_NAME;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.CountryHelper;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryModel extends EntityModel<CountryDAO> implements PartialCountryModel<CountryDAO>, CountryEntity<LocalDateTime> {

    public static final Factory FACTORY = new Factory();

    private final WeakEventHandlingReference<CountrySuccessEvent> modelEventHandler;
    private final ObjectProperty<Locale> locale;
    private final ReadOnlyStringBindingProperty name;
    private final ReadOnlyStringBindingProperty language;

    public CountryModel(CountryDAO dao) {
        super(dao);
        locale = new SimpleObjectProperty<>(this, PROP_LOCALE, dao.getLocale());
        name = new ReadOnlyStringBindingProperty(this, PROP_NAME, () -> CountryHelper.getCountryDisplayText(locale.get()), locale);
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, () -> CountryHelper.getLanguageDisplayText(locale.get()), locale);
        modelEventHandler = WeakEventHandlingReference.create(this::onModelEvent);
    }

    @Override
    protected void onDaoChanged(ModelEvent<CountryDAO, ? extends EntityModel<CountryDAO>> event) {
        locale.set(event.getDataAccessObject().getLocale());
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
        return ModelHelper.CountryHelper.appendModelProperties(this, new StringBuilder(CountryModel.class.getName()).append(" { ")).append("}").toString();
    }

    public final static class Factory extends EntityModel.EntityModelFactory<CountryDAO, CountryModel> {

        // Singleton
        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<CountryDAO, CountryModel> getDaoFactory() {
            return CountryDAO.FACTORY;
        }

        @Override
        public CountryModel createNew(CountryDAO dao) {
            CountryModel newModel = new CountryModel(dao);
            dao.addEventFilter(CountrySuccessEvent.SUCCESS_EVENT_TYPE, newModel.modelEventHandler.getWeakEventHandler());
            return newModel;
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
        public DataAccessObject.SaveDaoTask<CountryDAO, CountryModel> createSaveTask(CountryModel model) {
            return new CountryDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<CountryDAO, CountryModel> createDeleteTask(CountryModel model) {
            return new CountryDAO.DeleteTask(model, false);
        }

        @Override
        public CountryEvent validateForSave(CountryModel target) {
            CountryDAO dao = target.dataObject();
            String message;
            if (dao.getRowState() == DataRowState.DELETED) {
                message = "Country has already been deleted";
            } else {
                String name = dao.getName();
                if (name.isEmpty()) {
                    message = "Country name not defined";
                } else if (name.length() > MAX_LENGTH_NAME) {
                    message = "Name too long";
                } else {
                    Locale locale = dao.getLocale();
                    if (null == locale) {
                        message = "Locale not defined";
                    } else if (locale.getDisplayCountry().isEmpty()) {
                        message = "Locale does not specify a country";
                    } else if (locale.getDisplayLanguage().isEmpty()) {
                        message = "Locale does not specify a language";
                    } else {
                        return null;
                    }
                }
            }

            if (dao.getRowState() == DataRowState.NEW) {
                return CountryEvent.createInsertInvalidEvent(target, this, message);
            }
            return CountryEvent.createUpdateInvalidEvent(target, this, message);
        }

        @Override
        public Class<CountryEvent> getModelResultEventClass() {
            return CountryEvent.class;
        }

        @Override
        public EventType<CountrySuccessEvent> getSuccessEventType() {
            return CountrySuccessEvent.SUCCESS_EVENT_TYPE;
        }

        @Override
        public CountryOpRequestEvent createEditRequestEvent(CountryModel model, Object source) {
            return new CountryOpRequestEvent(model, source, false);
        }

        @Override
        public CountryOpRequestEvent createDeleteRequestEvent(CountryModel model, Object source) {
            return new CountryOpRequestEvent(model, source, true);
        }

        @Override
        public EventType<CountryOpRequestEvent> getBaseRequestEventType() {
            return CountryOpRequestEvent.COUNTRY_OP_REQUEST;
        }

        @Override
        public EventType<CountryOpRequestEvent> getEditRequestEventType() {
            return CountryOpRequestEvent.EDIT_REQUEST;
        }

        @Override
        public EventType<CountryOpRequestEvent> getDeleteRequestEventType() {
            return CountryOpRequestEvent.DELETE_REQUEST;
        }

    }

}
