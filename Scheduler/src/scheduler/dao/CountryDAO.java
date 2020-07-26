package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventDispatchChain;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.events.CountryEvent;
import scheduler.events.CountryFailedEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.model.Country;
import scheduler.model.CountryEntity;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.CountryHelper;
import scheduler.model.PredefinedData;
import scheduler.model.fx.CountryModel;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.Values;
import scheduler.view.country.EditCountry;
import static scheduler.view.country.EditCountryResourceKeys.RESOURCEKEY_SAVECONFLICTMESSAGE;

/**
 * Data access object for the {@code country} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.COUNTRY)
public final class CountryDAO extends DataAccessObject implements PartialCountryDAO, CountryEntity<Timestamp> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(CountryDAO.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(CountryDAO.class.getName());

    public static final FactoryImpl FACTORY = new FactoryImpl();

    private final OriginalValues originalValues;
    private String name;
    private Locale locale;
    private WeakReference<CountryModel> _cachedModel = null;

    /**
     * Initializes a {@link DataRowState#NEW} country data access object.
     */
    public CountryDAO() {
        name = "";
        originalValues = new OriginalValues();
    }

    public CountryDAO(PredefinedData.PredefinedCountry country) {
        name = country.getName();
        locale = country.getLocale();
        originalValues = new OriginalValues();
    }

    @Override
    public synchronized CountryModel cachedModel(boolean create) {
        CountryModel model;
        if (null != _cachedModel) {
            model = _cachedModel.get();
            if (null != model) {
                return model;
            }
            _cachedModel = null;
        }
        if (create) {
            model = CountryModel.FACTORY.createNew(this);
            _cachedModel = new WeakReference<>(model);
            return model;
        }
        return null;
    }

    private synchronized void setCachedModel(CountryModel model) {
        if (null == model) {
            if (null != _cachedModel) {
                if (null != _cachedModel.get()) {
                    _cachedModel.clear();
                }
                _cachedModel = null;
            }
        } else if (null == _cachedModel || !Objects.equals(_cachedModel.get(), model)) {
            _cachedModel = new WeakReference<>(model);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private synchronized void setName(String value) {
        String oldValue = name;
        name = Values.asNonNullAndWsNormalized(value);
        if (!name.equals(oldValue)) {
            firePropertyChange(PROP_NAME, oldValue, name);
            setModified();
        }
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    private synchronized void setLocale(Locale locale) {
        Locale oldLocale = this.locale;
        if (Objects.equals(locale, oldLocale)) {
            return;
        }
        String oldName = name;
        this.locale = locale;
        name = Values.asNonNullAndWsNormalized(CountryHelper.getCountryAndLanguageDisplayText(this.locale));
        firePropertyChange(PROP_LOCALE, oldLocale, this.locale);
        firePropertyChange(PROP_NAME, oldName, name);
        setModified();
    }

    @Override
    protected boolean verifyModified() {
        Locale a = locale;
        Locale b = originalValues.locale;
        return (null == a) ? null != b : null == b || !a.toLanguageTag().equals(b.toLanguageTag());
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.name = name;
        originalValues.locale = locale;
    }

    @Override
    protected void onRejectChanges() {
        String oldName = name;
        Locale oldLocale = locale;
        name = originalValues.name;
        locale = originalValues.locale;
        firePropertyChange(PROP_NAME, oldName, name);
        firePropertyChange(PROP_LOCALE, oldLocale, locale);
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        EventDispatchChain result = FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
        LOG.exiting(LOG.getName(), "buildEventDispatchChain");
        return result;
    }

    @Override
    public int hashCode() {
        if (getRowState() == DataRowState.NEW) {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(name);
            hash = 67 * hash + Objects.hashCode(locale);
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public String toString() {
        return ModelHelper.CountryHelper.appendDaoProperties(this, new StringBuilder(CountryDAO.class.getName()).append(" { ")).append("}").toString();
    }

    /**
     * Factory implementation for {@link CountryDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CountryDAO, CountryModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        void onBeforeSave(CountryModel model) {
            model.dataObject().setLocale(model.getLocale());
        }

        @Override
        public boolean isCompoundSelect() {
            return false;
        }

        @Override
        protected void applyColumnValue(CountryDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case COUNTRY_NAME:
                    ps.setString(index, dao.locale.toLanguageTag());
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public CountryDAO createNew() {
            return new CountryDAO();
        }

        public CountryDAO fromLocale(Locale locale) {
            CountryDAO result = new CountryDAO();
            result.setLocale(locale);
            result.setName(locale.getDisplayCountry());
            return result;
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            return new DmlSelectQueryBuilder(DbTable.COUNTRY, SchemaHelper.getTableColumns(DbTable.COUNTRY));
        }

        @Override
        public DaoFilter<CountryDAO> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
        }

        PartialCountryDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            String n = rs.getString(DbColumn.COUNTRY_NAME.toString());
            return new Partial(rs.getInt(DbColumn.CITY_COUNTRY.toString()), Locale.forLanguageTag(n));
        }

        @Override
        public Class<? extends CountryDAO> getDaoClass() {
            return CountryDAO.class;
        }

        @Override
        protected void onCloneProperties(CountryDAO fromDAO, CountryDAO toDAO) {
            String oldName = toDAO.name;
            Locale oldLocale = toDAO.locale;
            toDAO.name = fromDAO.name;
            toDAO.locale = fromDAO.locale;
            toDAO.originalValues.name = fromDAO.originalValues.name;
            toDAO.originalValues.locale = fromDAO.originalValues.locale;
            toDAO.firePropertyChange(PROP_NAME, oldName, toDAO.name);
            toDAO.firePropertyChange(PROP_LOCALE, oldLocale, toDAO.locale);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CountryDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                Locale oldLocale = dao.locale;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    t.firePropertyChange(PROP_LOCALE, oldLocale, dao.locale);
                }
            };

            String s = rs.getString(DbColumn.COUNTRY_NAME.toString());
            dao.locale = Locale.forLanguageTag(s);
            dao.name = CountryHelper.getCountryAndLanguageDisplayText(dao.locale);

            return propertyChanges;
        }

        public ArrayList<CountryDAO> getAllCountries(Connection connection) throws SQLException {
            String sql = createDmlSelectQueryBuilder().build().toString();
            LOG.fine(() -> String.format("Executing query \"%s\"", sql));
            ArrayList<CountryDAO> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        do {
                            result.add(fromResultSet(rs));
                        } while (rs.next());
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    if (LogHelper.logWarnings(connection, LOG)) {
                        LOG.log(Level.WARNING, "No results.");
                    } else {
                        LOG.log(Level.WARNING, "No results, no warnings.");
                    }
                }
            }
            return result;
        }

        public CountryDAO lookupCacheByRegionCode(final String rc) {
            return findFirstCached((result) -> result.getLocale().getCountry().equals(rc)).orElse(null);
        }

        // PENDING: (FIXME) Complement usages of this method with {@link #lookupCacheByRegionCode(java.lang.String)}
        public CountryDAO getByRegionCode(Connection connection, String regionCode) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().build().toString()).append(" WHERE ")
                    .append(DbColumn.COUNTRY_NAME).append("=?").toString();

            LOG.fine(() -> String.format("Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, regionCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        CountryDAO result = fromResultSet(rs);
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            return null;
        }

        public ArrayList<CountryDAO> getByRegionCodes(Connection connection, Collection<String> regionCodes) throws SQLException {
            StringBuffer buffer = new StringBuffer(createDmlSelectQueryBuilder().build().toString()).append(" WHERE ")
                    .append(DbColumn.COUNTRY_NAME).append("=?");
            int c = regionCodes.size();
            for (int i = 1; i < c; i++) {
                buffer.append(" OR ").append(DbColumn.COUNTRY_NAME).append("=?");
            }
            String sql = buffer.toString();
            LOG.fine(() -> String.format("Executing DML statement: %s", sql));
            ArrayList<CountryDAO> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                Iterator<String> iterator = regionCodes.iterator();
                int idx = 0;
                do {
                    ps.setString(++idx, iterator.next());
                } while (iterator.hasNext());
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        do {
                            result.add(fromResultSet(rs));
                        } while (rs.next());
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    if (LogHelper.logWarnings(connection, LOG)) {
                        LOG.log(Level.WARNING, "No results.");
                    } else {
                        LOG.log(Level.WARNING, "No results, no warnings.");
                    }
                }
            }
            return result;
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
            EventDispatchChain result = CountryModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
            LOG.exiting(LOG.getName(), "buildEventDispatchChain");
            return result;
        }

    }

    public static class SaveTask extends SaveDaoTask<CountryDAO, CountryModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(SaveTask.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(SaveTask.class.getName());

        private static final String ERROR_CHECKING_CONFLICTS = "Error checking country naming conflicts";

        public SaveTask(CountryModel model, boolean alreadyValidated) {
            super(model, CountryModel.FACTORY, alreadyValidated);
        }

        @Override
        protected CountryEvent validate(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "validate", connection);
            CountryModel targetModel = getEntityModel();
            CountryEvent saveEvent = CountryModel.FACTORY.validateForSave(targetModel);
            if (null != saveEvent && saveEvent instanceof CountryFailedEvent) {
                return saveEvent;
            }
            CountryDAO dao = getDataAccessObject();
            StringBuilder sb = new StringBuilder("SELECT COUNT(").append(DbColumn.COUNTRY_ID.getDbName())
                    .append(") FROM ").append(DbTable.COUNTRY.getDbName()).append(" WHERE ").append(DbColumn.COUNTRY_NAME.getDbName()).append("=?");
            if (getOriginalRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.COUNTRY_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();
            String lt = dao.locale.toLanguageTag();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, lt);
                if (getOriginalRowState() != DataRowState.NEW) {
                    ps.setInt(2, dao.getPrimaryKey());
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        LogHelper.logWarnings(connection, LOG);
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, ERROR_CHECKING_CONFLICTS, ex);
                throw new OperationFailureException(ERROR_CHECKING_CONFLICTS, ex);
            }

            CountryEvent resultEvent;
            if (count > 0) {
                if (getOriginalRowState() == DataRowState.NEW) {
                    resultEvent = CountryEvent.createInsertInvalidEvent(targetModel, this, ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_SAVECONFLICTMESSAGE));
                } else {
                    resultEvent = CountryEvent.createUpdateInvalidEvent(targetModel, this, ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_SAVECONFLICTMESSAGE));
                }
            } else {
                resultEvent = null;
            }
            LOG.exiting(LOG.getName(), "validate", resultEvent);
            return resultEvent;
        }

        @Override
        protected CountryEvent createSuccessEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return CountryEvent.createInsertSuccessEvent(getEntityModel(), this);
            }
            return CountryEvent.createUpdateSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected CountryEvent createCanceledEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return CountryEvent.createInsertCanceledEvent(getEntityModel(), this);
            }
            return CountryEvent.createUpdateCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected CountryEvent createFaultedEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return CountryEvent.createInsertFaultedEvent(getEntityModel(), this, getException());
            }
            return CountryEvent.createUpdateFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            CountryEvent event = (CountryEvent) getValue();
            if (null != event && event instanceof CountrySuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    public static final class DeleteTask extends DeleteDaoTask<CountryDAO, CountryModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DeleteTask.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(DeleteTask.class.getName());

        private static final String ERROR_CHECKING_DEPENDENCIES = "Error checking dependencies";

        public DeleteTask(CountryModel target, boolean alreadyValidated) {
            super(target, CountryModel.FACTORY, alreadyValidated);
        }

        @Override
        protected CountryEvent validate(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "validate", connection);
            CountryDAO dao = getDataAccessObject();
            int count;
            try {
                count = CityDAO.FACTORY.countByCountry(dao.getPrimaryKey(), connection);
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, ERROR_CHECKING_DEPENDENCIES, ex);
                throw new OperationFailureException(ERROR_CHECKING_DEPENDENCIES, ex);
            }
            CountryEvent resultEvent;
            switch (count) {
                case 0:
                    resultEvent = null;
                    break;
                case 1:
                    resultEvent = CountryEvent.createDeleteInvalidEvent(getEntityModel(), this,
                            ResourceBundleHelper.getResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGSINGLECOUNTRY));
                    break;
                default:
                    resultEvent = CountryEvent.createDeleteInvalidEvent(getEntityModel(), this,
                            ResourceBundleHelper.formatResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLECOUNTRY, count));
                    break;
            }
            LOG.exiting(LOG.getName(), "validate", resultEvent);
            return resultEvent;
        }

        @Override
        protected CountryEvent createSuccessEvent() {
            return CountryEvent.createDeleteSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected CountryEvent createCanceledEvent() {
            return CountryEvent.createDeleteCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected CountryEvent createFaultedEvent() {
            return CountryEvent.createDeleteFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            CountryEvent event = (CountryEvent) getValue();
            if (null != event && event instanceof CountrySuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    public final static class Partial extends PropertyBindable implements PartialCountryDAO {

        private final int primaryKey;
        private final String name;
        private final Locale locale;

        private Partial(int primaryKey, Locale locale) {
            this.primaryKey = primaryKey;
            this.locale = locale;
            this.name = CountryHelper.getCountryAndLanguageDisplayText(this.locale);
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public String toString() {
            return ModelHelper.CountryHelper.appendPartialDaoProperties(this, new StringBuilder(Partial.class.getName()).append(" { ")).append("}").toString();
        }

    }

    private class OriginalValues {

        private String name;
        private Locale locale;

        private OriginalValues() {
            this.name = CountryDAO.this.name;
            this.locale = CountryDAO.this.locale;
        }
    }

}
