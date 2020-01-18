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
public class CustomerImpl extends DataObjectImpl implements Customer {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT p.`");
        sql.append(COLNAME_CUSTOMERID).append("` AS ").append(COLNAME_CUSTOMERID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CUSTOMERID, COLNAME_CUSTOMERNAME,
                COLNAME_ADDRESSID).forEach((t) -> {
            sql.append(", p.`").append(t).append("` AS ").append(t);
        });
        Stream.of(Address.COLNAME_ADDRESS, Address.COLNAME_ADDRESS2, Address.COLNAME_CITYID, Address.COLNAME_POSTALCODE, Address.COLNAME_PHONE).forEach((t) -> {
            sql.append(", a.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", c.`").append(City.COLNAME_CITY).append("` AS ").append(City.COLNAME_CITY)
                .append(", c.`").append(City.COLNAME_COUNTRYID).append("` AS ").append(City.COLNAME_COUNTRYID)
                .append(", n.`").append(Country.COLNAME_COUNTRY).append("` AS ").append(Country.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(CustomerImpl.class))
                .append("` p LEFT JOIN `").append(getTableName(AddressImpl.class)).append("` a ON p.`").append(Customer.COLNAME_ADDRESSID).append("`=a.`").append(Address.COLNAME_ADDRESSID)
                .append("` LEFT JOIN `").append(getTableName(CityImpl.class)).append("` c ON a.`").append(Address.COLNAME_CITYID).append("`=c.`").append(City.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(City.COLNAME_COUNTRYID).append("`=n.`").append(Country.COLNAME_COUNTRYID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_CUSTOMERNAME, Address.COLNAME_ADDRESS,
                    City.COLNAME_CITY, Country.COLNAME_COUNTRY, Address.COLNAME_POSTALCODE, Address.COLNAME_PHONE, User.COLNAME_USERNAME,
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
    
    /**
     * Initializes a customer object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    private CustomerImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        name  = resultSet.getString(COLNAME_CUSTOMERNAME);
        if (resultSet.wasNull())
            name = "";
        
        int addressId = resultSet.getInt(COLNAME_ADDRESSID);
        if (resultSet.wasNull())
            address = null;
        else {
            String address1 = resultSet.getString(Address.COLNAME_ADDRESS);
            if (resultSet.wasNull())
                address1 = "";
            String address2 = resultSet.getString(Address.COLNAME_ADDRESS2);
            if (resultSet.wasNull())
                address2 = "";
            City city;
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
            String postalCode = resultSet.getString(Address.COLNAME_POSTALCODE);
            if (resultSet.wasNull())
                postalCode = "";
            String phone = resultSet.getString(Address.COLNAME_PHONE);
            address = Address.of(addressId, address1, address2, city, postalCode, (resultSet.wasNull()) ? "" : phone);
        }
        
        active = resultSet.getBoolean(COLNAME_ACTIVE);
        if (resultSet.wasNull())
            active = false;
    }
    
    public static ArrayList<CustomerImpl> lookupAll(Connection connection, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = getBaseSelectQuery() + SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.single(COLNAME_CUSTOMERNAME));
        try (PreparedStatement ps = connection.prepareStatement(getBaseSelectQuery())) {
            return toList(ps, (rs) -> new CustomerImpl(rs));
        }
    }
    
    public static Iterable<CustomerImpl> lookupAll(Connection connection) throws SQLException {
        return lookupAll(connection, null);
    }
    
    public static ArrayList<CustomerImpl> lookupByStatus(Connection connection, boolean isActive, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE `%s`=%%%s", getBaseSelectQuery(), COLNAME_ACTIVE, SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.single(COLNAME_CUSTOMERNAME)));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            return toList(ps, (rs) -> new CustomerImpl(rs));
        }
    }
    
    public static Iterable<CustomerImpl> lookupByStatus(Connection connection, boolean isActive) throws SQLException {
        return lookupByStatus(connection, isActive, null);
    }
    
    public static ArrayList<CustomerImpl> lookupByAddress(Connection connection, Address address, boolean isActive, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        if  (address.getRowState()== DataObject.ROWSTATE_DELETED || address.getRowState() == DataObject.ROWSTATE_NEW)
            return new ArrayList<>();
        String sql = String.format("%s WHERE `%s`=%% AND `%s`=%%%s", getBaseSelectQuery(), COLNAME_ADDRESSID, COLNAME_ACTIVE,
                SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.single(COLNAME_CUSTOMERNAME)));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, address.getPrimaryKey());
            ps.setBoolean(2, isActive);
            return toList(ps, (rs) -> new CustomerImpl(rs));
        }
    }
    
    public static ArrayList<CustomerImpl> lookupByAddress(Connection connection, Optional<Address> address, boolean isActive, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        if (address.isPresent())
            return lookupByAddress(connection, address.get(), isActive, orderBy);
        String sql = String.format("%s WHERE `%s`=%%%s", getBaseSelectQuery(), COLNAME_ADDRESSID, COLNAME_ACTIVE,
                SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.single(COLNAME_CUSTOMERNAME)));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            return toList(ps, (rs) -> new CustomerImpl(rs));
        }
    }
    
    public static ArrayList<CustomerImpl> lookupByAddress(Connection connection, Address address, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        if  (address.getRowState()== DataObject.ROWSTATE_DELETED || address.getRowState() == DataObject.ROWSTATE_NEW)
            return new ArrayList<>();
        String sql = String.format("%s WHERE `%s`=%%%s", getBaseSelectQuery(), COLNAME_ADDRESSID,
                SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
                () -> SelectOrderSpec.single(COLNAME_CUSTOMERNAME)));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, address.getPrimaryKey());
            return toList(ps, (rs) -> new CustomerImpl(rs));
        }
    }
    
    public static Optional<CustomerImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s`=%%", getBaseSelectQuery(), COLNAME_CUSTOMERID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new CustomerImpl(rs));
        }
    }

    public static int lookupUsageCount(Connection connection, Address address) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        if  (address.getRowState()== DataObject.ROWSTATE_DELETED || address.getRowState() == DataObject.ROWSTATE_NEW)
            return 0;
        String sql = String.format("SELECT COUNT(`%s`) FROM `%s` WHERE '%s'=%%", getPrimaryKeyColName(CustomerImpl.class), TABLENAME_CUSTOMER,
                COLNAME_ADDRESSID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, address.getPrimaryKey());
            ResultSet rs = ps.getResultSet();
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    @Override
    public synchronized void delete(Connection connection) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        assert AppointmentImpl.lookupCount(connection, Optional.empty(), Optional.empty(), Optional.of(this), Optional.empty()) == 0 : "Customer is associated with one or more appointments.";
        super.delete(connection);
    }
    
}
