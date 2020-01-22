package scheduler.dao.factory;

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
import scheduler.dao.Address;
import scheduler.dao.AddressImpl;
import scheduler.dao.CityImpl;
import scheduler.dao.CountryImpl;
import scheduler.dao.CustomerImpl;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.SqlStatementBuilder;
import scheduler.filter.ValueAccessor;
import view.ChildModel;
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

    public static final ValueAccessor<CustomerModel, CustomerAddress<?>> ADDRESS = new ValueAccessor<CustomerModel, CustomerAddress<?>>() {
        @Override
        public String get() { return COLNAME_ADDRESSID; }
        @Override
        public CustomerAddress<?> apply(CustomerModel t) {
            return t.getAddress();
        }
        @Override
        public void accept(CustomerAddress<?> t, ParameterConsumer u) throws SQLException {
            u.setInt(t.getDataObject().getPrimaryKey());
        }
    };
    
    public static final ValueAccessor<CustomerModel, AddressCity<?>> CITY = new ValueAccessor<CustomerModel, AddressCity<?>>() {
        @Override
        public String get() { return AddressFactory.COLNAME_CITYID; }
        @Override
        public AddressCity<?> apply(CustomerModel t) {
            CustomerAddress<?> addr = t.getAddress();
            if (addr != null)
                return addr.getCity();
            return null;
        }
        @Override
        public void accept(AddressCity<?> t, ParameterConsumer u) throws SQLException {
            u.setInt(t.getDataObject().getPrimaryKey());
        }
    };
    
    public static final ValueAccessor<CustomerModel, String> POSTALCODE = new ValueAccessor<CustomerModel, String>() {
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
        public void accept(String t, ParameterConsumer u) throws SQLException {
            u.setString(t);
        }
    };
    
    public static final ValueAccessor<CustomerModel, CityCountry<?>> COUNTRY = new ValueAccessor<CustomerModel, CityCountry<?>>() {
        @Override
        public String get() { return CityFactory.COLNAME_COUNTRYID; }
        @Override
        public CityCountry<?> apply(CustomerModel t) {
            CustomerAddress<?> addr = t.getAddress();
            if (addr != null) {
                AddressCity<?> city = addr.getCity();
                if (city != null)
                    return city.getCountry();
            }
            return null;
        }
        @Override
        public void accept(CityCountry<?> t, ParameterConsumer u) throws SQLException {
            u.setInt(t.getDataObject().getPrimaryKey());
        }
    };
    
    public static final ValueAccessor<CustomerModel, Boolean> ACTIVE = new ValueAccessor<CustomerModel, Boolean>() {
        @Override
        public String get() { return CityFactory.COLNAME_COUNTRYID; }
        @Override
        public Boolean apply(CustomerModel t) {
            return t.isActive();
        }
        @Override
        public void accept(Boolean t, ParameterConsumer u) throws SQLException {
            u.setBoolean(t);
        }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    public static ModelFilter<CustomerModel> addressIs(CustomerAddress<?> value) {
        return ModelFilter.columnIsEqualTo(ADDRESS, ModelFilter.COMPARATOR_ADDRESS, ChildModel.requireExisting(value, "Address"));
    }
    
    public static ModelFilter<CustomerModel> countryIs(CityCountry<?> value) {
        return ModelFilter.columnIsEqualTo(COUNTRY, ModelFilter.COMPARATOR_COUNTRY, ChildModel.requireExisting(value, "Country"));
    }
    
    public static ModelFilter<CustomerModel> countryIsNot(CityCountry<?> value) {
        return ModelFilter.columnIsNotEqualTo(COUNTRY, ModelFilter.COMPARATOR_COUNTRY, ChildModel.requireExisting(value, "Customer"));
    }
    
    public static ModelFilter<CustomerModel> cityIs(AddressCity<?> value) {
        return ModelFilter.columnIsEqualTo(CITY, ModelFilter.COMPARATOR_CITY, ChildModel.requireExisting(value, "City"));
    }
    
    public static ModelFilter<CustomerModel> postalCodeIs(String value) {
        return ModelFilter.columnIsEqualTo(POSTALCODE, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value, "Postal code cannot be null"));
    }
    
    public static ModelFilter<CustomerModel> activeIs(boolean value) {
        return ModelFilter.columnIsEqualTo(ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, value);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    @Deprecated
    public static ArrayList<CustomerImpl> loadAll(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return loadAll(connection, CustomerFactory.getBaseSelectQuery(), orderBy, (rs) -> new CustomerImpl(rs));
    }
    
    @Deprecated
    public static Iterable<CustomerImpl> loadAll(Connection connection) throws Exception {
        return loadAll(connection, null);
    }
    
    @Deprecated
    public static ArrayList<CustomerImpl> loadByStatus(Connection connection, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, CustomerFactory.getBaseSelectQuery(), CustomerFactory.activeIs(isActive), orderBy, (rs) -> new CustomerImpl(rs));
    }
    
    @Deprecated
    public static Iterable<CustomerImpl> loadByStatus(Connection connection, boolean isActive) throws Exception {
        return loadByStatus(connection, isActive, null);
    }
    
    @Deprecated
    public static ArrayList<CustomerImpl> loadByAddress(Connection connection, Address address, boolean isActive, Iterable<OrderBy> orderBy) throws Exception {
        Objects.requireNonNull(address, "Address cannot be null");
        return load(connection, CustomerFactory.getBaseSelectQuery(), CustomerFactory.addressIs(CustomerAddress.of(address)).and(CustomerFactory.activeIs(isActive)), orderBy, (rs) -> new CustomerImpl(rs));
    }
    
    @Deprecated
    public static ArrayList<CustomerImpl> loadByAddress(Connection connection, Address address, Iterable<OrderBy> orderBy) throws Exception {
        Objects.requireNonNull(address, "Address cannot be null");
        return load(connection, CustomerFactory.getBaseSelectQuery(), CustomerFactory.addressIs(CustomerAddress.of(address)), orderBy, (rs) -> new CustomerImpl(rs));
    }
    
    @Deprecated
    public static Optional<CustomerImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s`=%%", CustomerFactory.getBaseSelectQuery(), CustomerFactory.COLNAME_CUSTOMERID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new CustomerImpl(rs));
        }
    }

    @Deprecated
    public static int getCount(Connection connection, ModelFilter<CustomerModel> filter) throws Exception {
        try (SqlStatementBuilder<PreparedStatement> builder = SqlStatementBuilder.fromConnection(connection)) {
            builder.appendSql("SELECT COUNT(`").appendSql(CustomerFactory.COLNAME_CUSTOMERID).appendSql("`) FROM `")
                    .appendSql(TABLENAME_CUSTOMER).appendSql("`");
            if (null != filter) {
                String s = filter.get();
                if (!s.isEmpty())
                    builder.appendSql(" WHERE ").appendSql(s);
                filter.setParameterValues(builder.finalizeSql());
            }
        
            try (ResultSet rs = builder.getResult().executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        return 0;
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
