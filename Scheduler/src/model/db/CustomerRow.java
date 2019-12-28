package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
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
    
    /**
     * 
     */
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    /**
     * 
     */
    public static final String SQL_SELECT = "SELECT customer.*, address.address, address.address2," +
        " address.cityId, city.city, address.postalCode, address.phone, city.countryId, country.country FROM customer" +
        " LEFT OUTER JOIN address ON customer.addressId = address.addressId" +
        " LEFT OUTER JOIN city ON address.cityId = city.cityId" +
        " LEFT OUTER JOIN country ON city.countryId = country.countryId";
    
    //<editor-fold defaultstate="collapsed" desc="name">
    
    /**
     * 
     */
    public static final String PROP_CUSTOMERNAME = "customerName";
    
    private final NonNullableStringProperty name;

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
    
    private final IntegerBinding addressId;

    /**
     * Get the value of addressId
     *
     * @return the value of addressId
     */
    public int getAddressId() { return addressId.get(); }

    public IntegerBinding addressIdProperty() { return addressId; }
    
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

    public ObjectProperty<model.Address> addressProperty() { return address; }
    
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
    
    /**
     * 
     */
    public CustomerRow() {
        super();
        name = new NonNullableStringProperty();
        address = new SimpleObjectProperty<>();
        addressId = scheduler.Util.primaryKeyBinding(address);
        active = new SimpleBooleanProperty();
    }
    
    /**
     * 
     * @param name
     * @param address
     * @param active
     */
    public CustomerRow(String name, AddressRow address, boolean active) {
        super();
        this.name = new NonNullableStringProperty(name);
        this.address = new SimpleObjectProperty<>(address);
        addressId = scheduler.Util.primaryKeyBinding(this.address);
        this.active = new SimpleBooleanProperty(active);
    }
    
    /**
     * 
     * @param rs
     * @throws SQLException
     */
    public CustomerRow (ResultSet rs) throws SQLException {
        super(rs);
        name = new NonNullableStringProperty(scheduler.Util.resultStringOrDefault(rs, PROP_CUSTOMERNAME, ""));
        address = new SimpleObjectProperty<>(new Address(rs.getInt(PROP_ADDRESSID), rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE)));
        addressId = scheduler.Util.primaryKeyBinding(address);
        active = new SimpleBooleanProperty(rs.getBoolean(PROP_ACTIVE));
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    /**
     * 
     * @param connection
     * @param id
     * @return
     * @throws SQLException
     */
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
    
    /**
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
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
    
    /**
     * 
     * @param connection
     * @param addressId
     * @return
     * @throws SQLException
     */
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
    
    @Override
    public int hashCode() { return getPrimaryKey(); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((getRowState() != ROWSTATE_MODIFIED && getRowState() != ROWSTATE_UNMODIFIED) || obj == null || !(obj instanceof model.Customer))
            return false;
        final model.Customer other = (model.Customer)obj;
        return (other.getRowState() == ROWSTATE_MODIFIED || other.getRowState() == ROWSTATE_UNMODIFIED) && getPrimaryKey() == other.getPrimaryKey();
    }
    
    @Override
    public String toString() { return name.get(); }
    
    static class Address implements model.Address {
        private final ReadOnlyIntegerWrapper primaryKey;

        @Override
        public int getPrimaryKey() { return primaryKey.get(); }

        public ReadOnlyIntegerProperty primaryKeyProperty() { return primaryKey.getReadOnlyProperty(); }
        
        private final ReadOnlyStringWrapper address1;

        @Override
        public String getAddress1() { return address1.get(); }

        public ReadOnlyStringProperty address1Property() { return address1.getReadOnlyProperty(); }
           
        private final ReadOnlyStringWrapper address2;

        @Override
        public String getAddress2() { return address2.get(); }

        public ReadOnlyStringProperty address2Property() { return address2.getReadOnlyProperty(); }
     
        private final ReadOnlyObjectWrapper<AddressRow.City> city;

        @Override
        public AddressRow.City getCity() { return city.get(); }

        public ReadOnlyObjectProperty<AddressRow.City> cityProperty() { return city.getReadOnlyProperty(); }
        
        private final ReadOnlyStringWrapper postalCode;

        @Override
        public String getPostalCode() { return postalCode.get(); }

        public ReadOnlyStringProperty postalCodeProperty() { return postalCode.getReadOnlyProperty(); }
        
        private final ReadOnlyStringWrapper phone;

        @Override
        public String getPhone() { return phone.get(); }

        public ReadOnlyStringProperty phoneProperty() { return phone.getReadOnlyProperty(); }
        
        Address(int id, String address1, String address2, AddressRow.City city, String postalCode, String phone) {
            primaryKey = new ReadOnlyIntegerWrapper(id);
            this.address1 = new ReadOnlyStringWrapper(address1);
            this.address2 = new ReadOnlyStringWrapper(address2);
            this.city = new ReadOnlyObjectWrapper<>(city);
            this.postalCode = new ReadOnlyStringWrapper(postalCode);
            this.phone = new ReadOnlyStringWrapper(phone);
        }
        
        @Override
        public int hashCode() { return primaryKey.get(); }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof model.Address))
                return false;
            
            model.Address record = (model.Address)obj;
            return (record.getRowState() == DataRow.ROWSTATE_MODIFIED || record.getRowState() == DataRow.ROWSTATE_UNMODIFIED) &&
                    record.getPrimaryKey() == getPrimaryKey();
        }
    
        @Override
        public String toString() {
            model.City t = getCity();
            String ph = getPhone();
            Stream<String> lines;
            if (t == null) {
                if (ph.trim().isEmpty())
                    lines = Stream.of(getAddress1(), getAddress2(), getPostalCode());
                else
                    lines = Stream.of(getAddress1(), getAddress2(), getPostalCode(), "ph: " + ph);
            } else {
                model.Country c = t.getCountry();
                String n = getPostalCode();
                n = (n.trim().isEmpty()) ? t.getName() : t.getName() + " " + n;
                if (c == null) {
                    if (ph.trim().isEmpty())
                        lines = Stream.of(getAddress1(), getAddress2(), n);
                    else
                        lines = Stream.of(getAddress1(), getAddress2(), n, "ph: " + ph);
                } else if (ph.trim().isEmpty())
                    lines = Stream.of(getAddress1(), getAddress2(), t.getName(), n + ", " + c.getName());
                else
                    lines = Stream.of(getAddress1(), getAddress2(), t.getName(), n + ", " + c.getName(), "ph: " + ph);
            }
            Optional<String> result = lines.filter((String s) -> !s.trim().isEmpty()).reduce((s, u) -> {
                return s + "\n" + u; //To change body of generated lambdas, choose Tools | Templates.
            });
            return (result.isPresent()) ? result.get() : "";
        }
    }
}
