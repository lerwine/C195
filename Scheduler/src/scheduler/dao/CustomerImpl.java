/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import scheduler.view.customer.CustomerModel;

public class CustomerImpl extends DataObjectImpl implements Customer {

    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    //<editor-fold defaultstate="collapsed" desc="Column names">
    public static final String COLNAME_CUSTOMERID = "customerId";

    public static final String COLNAME_CUSTOMERNAME = "customerName";

    public static final String COLNAME_ADDRESSID = "addressId";

    public static final String COLNAME_ACTIVE = "active";

    //</editor-fold>
    private static final String BASE_SELECT_SQL = String.format("SELECT p.`%s` AS `%s`, p.`%s` AS `%s`, p.`%s` AS `%s`, p.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`,"
            + "a.`%s` AS `%s`, c.`%s` AS `%s`, c.`%s` AS `%s`, n.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`"
            + " FROM `%s` p LEFT JOIN `%s` a ON p.`%s`=a.`%s` LEFT JOIN `%s` c ON a.`%s`=c.`%s` LEFT JOIN `%s` n ON c.`%s`=n.`%s`", COLNAME_CUSTOMERID, COLNAME_CUSTOMERID,
            COLNAME_CUSTOMERNAME, COLNAME_CUSTOMERNAME, COLNAME_ACTIVE, COLNAME_ACTIVE, COLNAME_ADDRESSID, COLNAME_ADDRESSID, AddressImpl.COLNAME_ADDRESS,
            AddressImpl.COLNAME_ADDRESS, AddressImpl.COLNAME_ADDRESS2, AddressImpl.COLNAME_ADDRESS2, AddressImpl.COLNAME_CITYID, AddressImpl.COLNAME_CITYID,
            CityImpl.COLNAME_CITY, CityImpl.COLNAME_CITY, CityImpl.COLNAME_COUNTRYID, CityImpl.COLNAME_COUNTRYID, CountryImpl.COLNAME_COUNTRY,
            CountryImpl.COLNAME_COUNTRY, AddressImpl.COLNAME_POSTALCODE, AddressImpl.COLNAME_POSTALCODE, AddressImpl.COLNAME_PHONE, AddressImpl.COLNAME_PHONE,
            COLNAME_CREATEDATE, COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY,
            TABLENAME_CUSTOMER, TABLENAME_ADDRESS, COLNAME_ADDRESSID, AddressImpl.COLNAME_ADDRESSID, TABLENAME_CITY, AddressImpl.COLNAME_CITYID, CityImpl.COLNAME_CITYID,
            TABLENAME_COUNTRY, CityImpl.COLNAME_COUNTRYID, CountryImpl.COLNAME_COUNTRYID);

    //<editor-fold defaultstate="collapsed" desc="name property">
    private String name;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="address property">
    private DataObjectReference<AddressImpl, Address> address;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="active property">
    private boolean active;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //</editor-fold>
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
    
    public static FactoryImpl getFactory() { return FACTORY; }
    
    public static final class FactoryImpl extends DataObjectImpl.Factory<CustomerImpl, CustomerModel> {

        // This is a singleton instance
        private FactoryImpl() { }
        
        public Optional<CustomerImpl> findByName(Connection connection, String value) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
        //        return load(connection, activeIs(isActive), orderBy);
        //    }
        public ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
        //        return load(connection, activeIs(isActive).and(addressIdIs(addressId)), orderBy);
        //    }
        public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, Iterable<OrderBy> orderBy) throws Exception {
        //        return load(connection, addressIdIs(addressId), orderBy);
        //    }
        public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<CustomerImpl> loadByCity(Connection connection, int cityId, Iterable<OrderBy> orderBy) throws Exception {
        //        return load(connection, cityIdIs(cityId), orderBy);
        //    }
        //    
        //    public ArrayList<CustomerImpl> loadByCountry(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
        //        return load(connection, countryIdIs(countryId), orderBy);
        //    }
        
        public int countByAddress(Connection connection, int addressId) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    @Override
        //    protected void onApplyChanges(CustomerModel model) {
        //        CustomerImpl dao = model.getDataObject();
        //        dao.name = model.getName();
        //        CustomerAddress<?> address = model.getAddress();
        //        dao.address = (null == address) ? null : address.getDataObject();
        //        dao.active = model.isActive();
        //    }
        
        @Override
        protected CustomerImpl fromResultSet(ResultSet resultSet) throws SQLException {
            CustomerImpl r = new CustomerImpl();
            onInitializeDao(r, resultSet);
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
                String address1 = resultSet.getString(AddressImpl.COLNAME_ADDRESS);
                if (resultSet.wasNull()) {
                    address1 = "";
                }
                String address2 = resultSet.getString(AddressImpl.COLNAME_ADDRESS2);
                if (resultSet.wasNull()) {
                    address2 = "";
                }
                City city;
                int cityId = resultSet.getInt(AddressImpl.COLNAME_CITYID);
                if (resultSet.wasNull()) {
                    city = null;
                } else {
                    String cityName = resultSet.getString(CityImpl.COLNAME_CITY);
                    if (resultSet.wasNull()) {
                        cityName = "";
                    }
                    int countryId = resultSet.getInt(CityImpl.COLNAME_COUNTRYID);
                    if (resultSet.wasNull()) {
                        city = City.of(cityId, cityName, null);
                    } else {
                        String countryName = resultSet.getString(CountryImpl.COLNAME_COUNTRY);
                        city = City.of(cityId, cityName, DataObjectReference.of(Country.of(countryId, resultSet.wasNull() ? "" : countryName)));
                    }
                }
                String postalCode = resultSet.getString(AddressImpl.COLNAME_POSTALCODE);
                if (resultSet.wasNull()) {
                    postalCode = "";
                }
                String phone = resultSet.getString(AddressImpl.COLNAME_PHONE);
                target.address = DataObjectReference.of(Address.of(addressId, address1, address2,  DataObjectReference.of(city), postalCode,
                        (resultSet.wasNull()) ? "" : phone));
            }

            target.active = resultSet.getBoolean(COLNAME_ACTIVE);
            if (resultSet.wasNull()) {
                target.active = false;
            }
        }

        @Override
        public ModelFilter<CustomerImpl, CustomerModel> getAllItemsFilter() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ModelFilter<CustomerImpl, CustomerModel> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getDeleteDependencyMessage(CustomerImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSaveConflictMessage(CustomerImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static abstract class FilterImpl extends Filter<CustomerImpl> {
        
        @Override
        public FactoryImpl getFactory() { return FACTORY; }
        
    }
    
}
