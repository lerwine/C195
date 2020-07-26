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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.IntColumnValueFilter;
import scheduler.dao.filter.value.IntValueFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.events.CityEvent;
import scheduler.events.CityFailedEvent;
import scheduler.events.CitySuccessEvent;
import scheduler.events.CountryEvent;
import scheduler.events.CountryFailedEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.model.City;
import scheduler.model.CityEntity;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.model.fx.CityModel;
import scheduler.model.fx.CountryModel;
import scheduler.model.fx.PartialCountryModel;
import scheduler.util.DB;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.Values;
import scheduler.view.city.EditCity;
import static scheduler.view.city.EditCityResourceKeys.RESOURCEKEY_CITYNAMEINUSE;
import scheduler.view.country.EditCountry;
import scheduler.view.country.EditCountryResourceKeys;

/**
 * Data access object for the {@code city} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.CITY)
public final class CityDAO extends DataAccessObject implements PartialCityDAO, CityEntity<Timestamp> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(CityDAO.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(CityDAO.class.getName());

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final EventHandler<CountrySuccessEvent> COUNTRY_UPDATE_EVENT_HANDLER;

    static {
        COUNTRY_UPDATE_EVENT_HANDLER = FACTORY::onCountrySaved;
        CountryDAO.FACTORY.addEventHandler(CountrySuccessEvent.UPDATE_SUCCESS, COUNTRY_UPDATE_EVENT_HANDLER);
    }

    private final OriginalValues originalValues;
    private String name;
    private PartialCountryDAO country;
    private WeakReference<CityModel> _cachedModel = null;

    /**
     * Initializes a {@link DataRowState#NEW} city object.
     */
    public CityDAO() {
        name = "";
        country = null;
        originalValues = new OriginalValues();
    }

    public CityDAO(PredefinedData.PredefinedCity city) {
        name = city.getName();
        country = city.getCountry().getDataAccessObject();
        originalValues = new OriginalValues();
    }

    @Override
    public synchronized CityModel cachedModel(boolean create) {
        CityModel model;
        if (null != _cachedModel) {
            model = _cachedModel.get();
            if (null != model) {
                return model;
            }
            _cachedModel = null;
        }
        if (create) {
            model = CityModel.FACTORY.createNew(this);
            _cachedModel = new WeakReference<>(model);
            return model;
        }
        return null;
    }

    private synchronized void setCachedModel(CityModel model) {
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
    public PartialCountryDAO getCountry() {
        return country;
    }

    synchronized void setCountry(PartialCountryDAO country) {
        PartialCountryDAO oldValue = this.country;
        if (Objects.equals(oldValue, country)) {
            return;
        }
        this.country = country;
        firePropertyChange(PROP_COUNTRY, oldValue, this.country);
        setModified();
    }

    @Override
    protected boolean verifyModified() {
        return !(name.equals(originalValues.name) && ModelHelper.areSameRecord(country, originalValues.country));
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.name = name;
        originalValues.country = country;
    }

    @Override
    protected void onRejectChanges() {
        String oldName = name;
        PartialCountryDAO oldCountry = country;
        name = originalValues.name;
        country = originalValues.country;
        firePropertyChange(PROP_NAME, oldName, name);
        firePropertyChange(PROP_COUNTRY, oldCountry, country);
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
        if (getRowState() != DataRowState.NEW) {
            return getPrimaryKey();
        }
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.country);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = ModelHelper.CityHelper.appendDaoProperties(this, new StringBuilder(CityDAO.class.getName()).append(" { "));
        if (null == getCountry()) {
            return sb.append("}").toString();
        }
        return sb.append(Values.LINEBREAK_STRING).append("}").toString();
    }

    private synchronized void onCountryUpdated(CountryModel newModel) {
        if (null == country) {
            return;
        }
        CountryDAO newDao = newModel.dataObject();
        if (country == newDao || country.getPrimaryKey() != newDao.getPrimaryKey()) {
            return;
        }
        PartialCountryDAO oldCountry = country;
        country = newDao;
        firePropertyChange(PROP_COUNTRY, oldCountry, country);

        CityModel cityModel = cachedModel(false);
        if (null != cityModel) {
            PartialCountryModel<? extends PartialCountryDAO> oldModel = cityModel.getCountry();
            if (null != oldModel && oldModel != newModel) {
                cityModel.setCountry(newModel);
            }
        }
    }

    /**
     * Factory implementation for {@link CityDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CityDAO, CityModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        void onBeforeSave(CityModel model) {
            CityDAO dao = model.dataObject();
            dao.setName(model.getName());
            PartialCountryModel<? extends PartialCountryDAO> c = model.getCountry();
            if (null != c) {
                if (c instanceof CountryModel) {
                    CountryDAO.FACTORY.onBeforeSave((CountryModel) c);
                }
                dao.setCountry(c.dataObject());
            } else {
                dao.setCountry(null);
            }
        }

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(CityDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case CITY_NAME:
                    ps.setString(index, dao.name);
                    break;
                case CITY_COUNTRY:
                    ps.setInt(index, dao.country.getPrimaryKey());
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public CityDAO createNew() {
            return new CityDAO();
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            DmlSelectQueryBuilder builder = new DmlSelectQueryBuilder(DbTable.CITY, SchemaHelper.getTableColumns(DbTable.CITY));
            builder.join(DbColumn.CITY_COUNTRY, TableJoinType.LEFT, DbColumn.COUNTRY_ID,
                    SchemaHelper.getTableColumns(DbTable.COUNTRY, SchemaHelper::isForJoinedData));
            return builder;
        }

        @Override
        public DaoFilter<CityDAO> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
        }

        public DaoFilter<CityDAO> getByCountryFilter(int pk) {
            return DaoFilter.of(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES),
                    IntColumnValueFilter.of(DbColumn.CITY_COUNTRY, IntValueFilter.of(pk, ComparisonOperator.EQUALS),
                            (CityDAO t) -> ModelHelper.getPrimaryKey(t.getCountry())));
        }

        PartialCityDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            String s = rs.getString(DbColumn.CITY_NAME.toString());
            return new Partial(rs.getInt(DbColumn.ADDRESS_CITY.toString()), s, CountryDAO.FACTORY.fromJoinedResultSet(rs));
        }

        @Override
        public Class<? extends CityDAO> getDaoClass() {
            return CityDAO.class;
        }

        @Override
        protected void onCloneProperties(CityDAO fromDAO, CityDAO toDAO) {
            String oldName = toDAO.name;
            PartialCountryDAO oldCountry = toDAO.country;
            toDAO.name = fromDAO.name;
            toDAO.country = fromDAO.country;
            toDAO.originalValues.name = fromDAO.originalValues.name;
            toDAO.originalValues.country = fromDAO.originalValues.country;
            toDAO.firePropertyChange(PROP_NAME, oldName, toDAO.name);
            toDAO.firePropertyChange(PROP_COUNTRY, oldCountry, toDAO.country);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CityDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                private final Country oldCountry = dao.country;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    t.firePropertyChange(PROP_COUNTRY, oldCountry, dao.country);
                }
            };

            dao.name = rs.getString(DbColumn.CITY_NAME.toString());
            dao.country = CountryDAO.FACTORY.fromJoinedResultSet(rs);
            return propertyChanges;
        }

        public ArrayList<CityDAO> getByCountry(Connection connection, int countryId) throws SQLException {
            String sql = createDmlSelectQueryBuilder().build().append(" WHERE ")
                    .append(DbTable.CITY).append(".").append(DbColumn.CITY_COUNTRY).append("=?").toString();
            ArrayList<CityDAO> result = new ArrayList<>();
            LOG.fine(() -> String.format("getByCountry", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, countryId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(fromResultSet(rs));
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            return result;
        }

        public CityDAO lookupCacheByNameAndRegionCode(String name, String regionCode) {
            final String nzName = Values.asNonNullAndWsNormalized(name);
            final String nzRegionCode = Values.asNonNullAndWsNormalized(regionCode);
            return findFirstCached((t) -> {
                if (t.name.equals(nzName)) {
                    PartialCountryDAO c = t.getCountry();
                    if (null != c && c.getLocale().getCountry().equals(nzRegionCode)) {
                        return true;
                    }
                }
                return false;
            }).orElse(null);
        }

        public CityDAO lookupCacheByName(String name, final int countryPk) {
            final String nzName = Values.asNonNullAndWsNormalized(name);
            return findFirstCached((t) -> {
                if (t.name.equals(nzName)) {
                    PartialCountryDAO c = t.getCountry();
                    if (null != c && c.getPrimaryKey() == countryPk) {
                        return true;
                    }
                }
                return false;
            }).orElse(null);
        }

        public CityDAO getByName(Connection connection, String name, int countryId) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().build().toString()).append(" WHERE ")
                    .append(DbTable.CITY).append(".").append(DbColumn.CITY_COUNTRY).append("=? AND ")
                    .append(DbColumn.CITY_NAME.getDbName()).append(" LIKE ?").toString();
            LOG.fine(() -> String.format("Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                name = Values.asNonNullAndWsNormalized(name);
                ps.setInt(1, countryId);
                ps.setString(2, DB.escapeWC(name));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CityDAO result = fromResultSet(rs);
                        if (result.name.equalsIgnoreCase(name)) {
                            LogHelper.logWarnings(connection, LOG);
                            return result;
                        }
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            return null;
        }

        public ArrayList<CityDAO> getByNames(Connection connection, Collection<String> names, int countryId) throws SQLException {
            StringBuffer buffer = new StringBuffer(createDmlSelectQueryBuilder().build().toString()).append(" WHERE ")
                    .append(DbTable.CITY).append(".").append(DbColumn.CITY_COUNTRY);
            int c = names.size();
            if (c > 1) {
                buffer.append("=? AND (").append(DbColumn.CITY_NAME.getDbName()).append(" LIKE ?");
                for (int i = 1; i < c; i++) {
                    buffer.append(" OR ").append(DbColumn.COUNTRY_NAME).append(" LIKE ?");
                }
                buffer.append(")");
            } else {
                buffer.append("=? AND ").append(DbColumn.CITY_NAME.getDbName()).append(" LIKE ?");
            }
            String sql = buffer.toString();
            LOG.fine(() -> String.format("Executing DML statement: %s", sql));
            ArrayList<CityDAO> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, countryId);
                Iterator<String> iterator = names.iterator();
                int idx = 1;
                do {
                    ps.setString(++idx, DB.escapeWC(iterator.next()));
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

        int countByCountry(int primaryKey, Connection connection) throws SQLException {
            String sql = "SELECT COUNT(" + DbColumn.CITY_ID.getDbName() + ") FROM " + DbTable.CITY.getDbName()
                    + " WHERE " + DbColumn.CITY_COUNTRY.getDbName() + "=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, primaryKey);
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int result = rs.getInt(1);
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
            EventDispatchChain result = CityModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
            LOG.exiting(LOG.getName(), "buildEventDispatchChain");
            return result;
        }

        private void onCountrySaved(CountrySuccessEvent event) {
            LOG.entering(LOG.getName(), "onCountrySaved", event);
            CountryModel newModel = event.getEntityModel();
            streamCached().forEach((t) -> t.onCountryUpdated(newModel));
            CountryDAO dao = newModel.dataObject();
            Consumer<PartialCityDAO> cityConsumer = (t) -> {
                if (null != t && t instanceof Partial) {
                    ((Partial) t).onCountryUpdated(dao);
                }
            };
            AddressDAO.FACTORY.streamCached().map((t) -> t.getCity()).forEach(cityConsumer);
            CustomerDAO.FACTORY.streamCached().map((t) -> t.getAddress()).filter(Objects::nonNull).map((t) -> t.getCity()).forEach(cityConsumer);
            AppointmentDAO.FACTORY.streamCached().map((t) -> t.getCustomer()).filter(Objects::nonNull)
                    .map((t) -> t.getAddress()).filter(Objects::nonNull).map((t) -> t.getCity()).forEach(cityConsumer);
            LOG.exiting(LOG.getName(), "onCountrySaved");
        }

    }

    public static class SaveTask extends SaveDaoTask<CityDAO, CityModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(SaveTask.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(SaveTask.class.getName());

        private static final String ERROR_CHECKING_CONFLICTS = "Error checking city name conflicts";

        public SaveTask(CityModel model, boolean alreadyValidated) {
            super(model, CityModel.FACTORY, alreadyValidated);
        }

        @Override
        protected CityEvent validate(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "validate", connection);
            CityModel targetModel = getEntityModel();
            CityEvent saveEvent = CityModel.FACTORY.validateForSave(targetModel);
            if (null != saveEvent && saveEvent instanceof CityFailedEvent) {
                return saveEvent;
            }
            CityDAO dao = getDataAccessObject();
            StringBuilder sb = new StringBuilder("SELECT COUNT(").append(DbColumn.CITY_ID.getDbName())
                    .append(") FROM ").append(DbTable.CITY.getDbName())
                    .append(" LEFT JOIN ").append(DbTable.COUNTRY.getDbName())
                    .append(" ON ").append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_COUNTRY.getDbName())
                    .append("=").append(DbTable.COUNTRY.getDbName()).append(".").append(DbColumn.COUNTRY_ID.getDbName())
                    .append(" WHERE ").append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_NAME.getDbName())
                    .append(" LIKE ? AND ").append(DbTable.COUNTRY.getDbName()).append(".").append(DbColumn.COUNTRY_NAME.getDbName()).append("=?");
            if (getOriginalRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.CITY_ID.getDbName()).append("<>?");
            }

            String sql = sb.toString();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, dao.getCountry().getPrimaryKey());
                ps.setString(2, DB.escapeWC(dao.name));
                if (getOriginalRowState() != DataRowState.NEW) {
                    ps.setInt(3, dao.getPrimaryKey());
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

            if (count > 0) {
                if (getOriginalRowState() == DataRowState.NEW) {
                    return CityEvent.createInsertInvalidEvent(targetModel, this,
                            ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYNAMEINUSE));
                }
                return CityEvent.createUpdateInvalidEvent(targetModel, this,
                        ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYNAMEINUSE));
            }

            PartialCountryModel<? extends PartialCountryDAO> c = targetModel.getCountry();
            CityEvent resultEvent;
            if (c instanceof CountryModel) {
                switch (c.getRowState()) {
                    case NEW:
                    case MODIFIED:
                        CountryDAO.SaveTask saveTask = new CountryDAO.SaveTask((CountryModel) c, false);
                        saveTask.run();
                        CountryEvent event = (CountryEvent) saveTask.get();
                        if (null != event && event instanceof CountryFailedEvent) {
                            if (getOriginalRowState() == DataRowState.NEW) {
                                resultEvent = CityEvent.createInsertInvalidEvent(targetModel, this, (CountryFailedEvent) event);
                            } else {
                                resultEvent = CityEvent.createUpdateInvalidEvent(targetModel, this, (CountryFailedEvent) event);
                            }
                        } else {
                            resultEvent = null;
                        }
                        break;
                    default:
                        resultEvent = null;
                        break;
                }
            } else {
                resultEvent = null;
            }
            LOG.exiting(LOG.getName(), "validate", resultEvent);
            return resultEvent;
        }

        @Override
        protected CityEvent createSuccessEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return CityEvent.createInsertSuccessEvent(getEntityModel(), this);
            }
            return CityEvent.createUpdateSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected CityEvent createCanceledEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return CityEvent.createInsertCanceledEvent(getEntityModel(), this);
            }
            return CityEvent.createUpdateCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected CityEvent createFaultedEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return CityEvent.createInsertFaultedEvent(getEntityModel(), this, getException());
            }
            return CityEvent.createUpdateFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            CityEvent event = (CityEvent) getValue();
            if (null != event && event instanceof CitySuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    public static final class DeleteTask extends DeleteDaoTask<CityDAO, CityModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DeleteTask.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(DeleteTask.class.getName());

        public DeleteTask(CityModel target, boolean alreadyValidated) {
            super(target, CityModel.FACTORY, alreadyValidated);
        }

        @Override
        protected CityEvent validate(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "validate", connection);
            CityDAO dao = getDataAccessObject();
            int count;
            try {
                count = AddressDAO.FACTORY.countByCity(dao.getPrimaryKey(), connection);
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error checking dependencies", ex);
                throw new OperationFailureException("Error checking dependencies", ex);
            }
            CityEvent resultEvent;
            switch (count) {
                case 0:
                    resultEvent = null;
                    break;
                case 1:
                    resultEvent = CityEvent.createDeleteInvalidEvent(getEntityModel(), this,
                            ResourceBundleHelper.getResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGSINGLE));
                    break;
                default:
                    resultEvent = CityEvent.createDeleteInvalidEvent(getEntityModel(), this,
                            ResourceBundleHelper.formatResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLE, count));
                    break;
            }
            LOG.exiting(LOG.getName(), "validate", resultEvent);
            return resultEvent;
        }

        @Override
        protected CityEvent createSuccessEvent() {
            return CityEvent.createDeleteSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected CityEvent createCanceledEvent() {
            return CityEvent.createDeleteCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected CityEvent createFaultedEvent() {
            return CityEvent.createDeleteFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            CityEvent event = (CityEvent) getValue();
            if (null != event && event instanceof CitySuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    public static final class Partial extends PropertyBindable implements PartialCityDAO {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Partial.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(Partial.class.getName());

        private final int primaryKey;
        private final String name;
        private PartialCountryDAO country;

        private Partial(int primaryKey, String name, PartialCountryDAO country) {
            this.primaryKey = primaryKey;
            this.name = name;
            this.country = country;
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
        public PartialCountryDAO getCountry() {
            return country;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public String toString() {
            StringBuilder sb = ModelHelper.CityHelper.appendPartialDaoProperties(this, new StringBuilder(Partial.class.getName()).append(" { "));
            if (null == getCountry()) {
                return sb.append("}").toString();
            }
            return sb.append(Values.LINEBREAK_STRING).append("}").toString();
        }

        synchronized void onCountryUpdated(CountryDAO newDao) {
            if (null == country || country == newDao) {
                return;
            }

            PartialCountryDAO oldCountry = country;
            country = newDao;
            firePropertyChange(PROP_COUNTRY, oldCountry, country);
        }

    }

    private class OriginalValues {

        private String name;
        private PartialCountryDAO country;

        private OriginalValues() {
            this.name = CityDAO.this.name;
            this.country = CityDAO.this.country;
        }
    }

}
