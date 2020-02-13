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
import scheduler.view.city.CityModel;
import scheduler.view.country.CityCountry;

/**
 *
 * @author erwinel
 */
public class CityFactory extends DataObjectFactory<CityFactory.CityImpl, CityModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_CITYID = "cityId";
    
    public static final String COLNAME_CITY = "city";
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static final String BASE_SELECT_SQL = String.format("SELECT c.`%s` AS `%s`, c.`%s` AS `%s`, c.`%s` AS `%s`, n.`%s` AS `%s` FROM `%s` c"
            + " LEFT JOIN `%s` n ON c.`%s`=n.`%s`", COLNAME_CITYID, COLNAME_CITYID, COLNAME_CITY, COLNAME_CITY, COLNAME_COUNTRYID,
            COLNAME_COUNTRYID, CountryFactory.COLNAME_COUNTRY, CountryFactory.COLNAME_COUNTRY, COLNAME_CREATEDATE, COLNAME_CREATEDATE, COLNAME_CREATEDBY,
            COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY, TABLENAME_CITY, TABLENAME_COUNTRY,
            COLNAME_COUNTRYID, CountryFactory.COLNAME_COUNTRYID);
    
    @Override
    public String getBaseQuery() { return BASE_SELECT_SQL; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_CITY, CountryFactory.COLNAME_COUNTRY,
                    COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE,
                    COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    public Optional<CityImpl> findByName(Connection connection, int countryId, String value) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
//    public ArrayList<CityImpl> loadByCountryId(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
//        return load(connection, countryIdIs(countryId), orderBy);
//    }
//    
//    public ArrayList<CityImpl> loadByCountryName(Connection connection, String value, Iterable<OrderBy> orderBy) throws Exception {
//        return load(connection, countryNameIs(value), orderBy);
//    }
    
    public int countByCountry(Connection connection, int countryId) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    protected CityImpl fromResultSet(ResultSet resultSet) throws SQLException {
        CityImpl r = new CityImpl();
        initializeDao(r, resultSet);
        return r;
    }

    @Override
    public CityModel fromDataAccessObject(CityImpl dao) { return (dao == null) ? null : new CityModel(dao); }

    @Override
    public Class<? extends CityImpl> getDaoClass() { return CityImpl.class; }

    @Override
    protected Stream<String> getExtendedColNames() {
        return Stream.of(COLNAME_CITY, COLNAME_COUNTRYID);
    }

    @Override
    protected void setStatementValues(CityImpl dao, PreparedStatement ps) throws SQLException {
        ps.setString(1, dao.getName());
        ps.setInt(2, dao.getCountry().getPrimaryKey());
    }

    @Override
    protected void initializeDao(CityImpl target, ResultSet resultSet) throws SQLException {
        super.initializeDao(target, resultSet);
        target.name = resultSet.getString(CityFactory.COLNAME_CITY);
        if (resultSet.wasNull())
            target.name = "";
        int countryId = resultSet.getInt(CityFactory.COLNAME_COUNTRYID);
        if (resultSet.wasNull())
            target.country = null;
        else {
            String countryName = resultSet.getString(CountryFactory.COLNAME_COUNTRY);
            target.country = Country.of(countryId, resultSet.wasNull() ? "" : countryName);
        }
    }

    @Override
    public String getTableName() { return TABLENAME_CITY; }

    @Override
    public String getPrimaryKeyColName() { return COLNAME_CITYID; }
    
    /**
    *
    * @author erwinel
    */
   @TableName(DataObjectFactory.TABLENAME_CITY)
   @PrimaryKeyColumn(CityFactory.COLNAME_CITYID)
   public static final class CityImpl extends DataObjectFactory.DataObjectImpl implements City {
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

       @Override
       public void saveChanges(Connection connection) throws Exception {
           (new CityFactory()).save(this, connection);
       }

       @Override
       public void delete(Connection connection) throws Exception {
           (new CityFactory()).delete(this, connection);
       }

   }
}
