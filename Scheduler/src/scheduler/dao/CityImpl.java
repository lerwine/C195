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
    
    public static ArrayList<CityImpl> lookupAll(Connection connection, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = getBaseSelectQuery() + SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.of(SelectOrderSpec.of(Country.COLNAME_COUNTRY), SelectOrderSpec.of(COLNAME_CITY)));
        try (PreparedStatement ps = connection.prepareStatement(getBaseSelectQuery())) {
            return toList(ps, (rs) -> new CityImpl(rs));
        }
    }
    
    public static Iterable<CityImpl> lookupAll(Connection connection) throws SQLException {
        return lookupAll(connection, null);
    }
    
    public static ArrayList<CityImpl> lookupByCountry(Connection connection, Country country, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(country, "country cannot be null");
        if  (country.getRowState()== DataObject.ROWSTATE_DELETED || country.getRowState() == DataObject.ROWSTATE_NEW)
            return new ArrayList<>();
        String sql = String.format("%s WHERE '%s'=%%%s", getBaseSelectQuery(), COLNAME_COUNTRYID,
            SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.of(SelectOrderSpec.of(Country.COLNAME_COUNTRY), SelectOrderSpec.of(COLNAME_CITY))));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, country.getPrimaryKey());
            return toList(ps, (rs) -> new CityImpl(rs));
        }
    }
    
    public static Optional<CityImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s` = %%", getBaseSelectQuery(), COLNAME_CITYID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new CityImpl(rs));
        }
    }

    public static int lookupUsageCount(Connection connection, Country country) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(connection, "country cannot be null");
        if  (country.getRowState()== DataObject.ROWSTATE_DELETED || country.getRowState() == DataObject.ROWSTATE_NEW)
            return 0;
        String sql = String.format("SELECT COUNT(`%s`) FROM `%s` WHERE '%s'=%%", getPrimaryKeyColName(CityImpl.class), TABLENAME_CITY,
                COLNAME_COUNTRYID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, country.getPrimaryKey());
            ResultSet rs = ps.getResultSet();
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    @Override
    public synchronized void delete(Connection connection) throws SQLException {
        assert AddressImpl.lookupUsageCount(connection, this) == 0 : "City is associated with one or more addresses.";
        super.delete(connection);
    }
}
