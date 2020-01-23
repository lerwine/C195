package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import scheduler.view.country.CityCountry;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectFactory.TABLENAME_COUNTRY)
@PrimaryKeyColumn(CountryFactory.COLNAME_COUNTRYID)
public class CountryImpl extends DataObjectImpl implements Country {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="name property">
    
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() { return name; }

    /**
     * Set the value of name.
     * @param value new value of name.
     */
    public void setName(String value) { name = (value == null) ? "" : value; }

    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} country object.
     */
    public CountryImpl() {
        super();
        name = "";
    }
    
    /**
     * Initializes a country object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    CountryImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        name = resultSet.getString(CountryFactory.COLNAME_COUNTRY);
        if (resultSet.wasNull())
            name = "";
    }
    
    @Override
    public synchronized void delete(Connection connection) throws Exception {
        assert (new CityFactory()).countByCountry(connection, getPrimaryKey()) == 0 : "Country is associated with one or more cities.";
        super.delete(connection);
    }

}
