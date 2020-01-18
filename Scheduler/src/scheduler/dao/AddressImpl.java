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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 *
 * @author erwinel
 */
public class AddressImpl extends DataObjectImpl implements Address {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
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
        baseSelectQuery = sql.append(", c.`").append(City.COLNAME_CITY).append("` AS ").append(City.COLNAME_CITY)
                .append(", c.`").append(City.COLNAME_COUNTRYID).append("` AS ").append(City.COLNAME_COUNTRYID)
                .append(", n.`").append(Country.COLNAME_COUNTRY).append("` AS ").append(Country.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(AddressImpl.class))
                .append("` a LEFT JOIN `").append(getTableName(CityImpl.class)).append("` c ON a.`").append(Address.COLNAME_CITYID).append("`=c.`").append(City.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(City.COLNAME_COUNTRYID).append("`=n.`").append(Country.COLNAME_COUNTRYID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_ADDRESS, City.COLNAME_CITY,
                    Country.COLNAME_COUNTRY, COLNAME_POSTALCODE, COLNAME_PHONE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE,
                    COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="address1 property">
    
    private String address1;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress1() { return address1; }

    /**
     * Set the value of address1
     *
     * @param value new value of address1
     */
    public void setAddress1(String value) { address1 = (value == null) ? "" : value; }

    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="address2 property">
    
    private String address2;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress2() { return address2; }

    /**
     * Set the value of address2
     *
     * @param value new value of address2
     */
    public void setAddress2(String value) { address2 = (value == null) ? "" : value; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="city property">
    
    private City city;

    /**
     * {@inheritDoc}
     */
    @Override
    public City getCity() { return city; }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public void setCity(City city) { this.city = city; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="postalCode property">
    
    private String postalCode;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostalCode() { return postalCode; }

    /**
     * Set the value of postalCode
     *
     * @param value new value of postalCode
     */
    public void setPostalCode(String value) { postalCode = (value == null) ? "" : value; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="phone property">
    
    private String phone;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhone() { return phone; }

    /**
     * Set the value of phone
     *
     * @param value new value of phone
     */
    public void setPhone(String value) { phone = (value == null) ? "" : value; }

    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} address object.
     */
    public AddressImpl() {
        this.address1 = "";
        this.address2 = "";
        this.city = null;
        this.postalCode = "";
        this.phone = "";
    }

    /**
     * Initializes an address object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public AddressImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        address1 = resultSet.getString(Address.COLNAME_ADDRESS);
        if (resultSet.wasNull())
            address1 = "";
        address2 = resultSet.getString(Address.COLNAME_ADDRESS2);
        if (resultSet.wasNull())
            address2 = "";
        int cityId = resultSet.getInt(Address.COLNAME_CITYID);
        if (resultSet.wasNull())
            city = null;
        else {
            String cityName = resultSet.getString(City.COLNAME_CITY);
            if (resultSet.wasNull())
                cityName = "";
            int countryId = resultSet.getInt(City.COLNAME_COUNTRYID);
            if (resultSet.wasNull())
                city = City.of(cityId, cityName, null);
            else {
                String countryName = resultSet.getString(Country.COLNAME_COUNTRY);
                city = City.of(cityId, cityName, Country.of(countryId, resultSet.wasNull() ? "" : countryName));
            }
        }
        postalCode = resultSet.getString(Address.COLNAME_POSTALCODE);
        if (resultSet.wasNull())
            postalCode = "";
        phone = resultSet.getString(Address.COLNAME_PHONE);
        if (resultSet.wasNull())
            phone = "";
    }
    
    public static ArrayList<AddressImpl> lookupAll(Connection connection, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = getBaseSelectQuery() + SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.of(SelectOrderSpec.of(Country.COLNAME_COUNTRY), SelectOrderSpec.of(COLNAME_POSTALCODE),
                        SelectOrderSpec.of(City.COLNAME_CITY), SelectOrderSpec.of(COLNAME_ADDRESS),
                        SelectOrderSpec.of(COLNAME_ADDRESS2)));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return toList(ps, (rs) -> new AddressImpl(rs));
        }
    }
    
    public static Iterable<AddressImpl> lookupAll(Connection connection) throws SQLException {
        return lookupAll(connection, null);
    }
    
    public static ArrayList<AddressImpl> lookupByCity(Connection connection, City city, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        if  (city.getRowState()== DataObject.ROWSTATE_DELETED || city.getRowState() == DataObject.ROWSTATE_NEW)
            return new ArrayList<>();
        String sql = String.format("%s WHERE a.`%s` = %%%s", getBaseSelectQuery(), COLNAME_CITYID, SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.of(SelectOrderSpec.of(Country.COLNAME_COUNTRY), SelectOrderSpec.of(COLNAME_POSTALCODE),
                        SelectOrderSpec.of(City.COLNAME_CITY), SelectOrderSpec.of(COLNAME_ADDRESS),
                        SelectOrderSpec.of(COLNAME_ADDRESS2))));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return toList(ps, (rs) -> new AddressImpl(rs));
        }
    }
    
    public static Optional<AddressImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE a.`%s` = %%", getBaseSelectQuery(), COLNAME_ADDRESSID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new AddressImpl(rs));
        }
    }

    public static int lookupUsageCount(Connection connection, City city) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        if  (city.getRowState()== DataObject.ROWSTATE_DELETED || city.getRowState() == DataObject.ROWSTATE_NEW)
            return 0;
        String sql = String.format("SELECT COUNT(`%s`) FROM `%s` WHERE '%s' = %%", getPrimaryKeyColName(AddressImpl.class), TABLENAME_ADDRESS,
                COLNAME_CITYID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, city.getPrimaryKey());
            ResultSet rs = ps.getResultSet();
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    @Override
    public synchronized void delete(Connection connection) throws SQLException {
        assert CustomerImpl.lookupUsageCount(connection, this) == 0 : "Address is associated with one or more addresses.";
        super.delete(connection);
    }
}
