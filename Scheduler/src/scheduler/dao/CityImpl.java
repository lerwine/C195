package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import scheduler.AppResourceBundleConstants;
import scheduler.AppResources;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.dao.schema.DbTable;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.country.EditCountry;

public class CityImpl extends DataObjectImpl implements City<Country> {

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'country' property.
     */
    public static final String PROP_COUNTRY = "country";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private Country country;

    /**
     * Initializes a {@link DataRowState#NEW} city object.
     */
    public CityImpl() {
        super();
        name = "";
        country = null;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = (name == null) ? "" : name;
        firePropertyChange(PROP_NAME, oldValue, this.name);
    }

    @Override
    public Country getCountry() {
        return country;
    }

    /**
     * Set the value of country
     *
     * @param country new value of country
     */
    public void setCountry(Country country) {
        Country oldValue = this.country;
        this.country = country;
        firePropertyChange(PROP_COUNTRY, oldValue, this.country);
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
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof City) {
            City other = (City) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && name.equals(other.getName()) && country.equals(other.getCountry());
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    public static final class FactoryImpl extends DataObjectImpl.DaoFactory<CityImpl> {

        @Override
        public boolean isAssignableFrom(DataObjectImpl dao) {
            return null != dao && dao instanceof CityImpl;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.CITY;
        }

        @Override
        public DbColumn getPrimaryKeyColumn() {
            return DbColumn.CITY_ID;
        }

        @Override
        public CityImpl createNew() {
            return new CityImpl();
        }

        @Override
        public DaoFilter<CityImpl> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_LOADINGCITIES));
        }

        @Override
        public DaoFilter<CityImpl> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        protected void onInitializeFromResultSet(CityImpl dao, ResultSet rs) throws SQLException {
            String oldName = dao.name;
            dao.name = rs.getString(DbColumn.CITY_NAME.toString());
            Country oldCountry = dao.country;
            dao.country = CountryImpl.getFactory().fromJoinedResultSet(rs);
            dao.firePropertyChange(PROP_NAME, oldName, dao.name);
            dao.firePropertyChange(PROP_COUNTRY, oldCountry, dao.country);
        }

        City<? extends Country> fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new City<Country>() {
                private final String name = rs.getString(DbColumn.CITY_NAME.toString());
                private final Country country = CountryImpl.getFactory().fromJoinedResultSet(rs);
                private final int primaryKey = rs.getInt(DbColumn.ADDRESS_CITY.toString());

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Country getCountry() {
                    return country;
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
                    if (null != obj && obj instanceof City) {
                        City<? extends Country> other = (City<? extends Country>) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
        }

        @Override
        public StringBuilder getBaseSelectQuery() {
            StringBuilder sb = new StringBuilder();
            CountryImpl.getFactory().appendSelectColumns(sb.append("SELECT ")
                    .append(DbTable.CITY).append(".").append(DbColumn.CITY_ID).append(" AS ").append(DbColumn.CITY_ID)
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_NAME).append(" AS ").append(DbColumn.CITY_NAME)
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_COUNTRY).append(" AS ").append(DbColumn.CITY_COUNTRY));
            CountryImpl.getFactory().appendJoinStatement(sb
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_CREATE_DATE).append(" AS ").append(DbColumn.CITY_CREATE_DATE)
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_CREATED_BY).append(" AS ").append(DbColumn.CITY_CREATED_BY)
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_LAST_UPDATE).append(" AS ").append(DbColumn.CITY_LAST_UPDATE)
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_LAST_UPDATE_BY).append(" AS ").append(DbColumn.CITY_LAST_UPDATE_BY)
                    .append(" FROM ").append(DbTable.CITY.getDbName()).append(" ").append(DbTable.CITY));
            return sb;
        }

        void appendSelectColumns(StringBuilder sb) {
            CountryImpl.getFactory().appendSelectColumns(sb
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_NAME).append(" AS ").append(DbColumn.CITY_NAME)
                    .append(", ").append(DbTable.CITY).append(".").append(DbColumn.CITY_COUNTRY).append(" AS ").append(DbColumn.CITY_COUNTRY));
        }

        void appendJoinStatement(StringBuilder sb) {
            CountryImpl.getFactory().appendJoinStatement(sb.append(" LEFT JOIN ").append(DbTable.CITY.getDbName()).append(" ").append(DbTable.CITY)
                    .append(" ON ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_CITY).append(" = ")
                    .append(DbTable.CITY).append(".").append(DbColumn.CITY_ID));
        }

        @Override
        public Class<? extends CityImpl> getDaoClass() {
            return CityImpl.class;
        }

        @Override
        public String getDeleteDependencyMessage(CityImpl dao, Connection connection) throws SQLException {
            if (null != dao && dao.isExisting()) {
                try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?",
                        DbName.ADDRESS_ID, DbName.ADDRESS, DbName.CITY_ID))) {
                    ps.setInt(1, dao.getPrimaryKey());
                    try (ResultSet rs = ps.getResultSet()) {
                        int count = rs.getInt(1);
                        if (count == 1) {
                            return ResourceBundleLoader.getResourceString(EditCountry.class, EditCountry.RESOURCEKEY_DELETEMSGSINGLE);
                        }
                        if (count > 1) {
                            return ResourceBundleLoader.formatResourceString(EditCountry.class, EditCountry.RESOURCEKEY_DELETEMSGMULTIPLE, count);
                        }
                    }
                }
            }
            return "";
        }

        @Override
        public String getSaveConflictMessage(CityImpl dao, Connection connection) throws SQLException {
            if (null != dao) {
                int count;
                if (dao.isExisting()) {
                    try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ? AND %s = ? AND %1$s <> ?",
                            DbName.CITY_ID, DbName.CITY, DbName.COUNTRY_ID, DbName.CITY))) {
                        ps.setString(1, dao.getName());
                        ps.setInt(2, dao.getCountry().getPrimaryKey());
                        ps.setInt(3, dao.getPrimaryKey());
                        try (ResultSet rs = ps.getResultSet()) {
                            count = rs.getInt(1);
                        }
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ? AND %s = ?",
                            DbName.CITY_ID, DbName.CITY, DbName.COUNTRY_ID, DbName.CITY))) {
                        ps.setString(1, dao.getName());
                        ps.setInt(2, dao.getCountry().getPrimaryKey());
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

        public ArrayList<CityImpl> getByCountry(Connection connection, int countryId) throws SQLException {
            // TODO: Implement this if used
            throw new UnsupportedOperationException("Not supported yet.");
//            ArrayList<CityImpl> result = new ArrayList<>();
//            SelectColumnList dml = getSelectColumns();
//            try (PreparedStatement ps = connection.prepareStatement(dml.getSelectQuery()
//                    .append(" WHERE ").append(DbColumn.COUNTRY_ID.getTable()).append(" = ?").toString())) {
//                ps.setInt(1, countryId);
//                try (ResultSet rs = ps.getResultSet()) {
//                    while (rs.next()) {
//                        result.add(fromResultSet(rs, dml));
//                    }
//                }
//            }
//            return result;
        }

    }

}
