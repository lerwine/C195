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
@PrimaryKey(CustomerRow.COLNAME_CUSTOMERID)
@TableName("customer")
public class CustomerRow extends DataRow implements model.Customer {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    public static final String SQL_SELECT = "SELECT customer.*, address.address, address.address2," +
        " address.cityId, city.city, address.postalCode, address.phone, city.countryId, country.country FROM customer" +
        " LEFT OUTER JOIN address ON customer.addressId = address.addressId" +
        " LEFT OUTER JOIN city ON address.cityId = city.cityId" +
        " LEFT OUTER JOIN country ON city.countryId = country.countryId";
    
    //<editor-fold defaultstate="collapsed" desc="customerName">
    
    private String customerName;
    
    public static final String PROP_CUSTOMERNAME = "customerName";
    
    /**
     * Get the value of customerName
     *
     * @return the value of customerName
     */
    @Override
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
     * @throws java.sql.SQLException
     * @throws scheduler.InvalidArgumentException
     */
    public final void setAddressId(int value) throws SQLException, InvalidArgumentException {
        if (value == addressId && address != null)
            return;
        int oldId = addressId;
        model.Address oldAddress = address;
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        try {
            Optional<AddressRow> r = AddressRow.getById(dep.getconnection(), value);
            if (r.isPresent())
            address = r.get();
            else
                throw new InvalidArgumentException("value", "No address found that matches that ID");
        } finally { dep.close(); }
        addressId = value;
        try { firePropertyChange(PROP_ADDRESSID, oldId, addressId); }
        finally { firePropertyChange(PROP_ADDRESS, oldAddress, address); }
        
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="addressId">
    
    private model.Address address;
    
    public static final String PROP_ADDRESS = "address";
    
    /**
     * Get the value of address
     *
     * @return the value of address
     */
    @Override
    public final model.Address getAddress() { return address; }
    
    /**
     * Set the value of address
     *
     * @param value new value of address
     * @throws scheduler.InvalidArgumentException
     */
    public final void setAddress(model.Address value) throws InvalidArgumentException {
        if (value == null)
            throw new InvalidArgumentException("value", "Address cannot be null");
        if (value instanceof AddressRow) {
            int rowState = ((AddressRow)value).getRowState();
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidArgumentException("value", "Address was deleted");
            if (rowState == ROWSTATE_NEW)
                throw new InvalidArgumentException("value", "Address was not added to the database");
        }
        int oldId = addressId;
        model.Address oldAddress = address;
        addressId = (address = value).getPrimaryKey();
        try { firePropertyChange(PROP_ADDRESS, oldAddress, address); }
        finally { firePropertyChange(PROP_ADDRESSID, oldId, addressId); }
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
    @Override
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
    
    public CustomerRow() {
        super();
        customerName = "";
        addressId = 0;
        active = true;
    }
    
    public CustomerRow(String customerName, AddressRow address, boolean active) throws InvalidArgumentException {
        super();
        if (address == null)
            throw new InvalidArgumentException("address", "Address cannot be null");
        if (address.getRowState() == ROWSTATE_DELETED)
            throw new InvalidArgumentException("address", "Address was deleted");
        if (address.getRowState() == ROWSTATE_NEW)
            throw new InvalidArgumentException("address", "Address was not added to the database");
        this.customerName = (customerName == null) ? "" : customerName;
        addressId = (this.address = address).getPrimaryKey();
        this.active = active;
    }
    
    public CustomerRow (ResultSet rs) throws SQLException {
        super(rs);
        customerName = rs.getString(PROP_CUSTOMERNAME);
        if (rs.wasNull())
            customerName = "";
        addressId = rs.getInt(PROP_ADDRESSID);
        active = rs.getBoolean(PROP_ACTIVE);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<CustomerRow> getById(Connection connection, int id) throws SQLException {
        return selectFirstFromDb(connection, SQL_SELECT + " WHERE customerId = ?", (Function<ResultSet, CustomerRow>)(ResultSet rs) -> {
            CustomerRow u;
            try {
                u = new CustomerRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CustomerRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, id);
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    
    public static final ObservableList<CustomerRow> getByAddress(Connection connection, int addressId) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE addressId = ?", (Function<ResultSet, CustomerRow>)(ResultSet rs) -> {
            CustomerRow u;
            try {
                u = new CustomerRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CustomerRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, addressId);
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    
    @Override
    protected String[] getColumnNames() {
        return new String[] { PROP_CUSTOMERNAME, PROP_ADDRESSID, PROP_ACTIVE };
    }

    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        try {
            deferPropertyChangeEvent(PROP_CUSTOMERNAME);
            deferPropertyChangeEvent(PROP_ADDRESSID);
            deferPropertyChangeEvent(PROP_ADDRESS);
            deferPropertyChangeEvent(PROP_ACTIVE);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(CityRow.class.getName()).log(Level.SEVERE, null, ex);
        }
        customerName = rs.getString(PROP_CUSTOMERNAME);
        if (rs.wasNull())
            customerName = "";
        addressId = rs.getInt(PROP_ADDRESSID);
        address = new Address(addressId, rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE));
        active = rs.getBoolean(PROP_ACTIVE);
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

    @Override
    protected String getSelectQuery() { return SQL_SELECT; }
    
    //</editor-fold>
    
    static class Address implements model.Address {
        private final int id;
        private final String address1;
        private final String address2;
        private final model.City city;
        private final String postalCode;
        private final String phone;

        Address(int id, String address1, String address2, model.City city, String postalCode, String phone) {
            this.id = id;
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.postalCode = postalCode;
            this.phone = phone;
        }

        @Override
        public String getAddress1() { return address1; }
        
        @Override
        public String getAddress2() { return address2; }
        
        @Override
        public model.City getCity() { return city; }
        
        @Override
        public String getPostalCode() { return postalCode; }
        
        @Override
        public String getPhone() { return phone; }

        @Override
        public int getPrimaryKey() { return id; }
    }
}
