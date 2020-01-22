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
import view.ChildModel;
import view.address.AddressModel;
import view.city.AddressCity;
import view.country.CityCountry;

/**
 *
 * @author erwinel
 */
public class AddressFactory extends DataObjectFactory<AddressImpl, AddressModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">

    /**
     * The name of the 'addressId' column in the 'address' table, which is also the primary key.
     */
    public static final String COLNAME_ADDRESSID = "addressId";
    
    /**
     * The name of the 'address' column in the 'address' table.
     */
    public static final String COLNAME_ADDRESS = "address";
    
    /**
     * The name of the 'address2' column in the 'address' table.
     */
    public static final String COLNAME_ADDRESS2 = "address2";
    
    /**
     * The name of the 'cityId' column in the 'address' table.
     */
    public static final String COLNAME_CITYID = "cityId";
    
    /**
     * The name of the 'postalCode' column in the 'address' table.
     */
    public static final String COLNAME_POSTALCODE = "postalCode";
    
    /**
     * The name of the 'phone' column in the 'address' table.
     */
    public static final String COLNAME_PHONE = "phone";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT a.`");
        sql.append(COLNAME_ADDRESSID).append("` AS ").append(COLNAME_ADDRESSID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_ADDRESS, COLNAME_ADDRESS2,
                COLNAME_CITYID, COLNAME_POSTALCODE, COLNAME_PHONE).forEach((t) -> {
            sql.append(", a.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", c.`").append(CityFactory.COLNAME_CITY).append("` AS ").append(CityFactory.COLNAME_CITY)
                .append(", c.`").append(CityFactory.COLNAME_COUNTRYID).append("` AS ").append(CityFactory.COLNAME_COUNTRYID)
                .append(", n.`").append(CountryFactory.COLNAME_COUNTRY).append("` AS ").append(CountryFactory.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(AddressImpl.class))
                .append("` a LEFT JOIN `").append(getTableName(CityImpl.class)).append("` c ON a.`").append(COLNAME_CITYID).append("`=c.`")
                    .append(CityFactory.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(CityFactory.COLNAME_COUNTRYID)
                    .append("`=n.`").append(CountryFactory.COLNAME_COUNTRYID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_ADDRESS, CityFactory.COLNAME_CITY,
                    CountryFactory.COLNAME_COUNTRY, COLNAME_POSTALCODE, COLNAME_PHONE, COLNAME_LASTUPDATE,
                    COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">
    
    public static final ValueAccessor<AddressModel, String> VALUE_ACCESSOR_ADDRESS1 = new ValueAccessor<AddressModel, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(AddressModel t) { return t.getAddress1(); }
        @Override
        public String get() { return COLNAME_ADDRESS; }
    };
    
    public static final ValueAccessor<AddressModel, String> VALUE_ACCESSOR_ADDRESS2 = new ValueAccessor<AddressModel, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(AddressModel t) { return t.getAddress2(); }
        @Override
        public String get() { return COLNAME_ADDRESS2; }
    };
    
    public static final ValueAccessor<AddressModel, String> VALUE_ACCESSOR_CITY_NAME = new ValueAccessor<AddressModel, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(AddressModel t) { return t.getCityName(); }
        @Override
        public String get() { return CityFactory.COLNAME_CITY; }
    };
    
    public static final ValueAccessor<AddressModel, String> VALUE_ACCESSOR_COUNTRY_NAME = new ValueAccessor<AddressModel, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(AddressModel t) { return t.getCountryName(); }
        @Override
        public String get() { return CountryFactory.COLNAME_COUNTRY; }
    };
    
    public static final ValueAccessor<AddressModel, String> VALUE_ACCESSOR_POSTALCODE = new ValueAccessor<AddressModel, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(AddressModel t) { return t.getPostalCode(); }
        @Override
        public String get() { return COLNAME_POSTALCODE; }
    };
    
    public static final ValueAccessor<AddressModel, String> VALUE_ACCESSOR_PHONE = new ValueAccessor<AddressModel, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(AddressModel t) { return t.getPhone(); }
        @Override
        public String get() { return COLNAME_PHONE; }
    };
    
    public static final ValueAccessor<AddressModel, Integer> VALUE_ACCESSOR_CITY_ID = new ValueAccessor<AddressModel, Integer>() {
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
        @Override
        public Integer apply(AddressModel t) {
            AddressCity<?> c = t.getCity();
            return (null != c && c.getDataObject().isExisting()) ? c.getDataObject().getPrimaryKey() : Integer.MIN_VALUE;
        }
        @Override
        public String get() { return COLNAME_CITYID; }
    };
    
    public static final ValueAccessor<AddressModel, Integer> VALUE_ACCESSOR_COUNTRY_ID = new ValueAccessor<AddressModel, Integer>() {
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
        @Override
        public Integer apply(AddressModel t) {
            AddressCity c = t.getCity();
            if (null != c) {
                CityCountry<?> n = c.getCountry();
                if (null != n && n.getDataObject().isExisting())
                    return n.getDataObject().getPrimaryKey();
            }
            return Integer.MIN_VALUE;
        }
        @Override
        public String get() { return CityFactory.COLNAME_COUNTRYID; }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">
    
    public static ModelFilter<AddressModel> getFilterWhereAddress1Is(String value) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_ADDRESS1, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereAddress1IsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_ADDRESS1, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereAddress2Is(String value) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_ADDRESS1, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereAddress2IsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_ADDRESS1, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWherePostalCodeIs(String value) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_POSTALCODE, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWherePostalCodeIsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_POSTALCODE, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWherePhoneIs(String value) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_PHONE, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWherePhoneIsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_PHONE, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCityIdIs(Integer id) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_CITY_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCityIdIsNot(Integer id) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_CITY_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCityNameIs(String value) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_CITY_NAME, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCityNameIsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_CITY_NAME, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCountryIdIs(Integer id) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_COUNTRY_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCountryIdIsNot(Integer id) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_COUNTRY_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCountryNameIs(String value) {
        return ModelFilter.columnIsEqualTo(VALUE_ACCESSOR_COUNTRY_NAME, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereCountryNameIsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(VALUE_ACCESSOR_COUNTRY_NAME, ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereAddressLinesAre(String line1, String line2, int cityId, String postalCode, String phone) {
        return getFilterWhereCityIdIs(cityId).and(getFilterWhereAddress1Is(line1)).and(getFilterWhereAddress2Is(line2)).and(getFilterWherePostalCodeIs(postalCode))
                .and(getFilterWherePhoneIs(phone));
    }
    
    public static ModelFilter<AddressModel> getFilterWhereAddressLinesAre(String line1, String line2, int cityId, String postalCode) {
        return getFilterWhereCityIdIs(cityId).and(getFilterWhereAddress1Is(line1)).and(getFilterWhereAddress2Is(line2)).and(getFilterWherePostalCodeIs(postalCode));
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    public Optional<AddressImpl> findByAddressLines(Connection connection, String line1, String line2, int cityId, String postalCode, String phone) throws Exception {
        return loadFirst(connection, getFilterWhereAddressLinesAre(line1, line2, cityId, postalCode, phone));
    }
    
    public Optional<AddressImpl> findByAddressLines(Connection connection, String line1, String line2, int cityId, String postalCode) throws Exception {
        return loadFirst(connection, getFilterWhereAddressLinesAre(line1, line2, cityId, postalCode));
    }
    
    public ArrayList<AddressImpl> loadByCity(Connection connection, int cityId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, getFilterWhereCityIdIs(cityId), orderBy);
    }
    
    public ArrayList<AddressImpl> loadByCity(Connection connection, int cityId) throws Exception {
        return load(connection, getFilterWhereCityIdIs(cityId));
    }
    
    public ArrayList<AddressImpl> loadByCountry(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, getFilterWhereCountryIdIs(countryId), orderBy);
    }
    
    public ArrayList<AddressImpl> loadByCountry(Connection connection, int countryId) throws Exception {
        return load(connection, getFilterWhereCountryIdIs(countryId));
    }
    
    public int countByCity(Connection connection, int cityId) throws Exception {
        return count(connection, getFilterWhereCityIdIs(cityId));
    }
    
    @Override
    protected AddressImpl fromResultSet(ResultSet resultSet) throws SQLException { return new AddressImpl(resultSet); }

    @Override
    public AddressModel fromDataAccessObject(AddressImpl dao) { return (dao == null) ? null : new AddressModel(dao); }

    @Override
    public String getBaseQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<? extends AddressImpl> getDaoClass() { return AddressImpl.class; }
    
}
