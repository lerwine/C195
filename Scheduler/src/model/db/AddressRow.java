package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;
import scheduler.InvalidArgumentException;
import scheduler.SqlConnectionDependency;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(AddressRow.COLNAME_ADDRESSID)
@TableName("address")
public class AddressRow extends DataRow implements model.Address {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String SQL_SELECT = "SELECT address.*, city.city, city.countryId, country.country FROM address" +
        " LEFT OUTER JOIN city ON address.cityId = city.cityId" +
        " LEFT OUTER JOIN country ON city.countryId = country.countryId";
    
    public static final String COLNAME_ADDRESSID = "addressId";

    //<editor-fold defaultstate="collapsed" desc="address1">
    
    private String address1;
    
    public static final String PROP_ADDRESS1 = "address1";
    
    public static final String COLNAME_ADDRESS = "address";
    
    /**
     * Get the value of address1
     *
     * @return the value of address1
     */
    @Override
    public final String getAddress1() { return address1; }
    
    /**
     * Set the value of address1
     *
     * @param value new value of address1
     */
    public final void setAddress1(String value) {
        String oldValue = address1;
        address1 = (value == null) ? "" : value;
        firePropertyChange(PROP_ADDRESS1, oldValue, address1);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="address2">
    
    private String address2;
    
    public static final String PROP_ADDRESS2 = "address2";
    
    /**
     * Get the value of address2
     *
     * @return the value of address2
     */
    @Override
    public final String getAddress2() { return address2; }
    
    /**
     * Set the value of address2
     *
     * @param value new value of address2
     */
    public final void setAddress2(String value) {
        String oldValue = address2;
        address2 = (value == null) ? "" : value;
        firePropertyChange(PROP_ADDRESS2, oldValue, address2);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="cityId">
    
    private int cityId;
    
    public static final String PROP_CITYID = "cityId";
    
    /**
     * Get the value of cityId
     *
     * @return the value of cityId
     */
    public final int getCityId() { return cityId; }
    
    /**
     * Set the value of cityId
     *
     * @param value new value of cityId
     * @throws java.sql.SQLException
     * @throws scheduler.InvalidArgumentException
     */
    public final void setCityId(int value) throws SQLException, InvalidArgumentException {
        if (cityId == value && city != null)
            return;
        int oldId = cityId;
        model.City oldCity = city;
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        try {
            Optional<CityRow> r = CityRow.getById(dep.getconnection(), value);
            if (r.isPresent())
                city = r.get();
            else
                throw new InvalidArgumentException("value", "No city found that matches that ID");
        } finally { dep.close(); }
        cityId = value;
        try { firePropertyChange(PROP_CITYID, oldId, cityId); }
        finally { firePropertyChange(PROP_CITY, oldCity, city); }
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="city">
    
    private model.City city;
    
    public static final String PROP_CITY = "city";
    
    /**
     * Get the value of city
     *
     * @return the value of city
     */
    @Override
    public final model.City getCity() { return city; }
    
    /**
     * Set the value of cityId
     *
     * @param value new value of cityId
     * @throws scheduler.InvalidArgumentException
     */
    public final void setCity(model.City value) throws InvalidArgumentException {
        if (value == null)
            throw new InvalidArgumentException("value", "City cannot be null");
        if (value instanceof CityRow) {
            int rowState = ((CityRow)value).getRowState();
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidArgumentException("value", "City was deleted");
            if (rowState == ROWSTATE_NEW)
                throw new InvalidArgumentException("value", "City was not added to the database");
        }
        int oldId = cityId;
        model.City oldCity = city;
        cityId = (city = value).getPrimaryKey();
        try { firePropertyChange(PROP_CITY, oldCity, city); }
        finally { firePropertyChange(PROP_CITY, oldId, cityId); }
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="postalCode">
    
    private String postalCode;
    
    public static final String PROP_POSTALCODE = "postalCode";
    
    /**
     * Get the value of postalCode
     *
     * @return the value of postalCode
     */
    @Override
    public final String getPostalCode() { return postalCode; }
    
    /**
     * Set the value of postalCode
     *
     * @param value new value of postalCode
     */
    public final void setPostalCode(String value) {
        String oldValue = postalCode;
        postalCode = (value == null) ? "" : value;
        firePropertyChange(PROP_POSTALCODE, oldValue, postalCode);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="phone">
    
    private String phone;
    
    public static final String PROP_PHONE = "phone";
    
    /**
     * Get the value of phone
     *
     * @return the value of phone
     */
    @Override
    public final String getPhone() { return phone; }
    
    /**
     * Set the value of phone
     *
     * @param value new value of phone
     */
    public final void setPhone(String value) {
        String oldValue = phone;
        phone = (value == null) ? "" : value;
        firePropertyChange(PROP_PHONE, oldValue, phone);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public AddressRow() {
        super();
        address1 = address2 = postalCode = phone = "";
        cityId = 0;
    }
    
    public AddressRow(String address1, String address2, CityRow city, String postalCode, String phone) throws InvalidArgumentException {
        super();
        if (city == null)
            throw new InvalidArgumentException("city", "City cannot be null");
        if (city.getRowState() == ROWSTATE_DELETED)
            throw new InvalidArgumentException("city", "City was deleted");
        if (city.getRowState() == ROWSTATE_NEW)
            throw new InvalidArgumentException("city", "City was not added to the database");
        this.address1 = (address1 == null) ? "" : address1;
        this.address2 = (address2 == null) ? "" : address2;
        cityId = (this.city = city).getPrimaryKey();
        this.postalCode = (postalCode == null) ? "" : postalCode;
        this.phone = (phone == null) ? "" : phone;
    }
    
    public AddressRow(ResultSet rs) throws SQLException {
        super(rs);
        address1 = rs.getString(COLNAME_ADDRESS);
        if (rs.wasNull())
            address1 = "";
        address2 = rs.getString(PROP_ADDRESS2);
        if (rs.wasNull())
            address2 = "";
        cityId = rs.getInt(PROP_CITYID);
        city = new City(cityId, rs.getString(PROP_CITY), new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY)));
        postalCode = rs.getString(PROP_POSTALCODE);
        if (rs.wasNull())
            postalCode = "";
        phone = rs.getString(PROP_PHONE);
        if (rs.wasNull())
            phone = "";
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">

    public static final Optional<AddressRow> getById(Connection connection, int id) throws SQLException {
        return selectFirstFromDb(connection, SQL_SELECT + " WHERE address.addressId = ?", (Function<ResultSet, AddressRow>)(ResultSet rs) -> {
            AddressRow u;
            try {
                u = new AddressRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AddressRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AddressRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, id);
            } catch (SQLException ex) {
                Logger.getLogger(AddressRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<AddressRow> getByCity(Connection connection, int cityId) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE address.cityId = ?", (Function<ResultSet, AddressRow>)(ResultSet rs) -> {
            AddressRow u;
            try {
                u = new AddressRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AddressRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AddressRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, cityId);
            } catch (SQLException ex) {
                Logger.getLogger(AddressRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<AddressRow> getAll(Connection connection) throws SQLException {
        return selectFromDb(connection, SQL_SELECT, (Function<ResultSet, AddressRow>)(ResultSet rs) -> {
            AddressRow u;
            try {
                u = new AddressRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AddressRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AddressRow object from result set.");
            }
            return u;
        }, null);
    }
    
    @Override
    protected String[] getColumnNames() {
        return new String[] { COLNAME_ADDRESS, PROP_ADDRESS2, PROP_CITYID, PROP_POSTALCODE, PROP_PHONE };
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            switch (fieldNames[index]) {
                case COLNAME_ADDRESS:
                    ps.setString(index + 1, address1);
                    break;
                case PROP_ADDRESS2:
                    ps.setString(index + 1, address2);
                    break;
                case PROP_CITYID:
                    ps.setInt(index + 1, cityId);
                    break;
                case PROP_POSTALCODE:
                    ps.setString(index + 1, postalCode);
                    break;
                case PROP_PHONE:
                    ps.setString(index + 1, phone);
                    break;
            }
        }
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        String oldAddress1 = address1;
        String oldAddress2 = address2;
        int oldCityId = cityId;
        model.City oldCity = city;
        String oldPostalCode = postalCode;
        String oldPhone = phone;
        address1 = rs.getString(COLNAME_ADDRESS);
        if (rs.wasNull())
            address1 = "";
        address2 = rs.getString(PROP_ADDRESS2);
        if (rs.wasNull())
            address2 = "";
        cityId = rs.getInt(PROP_CITYID);
        city = new City(cityId, rs.getString(PROP_CITY), new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY)));
        postalCode = rs.getString(PROP_POSTALCODE);
        if (rs.wasNull())
            postalCode = "";
        phone = rs.getString(PROP_PHONE);
        if (rs.wasNull())
            phone = "";
        // Execute property change events in nested try/finally statements to ensure that all
        // events get fired, even if one of the property change listeners throws an exception.
        try { firePropertyChange(PROP_ADDRESS1, oldAddress1, address1); }
        finally {
            try { firePropertyChange(PROP_ADDRESS2, oldAddress2, address2); }
            finally {
                try { firePropertyChange(PROP_CITYID, oldCityId, cityId); }
                finally {
                    try { firePropertyChange(PROP_POSTALCODE, oldPostalCode, postalCode); }
                    finally {
                        try { firePropertyChange(PROP_PHONE, oldPhone, phone); }
                        finally { firePropertyChange(PROP_CITY, oldCity, city); }
                    }
                }
            }
        }
    }

    @Override
    protected String getSelectQuery() { return SQL_SELECT; }
    
    //</editor-fold>
    
    static class City implements model.City {
        private final int id;
        private final String name;
        private final CityRow.Country country;
        City(int id, String name, CityRow.Country country) {
            this.id = id;
            this.name = name;
            this.country = country;
        }

        @Override
        public String getName() { return name; }

        @Override
        public int getPrimaryKey() { return id; }

        @Override
        public model.Country getCountry() { return country; }
    }
}
