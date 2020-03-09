package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import scheduler.view.customer.CustomerModel;

public class CustomerImpl extends DataObjectImpl implements Customer, CustomerColumns {

    private static final String BASE_SELECT_SQL = String.format("SELECT `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, "
            + "`%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, "
            + "`%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`, `%s`.`%s` AS `%s`"
            + " FROM `%s` `%s` LEFT JOIN `%s` `%s` ON `%s`.`%s`=`%s`.`%s` "
            + " LEFT JOIN `%s` `%s` ON `%s`.`%s`=`%s`.`%s`"
            + "LEFT JOIN `%s` `%s` ON `%s`.`%s`=`%s`.`%s`", COLNAME_CUSTOMERID, COLNAME_CUSTOMERID,
            TABLEALIAS_CUSTOMER, COLNAME_CUSTOMERNAME, COLNAME_CUSTOMERNAME, TABLEALIAS_CUSTOMER, COLNAME_ACTIVE, COLNAME_ACTIVE,
            TABLEALIAS_CUSTOMER, COLNAME_ADDRESSID, COLNAME_ADDRESSID, TABLEALIAS_ADDRESS, COLNAME_ADDRESS, COLNAME_ADDRESS,
            TABLEALIAS_ADDRESS, COLNAME_ADDRESS2, COLNAME_ADDRESS2, TABLEALIAS_ADDRESS, COLNAME_CITYID, COLNAME_CITYID,
            TABLEALIAS_CITY, COLNAME_CITY, COLNAME_CITY, TABLEALIAS_CITY, COLNAME_COUNTRYID, TABLEALIAS_CITY, COLNAME_COUNTRYID,
            TABLEALIAS_COUNTRY, COLNAME_COUNTRY, COLNAME_COUNTRY, TABLEALIAS_ADDRESS, COLNAME_POSTALCODE, COLNAME_POSTALCODE,
            TABLEALIAS_ADDRESS, COLNAME_PHONE, COLNAME_PHONE, TABLEALIAS_CUSTOMER, COLNAME_CREATEDATE, COLNAME_CREATEDATE,
            TABLEALIAS_CUSTOMER, COLNAME_CREATEDBY, COLNAME_CREATEDBY, TABLEALIAS_CUSTOMER, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE,
            TABLEALIAS_CUSTOMER, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY,
            TABLENAME_CUSTOMER, TABLEALIAS_CUSTOMER, TABLENAME_ADDRESS, TABLEALIAS_ADDRESS, TABLEALIAS_CUSTOMER, COLNAME_ADDRESSID, TABLEALIAS_ADDRESS, COLNAME_ADDRESSID,
            TABLENAME_CITY, TABLEALIAS_CITY, TABLEALIAS_CUSTOMER, COLNAME_CITYID, TABLEALIAS_CITY, COLNAME_CITYID,
            TABLENAME_COUNTRY, TABLEALIAS_COUNTRY, TABLEALIAS_CUSTOMER, COLNAME_COUNTRYID, TABLEALIAS_COUNTRY, COLNAME_COUNTRYID);

    private String name;

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
        this.name = name;
    }

    private DataObjectReference<AddressImpl, Address> address;

    @Override
    public DataObjectReference<AddressImpl, Address> getAddress() {
        return address;
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(DataObjectReference<AddressImpl, Address> address) {
        this.address = address;
    }

    private boolean active;

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Set the value of active
     *
     * @param active new value of active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Initializes a {@link scheduler.util.Values#ROWSTATE_NEW} customer object.
     */
    public CustomerImpl() {
        super();
        name = "";
        address = null;
        active = true;
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<CustomerImpl, CustomerModel> {

        // This is a singleton instance
        private FactoryImpl() {
        }

        public Optional<CustomerImpl> findByName(Connection connection, String value) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByAddress(Connection connection, int addressId) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        protected CustomerImpl fromResultSet(ResultSet resultSet) throws SQLException {
            CustomerImpl r = new CustomerImpl();
            initializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseSelectQuery() {
            return BASE_SELECT_SQL;
        }

        @Override
        public Class<? extends CustomerImpl> getDaoClass() {
            return CustomerImpl.class;
        }

        @Override
        public String getTableName() {
            return TABLENAME_CUSTOMER;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_CUSTOMERID;
        }

        @Override
        protected List<String> getExtendedColNames() {
            return Arrays.asList(COLNAME_CUSTOMERNAME, COLNAME_ACTIVE, COLNAME_ADDRESSID);
        }

        @Override
        protected void setSaveStatementValues(CustomerImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getName());
            ps.setBoolean(2, dao.isActive());
            ps.setInt(3, dao.getAddress().getPrimaryKey());
        }

        @Override
        protected void onInitializeDao(CustomerImpl target, ResultSet resultSet) throws SQLException {
            target.name = resultSet.getString(COLNAME_CUSTOMERNAME);
            if (resultSet.wasNull()) {
                target.name = "";
            }

            int addressId = resultSet.getInt(COLNAME_ADDRESSID);
            if (resultSet.wasNull()) {
                target.address = null;
            } else {
                String address1 = resultSet.getString(COLNAME_ADDRESS);
                if (resultSet.wasNull()) {
                    address1 = "";
                }
                String address2 = resultSet.getString(COLNAME_ADDRESS2);
                if (resultSet.wasNull()) {
                    address2 = "";
                }
                City city;
                int cityId = resultSet.getInt(COLNAME_CITYID);
                if (resultSet.wasNull()) {
                    city = null;
                } else {
                    String cityName = resultSet.getString(COLNAME_CITY);
                    if (resultSet.wasNull()) {
                        cityName = "";
                    }
                    int countryId = resultSet.getInt(COLNAME_COUNTRYID);
                    if (resultSet.wasNull()) {
                        city = City.of(cityId, cityName, null);
                    } else {
                        String countryName = resultSet.getString(COLNAME_COUNTRY);
                        city = City.of(cityId, cityName, DataObjectReference.of(Country.of(countryId, resultSet.wasNull() ? "" : countryName)));
                    }
                }
                String postalCode = resultSet.getString(COLNAME_POSTALCODE);
                if (resultSet.wasNull()) {
                    postalCode = "";
                }
                String phone = resultSet.getString(COLNAME_PHONE);
                target.address = DataObjectReference.of(Address.of(addressId, address1, address2, DataObjectReference.of(city), postalCode,
                        (resultSet.wasNull()) ? "" : phone));
            }

            target.active = resultSet.getBoolean(COLNAME_ACTIVE);
            if (resultSet.wasNull()) {
                target.active = false;
            }
        }

        @Override
        public CustomerFilter getAllItemsFilter() {
            return CustomerFilter.all();
        }

        @Override
        public CustomerFilter getDefaultFilter() {
            return CustomerFilter.byStatus(true);
        }

        @Override
        public String getDeleteDependencyMessage(CustomerImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSaveConflictMessage(CustomerImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public ArrayList<CustomerImpl> getAll(Connection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
