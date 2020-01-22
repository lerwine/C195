package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.dao.factory.AddressFactory;
import scheduler.dao.factory.AppointmentFactory;
import scheduler.dao.factory.CityFactory;
import scheduler.dao.factory.CountryFactory;
import scheduler.dao.factory.CustomerFactory;
import scheduler.dao.factory.DataObjectFactory;
import view.customer.AppointmentCustomer;


/**
 *
 * @author erwinel
 */
@TableName(DataObjectFactory.TABLENAME_CUSTOMER)
@PrimaryKeyColumn(CustomerFactory.COLNAME_CUSTOMERID)
public class CustomerImpl extends DataObjectImpl implements Customer {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
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
    public CustomerImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        name  = resultSet.getString(CustomerFactory.COLNAME_CUSTOMERNAME);
        if (resultSet.wasNull())
            name = "";
        
        int addressId = resultSet.getInt(CustomerFactory.COLNAME_ADDRESSID);
        if (resultSet.wasNull())
            address = null;
        else {
            String address1 = resultSet.getString(AddressFactory.COLNAME_ADDRESS);
            if (resultSet.wasNull())
                address1 = "";
            String address2 = resultSet.getString(AddressFactory.COLNAME_ADDRESS2);
            if (resultSet.wasNull())
                address2 = "";
            City city;
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
            String postalCode = resultSet.getString(AddressFactory.COLNAME_POSTALCODE);
            if (resultSet.wasNull())
                postalCode = "";
            String phone = resultSet.getString(AddressFactory.COLNAME_PHONE);
            address = Address.of(addressId, address1, address2, city, postalCode, (resultSet.wasNull()) ? "" : phone);
        }
        
        active = resultSet.getBoolean(CustomerFactory.COLNAME_ACTIVE);
        if (resultSet.wasNull())
            active = false;
    }
    
    @Override
    public synchronized void delete(Connection connection) throws Exception {
        Objects.requireNonNull(connection, "Connection cannot be null");
        assert AppointmentFactory.getCount(connection, AppointmentFactory.customerIs(AppointmentCustomer.of(this))) == 0 : "Customer is associated with one or more appointments.";
        super.delete(connection);
    }
    
}
