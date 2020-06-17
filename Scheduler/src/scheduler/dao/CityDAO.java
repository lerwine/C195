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
import javafx.event.EventDispatchChain;
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
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.ui.CityModel;
import scheduler.util.DB;
import scheduler.util.InternalException;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.city.EditCity;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.country.EditCountry;
import scheduler.view.country.EditCountryResourceKeys;
import events.DbOperationType;
import events.CityEvent;
import events.CountryEvent;
import events.EventEvaluationStatus;

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

//        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);
        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

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
        @SuppressWarnings("incomplete-switch")
        public CityEvent save(CityEvent event, Connection connection, boolean force) {
            if (event.getStatus() != EventEvaluationStatus.EVALUATING) {
                return event;
            }
            if (event.isConsumed()) {
                event.setCanceled();
                return event;
            }
            CityDAO dao = event.getDataAccessObject();
            switch (dao.getRowState()) {
                case DELETED:
                    throw new IllegalStateException("Data access object already deleted");
                case UNMODIFIED:
                    if (!force) {
                        event.setSucceeded();
                        return event;
                    }
                    break;
            }

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.CITY_ID.getDbName())
                    .append(") FROM ").append(DbTable.CITY.getDbName())
                    .append(" LEFT JOIN ").append(DbTable.COUNTRY.getDbName()).append(" ON ")
                    .append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_COUNTRY.getDbName())
                    .append("=").append(DbTable.COUNTRY.getDbName()).append(".")
                    .append(DbColumn.COUNTRY_ID.getDbName()).append(" WHERE ")
                    .append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_NAME.getDbName())
                    .append(" LIKE ? AND ").append(DbTable.COUNTRY.getDbName()).append(".")
                    .append(DbColumn.COUNTRY_NAME.getDbName()).append("=?");
            if (dao.getRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.CITY_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();

            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, dao.getCountry().getPrimaryKey());
                ps.setString(2, String.format("%s;*", DB.escapeWC(dao.name)));
                if (dao.getRowState() != DataRowState.NEW) {
                    ps.setInt(3, dao.getPrimaryKey());
                }
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
                return event;
            }

            if (count > 0) {
                sb = new StringBuffer("SELECT ").append(DbColumn.CITY_NAME.getDbName())
                        .append(" FROM ").append(DbTable.CITY.getDbName())
                        .append(" LEFT JOIN ").append(DbTable.COUNTRY.getDbName()).append(" ON ")
                        .append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_COUNTRY.getDbName())
                        .append("=").append(DbTable.COUNTRY.getDbName()).append(".")
                        .append(DbColumn.COUNTRY_ID.getDbName()).append(" WHERE ")
                        .append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_NAME.getDbName())
                        .append(" LIKE ? AND ").append(DbTable.COUNTRY.getDbName()).append(".")
                        .append(DbColumn.COUNTRY_NAME.getDbName()).append("=?");
                if (dao.getRowState() != DataRowState.NEW) {
                    sb.append(" AND ").append(DbColumn.CITY_ID.getDbName()).append("<>?");
                }
                String sql2 = sb.toString();
                count = 0;
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, dao.getCountry().getPrimaryKey());
                    ps.setString(2, String.format("%s;*", DB.escapeWC(dao.name)));
                    if (dao.getRowState() != DataRowState.NEW) {
                        ps.setInt(3, dao.getPrimaryKey());
                    }
                    LOG.fine(() -> String.format("Executing DML statement: %s", sql2));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String n = rs.getString(1);
                            int i = n.lastIndexOf(";");
                            if (i >= 0) {
                                n = n.substring(0, i);
                            }
                            if (n.equalsIgnoreCase(dao.name)) {
                                count++;
                            }
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
                    return event;
                }
                if (count > 0) {
                    event.setInvalid("Name already in use", ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYNAMEINUSE));
                    return event;
                }
            }

            ICountryDAO country = ICityDAO.assertValidCity(event.getDataAccessObject()).country;
            if (country instanceof CountryDAO) {
                CountryEvent countryEvent = CountryDAO.FACTORY.save(new CountryEvent(event.getSource(), event.getTarget(), (CountryDAO) country,
                        (country.getRowState() == DataRowState.NEW) ? DbOperationType.INSERTING : DbOperationType.UPDATING), connection, force);
                switch (countryEvent.getStatus()) {
                    case SUCCEEDED:
                        break;
                    case FAULTED:
                        event.setFaulted(countryEvent.getSummaryTitle(), countryEvent.getDetailMessage(), countryEvent.getFault());
                        return event;
                    case INVALID:
                        event.setInvalid(countryEvent.getSummaryTitle(), countryEvent.getDetailMessage());
                        return event;
                    default:
                        event.setCanceled();
                        return event;
                }
            }
            return super.save(event, connection, force);
        }

        @Override
        @SuppressWarnings("incomplete-switch")
        public CityEvent delete(CityEvent event, Connection connection) {
            if (event.getStatus() != EventEvaluationStatus.EVALUATING) {
                return event;
            }
            if (event.isConsumed()) {
                event.setCanceled();
                return event;
            }
            CityDAO dao = event.getDataAccessObject();
            switch (dao.getRowState()) {
                case NEW:
                    throw new IllegalStateException("Data access object was never saved");
                case DELETED:
                    event.setSucceeded();
                    return event;
            }

            CityEvent resultEvent = event;
            int count;
            try {
                count = AddressDAO.FACTORY.countByCity(dao.getPrimaryKey(), connection);
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking dependencies", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                return event;
            }
            switch (count) {
                case 0:
                    resultEvent = super.delete(event, connection);
                    break;
                case 1:
                    event.setInvalid("City in use", ResourceBundleHelper.getResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGSINGLE));
                    break;
                default:
                    event.setInvalid("City in use", ResourceBundleHelper.formatResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLE, count));
                    break;
            }
            return resultEvent;
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
        private final ICountryDAO country;
        private final TimeZone timeZone;

        private Related(int primaryKey, String name, ICountryDAO country, TimeZone zoneId) {
            this.primaryKey = primaryKey;
            this.name = name;
            this.country = country;
            this.timeZone = zoneId;
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
