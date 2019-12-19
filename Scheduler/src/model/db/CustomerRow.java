package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;

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
    
    public static final String PROP_CUSTOMERNAME = "customerName";
    
    private final StringProperty name;

    /**
     * Get the value of customerName
     *
     * @return the value of customerName
     */
    @Override
    public String getName() { return name.get(); }

    /**
     * Set the value of customerName
     *
     * @param value new value of name
     */
    public void setName(String value) { name.set(value); }

    public StringProperty nameProperty() { return name; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="addressId">
    
    public static final String PROP_ADDRESSID = "addressId";
    
    private final ReadOnlyIntegerWrapper addressId;

    /**
     * Get the value of addressId
     *
     * @return the value of addressId
     */
    public int getAddressId() { return addressId.get(); }

    public ReadOnlyIntegerProperty addressIdProperty() { return addressId.getReadOnlyProperty(); }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="address">
    
    public static final String PROP_ADDRESS = "address";
    
    private final ObjectProperty<model.Address> address;

    /**
     * Get the value of address
     *
     * @return the value of address
     */
    @Override
    public model.Address getAddress() { return address.get(); }

    /**
     * Set the value of address
     *
     * @param value new value of address
     */
    public void setAddress(model.Address value) { address.set(value); }

    public ObjectProperty addressProperty() { return address; }
    
    private final RowIdChangeListener<model.Address> addressIdChangeListener;
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="active">
    
    public static final String PROP_ACTIVE = "active";
    
    private final BooleanProperty active;

    /**
     * Get the value of active
     *
     * @return the value of active
     */
    @Override
    public boolean isActive() { return active.get(); }

    /**
     * Set the value of active
     *
     * @param value new value of active
     */
    public void setActive(boolean value) { active.set(value); }

    public BooleanProperty activeProperty() { return active; }

    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public CustomerRow() {
        super();
        this.name = new NonNullableStringProperty();
        this.addressId = new ReadOnlyIntegerWrapper();
        this.address = new SimpleObjectProperty<>();
        this.active = new SimpleBooleanProperty();
        addressIdChangeListener = new RowIdChangeListener<>(this.address, addressId);
    }
    
    public CustomerRow(String name, AddressRow address, boolean active) {
        super();
        this.name = new NonNullableStringProperty(name);
        this.addressId = new ReadOnlyIntegerWrapper();
        this.address = new SimpleObjectProperty<>(address);
        this.active = new SimpleBooleanProperty(active);
        addressIdChangeListener = new RowIdChangeListener<>(this.address, addressId);
    }
    
    public CustomerRow (ResultSet rs) throws SQLException {
        super(rs);
        this.name = new NonNullableStringProperty(rs.getString(PROP_CUSTOMERNAME));
        if (rs.wasNull())
            name.set("");
        this.addressId = new ReadOnlyIntegerWrapper();
        this.address = new SimpleObjectProperty<>(new Address(rs.getInt(PROP_ADDRESSID), rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE)));
        this.active = new SimpleBooleanProperty(rs.getBoolean(PROP_ACTIVE));
        addressIdChangeListener = new RowIdChangeListener<>(this.address, addressId);
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
    
    public static final ObservableList<CustomerRow> getActive(Connection connection) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE active = true", (Function<ResultSet, CustomerRow>)(ResultSet rs) -> {
            CustomerRow u;
            try {
                u = new CustomerRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CustomerRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CustomerRow object from result set.");
            }
            return u;
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
        name.set(rs.getString(PROP_CUSTOMERNAME));
        if (rs.wasNull())
            name.set("");
        address.set(new Address(rs.getInt(PROP_ADDRESSID), rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE)));
        active.set(rs.getBoolean(PROP_ACTIVE));
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            switch (fieldNames[index]) {
                case PROP_CUSTOMERNAME:
                    ps.setString(index + 1, getName());
                    break;
                case PROP_ADDRESSID:
                    ps.setInt(index + 1, getAddressId());
                    break;
                case PROP_ACTIVE:
                    ps.setBoolean(index + 1, isActive());
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
