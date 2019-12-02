/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import static model.db.DataRow.selectFromDbById;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(Address.COLNAME_ADDRESSID)
@TableName("address")
public class Address extends DataRow {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String COLNAME_ADDRESSID = "addressId";

    private final static HashMap<Integer, Address> LOOKUP_CACHE = new HashMap<>();
    
    //<editor-fold defaultstate="collapsed" desc="address1">
    
    private String address1;
    
    public static final String PROP_ADDRESS1 = "address1";
    
    public static final String COLNAME_ADDRESS = "address";
    
    /**
     * Get the value of address1
     *
     * @return the value of address1
     */
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
     */
    public final void setCityId(int value) {
        int oldValue = cityId;
        cityId = value;
        firePropertyChange(PROP_CITYID, oldValue, cityId);
    }
    
    public Optional<City> lookupCurrentCity(Connection connection) throws SQLException {
        return City.getById(connection, cityId, true);
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
    
    public Address() {
        super();
        address1 = address2 = postalCode = phone = "";
        cityId = 0;
    }
    
    public Address(String address1, String address2, int cityId, String postalCode, String phone) {
        super();
        this.address1 = (address1 == null) ? "" : address1;
        this.address2 = (address2 == null) ? "" : address2;
        this.cityId = cityId;
        this.postalCode = (postalCode == null) ? "" : postalCode;
        this.phone = (phone == null) ? "" : phone;
    }
    
    public Address(ResultSet rs) throws SQLException {
        super(rs);
        address1 = rs.getString(COLNAME_ADDRESS);
        if (rs.wasNull())
            address1 = "";
        address2 = rs.getString(PROP_ADDRESS2);
        if (rs.wasNull())
            address2 = "";
        cityId = rs.getInt(PROP_CITYID);
        postalCode = rs.getString(PROP_POSTALCODE);
        if (rs.wasNull())
            postalCode = "";
        phone = rs.getString(PROP_PHONE);
        if (rs.wasNull())
            phone = "";
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<Address> getById(Connection connection, int id, boolean includeCache) throws SQLException {
        if (includeCache && LOOKUP_CACHE.containsKey(id))
            return Optional.of(LOOKUP_CACHE.get(id));
        
        return selectFromDbById(connection, (Class<Address>)Address.class, (Function<ResultSet, Address>)(ResultSet rs) -> {
            Address r;
            try {
                r = new Address(rs);
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, id);
    }
    
    public static final ObservableList<Address> getByCity(Connection connection, int cityId) throws SQLException {
        return selectFromDb(connection, (Class<Address>)Address.class, (Function<ResultSet, Address>)(ResultSet rs) -> {
            Address r;
            try {
                r = new Address(rs);
                int id = r.getPrimaryKey();
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, "`" + PROP_CITYID + "` = ?",
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, cityId);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
        String oldPostalCode = postalCode;
        String oldPhone = phone;
        address1 = rs.getString(COLNAME_ADDRESS);
        if (rs.wasNull())
            address1 = "";
        address2 = rs.getString(PROP_ADDRESS2);
        if (rs.wasNull())
            address2 = "";
        cityId = rs.getInt(PROP_CITYID);
        postalCode = rs.getString(PROP_POSTALCODE);
        if (rs.wasNull())
            postalCode = "";
        phone = rs.getString(PROP_PHONE);
        if (rs.wasNull())
            phone = "";
        if (!LOOKUP_CACHE.containsKey(getPrimaryKey()))
            LOOKUP_CACHE.put(getPrimaryKey(), this);
        // Execute property change events in nested try/finally statements to ensure that all
        // events get fired, even if one of the property change listeners throws an exception.
        try { firePropertyChange(PROP_ADDRESS1, oldAddress1, address1); }
        finally {
            try { firePropertyChange(PROP_ADDRESS2, oldAddress2, address2); }
            finally {
                try { firePropertyChange(PROP_CITYID, oldCityId, cityId); }
                finally {
                    try { firePropertyChange(PROP_POSTALCODE, oldPostalCode, postalCode); }
                    finally { firePropertyChange(PROP_PHONE, oldPhone, phone); }
                }
            }
        }
    }

    //</editor-fold>
}
