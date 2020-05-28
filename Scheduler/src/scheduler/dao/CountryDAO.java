package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableMap;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.event.CountryDaoEvent;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DbChangeType;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.model.CustomerCountry;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.util.DB;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.Values;
import scheduler.view.country.EditCountry;
import static scheduler.view.country.EditCountryResourceKeys.*;

/**
 * Data access object for the {@code country} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.COUNTRY)
public final class CountryDAO extends DataAccessObject implements CountryDbRecord {

    public static final int MAX_LENGTH_NAME = 50;

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";
    public static final String PROP_LOCALE = "locale";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private Locale locale;

    /**
     * Initializes a {@link DataRowState#NEW} country object.
     */
    public CountryDAO() {
        super();
        name = "";
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
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        Locale oldValue = this.locale;
        this.locale = locale;
        firePropertyChange(PROP_LOCALE, oldValue, this.locale);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(name);
        hash = 67 * hash + Objects.hashCode(locale);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof CustomerCountry && ModelHelper.areSameRecord(this, (CustomerCountry) obj);
    }

    @Override
    public String toString() {
        Locale l = locale;
        if (getRowState() == DataRowState.NEW) {
            if (null == l) {
                return String.format("CountryDAO{name=%s}", name);
            }
            return String.format("CountryDAO{name=%s, locale=%s}", name, l.toLanguageTag());
        }
        if (null == l) {
            return String.format("CountryDAO{primaryKey=%d, name=%s}", getPrimaryKey(), name);
        }
        return String.format("CountryDAO{primaryKey=%d, name=%s, locale=%s}", getPrimaryKey(), name, l.toLanguageTag());
    }

    /**
     * Factory implementation for {@link CountryDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CountryDAO> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public boolean isCompoundSelect() {
            return false;
        }

        @Override
        protected void applyColumnValue(CountryDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case COUNTRY_NAME:
                    String lt = dao.locale.toLanguageTag();
                    Map<String, String> localeDisplayMap = PredefinedData.getLocaleDisplayMap();
                    if (localeDisplayMap.containsKey(lt) && dao.name.equals(localeDisplayMap.get(lt))) {
                        ps.setString(index, lt);
                    } else {
                        ps.setString(index, String.format("%s;%s", dao.name, lt));
                    }
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public CountryDAO createNew() {
            return new CountryDAO();
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

        ICountryDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            String n = rs.getString(DbColumn.COUNTRY_NAME.toString());
            int i = n.lastIndexOf(";");
            if (i < 0) {
                Map<String, String> localeDisplayMap = PredefinedData.getLocaleDisplayMap();
                if (localeDisplayMap.containsKey(n)) {
                    return new Related(rs.getInt(DbColumn.CITY_COUNTRY.toString()), localeDisplayMap.get(n), Locale.forLanguageTag(n));
                }
                return new Related(rs.getInt(DbColumn.CITY_COUNTRY.toString()), n, null);
            }
            return new Related(rs.getInt(DbColumn.CITY_COUNTRY.toString()), n.substring(0, i), Locale.forLanguageTag(n.substring(i + 1)));
        }

        @Override
        public Class<? extends CountryDAO> getDaoClass() {
            return CountryDAO.class;
        }

        @Override
        public String getDeleteDependencyMessage(CountryDAO dao, Connection connection) throws SQLException {
            if (null == dao || !DataRowState.existsInDb(dao.getRowState())) {
                return "";
            }
            int count = CityDAO.getFactory().countByCountry(dao.getPrimaryKey(), connection);
            switch (count) {
                case 0:
                    return "";
                case 1:
                    return ResourceBundleHelper.getResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGSINGLECOUNTRY);
                default:
                    return ResourceBundleHelper.formatResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLECOUNTRY,
                            count);
            }
        }

        @Override
        public void save(CountryDAO dao, Connection connection, boolean force) throws SQLException {
            super.save(ICountryDAO.assertValidCountry(dao), connection, force);
        }

        @Override
        protected void onCloneProperties(CountryDAO fromDAO, CountryDAO toDAO) {
            String oldName = toDAO.name;
            Locale oldLocale = toDAO.locale;
            toDAO.name = fromDAO.name;
            toDAO.locale = fromDAO.locale;
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

            int i = s.lastIndexOf(";");
            if (i < 0) {
                Map<String, String> localeDisplayMap = PredefinedData.getLocaleDisplayMap();
                if (localeDisplayMap.containsKey(s)) {
                    dao.name = localeDisplayMap.get(s);
                    dao.locale = Locale.forLanguageTag(s);
                } else {
                    dao.name = s;
                    dao.locale = null;
                }
            } else {
                dao.name = s.substring(0, i).trim();
                dao.locale = Locale.forLanguageTag(s.substring(i + 1).trim());
            }

            return propertyChanges;
        }

        @Override
        public String getSaveDbConflictMessage(CountryDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                return ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_COUNTRYALREADYDELETED);
            }

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.COUNTRY_ID.getDbName())
                    .append(") FROM ").append(DbTable.COUNTRY.getDbName())
                    .append(" WHERE (").append(DbColumn.COUNTRY_NAME.getDbName()).append(" LIKE ? OR ")
                    .append(DbColumn.COUNTRY_NAME.getDbName()).append("=?)");

            if (dao.getRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.COUNTRY_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();
            String lt = dao.locale.toLanguageTag();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, String.format("%s;*", DB.escapeWC(dao.name)));
                ps.setString(2, lt);
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
                sb = new StringBuffer("SELECT ").append(DbColumn.COUNTRY_NAME.getDbName())
                        .append(") FROM ").append(DbTable.COUNTRY.getDbName())
                        .append(" WHERE (").append(DbColumn.COUNTRY_NAME.getDbName()).append(" LIKE ? OR ")
                        .append(DbColumn.COUNTRY_NAME.getDbName()).append("=?)");

                if (dao.getRowState() != DataRowState.NEW) {
                    sb.append(" AND ").append(DbColumn.COUNTRY_ID.getDbName()).append("<>?");
                }
                String sql2 = sb.toString();
                count = 0;
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, String.format("%s;*", DB.escapeWC(dao.name)));
                    ps.setString(2, lt);
                    if (dao.getRowState() != DataRowState.NEW) {
                        ps.setInt(3, dao.getPrimaryKey());
                    }
                    LOG.fine(() -> String.format("Executing DML statement: %s", sql2));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String n = rs.getString(1);
                            int i = n.lastIndexOf(";");
                            if ((i < 0) ? (n.equals(lt) || n.equalsIgnoreCase(dao.name)) : n.substring(0, i).equalsIgnoreCase(dao.name)) {
                                count++;
                            }
                        }
                    }
                }
                if (count > 0) {
                    return ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_SAVECONFLICTMESSAGE);
                }
            }

            return "";
        }

        public ArrayList<CountryDAO> getAllCountries(Connection connection) throws SQLException {
            String sql = createDmlSelectQueryBuilder().toString();
            LOG.fine(() -> String.format("Executing query \"%s\"", sql));
            ArrayList<CountryDAO> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs) {
                        while (rs.next()) {
                            result.add(fromResultSet(rs));
                        }
                    }
                    // PENDING: Check for warnings on other queries.
                    SQLWarning w = connection.getWarnings();
                    if (null == w) {
                        LOG.log(Level.WARNING, "Null results, no warnings.");
                    } else {
                        LOG.log(Level.WARNING, "Encountered warning", w);
                    }
                }
            }
            return result;
        }

        public CountryDAO getByRegionCode(Connection connection, String rc) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().toString()).append(" WHERE ")
                    .append(DbColumn.COUNTRY_NAME).append("=?").toString();
            LOG.fine(() -> String.format("getByRegionCode", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, rc);
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        return fromResultSet(rs);
                    }
                }
            }
            return null;
        }

        @Override
        protected DataObjectEvent<? extends CountryDAO> createDataObjectEvent(Object source, CountryDAO dataAccessObject, DbChangeType changeAction) {
            return new CountryDaoEvent(source, dataAccessObject, changeAction);
        }

    }

    private final static class Related extends PropertyBindable implements ICountryDAO {

        private final int primaryKey;
        private final String name;
        private final Locale locale;

        Related(int primaryKey, String name, Locale locale) {
            this.primaryKey = primaryKey;
            this.name = name;
            this.locale = locale;
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
            return null != obj && obj instanceof CustomerCountry && ModelHelper.areSameRecord(this, (CustomerCountry) obj);
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

    }

}
