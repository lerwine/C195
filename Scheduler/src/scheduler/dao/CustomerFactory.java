package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.ValueAccessor;
import scheduler.view.address.CustomerAddress;
import scheduler.view.city.AddressCity;
import scheduler.view.country.CityCountry;
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
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">

    public static final ValueAccessor<CustomerModel, String> ACCESSOR_NAME = new ValueAccessor<CustomerModel, String>() {
        @Override
        public String get() { return COLNAME_CUSTOMERNAME; }
        @Override
        public String apply(CustomerModel t) { return t.getName(); }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public static final ValueAccessor<CustomerModel, Integer> ACCESSOR_ADDRESSID = new ValueAccessor<CustomerModel, Integer>() {
        @Override
        public String get() { return COLNAME_ADDRESSID; }
        @Override
        public Integer apply(CustomerModel t) {
            CustomerAddress<?> a = t.getAddress();
            return (null != a && a.getDataObject().isExisting()) ? a.getDataObject().getPrimaryKey() : Integer.MIN_VALUE;
        }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    
    public static final ValueAccessor<CustomerModel, String> ACCESSOR_ADDRESS1 = new ValueAccessor<CustomerModel, String>() {
        @Override
        public String get() { return AddressFactory.COLNAME_ADDRESS; }
        @Override
        public String apply(CustomerModel t) {
            CustomerAddress<?> a = t.getAddress();
            return (null == a) ? "" : a.getAddress1();
        }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public static final ValueAccessor<CustomerModel, Integer> ACCESSOR_CITYID = new ValueAccessor<CustomerModel, Integer>() {
        @Override
        public String get() { return AddressFactory.COLNAME_CITYID; }
        @Override
        public Integer apply(CustomerModel t) {
            CustomerAddress<?> a = t.getAddress();
            if (null != a) {
                AddressCity<?> c = a.getCity();
                if (null != c && c.getDataObject().isExisting())
                    return c.getDataObject().getPrimaryKey();
            }
            return Integer.MIN_VALUE;
        }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    
    public static final ValueAccessor<CustomerModel, String> ACCESSOR_CITY_NAME = new ValueAccessor<CustomerModel, String>() {
        @Override
        public String get() { return CityFactory.COLNAME_CITY; }
        @Override
        public String apply(CustomerModel t) {
            CustomerAddress<?> a = t.getAddress();
            if (null != a) {
                AddressCity<?> c = a.getCity();
                if (null != c)
                    return c.getName();
            }
            return "";
        }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public static final ValueAccessor<CustomerModel, String> ACCESSOR_POSTALCODE = new ValueAccessor<CustomerModel, String>() {
        @Override
        public String get() { return AddressFactory.COLNAME_POSTALCODE; }
        @Override
        public String apply(CustomerModel t) {
            CustomerAddress<?> addr = t.getAddress();
            if (addr != null)
                return addr.getPostalCode();
            return "";
        }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public static final ValueAccessor<CustomerModel, Integer> ACCESSOR_COUNTRYID = new ValueAccessor<CustomerModel, Integer>() {
        @Override
        public String get() { return CityFactory.COLNAME_COUNTRYID; }
        @Override
        public Integer apply(CustomerModel t) {
            CustomerAddress<?> a = t.getAddress();
            if (null != a) {
                AddressCity<?> c = a.getCity();
                if (null != c) {
                    CityCountry<?> n = c.getCountry();
                    if (null != n && n.getDataObject().isExisting())
                        return n.getDataObject().getPrimaryKey();
                }
            }
            return Integer.MIN_VALUE;
        }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    
    public static final ValueAccessor<CustomerModel, String> ACCESSOR_COUNTRY_NAME = new ValueAccessor<CustomerModel, String>() {
        @Override
        public String get() { return CountryFactory.COLNAME_COUNTRY; }
        @Override
        public String apply(CustomerModel t) {
            CustomerAddress<?> a = t.getAddress();
            if (null != a) {
                AddressCity<?> c = a.getCity();
                if (null != c) {
                    CityCountry<?> n = c.getCountry();
                    if (null != n)
                        return n.getName();
                }
            }
            return "";
        }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public static final ValueAccessor<CustomerModel, Boolean> ACCESSOR_ACTIVE = new ValueAccessor<CustomerModel, Boolean>() {
        @Override
        public String get() { return CityFactory.COLNAME_COUNTRYID; }
        @Override
        public Boolean apply(CustomerModel t) {
            return t.isActive();
        }
        @Override
        public void accept(Boolean t, ParameterConsumer u) throws SQLException { u.setBoolean(t); }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    public static ModelFilter<CustomerModel>nameIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_NAME, ModelFilter.COMPARATOR_STRING, value);
    }
    
    public static ModelFilter<CustomerModel> addressIdIs(int value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_ADDRESSID, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    public static ModelFilter<CustomerModel> address1Is(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_ADDRESS1, ModelFilter.COMPARATOR_STRING, value);
    }
    
    public static ModelFilter<CustomerModel> cityIdIs(int value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_CITYID, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    public static ModelFilter<CustomerModel> cityNameIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_CITY_NAME, ModelFilter.COMPARATOR_STRING, value);
    }
    
    public static ModelFilter<CustomerModel> countryIdIs(int value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_COUNTRYID, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    public static ModelFilter<CustomerModel> countryNameIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_COUNTRY_NAME, ModelFilter.COMPARATOR_STRING, value);
    }
    public static ModelFilter<CustomerModel> postalCodeIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_POSTALCODE, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value, "Postal code cannot be null"));
    }
    
    public static ModelFilter<CustomerModel> activeIs(boolean value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, value);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    public Optional<CustomerImpl> findByName(Connection connection, String value) throws Exception { return loadFirst(connection, nameIs(value)); }
    
    public ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, activeIs(isActive), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive) throws Exception {
        return load(connection, activeIs(isActive));
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, activeIs(isActive).and(addressIdIs(addressId)), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive) throws Exception {
        return load(connection, activeIs(isActive).and(addressIdIs(addressId)));
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, addressIdIs(addressId), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId) throws Exception {
        return load(connection, addressIdIs(addressId));
    }
    
    public ArrayList<CustomerImpl> loadByCity(Connection connection, int cityId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, cityIdIs(cityId), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByCountry(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, countryIdIs(countryId), orderBy);
    }
    
    public int countByAddress(Connection connection, int addressId) throws Exception {
        return count(connection, addressIdIs(addressId));
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
   public static final class CustomerImpl extends DataObjectImpl implements Customer {
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
