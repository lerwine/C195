package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.view.customer.CustomerModel;

/**
 *
 * @author erwinel
 */
public class CustomerFactory extends DataObjectFactory<CustomerFactory.CustomerImpl, CustomerModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    public static final String COLNAME_CUSTOMERNAME = "customerName";
    
    public static final String COLNAME_ADDRESSID = "addressId";
    
    public static final String COLNAME_ACTIVE = "active";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_CUSTOMERNAME, AddressFactory.COLNAME_ADDRESS,
                    CityFactory.COLNAME_CITY, CountryFactory.COLNAME_COUNTRY, AddressFactory.COLNAME_POSTALCODE, AddressFactory.COLNAME_PHONE,
                    UserFactory.COLNAME_USERNAME, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY,
                    COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    public Optional<CustomerImpl> findByName(Connection connection, String value) throws Exception { throw new UnsupportedOperationException("Not implemented"); }
    
//    public ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
//        return load(connection, activeIs(isActive), orderBy);
//    }
    
    public ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
//    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
//        return load(connection, activeIs(isActive).and(addressIdIs(addressId)), orderBy);
//    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
//    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, Iterable<OrderBy> orderBy) throws Exception {
//        return load(connection, addressIdIs(addressId), orderBy);
//    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
//    public ArrayList<CustomerImpl> loadByCity(Connection connection, int cityId, Iterable<OrderBy> orderBy) throws Exception {
//        return load(connection, cityIdIs(cityId), orderBy);
//    }
//    
//    public ArrayList<CustomerImpl> loadByCountry(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
//        return load(connection, countryIdIs(countryId), orderBy);
//    }
    
    public int countByAddress(Connection connection, int addressId) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    protected CustomerImpl fromResultSet(ResultSet resultSet) throws SQLException {
        CustomerImpl r = new CustomerImpl();
        initializeDao(r, resultSet);
        return r;
    }

    @Override
    public CustomerModel fromDataAccessObject(CustomerImpl dao) { return (dao == null) ? null : new CustomerModel(dao); }

    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static final String BASE_SELECT_SQL = String.format("SELECT p.`%s` AS `%s`, p.`%s` AS `%s`, p.`%s` AS `%s`, p.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`,"
            + "a.`%s` AS `%s`, c.`%s` AS `%s`, c.`%s` AS `%s`, n.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`"
            + " FROM `%s` p LEFT JOIN `%s` a ON p.`%s`=a.`%s` LEFT JOIN `%s` c ON a.`%s`=c.`%s` LEFT JOIN `%s` n ON c.`%s`=n.`%s`", COLNAME_CUSTOMERID, COLNAME_CUSTOMERID, 
            COLNAME_CUSTOMERNAME, COLNAME_CUSTOMERNAME, COLNAME_ACTIVE, COLNAME_ACTIVE, COLNAME_ADDRESSID, COLNAME_ADDRESSID, AddressFactory.COLNAME_ADDRESS, 
            AddressFactory.COLNAME_ADDRESS, AddressFactory.COLNAME_ADDRESS2, AddressFactory.COLNAME_ADDRESS2, AddressFactory.COLNAME_CITYID, AddressFactory.COLNAME_CITYID,
            CityFactory.COLNAME_CITY, CityFactory.COLNAME_CITY, CityFactory.COLNAME_COUNTRYID, CityFactory.COLNAME_COUNTRYID, CountryFactory.COLNAME_COUNTRY,
            CountryFactory.COLNAME_COUNTRY, AddressFactory.COLNAME_POSTALCODE, AddressFactory.COLNAME_POSTALCODE, AddressFactory.COLNAME_PHONE, AddressFactory.COLNAME_PHONE,
            COLNAME_CREATEDATE, COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY,
            TABLENAME_CUSTOMER, TABLENAME_ADDRESS, COLNAME_ADDRESSID, AddressFactory.COLNAME_ADDRESSID, TABLENAME_CITY, AddressFactory.COLNAME_CITYID, CityFactory.COLNAME_CITYID,
            TABLENAME_COUNTRY, CityFactory.COLNAME_COUNTRYID, CountryFactory.COLNAME_COUNTRYID);
    
    @Override
    public String getBaseQuery() { return BASE_SELECT_SQL; }

    //</editor-fold>
    
    @Override
    public Class<? extends CustomerImpl> getDaoClass() { return CustomerImpl.class; }

    @Override
    protected Stream<String> getExtendedColNames() {
        return Stream.of(COLNAME_CUSTOMERNAME, COLNAME_ACTIVE, COLNAME_ADDRESSID);
    }

    @Override
    protected void setStatementValues(CustomerImpl dao, PreparedStatement ps) throws SQLException {
        ps.setString(1, dao.getName());
        ps.setBoolean(2, dao.isActive());
        ps.setInt(3, dao.getAddress().getPrimaryKey());
    }

    @Override
    protected void initializeDao(CustomerImpl target, ResultSet resultSet) throws SQLException {
        super.initializeDao(target, resultSet);
           target.name  = resultSet.getString(COLNAME_CUSTOMERNAME);
           if (resultSet.wasNull())
               target.name = "";

           int addressId = resultSet.getInt(COLNAME_ADDRESSID);
           if (resultSet.wasNull())
               target.address = null;
           else {
               String address1 = resultSet.getString(AddressFactory.COLNAME_ADDRESS);
               if (resultSet.wasNull())
                   address1 = "";
               String address2 = resultSet.getString(AddressFactory.COLNAME_ADDRESS2);
               if (resultSet.wasNull())
                   address2 = "";
               City city;
               int cityId = resultSet.getInt(AddressFactory.COLNAME_CITYID);
               if (resultSet.wasNull())
                   city = null;
               else {
                   String cityName = resultSet.getString(CityFactory.COLNAME_CITY);
                   if (resultSet.wasNull())
                       cityName = "";
                   int countryId = resultSet.getInt(CityFactory.COLNAME_COUNTRYID);
                   if (resultSet.wasNull())
                       city = City.of(cityId, cityName, null);
                   else {
                       String countryName = resultSet.getString(CountryFactory.COLNAME_COUNTRY);
                       city = City.of(cityId, cityName, Country.of(countryId, resultSet.wasNull() ? "" : countryName));
                   }
               }
               String postalCode = resultSet.getString(AddressFactory.COLNAME_POSTALCODE);
               if (resultSet.wasNull())
                   postalCode = "";
               String phone = resultSet.getString(AddressFactory.COLNAME_PHONE);
               target.address = Address.of(addressId, address1, address2, city, postalCode, (resultSet.wasNull()) ? "" : phone);
           }

           target.active = resultSet.getBoolean(COLNAME_ACTIVE);
           if (resultSet.wasNull())
               target.active = false;
    }

    @Override
    public String getTableName() { return TABLENAME_CUSTOMER; }

    @Override
    public String getPrimaryKeyColName() { return COLNAME_CUSTOMERID; }
 
    /**
    *
    * @author erwinel
    */
//   @TableName(DataObjectFactory.TABLENAME_CUSTOMER)
//   @PrimaryKeyColumn(CustomerFactory.COLNAME_CUSTOMERID)
   public static final class CustomerImpl extends DataObjectFactory.DataObjectImpl implements Customer {
       //<editor-fold defaultstate="collapsed" desc="Properties and Fields">

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

       private Address address;

       /**
        * {@inheritDoc}
        */
       @Override
       public Address getAddress() {
           return address;
       }

       /**
        * Set the value of address
        *
        * @param address new value of address
        */
       public void setAddress(Address address) {
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
        * Initializes a {@link DataObject.ROWSTATE_NEW} customer object.
        */
       public CustomerImpl() {
           super();
           name = "";
           address = null;
           active = true;
       }

       @Override
       public void saveChanges(Connection connection) throws Exception {
           (new CustomerFactory()).save(this, connection);
       }

       @Override
       public void delete(Connection connection) throws Exception {
           (new CustomerFactory()).delete(this, connection);
       }

   }
}
