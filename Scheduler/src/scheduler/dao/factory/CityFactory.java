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
import scheduler.dao.CityImpl;
import scheduler.dao.Country;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.SqlStatementBuilder;
import scheduler.filter.ValueAccessor;
import view.ChildModel;
import view.city.CityModel;
import view.country.CityCountry;

/**
 *
 * @author erwinel
 */
public class CityFactory extends DataObjectFactory<CityImpl, CityModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_CITYID = "cityId";
    
    public static final String COLNAME_CITY = "city";
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT c.`");
        sql.append(COLNAME_CITYID).append("` AS ").append(COLNAME_CITYID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY).forEach((t) -> {
            sql.append(", c.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", c.`").append(COLNAME_CITY).append("` AS ").append(COLNAME_CITY)
                .append(", c.`").append(COLNAME_COUNTRYID).append("` AS ").append(COLNAME_COUNTRYID)
                .append(", n.`").append(CountryFactory.COLNAME_COUNTRY).append("` AS ").append(CountryFactory.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(CityImpl.class))
                .append("` c LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(COLNAME_COUNTRYID)
                .append("`=n.`").append(CountryFactory.COLNAME_COUNTRYID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_CITY, CountryFactory.COLNAME_COUNTRY,
                    COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE,
                    COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">

    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#start} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<CityModel, CityCountry<?>> COUNTRY = new ValueAccessor<CityModel, CityCountry<?>>() {
        @Override
        public String get() { return COLNAME_COUNTRYID; }
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
    
    @Deprecated
    public static ArrayList<CityImpl> loadAll(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return loadAll(connection, getBaseSelectQuery(),
                OrderBy.getOrderByOrDefault(orderBy, () -> OrderBy.of(OrderBy.of(CountryFactory.COLNAME_COUNTRY), OrderBy.of(COLNAME_CITY))),
                (rs) -> new CityImpl(rs));
    }
    
    @Deprecated
    public static Iterable<CityImpl> loadAll(Connection connection) throws Exception {
        return loadAll(connection, null);
    }
    
    @Deprecated
    public static ArrayList<CityImpl> loadByCountry(Connection connection, Country country, Iterable<OrderBy> orderBy) throws Exception {
        Objects.requireNonNull(country, "Country cannot be null");
        return load(connection, getBaseSelectQuery(), countryIs(CityCountry.of(country)), orderBy, (rs) -> new CityImpl(rs));
    }
    
    @Deprecated
    public static Optional<CityImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s` = %%", getBaseSelectQuery(), COLNAME_CITYID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new CityImpl(rs));
        }
    }

    @Deprecated
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
    protected CityImpl fromResultSet(ResultSet resultSet) throws SQLException { return new CityImpl(resultSet); }

    @Override
    public CityModel fromDataAccessObject(CityImpl dao) { return (dao == null) ? null : new CityModel(dao); }

    @Override
    public String getBaseQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<? extends CityImpl> getDaoClass() { return CityImpl.class; }
    
}
