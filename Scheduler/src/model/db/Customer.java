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
import utils.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(Customer.COLNAME_CUSTOMERID)
@TableName("customer")
public class Customer extends DataRow {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    private final static HashMap<Integer, Customer> LOOKUP_CACHE = new HashMap<>();
    
    //<editor-fold defaultstate="collapsed" desc="customerName">
    
    private String customerName;
    
    public static final String PROP_CUSTOMERNAME = "customerName";
    
    /**
     * Get the value of customerName
     *
     * @return the value of customerName
     */
    public final String getCustomerName() { return customerName; }
    
    /**
     * Set the value of customerName
     *
     * @param value new value of name
     */
    public final void setCustomerName(String value) {
        String oldValue = customerName;
        customerName = (value == null) ? "" : value;
        firePropertyChange(PROP_CUSTOMERNAME, oldValue, customerName);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="addressId">
    
    private int addressId;
    
    public static final String PROP_ADDRESSID = "addressId";
    
    /**
     * Get the value of addressId
     *
     * @return the value of addressId
     */
    public final int getAddressId() { return addressId; }
    
    /**
     * Set the value of addressId
     *
     * @param value new value of addressId
     */
    public final void setAddressId(int value) {
        int oldValue = addressId;
        addressId = value;
        firePropertyChange(PROP_ADDRESSID, oldValue, addressId);
    }
    
    public Optional<Address> lookupCurrentAddress(Connection connection) throws SQLException {
        return Address.getById(connection, addressId, true);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="active">
    
    private boolean active;

    public static final String PROP_ACTIVE = "active";

    /**
     * Get the value of active
     *
     * @return the value of active
     */
    public boolean isActive() { return active; }

    /**
     * Set the value of active
     *
     * @param value new value of active
     */
    public void setActive(boolean value) {
        boolean oldValue = active;
        active = value;
        firePropertyChange(PROP_ACTIVE, oldValue, value);
    }

    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public Customer() {
        super();
        customerName = "";
        addressId = 0;
        active = true;
    }
    
    public Customer(String customerName, int addressId, boolean active) {
        super();
        this.customerName = (customerName == null) ? "" : customerName;
        this.addressId = addressId;
        this.active = active;
    }
    
    public Customer (ResultSet rs) throws SQLException {
        super(rs);
        customerName = rs.getString(PROP_CUSTOMERNAME);
        if (rs.wasNull())
            customerName = "";
        addressId = rs.getInt(PROP_ADDRESSID);
        active = rs.getBoolean(PROP_ACTIVE);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<Customer> getById(Connection connection, int id, boolean includeCache) throws SQLException {
        if (includeCache && LOOKUP_CACHE.containsKey(id))
            return Optional.of(LOOKUP_CACHE.get(id));
        
        return selectFromDbById(connection, (Class<Customer>)Customer.class, (Function<ResultSet, Customer>)(ResultSet rs) -> {
            Customer r;
            try {
                r = new Customer(rs);
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, id);
    }
    
    public static final ObservableList<Customer> getByAddress(Connection connection, int addressId) throws SQLException {
        return selectFromDb(connection, (Class<Customer>)Customer.class, (Function<ResultSet, Customer>)(ResultSet rs) -> {
            Customer r;
            try {
                r = new Customer(rs);
                int id = r.getPrimaryKey();
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, "`" + PROP_ADDRESSID + "` = ?",
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, addressId);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    @Override
    protected String[] getColumnNames() {
        return new String[] { PROP_CUSTOMERNAME, PROP_ADDRESSID, PROP_ACTIVE };
    }

    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        String oldCustomerName = customerName;
        int oldAddressId = addressId;
        boolean oldActive = active;
        customerName = rs.getString(PROP_CUSTOMERNAME);
        if (rs.wasNull())
            customerName = "";
        addressId = rs.getInt(PROP_ADDRESSID);
        active = rs.getBoolean(PROP_ACTIVE);
        if (!LOOKUP_CACHE.containsKey(getPrimaryKey()))
            LOOKUP_CACHE.put(getPrimaryKey(), this);
        // Execute property change events in nested try/finally statements to ensure that all
        // events get fired, even if one of the property change listeners throws an exception.
        try { firePropertyChange(PROP_CUSTOMERNAME, oldCustomerName, customerName); }
        finally {
            try { firePropertyChange(PROP_ADDRESSID, oldAddressId, addressId); }
            finally { firePropertyChange(PROP_ACTIVE, oldActive, active); }
        }
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            switch (fieldNames[index]) {
                case PROP_CUSTOMERNAME:
                    ps.setString(index + 1, customerName);
                    break;
                case PROP_ADDRESSID:
                    ps.setInt(index + 1, addressId);
                    break;
                case PROP_ACTIVE:
                    ps.setBoolean(index + 1, active);
                    break;
            }
        }
    }
    
    //</editor-fold>
}
