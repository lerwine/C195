package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.event.CityDaoEvent;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DbChangeType;
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
import scheduler.util.DB;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
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

    public static final int MAX_LENGTH_NAME = 50;

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'country' property.
     */
    public static final String PROP_COUNTRY = "country";

    /**
     * The name of the 'zoneId' property.
     */
    public static final String PROP_ZONEID = "zoneId";

    public static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private ICountryDAO country;
    private ZoneId zoneId;

    /**
     * Initializes a {@link DataRowState#NEW} city object.
     */
    public CityDAO() {
        super();
        name = "";
        country = null;
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
    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        ZoneId oldValue = this.zoneId;
        this.zoneId = zoneId;
        firePropertyChange(PROP_ZONEID, oldValue, this.zoneId);
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.country);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
    }

    @Override
    public String toString() {
        ICountryDAO c = country;
        if (getRowState() == DataRowState.NEW) {
            return String.format("CityDAO{name=%s, country=%s}", name, (null == c) ? "null" : c.toString());
        }
        return String.format("CityDAO{primaryKey=%d, name=%s, country=%s}", getPrimaryKey(), name, (null == c) ? "null" : c.toString());
    }

    /**
     * Factory implementation for {@link CityDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CityDAO> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(CityDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case CITY_NAME:
                    ps.setString(index, String.format("%s;%s", dao.name, dao.zoneId.getId()));
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
                    ZoneId.of(s.substring(i + 1).trim()));
        }

        @Override
        public Class<? extends CityDAO> getDaoClass() {
            return CityDAO.class;
        }

        @Override
        public String getDeleteDependencyMessage(CityDAO dao, Connection connection) throws SQLException {
            if (null == dao || !DataRowState.existsInDb(dao.getRowState())) {
                return "";
            }
            int count = AddressDAO.FACTORY.countByCity(dao.getPrimaryKey(), connection);
            switch (count) {
                case 0:
                    return "";
                case 1:
                    return ResourceBundleHelper.getResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGSINGLE);
                default:
                    return ResourceBundleHelper.formatResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLE, count);
            }
        }

        @Override
        public void save(CityDAO dao, Connection connection, boolean force) throws SQLException {
            ICountryDAO country = ICityDAO.assertValidCity(dao).country;
            if (country instanceof CountryDAO) {
                CountryDAO.FACTORY.save((CountryDAO) country, connection, force);
            }
            super.save(dao, connection, force);
        }

        @Override
        protected void onCloneProperties(CityDAO fromDAO, CityDAO toDAO) {
            String oldName = toDAO.name;
            ICountryDAO oldCountry = toDAO.country;
            ZoneId oldZoneId = toDAO.zoneId;
            toDAO.name = fromDAO.name;
            toDAO.country = fromDAO.country;
            toDAO.zoneId = fromDAO.zoneId;
            toDAO.firePropertyChange(PROP_NAME, oldName, toDAO.name);
            toDAO.firePropertyChange(PROP_ZONEID, oldZoneId, toDAO.zoneId);
            toDAO.firePropertyChange(PROP_COUNTRY, oldCountry, toDAO.country);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CityDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                ZoneId oldZoneId = dao.zoneId;
                private final Country oldCountry = dao.country;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    t.firePropertyChange(PROP_ZONEID, oldZoneId, dao.zoneId);
                    t.firePropertyChange(PROP_COUNTRY, oldCountry, dao.country);
                }
            };

            String s = rs.getString(DbColumn.CITY_NAME.toString());
            int i = s.lastIndexOf(";");
            if (i < 0) {
                dao.name = s;
                dao.zoneId = null;
            } else {
                dao.name = s.substring(0, i).trim();
                dao.zoneId = ZoneId.of(s.substring(i + 1).trim());
            }
            dao.country = CountryDAO.FACTORY.fromJoinedResultSet(rs);
            return propertyChanges;
        }

        @Override
        public String getSaveDbConflictMessage(CityDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                return ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYALREADYDELETED);
            }

            ICountryDAO country = dao.getCountry();

            if (country instanceof CountryDAO && country.getRowState() != DataRowState.UNMODIFIED) {
                String msg = CountryDAO.FACTORY.getSaveDbConflictMessage((CountryDAO) country, connection);
                if (!msg.isEmpty()) {
                    return msg;
                }
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
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                }
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
                    }
                }
                if (count > 0) {
                    return ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYNAMEINUSE);
                }
            }
            return "";
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
                }
            }
            return result;
        }

        public CityDAO getByResourceKey(Connection connection, String rk) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().toString()).append(" WHERE ")
                    .append(DbColumn.CITY_NAME.getDbName()).append("=?").toString();
            LOG.fine(() -> String.format("getByResourceKey", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, rk);
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        return fromResultSet(rs);
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
                        return rs.getInt(1);
                    }
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        protected DataObjectEvent<? extends CityDAO> createDataObjectEvent(Object source, CityDAO dataAccessObject, DbChangeType changeAction) {
            return new CityDaoEvent(source, dataAccessObject, changeAction);
        }

    }

    private static final class Related extends PropertyBindable implements ICityDAO {

        private final int primaryKey;
        private final String name;
        private final ICountryDAO country;
        private final ZoneId zoneId;

        Related(int primaryKey, String name, ICountryDAO country, ZoneId zoneId) {
            this.primaryKey = primaryKey;
            this.name = name;
            this.country = country;
            this.zoneId = zoneId;
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
        public ZoneId getZoneId() {
            return zoneId;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

    }

}
