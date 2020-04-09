package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface CityColumns extends CountryColumns, TableNames {
    
    public static final String COLNAME_CITYID = "cityId";
    
    public static final String COLNAME_CITY = "city";
    
    public static final String SQL_JOIN_COUNTRY = String.format("LEFT JOIN %s %s ON %s.%s = %s.%s", TABLENAME_COUNTRY, TABLEALIAS_COUNTRY,
            TABLEALIAS_CITY, COLNAME_COUNTRYID, TABLEALIAS_COUNTRY, COLNAME_COUNTRYID);
    
    public static final String SQL_CITY_SELECT_FIELDS = String.format(".%s as %s, %s.%s as %s, %s.%s as %s, %s.%s as %s",
            COLNAME_CITYID, COLNAME_CITYID, TABLEALIAS_CITY, COLNAME_CITY, COLNAME_CITY,
            TABLEALIAS_COUNTRY, COLNAME_COUNTRYID, COLNAME_COUNTRYID, TABLEALIAS_COUNTRY, COLNAME_COUNTRY, COLNAME_COUNTRY);
}
