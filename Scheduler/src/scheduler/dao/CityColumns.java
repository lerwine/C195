package scheduler.dao;

/**
 *
 * @author lerwi
 */
    @Deprecated
public interface CityColumns extends CountryColumns, TableNames {
    
    @Deprecated
    public static final String COLNAME_CITYID = "cityId";
    
    @Deprecated
    public static final String COLNAME_CITY = "city";
    
    @Deprecated
    public static final String SQL_JOIN_COUNTRY = String.format("LEFT JOIN %1$s %2$s ON %3$s.%4$s = %2$s.%4$s", TABLENAME_COUNTRY, TABLEALIAS_COUNTRY,
            TABLEALIAS_CITY, TABLEALIAS_COUNTRY);
    
    @Deprecated
    public static final String SQL_CITY_SELECT_FIELDS = String.format(".%1$s as %1$s, %2$s.%3$s as %3$s, %4$s.%5$s as %5$s, %4$s.%6$s as %6$s",
            COLNAME_CITYID, TABLEALIAS_CITY, COLNAME_CITY, TABLEALIAS_COUNTRY, COLNAME_COUNTRYID, COLNAME_COUNTRY);
}
