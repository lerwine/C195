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
import static scheduler.dao.CityColumns.COLNAME_CITYID;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.country.CountryModel;
import scheduler.view.country.EditCountry;

public class CountryImpl extends DataObjectImpl implements Country, CountryColumns {

    @Deprecated
    private static final String BASE_SELECT_SQL = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s", COLNAME_COUNTRYID, COLNAME_COUNTRY,
            COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, TABLENAME_COUNTRY);

    private String name;

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     *
     * @param value new value of name.
     */
    public void setName(String value) {
        name = (value == null) ? "" : value;
    }

    /**
     * Initializes a {@link scheduler.util.Values#ROWSTATE_NEW} country object.
     */
    public CountryImpl() {
        super();
        name = "";
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<CountryImpl, CountryModel> {

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected CountryImpl fromResultSet(ResultSet resultSet) throws SQLException {
            CountryImpl result = new CountryImpl();
            initializeDao(result, resultSet);
            return result;
        }

        @Override
        public String getBaseSelectQuery() {
            return BASE_SELECT_SQL;
        }

        @Override
        public Class<? extends CountryImpl> getDaoClass() {
            return CountryImpl.class;
        }

        @Override
        public DbTable getTableName() {
            return DbTable.COUNTRY;
        }

        @Override
        public String getTableName_old() {
            return TABLENAME_COUNTRY;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_COUNTRYID;
        }

        @Override
        protected List<String> getExtendedColNames() {
            return Arrays.asList(COLNAME_COUNTRY);
        }

        @Override
        protected void setSaveStatementValues(CountryImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getName());
        }

        @Override
        protected void onInitializeDao(CountryImpl target, ResultSet resultSet) throws SQLException {
            target.name = resultSet.getString(COLNAME_COUNTRY);
            if (resultSet.wasNull()) {
                target.name = "";
            }
        }

        @Override
        public ModelFilter<CountryImpl, CountryModel> getAllItemsFilter() {
            return ModelFilter.all(this, AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCOUNTRIES),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ALLCOUNTRIES), null);
        }

        @Override
        public ModelFilter<CountryImpl, CountryModel> getDefaultFilter() {
            return getAllItemsFilter();
        }

        @Override
        public String getDeleteDependencyMessage(CountryImpl dao, Connection connection) throws SQLException {
            if (null != dao && dao.isExisting()) {
                try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?", COLNAME_CITYID,
                        TABLENAME_CITY, COLNAME_COUNTRYID))) {
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
                            COLNAME_COUNTRYID, TABLENAME_COUNTRY, COLNAME_COUNTRY))) {
                        ps.setString(1, dao.getName());
                        ps.setInt(2, dao.getPrimaryKey());
                        try (ResultSet rs = ps.getResultSet()) {
                            count = rs.getInt(1);
                        }
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement(String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?",
                            COLNAME_COUNTRYID, TABLENAME_COUNTRY, COLNAME_COUNTRY))) {
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

        public ArrayList<CountryImpl> getAllCountries(Connection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
