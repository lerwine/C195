package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import scheduler.dao.factory.AddressFactory;
import scheduler.dao.factory.CityFactory;
import scheduler.dao.factory.CountryFactory;
import scheduler.dao.factory.CustomerFactory;
import scheduler.dao.factory.DataObjectFactory;
import view.address.CustomerAddress;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectFactory.TABLENAME_ADDRESS)
@PrimaryKeyColumn(AddressFactory.COLNAME_ADDRESSID)
public class AddressImpl extends DataObjectImpl implements Address {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
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
        address1 = resultSet.getString(AddressFactory.COLNAME_ADDRESS);
        if (resultSet.wasNull())
            address1 = "";
        address2 = resultSet.getString(AddressFactory.COLNAME_ADDRESS2);
        if (resultSet.wasNull())
            address2 = "";
        int cityId = resultSet.getInt(AddressFactory.COLNAME_CITYID);
        if (resultSet.wasNull())
            city = null;
        else {
            String cityName = resultSet.getString(CityFactory.COLNAME_CITY);
            if (resultSet.wasNull())
                cityName = "";
            int countryId = resultSet.getInt(CityFactory.COLNAME_COUNTRYID);
            if (resultSet.wasNull())
                city = City.of(cityId, cityName, null);
            else {
                String countryName = resultSet.getString(CountryFactory.COLNAME_COUNTRY);
                city = City.of(cityId, cityName, Country.of(countryId, resultSet.wasNull() ? "" : countryName));
            }
        }
        postalCode = resultSet.getString(AddressFactory.COLNAME_POSTALCODE);
        if (resultSet.wasNull())
            postalCode = "";
        phone = resultSet.getString(AddressFactory.COLNAME_PHONE);
        if (resultSet.wasNull())
            phone = "";
    }
    
    @Override
    public synchronized void delete(Connection connection) throws Exception {
        assert CustomerFactory.getCount(connection, CustomerFactory.addressIs(CustomerAddress.of(this))) == 0 : "Address is associated with one or more addresses.";
        super.delete(connection);
    }
    
}
