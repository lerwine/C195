package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import scheduler.AppResources;
import scheduler.dao.dml.deprecated.ColumnReference;
import scheduler.dao.dml.deprecated.SelectColumnList;
import scheduler.dao.dml.deprecated.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.country.CountryModel;
import scheduler.view.country.EditCountry;

public class CountryImpl extends DataObjectImpl implements Country {

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    private String name;

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

    /**
     * Initializes a {@link DataRowState#NEW} country object.
     */
    public CountryImpl() {
        super();
        name = "";
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory_obsolete<CountryImpl, CountryModel> {

        private static final SelectColumnList DETAIL_DML;

        static {
            DETAIL_DML = new SelectColumnList(DbTable.COUNTRY);
            DETAIL_DML.makeUnmodifiable();
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected CountryImpl fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            CountryImpl result = new CountryImpl();
            initializeDao(result, resultSet, columns);
            return result;
        }

        @Override
        public SelectColumnList getSelectColumns() {
            return DETAIL_DML;
        }

        @Override
        public Class<? extends CountryImpl> getDaoClass() {
            return CountryImpl.class;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.COUNTRY;
        }

        @Override
        protected void setSqlParameter(CountryImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
            if (column != DbColumn.COUNTRY_NAME) {
                throw new UnsupportedOperationException("Unexpected column name");
            }
            ps.setString(index, dao.getName());
        }

        @Override
        protected void onInitializeDao(CountryImpl target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            target.name = resultSet.getString(DbColumn.COUNTRY_NAME.getDbName().getValue());
            if (resultSet.wasNull()) {
                target.name = "";
            }
        }

        @Override
        public ModelListingFilter<CountryImpl, CountryModel> getAllItemsFilter() {
            return ModelListingFilter.all(this, AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCOUNTRIES),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ALLCOUNTRIES), null);
        }

        @Override
        public ModelListingFilter<CountryImpl, CountryModel> getDefaultFilter() {
            return getAllItemsFilter();
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

        public ArrayList<CountryImpl> getAllCountries(Connection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
