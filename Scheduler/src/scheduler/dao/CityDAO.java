package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.WeakEventHandler;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import static scheduler.ZoneIdMappings.fromZoneId;
import static scheduler.ZoneIdMappings.toZoneId;
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
import scheduler.events.CountryEvent;
import scheduler.events.DbOperationType;
import scheduler.events.EventEvaluationStatus;
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.CountryModel;
import scheduler.util.DB;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.city.EditCity;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.country.EditCountry;
import scheduler.view.country.EditCountryResourceKeys;

/**
 * Data access object for the {@code city} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.CITY)
public final class CityDAO extends DataAccessObject implements CityDbRecord {

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final Logger LOG = Logger.getLogger(CityDAO.class.getName());

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private final OriginalValues originalValues;
    private String name;
    private ICountryDAO country;
    private TimeZone timeZone;
    private WeakEventHandler<CountryEvent> countryChangeHandler;

    /**
     * Initializes a {@link DataRowState#NEW} city object.
     */
    public CityDAO() {
        super();
        name = "";
        country = null;
        originalValues = new OriginalValues();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String value) {
        String oldValue = name;
        name = Values.asNonNullAndWsNormalized(value);
        firePropertyChange(PROP_NAME, oldValue, name);
    }

    @Override
    public ICountryDAO getCountry() {
        return country;
    }

    public void setCountry(ICountryDAO country) {
        ICountryDAO oldValue = this.country;
        this.country = country;
        firePropertyChange(PROP_COUNTRY, oldValue, this.country);
        if (null == country || country instanceof CountryDAO) {
            if (null != countryChangeHandler) {
                CountryDAO.FACTORY.removeEventHandler(CountryEvent.COUNTRY_MODEL_EVENT_TYPE, countryChangeHandler);
                countryChangeHandler = null;
            }
        } else if (null == countryChangeHandler) {
            countryChangeHandler = new WeakEventHandler<>(this::onCountryEvent);
            CountryDAO.FACTORY.addEventHandler(CountryEvent.COUNTRY_MODEL_EVENT_TYPE, countryChangeHandler);
        }
    }

    private void onCountryEvent(CountryEvent event) {
        ICountryDAO newValue = event.getDataAccessObject();
        if (newValue.getPrimaryKey() == country.getPrimaryKey()) {
            CountryDAO.FACTORY.removeEventHandler(CountryEvent.COUNTRY_MODEL_EVENT_TYPE, countryChangeHandler);
            countryChangeHandler = null;
            ICountryDAO oldValue = country;
            country = newValue;
            firePropertyChange(PROP_COUNTRY, oldValue, country);
        }
    }

    @Override
    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone zoneId) {
        TimeZone oldValue = this.timeZone;
        this.timeZone = zoneId;
        firePropertyChange(PROP_TIMEZONE, oldValue, this.timeZone);
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.name = name;
        originalValues.country = country;
        originalValues.timeZone = timeZone;
    }

    @Override
    protected void onRejectChanges() {
        String oldName = name;
        ICountryDAO oldCountry = country;
        TimeZone oldTimeZone = timeZone;
        name = originalValues.name;
        country = originalValues.country;
        timeZone = originalValues.timeZone;
        firePropertyChange(PROP_NAME, oldName, name);
        firePropertyChange(PROP_COUNTRY, oldCountry, country);
        firePropertyChange(PROP_TIMEZONE, oldTimeZone, timeZone);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.fine(() -> String.format("Adding %s to dispatch chain", FACTORY.getClass().getName()));
        return FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
    }

    @Override
    public int hashCode() {
        if (getRowState() == DataRowState.NEW) {
            int hash = 7;
            hash = 89 * hash + Objects.hashCode(name);
            hash = 89 * hash + Objects.hashCode(country);
            hash = 89 * hash + Objects.hashCode(timeZone);
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(PROP_PRIMARYKEY, getPrimaryKey());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(PROP_NAME, name)
                .addDataObject(PROP_COUNTRY, country)
                .addTimeZone(PROP_TIMEZONE, timeZone)
                .addTimestamp(PROP_CREATEDATE, getCreateDate())
                .addString(PROP_CREATEDBY, getCreatedBy())
                .addTimestamp(PROP_LASTMODIFIEDDATE, getLastModifiedDate())
                .addString(PROP_LASTMODIFIEDBY, getLastModifiedBy());
    }

    /**
     * Factory implementation for {@link CityDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CityDAO, CityEvent> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        void insert(CityEvent event, Connection connection) {
            if (event.getOperation() != DbOperationType.DB_INSERT || event.getStatus() != EventEvaluationStatus.EVALUATING) {
                throw new IllegalArgumentException();
            }
            String sql = "SELECT COUNT(" + DbColumn.CITY_ID.getDbName() + ") FROM " + DbTable.CITY.getDbName()
                    + " LEFT JOIN " + DbTable.COUNTRY.getDbName() + " ON " + DbTable.CITY.getDbName() + "." + DbColumn.CITY_COUNTRY.getDbName()
                    + "=" + DbTable.COUNTRY.getDbName() + "." + DbColumn.COUNTRY_ID.getDbName()
                    + " WHERE " + DbTable.CITY.getDbName() + "." + DbColumn.CITY_NAME.getDbName()
                    + " LIKE ? AND " + DbTable.COUNTRY.getDbName() + "." + DbColumn.COUNTRY_NAME.getDbName() + "=?";
            CityDAO dao = ICityDAO.assertValidCity(event.getDataAccessObject());
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, dao.getCountry().getPrimaryKey());
                ps.setString(2, String.format("%s;*", DB.escapeWC(dao.name)));
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking city naming conflicts", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }

            if (count > 0) {
                event.setInvalid("Name already in use", ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYNAMEINUSE));
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }

            ICountryDAO country;
            CountryItem<? extends ICountryDAO> cm;
            CityModel model = event.getModel();
            if (null == model) {
                cm = null;
                country = ICityDAO.assertValidCity(event.getDataAccessObject()).getCountry();
            } else {
                country = (cm = model.getCountry()).dataObject();
            }
            if (country instanceof CountryDAO) {
                CountryEvent countryEvent;
                switch (country.getRowState()) {
                    case NEW:
                        // FIXME: INSERT_VALIDATION event needs to be created from FX app thread and fired on DAO
                        if (null != cm && cm instanceof CountryModel) {
                            countryEvent = CountryDAO.FACTORY.badInsert(new CountryEvent((CountryModel) cm, event.getSource(), event.getTarget(),
                                    DbOperationType.INSERT_VALIDATION), connection);
                        } else {
                            countryEvent = CountryDAO.FACTORY.badInsert(new CountryEvent(event.getSource(), event.getTarget(), (CountryDAO) country,
                                    DbOperationType.INSERT_VALIDATION), connection);
                        }
                        break;
                    case UNMODIFIED:
                        super.insert(event, connection);
                        return;
                    default:
                        // FIXME: UPDATE_VALIDATION event needs to be created from FX app thread and fired on DAO
                        if (null != cm && cm instanceof CountryModel) {
                            countryEvent = CountryDAO.FACTORY.badInsert(new CountryEvent((CountryModel) cm, event.getSource(), event.getTarget(),
                                    DbOperationType.UPDATE_VALIDATION), connection);
                        } else {
                            countryEvent = CountryDAO.FACTORY.badInsert(new CountryEvent(event.getSource(), event.getTarget(), (CountryDAO) country,
                                    DbOperationType.UPDATE_VALIDATION), connection);
                        }
                }
                switch (countryEvent.getStatus()) {
                    case SUCCEEDED:
                        super.insert(event, connection);
                        return;
                    case FAULTED:
                        event.setFaulted(countryEvent.getSummaryTitle(), countryEvent.getDetailMessage(), countryEvent.getFault());
                        break;
                    case INVALID:
                        event.setInvalid(countryEvent.getSummaryTitle(), countryEvent.getDetailMessage());
                        break;
                    default:
                        event.setCanceled();
                        break;
                }
                Platform.runLater(() -> Event.fireEvent(dao, event));
            } else {
                super.insert(event, connection);
            }
        }

        @Override
        void update(CityEvent event, Connection connection) {
            if (event.getOperation() != DbOperationType.DB_UPDATE || event.getStatus() != EventEvaluationStatus.EVALUATING) {
                throw new IllegalArgumentException();
            }
            super.update(event, connection);
            String sql = "SELECT COUNT(" + DbColumn.CITY_ID.getDbName() + ") FROM " + DbTable.CITY.getDbName()
                    + " LEFT JOIN " + DbTable.COUNTRY.getDbName() + " ON " + DbTable.CITY.getDbName() + "." + DbColumn.CITY_COUNTRY.getDbName()
                    + "=" + DbTable.COUNTRY.getDbName() + "." + DbColumn.COUNTRY_ID.getDbName()
                    + " WHERE " + DbTable.CITY.getDbName() + "." + DbColumn.CITY_NAME.getDbName()
                    + " LIKE ? AND " + DbTable.COUNTRY.getDbName() + "." + DbColumn.COUNTRY_NAME.getDbName() + "=?"
                    + " AND " + DbColumn.CITY_ID.getDbName() + "<>?";
            CityDAO dao = ICityDAO.assertValidCity(event.getDataAccessObject());
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, dao.getCountry().getPrimaryKey());
                ps.setString(2, String.format("%s;*", DB.escapeWC(dao.name)));
                ps.setInt(3, dao.getPrimaryKey());
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking city naming conflicts", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }

            if (count > 0) {
                event.setInvalid("Name already in use", ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYNAMEINUSE));
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }

            ICountryDAO country;
            CountryItem<? extends ICountryDAO> cm;
            CityModel model = event.getModel();
            if (null == model) {
                cm = null;
                country = ICityDAO.assertValidCity(event.getDataAccessObject()).getCountry();
            } else {
                country = (cm = model.getCountry()).dataObject();
            }
            if (country instanceof CountryDAO) {
                CountryEvent countryEvent;
                switch (country.getRowState()) {
                    case NEW:
                        // FIXME: INSERT_VALIDATION event needs to be created from FX app thread and fired on DAO
                        if (null != cm && cm instanceof CountryModel) {
                            countryEvent = CountryDAO.FACTORY.badInsert(new CountryEvent((CountryModel) cm, event.getSource(), event.getTarget(),
                                    DbOperationType.INSERT_VALIDATION), connection);
                        } else {
                            countryEvent = CountryDAO.FACTORY.badInsert(new CountryEvent(event.getSource(), event.getTarget(), (CountryDAO) country,
                                    DbOperationType.INSERT_VALIDATION), connection);
                        }
                        break;
                    case UNMODIFIED:
                        super.update(event, connection);
                        return;
                    default:
                        // FIXME: UPDATE_VALIDATION event needs to be created from FX app thread and fired on DAO
                        if (null != cm && cm instanceof CountryModel) {
                            countryEvent = CountryDAO.FACTORY.badUpdate(new CountryEvent((CountryModel) cm, event.getSource(), event.getTarget(),
                                    DbOperationType.UPDATE_VALIDATION), connection);
                        } else {
                            countryEvent = CountryDAO.FACTORY.badUpdate(new CountryEvent(event.getSource(), event.getTarget(), (CountryDAO) country,
                                    DbOperationType.UPDATE_VALIDATION), connection);
                        }
                }
                switch (countryEvent.getStatus()) {
                    case SUCCEEDED:
                        super.update(event, connection);
                        return;
                    case FAULTED:
                        event.setFaulted(countryEvent.getSummaryTitle(), countryEvent.getDetailMessage(), countryEvent.getFault());
                    case INVALID:
                        event.setInvalid(countryEvent.getSummaryTitle(), countryEvent.getDetailMessage());
                    default:
                        event.setCanceled();
                }
                Platform.runLater(() -> Event.fireEvent(dao, event));
            } else {
                super.update(event, connection);
            }
        }

        @Override
        protected void delete(CityEvent event, Connection connection) {
            if (event.getOperation() != DbOperationType.DB_DELETE || event.getStatus() != EventEvaluationStatus.EVALUATING) {
                throw new IllegalArgumentException();
            }
            CityDAO dao = event.getDataAccessObject();
            int count;
            try {
                count = AddressDAO.FACTORY.countByCity(dao.getPrimaryKey(), connection);
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking dependencies", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }
            switch (count) {
                case 0:
                    super.delete(event, connection);
                    return;
                case 1:
                    event.setInvalid("City in use", ResourceBundleHelper.getResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGSINGLE));
                    break;
                default:
                    event.setInvalid("City in use", ResourceBundleHelper.formatResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLE, count));
                    break;
            }
            Platform.runLater(() -> Event.fireEvent(dao, event));
        }

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(CityDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case CITY_NAME:
                    ps.setString(index, String.format("%s;%s", dao.name, fromZoneId(dao.timeZone.toZoneId().getId())));
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

        ICityDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            String s = rs.getString(DbColumn.CITY_NAME.toString());
            int i = s.lastIndexOf(";");
            if (i < 0) {
                return new Related(rs.getInt(DbColumn.ADDRESS_CITY.toString()), s, CountryDAO.FACTORY.fromJoinedResultSet(rs), null);
            }
            return new Related(rs.getInt(DbColumn.ADDRESS_CITY.toString()), s.substring(0, i).trim(), CountryDAO.FACTORY.fromJoinedResultSet(rs),
                    TimeZone.getTimeZone(ZoneId.of(toZoneId(s.substring(i + 1).trim()))));
        }

        @Override
        public Class<? extends CityDAO> getDaoClass() {
            return CityDAO.class;
        }

        @Override
        protected void onCloneProperties(CityDAO fromDAO, CityDAO toDAO) {
            String oldName = toDAO.name;
            ICountryDAO oldCountry = toDAO.country;
            TimeZone oldZoneId = toDAO.timeZone;
            toDAO.name = fromDAO.name;
            toDAO.country = fromDAO.country;
            toDAO.timeZone = fromDAO.timeZone;
            toDAO.originalValues.name = fromDAO.originalValues.name;
            toDAO.originalValues.country = fromDAO.originalValues.country;
            toDAO.originalValues.timeZone = fromDAO.originalValues.timeZone;
            toDAO.firePropertyChange(PROP_NAME, oldName, toDAO.name);
            toDAO.firePropertyChange(PROP_TIMEZONE, oldZoneId, toDAO.timeZone);
            toDAO.firePropertyChange(PROP_COUNTRY, oldCountry, toDAO.country);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CityDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                TimeZone oldZoneId = dao.timeZone;
                private final Country oldCountry = dao.country;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    t.firePropertyChange(PROP_TIMEZONE, oldZoneId, dao.timeZone);
                    t.firePropertyChange(PROP_COUNTRY, oldCountry, dao.country);
                }
            };

            String s = rs.getString(DbColumn.CITY_NAME.toString());
            int i = s.lastIndexOf(";");
            if (i < 0) {
                dao.name = s;
                dao.timeZone = null;
            } else {
                dao.name = s.substring(0, i).trim();
                dao.timeZone = TimeZone.getTimeZone(ZoneId.of(toZoneId(s.substring(i + 1).trim())));
            }
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
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        result.add(fromResultSet(rs));
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            }
            return result;
        }

        public CityDAO getByResourceKey(Connection connection, String rk) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().build().toString()).append(" WHERE ")
                    .append(DbColumn.CITY_NAME.getDbName()).append("=?").toString();
            LOG.fine(() -> String.format("getByResourceKey", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, rk);
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        CityDAO result = fromResultSet(rs);
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        return result;
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            }
            return null;
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
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        return result;
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        protected CityEvent createDbOperationEvent(CityEvent sourceEvent, DbOperationType operation) {
            CityModel model = sourceEvent.getModel();
            if (null != model) {
                return new CityEvent(model, sourceEvent.getSource(), this, operation);
            }
            return new CityEvent(sourceEvent.getSource(), this, sourceEvent.getDataAccessObject(), operation);
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.fine(() -> String.format("Adding %s to dispatch chain", CityModel.FACTORY.getClass().getName()));
            return CityModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
        }

    }

    public static final class Related extends PropertyBindable implements ICityDAO {

        private final int primaryKey;
        private final String name;
        private ICountryDAO country;
        private final TimeZone timeZone;
        private WeakEventHandler<CountryEvent> countryChangeHandler;

        private Related(int primaryKey, String name, ICountryDAO country, TimeZone zoneId) {
            this.primaryKey = primaryKey;
            this.name = name;
            this.country = country;
            this.timeZone = zoneId;
            if (!(null == country || country instanceof CountryDAO)) {
                countryChangeHandler = new WeakEventHandler<>(this::onCountryEvent);
                CountryDAO.FACTORY.addEventHandler(CountryEvent.COUNTRY_MODEL_EVENT_TYPE, countryChangeHandler);
            }
        }

        private void onCountryEvent(CountryEvent event) {
            ICountryDAO newValue = event.getDataAccessObject();
            if (newValue.getPrimaryKey() == country.getPrimaryKey()) {
                CountryDAO.FACTORY.removeEventHandler(CountryEvent.COUNTRY_MODEL_EVENT_TYPE, countryChangeHandler);
                countryChangeHandler = null;
                ICountryDAO oldValue = country;
                country = newValue;
                firePropertyChange(PROP_COUNTRY, oldValue, country);
            }
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
        public ICountryDAO getCountry() {
            return country;
        }

        @Override
        public TimeZone getTimeZone() {
            return timeZone;
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
            return toStringBuilder().build();
        }

        @Override
        public ToStringPropertyBuilder toStringBuilder() {
            return ToStringPropertyBuilder.create(this)
                    .addNumber(PROP_PRIMARYKEY, getPrimaryKey())
                    .addString(PROP_NAME, name)
                    .addDataObject(PROP_COUNTRY, country);
        }

    }

    private class OriginalValues {

        private String name;
        private ICountryDAO country;
        private TimeZone timeZone;

        private OriginalValues() {
            this.name = CityDAO.this.name;
            this.country = CityDAO.this.country;
            this.timeZone = CityDAO.this.timeZone;
        }
    }

}
