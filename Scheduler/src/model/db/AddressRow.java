package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
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
    
    private final ReadOnlyIntegerWrapper cityId;

    /**
    * Get the value of cityId
    *
    * @return the value of cityId
    */
    public int getCityId() { return cityId.get(); }

    public ReadOnlyIntegerProperty cityIdProperty() { return cityId.getReadOnlyProperty(); }
    
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
    
    private final RowIdChangeListener<model.City> cityIdChangeListener;
    
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
        cityId = new ReadOnlyIntegerWrapper(0);
        city = new SimpleObjectProperty<>();
        postalCode = new NonNullableStringProperty();
        phone = new NonNullableStringProperty();
        cityIdChangeListener = new RowIdChangeListener<>(this.city, cityId);
    }
    
    public AddressRow(String address1, String address2, CityRow city, String postalCode, String phone) {
        super();
        this.address1 = new NonNullableStringProperty(address1);
        this.address2 = new NonNullableStringProperty(address2);
        cityId = new ReadOnlyIntegerWrapper();
        this.city = new SimpleObjectProperty<>(city);
        this.postalCode = new NonNullableStringProperty(postalCode);
        this.phone = new NonNullableStringProperty(phone);
        cityIdChangeListener = new RowIdChangeListener<>(this.city, cityId);
    }
    
    public AddressRow(ResultSet rs) throws SQLException {
        super(rs);
        address1 = new NonNullableStringProperty(rs.getString(COLNAME_ADDRESS));
        if (rs.wasNull())
            address1.setValue("");
        address2 = new NonNullableStringProperty(rs.getString(PROP_ADDRESS2));
        if (rs.wasNull())
            address2.setValue("");
        cityId = new ReadOnlyIntegerWrapper(rs.getInt(PROP_CITYID));
        city = new SimpleObjectProperty<>(new City(getCityId(), rs.getString(PROP_CITY), new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))));
        postalCode = new NonNullableStringProperty(rs.getString(PROP_POSTALCODE));
        if (rs.wasNull())
            postalCode.setValue("");
        phone = new NonNullableStringProperty(rs.getString(PROP_PHONE));
        if (rs.wasNull())
            phone.setValue("");
        cityIdChangeListener = new RowIdChangeListener<>(this.city, cityId);
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
