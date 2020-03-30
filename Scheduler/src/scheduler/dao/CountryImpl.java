package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppResourceBundleConstants;
import scheduler.AppResources;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.country.EditCountry;

public class CountryImpl extends DataObjectImpl implements Country {

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";
    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;

    /**
     * Initializes a {@link DataRowState#NEW} country object.
     */
    public CountryImpl() {
        super();
        name = "";
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     *
     * @param name new value of name.
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = (name == null) ? "" : name;
        firePropertyChange(PROP_NAME, oldValue, this.name);
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof Country) {
            Country other = (Country) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && name.equals(other.getName());
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    public static final class FactoryImpl extends DataObjectImpl.DaoFactory<CountryImpl> {

        @Override
        public boolean isAssignableFrom(DataObjectImpl dao) {
            return null != dao && dao instanceof CountryImpl;
        }

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        @Override
        public DbTable getDbTable() {
            return DbTable.COUNTRY;
        }

        @Override
        public DbColumn getPrimaryKeyColumn() {
            return DbColumn.COUNTRY_ID;
        }

        @Override
        public CountryImpl createNew() {
            return new CountryImpl();
        }

        @Override
        public DaoFilter<CountryImpl> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_LOADINGCOUNTRIES));
        }

        @Override
        public DaoFilter<CountryImpl> getDefaultFilter() {
            return getAllItemsFilter();
        }

        @Override
        protected void onInitializeFromResultSet(CountryImpl dao, ResultSet rs) throws SQLException {
            String oldName = dao.name;
            dao.name = rs.getString(DbColumn.COUNTRY_NAME.toString());
            dao.firePropertyChange(PROP_NAME, oldName, dao.name);
        }

        Country fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Country() {
                private final String name = rs.getString(DbColumn.CITY_NAME.toString());
                private final int primaryKey = rs.getInt(DbColumn.CITY_COUNTRY.toString());

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public int getPrimaryKey() {
                    return primaryKey;
                }

                @Override
                public DataRowState getRowState() {
                    return DataRowState.UNMODIFIED;
                }

                @Override
                public boolean isExisting() {
                    return true;
                }

                @Override
                public int hashCode() {
                    return primaryKey;
                }

                @Override
                public boolean equals(Object obj) {
                    if (null != obj && obj instanceof Country) {
                        Country other = (Country) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
        }

        @Override
        public StringBuilder getBaseSelectQuery() {
            StringBuilder sb = new StringBuilder();
            return sb.append("SELECT ").append(DbColumn.COUNTRY_ID).append(", ").append(DbColumn.COUNTRY_NAME)
                    .append(", ").append(DbColumn.COUNTRY_CREATE_DATE).append(", ").append(DbColumn.COUNTRY_CREATED_BY).append(", ")
                    .append(DbColumn.COUNTRY_LAST_UPDATE).append(", ").append(DbColumn.COUNTRY_LAST_UPDATE_BY);
        }

        void appendSelectColumns(StringBuilder sb) {
            sb.append(", ").append(DbTable.COUNTRY).append(".").append(DbColumn.COUNTRY_NAME).append(" AS ").append(DbColumn.COUNTRY_NAME);
        }

        void appendJoinStatement(StringBuilder sb) {
            sb.append(" LEFT JOIN ").append(DbTable.COUNTRY.getDbName()).append(" ").append(DbTable.COUNTRY).append(" ON ")
                    .append(DbTable.CITY).append(".").append(DbColumn.CITY_COUNTRY).append(" = ")
                    .append(DbTable.COUNTRY).append(".").append(DbColumn.COUNTRY_ID);
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public Class<? extends CountryImpl> getDaoClass() {
            return CountryImpl.class;
        }

        @Override
        public String getDeleteDependencyMessage(CountryImpl dao, Connection connection) throws SQLException {
            if (null != dao && dao.isExisting()) {
                try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?", DbName.CITY_ID,
                        DbName.CITY, DbName.COUNTRY_ID))) {
                    ps.setInt(1, dao.getPrimaryKey());
                    try (ResultSet rs = ps.getResultSet()) {
                        int count = rs.getInt(1);
                        if (count == 1) {
                            return ResourceBundleLoader.getResourceString(AppResources.class, AppResources.RESOURCEKEY_DELETEMSGSINGLECOUNTRY);
                        }
                        if (count > 1) {
                            return ResourceBundleLoader.formatResourceString(AppResources.class, AppResources.RESOURCEKEY_DELETEMSGMULTIPLECOUNTRY,
                                    count);
                        }
                    }
                }
            }
            return "";
        }

        @Override
        public String getSaveConflictMessage(CountryImpl dao, Connection connection) throws SQLException {
            if (null != dao) {
                int count;
                if (dao.isExisting()) {
                    try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE AND %s = ? AND %1$s <> ?",
                            DbName.COUNTRY_ID, DbName.COUNTRY, DbName.COUNTRY))) {
                        ps.setString(1, dao.getName());
                        ps.setInt(2, dao.getPrimaryKey());
                        try (ResultSet rs = ps.getResultSet()) {
                            count = rs.getInt(1);
                        }
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?",
                            DbName.COUNTRY_ID, DbName.COUNTRY, DbName.COUNTRY))) {
                        ps.setString(1, dao.getName());
                        try (ResultSet rs = ps.getResultSet()) {
                            count = rs.getInt(1);
                        }
                    }
                }
                if (count > 0) {
                    return ResourceBundleLoader.getResourceString(EditCountry.class, EditCountry.RESOURCEKEY_SAVECONFLICTMESSAGE);
                }
            }
            return "";
        }

        public ArrayList<CountryImpl> getAllCountries(Connection connection) throws SQLException {
            String sql = getBaseSelectQuery().toString();
            LOG.logp(Level.INFO, getClass().getName(), "getAllCountries", String.format("Executing query \"%s\"", sql));
            ArrayList<CountryImpl> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs) {
                        while (rs.next()) {
                            CountryImpl e = new CountryImpl();
                            initializeFromResultSet(e, rs);
                            result.add(e);
                        }
                    }
                    SQLWarning w = connection.getWarnings();
                    if (null == w) {
                        LOG.logp(Level.WARNING, getClass().getName(), "getAllCountries", "Null results, no warnings.");
                    } else {
                        LOG.logp(Level.WARNING, getClass().getName(), "getAllCountries", "Encountered warning", w);
                    }
                }
            }
            return result;
        }

    }

}
