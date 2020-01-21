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
import scheduler.filter.SqlStatementBuilder;
import scheduler.filter.ValueAccessor;
import view.ChildModel;
import view.city.AddressCity;
import view.city.CityModel;
import view.country.CityCountry;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectImpl.TABLENAME_CITY)
@PrimaryKeyColumn(CityImpl.COLNAME_CITYID)
public class CityImpl extends DataObjectImpl implements City {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT c.`");
        sql.append(COLNAME_CITYID).append("` AS ").append(COLNAME_CITYID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY).forEach((t) -> {
            sql.append(", c.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", c.`").append(COLNAME_CITY).append("` AS ").append(COLNAME_CITY)
                .append(", c.`").append(COLNAME_COUNTRYID).append("` AS ").append(COLNAME_COUNTRYID)
                .append(", n.`").append(Country.COLNAME_COUNTRY).append("` AS ").append(Country.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(CityImpl.class))
                .append("` c LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(COLNAME_COUNTRYID)
                .append("`=n.`").append(Country.COLNAME_COUNTRYID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_CITY, Country.COLNAME_COUNTRY,
                    COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="name property">
    
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() { return name; }

    /**
     * Set the value of name
     *
     * @param value new value of name
     */
    public void setName(String value) { name = (value == null) ? "" : value; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="country property">
    
    private Country country;

    /**
     * {@inheritDoc}
     */
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
        this.country = country;
    }

    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} city object.
     */
    public CityImpl() {
        super();
        name = "";
        country = null;
    }
    
    /**
     * Initializes a city object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public CityImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        name = resultSet.getString(COLNAME_CITY);
        if (resultSet.wasNull())
            name = "";
        int countryId = resultSet.getInt(City.COLNAME_COUNTRYID);
        if (resultSet.wasNull())
            country = null;
        else {
            String countryName = resultSet.getString(Country.COLNAME_COUNTRY);
            country = Country.of(countryId, resultSet.wasNull() ? "" : countryName);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">

    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#start} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<CityModel, CityCountry<?>> COUNTRY = new ValueAccessor<CityModel, CityCountry<?>>() {
        @Override
        public String get() { return City.COLNAME_COUNTRYID; }
        @Override
        public CityCountry<?> apply(CityModel t) { return t.getCountry(); }
        @Override
        public void accept(CityCountry<?> t, ParameterConsumer u) throws SQLException {
            u.setInt(t.getDataObject().getPrimaryKey());
        }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    /**
     * Creates a {@link ModelFilter} for cities where the primary key of the {@link CityModel#country} column/property is equal to the primary key of the specified {@link AppointmentCustomer} object.
     * @param value The {@link CityCountry} to compare to.
     * @return A {@link ModelFilter} for {@link CityModel} objects where the primary key of the {@link CityModel#country} column/property
     * is equal to the primary key of the specified {@link CityCountry} object.
     */
    public static ModelFilter<CityModel> countryIs(CityCountry<?> value) {
        return ModelFilter.columnIsEqualTo(COUNTRY, ModelFilter.COMPARATOR_COUNTRY, ChildModel.requireExisting(value, "Country"));
    }
    
    /**
     * Creates a {@link ModelFilter} for cities where the primary key of the {@link CityModel#country} column/property is not equal to the primary key of the specified {@link AppointmentCustomer} object.
     * @param value The {@link CityCountry} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the primary key of the {@link CityModel#country} column/property
     * is not equal to the primary key of the specified {@link CityCountry} object.
     */
    public static ModelFilter<CityModel> countryIsNot(CityCountry<?> value) {
        return ModelFilter.columnIsNotEqualTo(COUNTRY, ModelFilter.COMPARATOR_COUNTRY, ChildModel.requireExisting(value, "Customer"));
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    public static ArrayList<CityImpl> loadAll(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return loadAll(connection, getBaseSelectQuery(),
                OrderBy.getOrderByOrDefault(orderBy, () -> OrderBy.of(OrderBy.of(Country.COLNAME_COUNTRY), OrderBy.of(COLNAME_CITY))),
                (rs) -> new CityImpl(rs));
    }
    
    public static Iterable<CityImpl> loadAll(Connection connection) throws Exception {
        return loadAll(connection, null);
    }
    
    public static ArrayList<CityImpl> loadByCountry(Connection connection, Country country, Iterable<OrderBy> orderBy) throws Exception {
        Objects.requireNonNull(country, "Country cannot be null");
        return load(connection, getBaseSelectQuery(), countryIs(CityCountry.of(country)), orderBy, (rs) -> new CityImpl(rs));
    }
    
    public static Optional<CityImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s` = %%", getBaseSelectQuery(), COLNAME_CITYID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new CityImpl(rs));
        }
    }

    public static int getCount(Connection connection, ModelFilter<CityModel> filter) throws Exception {
        try (SqlStatementBuilder<PreparedStatement> builder = SqlStatementBuilder.fromConnection(connection)) {
            builder.appendSql("SELECT COUNT(`").appendSql(COLNAME_CITYID).appendSql("`) FROM `")
                    .appendSql(TABLENAME_CITY).appendSql("`");
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
    public synchronized void delete(Connection connection) throws Exception {
        assert AddressImpl.getCount(connection, AddressImpl.cityIs(AddressCity.of(this))) == 0 : "City is associated with one or more addresses.";
        super.delete(connection);
    }
    
}
