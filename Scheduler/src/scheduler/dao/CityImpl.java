package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import scheduler.AppResources;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.SelectList;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.city.CityModel;
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
     * @param value new value of name
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

    public static final class FactoryImpl extends DataObjectImpl.Factory<CityImpl, CityModel> {

        private static final SelectList DETAIL_DML;

        static {
            DETAIL_DML = new SelectList(DbTable.CITY);
            DETAIL_DML.leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
            DETAIL_DML.makeUnmodifiable();
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected CityImpl fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            CityImpl r = new CityImpl();
            onInitializeDao(r, resultSet, columns);
            return r;
        }

        @Override
        public SelectList getDetailDml() {
            return DETAIL_DML;
        }

        @Override
        public Class<? extends CityImpl> getDaoClass() {
            return CityImpl.class;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.CITY;
        }

        @Override
        protected void setSaveStatementValue(CityImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
            switch (column) {
                case CITY_NAME:
                    ps.setString(index, dao.getName());
                    break;
                case CITY_COUNTRY:
                    ps.setInt(index, dao.getCountry().getPrimaryKey());
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected column name");
            }
        }

        @Override
        protected void onInitializeDao(CityImpl target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            target.name = columns.getString(resultSet, DbColumn.CITY_NAME, "");
            Optional<Integer> countryId = columns.tryGetInt(resultSet, DbColumn.CITY_COUNTRY);
            if (countryId.isPresent()) {
                target.country = Country.of(countryId.get(), columns.getString(resultSet, DbColumn.COUNTRY_NAME, ""));
            } else {
                target.country = null;
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
            ArrayList<CityImpl> result = new ArrayList<>();
            SelectList dml = getDetailDml();
            try (PreparedStatement ps = connection.prepareStatement(dml.getSelectQuery()
                    .append(" WHERE ").append(DbColumn.COUNTRY_ID.getTable().getAlias()).append(" = ?").toString())) {
                ps.setInt(1, countryId);
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        result.add(fromResultSet(rs, dml));
                    }
                }
            }
            return result;
        }

    }

}
