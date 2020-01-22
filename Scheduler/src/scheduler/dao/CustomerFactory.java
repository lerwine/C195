package scheduler.dao;

import java.sql.Connection;
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
import view.address.CustomerAddress;
import view.city.AddressCity;
import view.country.CityCountry;
import view.customer.CustomerModel;

/**
 *
 * @author erwinel
 */
public class CustomerFactory extends DataObjectFactory<CustomerImpl, CustomerModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    public static final String COLNAME_CUSTOMERNAME = "customerName";
    
    public static final String COLNAME_ADDRESSID = "addressId";
    
    public static final String COLNAME_ACTIVE = "active";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT p.`");
        sql.append(CustomerFactory.COLNAME_CUSTOMERID).append("` AS ").append(CustomerFactory.COLNAME_CUSTOMERID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY, CustomerFactory.COLNAME_CUSTOMERID, CustomerFactory.COLNAME_CUSTOMERNAME,
                CustomerFactory.COLNAME_ADDRESSID).forEach((t) -> {
            sql.append(", p.`").append(t).append("` AS ").append(t);
        });
        Stream.of(AddressFactory.COLNAME_ADDRESS, AddressFactory.COLNAME_ADDRESS2, AddressFactory.COLNAME_CITYID, AddressFactory.COLNAME_POSTALCODE,
                AddressFactory.COLNAME_PHONE).forEach((t) -> {
            sql.append(", a.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", c.`").append(CityFactory.COLNAME_CITY).append("` AS ").append(CityFactory.COLNAME_CITY)
                .append(", c.`").append(CityFactory.COLNAME_COUNTRYID).append("` AS ").append(CityFactory.COLNAME_COUNTRYID)
                .append(", n.`").append(CountryFactory.COLNAME_COUNTRY).append("` AS ").append(CountryFactory.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(CustomerImpl.class))
                .append("` p LEFT JOIN `").append(getTableName(AddressImpl.class)).append("` a ON p.`").append(CustomerFactory.COLNAME_ADDRESSID).append("`=a.`").append(AddressFactory.COLNAME_ADDRESSID)
                .append("` LEFT JOIN `").append(getTableName(CityImpl.class)).append("` c ON a.`").append(AddressFactory.COLNAME_CITYID).append("`=c.`").append(CityFactory.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(CityFactory.COLNAME_COUNTRYID).append("`=n.`").append(CountryFactory.COLNAME_COUNTRYID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(CustomerFactory.COLNAME_CUSTOMERNAME, AddressFactory.COLNAME_ADDRESS,
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
        return load(connection, CustomerFactory.activeIs(isActive), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive) throws Exception {
        return load(connection, CustomerFactory.activeIs(isActive));
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, CustomerFactory.activeIs(isActive).and(CustomerFactory.addressIdIs(addressId)), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, boolean isActive) throws Exception {
        return load(connection, CustomerFactory.activeIs(isActive).and(CustomerFactory.addressIdIs(addressId)));
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, CustomerFactory.addressIdIs(addressId), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByAddress(Connection connection, int addressId) throws Exception {
        return load(connection, CustomerFactory.addressIdIs(addressId));
    }
    
    public ArrayList<CustomerImpl> loadByCity(Connection connection, int cityId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, CustomerFactory.cityIdIs(cityId), orderBy);
    }
    
    public ArrayList<CustomerImpl> loadByCountry(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, CustomerFactory.countryIdIs(countryId), orderBy);
    }
    
    public int countByAddress(Connection connection, int addressId) throws Exception {
        return count(connection, CustomerFactory.addressIdIs(addressId));
    }
    
    @Override
    protected CustomerImpl fromResultSet(ResultSet resultSet) throws SQLException { return new CustomerImpl(resultSet); }

    @Override
    public CustomerModel fromDataAccessObject(CustomerImpl dao) { return (dao == null) ? null : new CustomerModel(dao); }

    @Override
    public String getBaseQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<? extends CustomerImpl> getDaoClass() { return CustomerImpl.class; }
    
}
