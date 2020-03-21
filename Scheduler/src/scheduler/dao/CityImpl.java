package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import scheduler.AppResources;
import static scheduler.dao.AddressColumns.COLNAME_ADDRESSID;
import static scheduler.dao.CityColumns.COLNAME_CITY;
import static scheduler.dao.CityColumns.COLNAME_CITYID;
import static scheduler.dao.CountryColumns.COLNAME_COUNTRYID;
import static scheduler.dao.TableNames.TABLENAME_CITY;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.city.CityModel;
import scheduler.view.country.EditCountry;

public class CityImpl extends DataObjectImpl implements City, CityColumns {

    @Deprecated
    private static final String BASE_SELECT_SQL = String.format("SELECT %1$s.%2$s AS %2$s, %1$s.%3$s AS %3$s`, %1$s.%4$s AS %4$s, %5$s.%6$s AS %6$s"
            + " FROM %7$s %1$s LEFT JOIN %8$s %5$s ON %1$s.%4$s=%5$s.%4$s",
            TABLEALIAS_CITY, COLNAME_CITYID, COLNAME_CITY, COLNAME_COUNTRYID, TABLEALIAS_COUNTRY, COLNAME_COUNTRY, TABLENAME_CITY, TABLENAME_COUNTRY);
    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private DataObjectReference<CountryImpl, Country> country;

    /**
     * Initializes a {@link scheduler.util.Values#ROWSTATE_NEW} city object.
     */
    public CityImpl() {
        super();
        name = "";
        country = new DataObjectReference<>();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param value new value of name
     */
    public void setName(String value) {
        name = (value == null) ? "" : value;
    }

    @Override
    public DataObjectReference<CountryImpl, Country> getCountryReference() {
        return country;
    }

    @Override
    public Country getCountry() {
        return country.getPartial();
    }

    /**
     * Set the value of country
     *
     * @param country new value of country
     */
    public void setCountry(Country country) {
        this.country = DataObjectReference.of(country);
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<CityImpl, CityModel> {

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected CityImpl fromResultSet(ResultSet resultSet) throws SQLException {
            CityImpl r = new CityImpl();
            onInitializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseSelectQuery() {
            return BASE_SELECT_SQL;
        }

        @Override
        public Class<? extends CityImpl> getDaoClass() {
            return CityImpl.class;
        }

        @Override
        public DbTable getTableName() {
            return DbTable.CITY;
        }

        @Override
        public String getTableName_old() {
            return TABLENAME_CITY;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_CITYID;
        }

        @Override
        protected List<String> getExtendedColNames() {
            return Arrays.asList(COLNAME_CITY, COLNAME_COUNTRYID);
        }

        @Override
        protected void setSaveStatementValues(CityImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getName());
            ps.setInt(2, dao.getCountry().getPrimaryKey());
        }

        @Override
        protected void onInitializeDao(CityImpl target, ResultSet resultSet) throws SQLException {
            target.name = resultSet.getString(COLNAME_CITY);
            if (resultSet.wasNull()) {
                target.name = "";
            }
            int countryId = resultSet.getInt(COLNAME_COUNTRYID);
            if (resultSet.wasNull()) {
                target.country = new DataObjectReference<>();
            } else {
                String countryName = resultSet.getString(COLNAME_COUNTRY);
                target.country = DataObjectReference.of(Country.of(countryId, resultSet.wasNull() ? "" : countryName));
            }
        }

        @Override
        public ModelFilter<CityImpl, CityModel> getAllItemsFilter() {
            return ModelFilter.all(this, AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCITIES),
                    ResourceBundleLoader.getResourceString(EditCountry.class, EditCountry.RESOURCEKEY_CITIES), null);
        }

        @Override
        public ModelFilter<CityImpl, CityModel> getDefaultFilter() {
            return getAllItemsFilter();
        }

        @Override
        public String getDeleteDependencyMessage(CityImpl dao, Connection connection) throws SQLException {
            if (null != dao && dao.isExisting()) {
                try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?", COLNAME_ADDRESSID,
                        TABLENAME_ADDRESS, COLNAME_CITYID))) {
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
                            COLNAME_CITYID, TABLENAME_CITY, COLNAME_COUNTRYID, COLNAME_CITY))) {
                        ps.setString(1, dao.getName());
                        ps.setInt(2, dao.getCountry().getPrimaryKey());
                        ps.setInt(3, dao.getPrimaryKey());
                        try (ResultSet rs = ps.getResultSet()) {
                            count = rs.getInt(1);
                        }
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ? AND %s = ?",
                            COLNAME_CITYID, TABLENAME_CITY, COLNAME_COUNTRYID, COLNAME_CITY))) {
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
            ArrayList<CityImpl> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(String.format("%s WHERE %s = ?", BASE_SELECT_SQL, COLNAME_COUNTRYID))) {
                ps.setInt(1, countryId);
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        result.add(fromResultSet(rs));
                    }
                }
            }
            return result;
        }

    }

}
