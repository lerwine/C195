package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
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
@PrimaryKey(AddressRow.COLNAME_ADDRESSID)
@TableName("address")
public class AddressRow extends DataRow implements model.Address {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String SQL_SELECT = "SELECT address.*, city.city, city.countryId, country.country FROM address" +
        " LEFT OUTER JOIN city ON address.cityId = city.cityId" +
        " LEFT OUTER JOIN country ON city.countryId = country.countryId";
    
    public static final String COLNAME_ADDRESSID = "addressId";
    
    //<editor-fold defaultstate="collapsed" desc="address1">
    
    public static final String PROP_ADDRESS1 = "address1";
    
    public static final String COLNAME_ADDRESS = "address";
    
    private final NonNullableStringProperty address1;

    /**
    * Get the value of address1
    *
    * @return the value of address1
    */
    @Override
    public String getAddress1() { return address1.get(); }

    /**
     * Set the value of address1
     *
     * @param value new value of address1
     */
    public void setAddress1(String value) { address1.set(value); }

    public StringProperty address1Property() { return address1; }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="address2">
    
    public static final String PROP_ADDRESS2 = "address2";
    
    private final NonNullableStringProperty address2;

    /**
    * Get the value of address2
    *
    * @return the value of address2
    */
    @Override
    public String getAddress2() { return address2.get(); }

    /**
     * Set the value of address2
     *
     * @param value new value of address2
     */
    public void setAddress2(String value) { address2.set(value); }

    public StringProperty address2Property() { return address2; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="cityId">
    
    public static final String PROP_CITYID = "cityId";
    
    private final IntegerBinding cityId;

    /**
    * Get the value of cityId
    *
    * @return the value of cityId
    */
    public int getCityId() { return cityId.get(); }

    public IntegerBinding cityIdProperty() { return cityId; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="city">
    
    public static final String PROP_CITY = "city";
    
    private final ObjectProperty<model.City> city;

    /**
    * Get the value of city
    *
    * @return the value of city
    */
    @Override
    public model.City getCity() { return city.get(); }

    /**
     * Set the value of cityId
     *
     * @param value new value of cityId
     */
    public void setCity(model.City value) { city.set(value); }

    public ObjectProperty<model.City> cityProperty() { return city; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="postalCode">
    
    public static final String PROP_POSTALCODE = "postalCode";
    
    private final NonNullableStringProperty postalCode;

    /**
    * Get the value of postalCode
    *
    * @return the value of postalCode
    */
    @Override
    public String getPostalCode() { return postalCode.get(); }

    /**
     * Set the value of postalCode
     *
     * @param value new value of postalCode
     */
    public void setPostalCode(String value) { postalCode.set(value); }

    public StringProperty postalCodeProperty() { return postalCode; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="phone">
    
    public static final String PROP_PHONE = "phone";
    
    private final NonNullableStringProperty phone;

    /**
    * Get the value of phone
    *
    * @return the value of phone
    */
    @Override
    public String getPhone() { return phone.get(); }

    /**
     * Set the value of phone
     *
     * @param value new value of phone
     */
    public void setPhone(String value) { phone.set(value); }

    public StringProperty phoneProperty() { return phone; }
    
    //</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public AddressRow() {
        super();
        address1 = new NonNullableStringProperty();
        address2 = new NonNullableStringProperty();
        city = new SimpleObjectProperty<>();
        cityId = scheduler.Util.primaryKeyBinding(city);
        postalCode = new NonNullableStringProperty();
        phone = new NonNullableStringProperty();
    }
    
    public AddressRow(String address1, String address2, CityRow city, String postalCode, String phone) {
        super();
        this.address1 = new NonNullableStringProperty(address1);
        this.address2 = new NonNullableStringProperty(address2);
        this.city = new SimpleObjectProperty<>(city);
        cityId = scheduler.Util.primaryKeyBinding(this.city);
        this.postalCode = new NonNullableStringProperty(postalCode);
        this.phone = new NonNullableStringProperty(phone);
    }
    
    public AddressRow(ResultSet rs) throws SQLException {
        super(rs);
        address1 = new NonNullableStringProperty(scheduler.Util.resultStringOrDefault(rs, COLNAME_ADDRESS, ""));
        address2 = new NonNullableStringProperty(scheduler.Util.resultStringOrDefault(rs, PROP_ADDRESS2, ""));
        city = new SimpleObjectProperty<>(new City(rs.getInt(PROP_CITYID), rs.getString(PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))));
        cityId = scheduler.Util.primaryKeyBinding(this.city);
        postalCode = new NonNullableStringProperty(scheduler.Util.resultStringOrDefault(rs, PROP_POSTALCODE, ""));
        phone = new NonNullableStringProperty(scheduler.Util.resultStringOrDefault(rs, PROP_PHONE, ""));
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
                    ps.setString(index + 1, getAddress1());
                    break;
                case PROP_ADDRESS2:
                    ps.setString(index + 1, getAddress2());
                    break;
                case PROP_CITYID:
                    ps.setInt(index + 1, getCityId());
                    break;
                case PROP_POSTALCODE:
                    ps.setString(index + 1, getPostalCode());
                    break;
                case PROP_PHONE:
                    ps.setString(index + 1, getPhone());
                    break;
            }
        }
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        address1.setValue(rs.getString(COLNAME_ADDRESS));
        if (rs.wasNull())
            address1.setValue("");
        address2.setValue(rs.getString(PROP_ADDRESS2));
        if (rs.wasNull())
            address2.setValue("");
        city.setValue(new City(rs.getInt(PROP_CITYID), rs.getString(PROP_CITY), new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))));
        postalCode.setValue(rs.getString(PROP_POSTALCODE));
        if (rs.wasNull())
            postalCode.setValue("");
        phone.setValue(rs.getString(PROP_PHONE));
        if (rs.wasNull())
            phone.setValue("");
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
        if ((getRowState() != ROWSTATE_MODIFIED && getRowState() != ROWSTATE_UNMODIFIED) || obj == null || !(obj instanceof model.Address))
            return false;
        final model.Address other = (model.Address)obj;
        return (other.getRowState() == ROWSTATE_MODIFIED || other.getRowState() == ROWSTATE_UNMODIFIED) && getPrimaryKey() == other.getPrimaryKey();
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
    
    static class City implements model.City {
        private final ReadOnlyIntegerWrapper primaryKey;

        @Override
        public int getPrimaryKey() { return primaryKey.get(); }

        public ReadOnlyIntegerProperty primaryKeyProperty() { return primaryKey.getReadOnlyProperty(); }
        
        private final ReadOnlyStringWrapper name;

        @Override
        public String getName() { return name.get(); }

        public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<CityRow.Country> country;

        @Override
        public CityRow.Country getCountry() { return country.get(); }

        public ReadOnlyObjectProperty<CityRow.Country> countryProperty() { return country.getReadOnlyProperty(); }
         
        City(int id, String name, CityRow.Country country) {
            primaryKey = new ReadOnlyIntegerWrapper(id);
            this.name = new ReadOnlyStringWrapper(name);
            this.country = new ReadOnlyObjectWrapper<>(country);
        }
        
        @Override
        public int hashCode() { return primaryKey.get(); }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof model.City))
                return false;
            
            model.City record = (model.City)obj;
            return (record.getRowState() == DataRow.ROWSTATE_MODIFIED || record.getRowState() == DataRow.ROWSTATE_UNMODIFIED) &&
                    record.getPrimaryKey() == getPrimaryKey();
        }
    
        @Override
        public String toString() {
            model.Country c = getCountry();
            return (c == null) ? getName() : String.format("%s, %s", getName(), c.getName());
        }
    }
}
