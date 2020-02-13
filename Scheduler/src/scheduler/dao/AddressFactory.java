package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.view.address.AddressModel;

/**
 *
 * @author erwinel
 */
public class AddressFactory extends DataObjectFactory<AddressFactory.AddressImpl, AddressModel> {

    //<editor-fold defaultstate="collapsed" desc="Column names">

    /**
     * The name of the 'addressId' column in the 'address' table, which is also the primary key.
     */
    public static final String COLNAME_ADDRESSID = "addressId";
    
    /**
     * The name of the 'address' column in the 'address' table.
     */
    public static final String COLNAME_ADDRESS = "address";
    
    /**
     * The name of the 'address2' column in the 'address' table.
     */
    public static final String COLNAME_ADDRESS2 = "address2";
    
    /**
     * The name of the 'cityId' column in the 'address' table.
     */
    public static final String COLNAME_CITYID = "cityId";
    
    /**
     * The name of the 'postalCode' column in the 'address' table.
     */
    public static final String COLNAME_POSTALCODE = "postalCode";
    
    /**
     * The name of the 'phone' column in the 'address' table.
     */
    public static final String COLNAME_PHONE = "phone";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SQL definitions">
    
    private static final String BASE_SELECT_SQL = String.format("SELECT a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, c.`%s` AS `%s`,"
        + " c.`%s` AS `%s`, n.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`"
        + " FROM `%s` a"
        + " LEFT JOIN `%s` c ON a.`%s`=c.`%s`"
        + " LEFT JOIN `%s` n ON c.`%s`=n.`%s`", COLNAME_ADDRESSID, COLNAME_ADDRESSID, COLNAME_ADDRESS, COLNAME_ADDRESS,
        COLNAME_ADDRESS2, COLNAME_ADDRESS2, COLNAME_CITYID, COLNAME_CITYID, CityFactory.COLNAME_CITY, CityFactory.COLNAME_CITY,
        CityFactory.COLNAME_COUNTRYID, CityFactory.COLNAME_COUNTRYID, CountryFactory.COLNAME_COUNTRY, CountryFactory.COLNAME_COUNTRY,
        COLNAME_POSTALCODE, COLNAME_POSTALCODE, COLNAME_PHONE, COLNAME_PHONE, COLNAME_CREATEDATE, COLNAME_CREATEDATE,
        COLNAME_CREATEDBY, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY,
        TABLENAME_ADDRESS, TABLENAME_CITY, COLNAME_CITYID, CityFactory.COLNAME_CITYID, TABLENAME_COUNTRY,
        CityFactory.COLNAME_COUNTRYID, CountryFactory.COLNAME_COUNTRYID);
    
    @Override
    public String getBaseQuery() { return BASE_SELECT_SQL; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_ADDRESS, CityFactory.COLNAME_CITY,
                    CountryFactory.COLNAME_COUNTRY, COLNAME_POSTALCODE, COLNAME_PHONE, COLNAME_LASTUPDATE,
                    COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    public Optional<AddressImpl> findByAddressLines(Connection connection, String line1, String line2, int cityId, String postalCode, String phone) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public Optional<AddressImpl> findByAddressLines(Connection connection, String line1, String line2, int cityId, String postalCode) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
//    public ArrayList<AddressImpl> loadByCity(Connection connection, int cityId, Iterable<OrderBy> orderBy) throws Exception {
//        throw new UnsupportedOperationException("Not implemented");
//    }
    
    public ArrayList<AddressImpl> loadByCity(Connection connection, int cityId) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
//    public ArrayList<AddressImpl> loadByCountry(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
//        throw new UnsupportedOperationException("Not implemented");
//    }
    
    public ArrayList<AddressImpl> loadByCountry(Connection connection, int countryId) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public int countByCity(Connection connection, int cityId) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    protected AddressImpl fromResultSet(ResultSet resultSet) throws SQLException {
        AddressImpl r = new AddressImpl();
        initializeDao(r, resultSet);
        return r;
    }

    @Override
    public AddressModel fromDataAccessObject(AddressImpl dao) { return (dao == null) ? null : new AddressModel(dao); }

    @Override
    public Class<? extends AddressImpl> getDaoClass() { return AddressImpl.class; }

    @Override
    protected Stream<String> getExtendedColNames() {
        return Stream.of(COLNAME_ADDRESS, COLNAME_ADDRESS2, COLNAME_CITYID, COLNAME_POSTALCODE, COLNAME_PHONE);
    }

    @Override
    protected void initializeDao(AddressImpl target, ResultSet resultSet) throws SQLException {
        super.initializeDao(target, resultSet);
        target.address1 = resultSet.getString(AddressFactory.COLNAME_ADDRESS);
        if (resultSet.wasNull())
            target.address1 = "";
        target.address2 = resultSet.getString(AddressFactory.COLNAME_ADDRESS2);
        if (resultSet.wasNull())
            target.address2 = "";
        int cityId = resultSet.getInt(AddressFactory.COLNAME_CITYID);
        if (resultSet.wasNull())
            target.city = null;
        else {
            String cityName = resultSet.getString(CityFactory.COLNAME_CITY);
            if (resultSet.wasNull())
                cityName = "";
            int countryId = resultSet.getInt(CityFactory.COLNAME_COUNTRYID);
            if (resultSet.wasNull())
                target.city = City.of(cityId, cityName, null);
            else {
                String countryName = resultSet.getString(CountryFactory.COLNAME_COUNTRY);
                target.city = City.of(cityId, cityName, Country.of(countryId, resultSet.wasNull() ? "" : countryName));
            }
        }
        target.postalCode = resultSet.getString(AddressFactory.COLNAME_POSTALCODE);
        if (resultSet.wasNull())
            target.postalCode = "";
        target.phone = resultSet.getString(AddressFactory.COLNAME_PHONE);
        if (resultSet.wasNull())
            target.phone = "";
    }

    @Override
    protected void setStatementValues(AddressImpl dao, PreparedStatement ps) throws SQLException {
        ps.setString(1, dao.getAddress1());
        ps.setString(2, dao.getAddress2());
        ps.setInt(3, dao.getCity().getPrimaryKey());
        ps.setString(4, dao.getPostalCode());
        ps.setString(5, dao.getPhone());
    }

    @Override
    public String getTableName() { return TABLENAME_ADDRESS; }

    @Override
    public String getPrimaryKeyColName() { return COLNAME_ADDRESSID; }
    
    /**
    *
    * @author erwinel
    */
//   @TableName(DataObjectFactory.TABLENAME_ADDRESS)
//   @PrimaryKeyColumn(AddressFactory.COLNAME_ADDRESSID)
   public static final class AddressImpl extends DataObjectFactory.DataObjectImpl implements Address {
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
        * Initializes a {@link DataObjectFactory.ROWSTATE_NEW} address object.
        */
       public AddressImpl() {
           this.address1 = "";
           this.address2 = "";
           this.city = null;
           this.postalCode = "";
           this.phone = "";
       }

       @Override
       public void saveChanges(Connection connection) throws Exception {
           (new AddressFactory()).save(this, connection);
       }

       @Override
       public void delete(Connection connection) throws Exception {
           (new AddressFactory()).delete(this, connection);
       }

   }
}
