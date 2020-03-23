package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.SelectList;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.view.address.AddressModel;

public class AddressImpl extends DataObjectImpl implements Address {

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String address1;
    private String address2;
    private DataObjectReference<CityImpl, City> city;
    private String postalCode;
    private String phone;

    /**
     * Initializes a {@link DataRowState#NEW} address object.
     */
    public AddressImpl() {
        address1 = "";
        address2 = "";
        city = DataObjectReference.of(null);
        postalCode = "";
        phone = "";
    }

    @Override
    public String getAddress1() {
        return address1;
    }

    /**
     * Set the value of address1
     *
     * @param value new value of address1
     */
    public void setAddress1(String value) {
        address1 = (value == null) ? "" : value;
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    /**
     * Set the value of address2
     *
     * @param value new value of address2
     */
    public void setAddress2(String value) {
        address2 = (value == null) ? "" : value;
    }

    @Override
    public DataObjectReference<CityImpl, City> getCityReference() {
        return city;
    }

    @Override
    public City getCity() {
        return city.getPartial();
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public void setCity(City city) {
        this.city = DataObjectReference.of(city);
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Set the value of postalCode
     *
     * @param value new value of postalCode
     */
    public void setPostalCode(String value) {
        postalCode = (value == null) ? "" : value;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    /**
     * Set the value of phone
     *
     * @param value new value of phone
     */
    public void setPhone(String value) {
        phone = (value == null) ? "" : value;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<AddressImpl, AddressModel> {

        private static final SelectList DETAIL_DML;

        static {
            DETAIL_DML = new SelectList(DbTable.ADDRESS);
            DETAIL_DML.leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID)
                    .leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
            DETAIL_DML.makeUnmodifiable();
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected AddressImpl fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            AddressImpl r = new AddressImpl();
            initializeDao(r, resultSet, columns);
            return r;
        }

        @Override
        public SelectList getDetailDml() {
            return DETAIL_DML;
        }

        @Override
        public Class<? extends AddressImpl> getDaoClass() {
            return AddressImpl.class;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.ADDRESS;
        }

        @Override
        protected void setSaveStatementValue(AddressImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
            switch (column) {
                case ADDRESS1:
                    ps.setString(index, dao.getAddress1());
                    break;
                case ADDRESS2:
                    ps.setString(index, dao.getAddress2());
                    break;
                case ADDRESS_CITY:
                    ps.setInt(index, dao.getCity().getPrimaryKey());
                    break;
                case POSTAL_CODE:
                    ps.setString(index, dao.getPostalCode());
                    break;
                case PHONE:
                    ps.setString(index, dao.getPhone());
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected column name");
            }
        }

        @Override
        protected void onInitializeDao(AddressImpl target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            target.address1 = columns.getString(resultSet, DbColumn.ADDRESS1, "");
            target.address2 = columns.getString(resultSet, DbColumn.ADDRESS2, "");
            Optional<Integer> cityId = columns.tryGetInt(resultSet, DbColumn.ADDRESS_CITY);
            if (cityId.isPresent()) {
                Optional<Integer> countryId = columns.tryGetInt(resultSet, DbColumn.CITY_COUNTRY);
                if (countryId.isPresent()) {
                    target.city = DataObjectReference.of(City.of(cityId.get(), columns.getString(resultSet, DbColumn.CITY_NAME, ""),
                            DataObjectReference.of(Country.of(countryId.get(), columns.getString(resultSet, DbColumn.COUNTRY_NAME, "")))));
                } else {
                    target.city = DataObjectReference.of(City.of(cityId.get(), columns.getString(resultSet, DbColumn.CITY_NAME, ""), null));
                }
            } else {
                target.city = null;
            }
            target.postalCode = columns.getString(resultSet, DbColumn.POSTAL_CODE, "");
            target.phone = columns.getString(resultSet, DbColumn.PHONE, "");
        }

        @Override
        public ModelFilter<AddressImpl, AddressModel> getAllItemsFilter() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ModelFilter<AddressImpl, AddressModel> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getDeleteDependencyMessage(AddressImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSaveConflictMessage(AddressImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
