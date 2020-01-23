package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import scheduler.view.city.AddressCity;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectFactory.TABLENAME_CITY)
@PrimaryKeyColumn(CityFactory.COLNAME_CITYID)
public class CityImpl extends DataObjectImpl implements City {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="name property">
    
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() { return name; }

    /**
     * Set the value of name
     *
     * @param value new value of name
     */
    public void setName(String value) { name = (value == null) ? "" : value; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="country property">
    
    private Country country;

    /**
     * {@inheritDoc}
     */
    @Override
    public Country getCountry() {
        return country;
    }

    /**
     * Set the value of country
     *
     * @param country new value of country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} city object.
     */
    public CityImpl() {
        super();
        name = "";
        country = null;
    }
    
    /**
     * Initializes a city object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    CityImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        name = resultSet.getString(CityFactory.COLNAME_CITY);
        if (resultSet.wasNull())
            name = "";
        int countryId = resultSet.getInt(CityFactory.COLNAME_COUNTRYID);
        if (resultSet.wasNull())
            country = null;
        else {
            String countryName = resultSet.getString(CountryFactory.COLNAME_COUNTRY);
            country = Country.of(countryId, resultSet.wasNull() ? "" : countryName);
        }
    }
    
    @Override
    public synchronized void delete(Connection connection) throws Exception {
        assert (new AddressFactory()).countByCity(connection, getPrimaryKey()) == 0 : "City is associated with one or more addresses.";
        super.delete(connection);
    }
    
}
