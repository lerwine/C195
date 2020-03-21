package scheduler.dao;

/**
 *
 * @author lerwi
 */
@Deprecated
public interface AddressColumns extends CityColumns {
    /**
     * The name of the 'addressId' column in the 'address' table, which is also the primary key.
     */
    @Deprecated
    public static final String COLNAME_ADDRESSID = "addressId";

    /**
     * The name of the 'address' column in the 'address' table.
     */
    @Deprecated
    public static final String COLNAME_ADDRESS = "address";

    /**
     * The name of the 'address2' column in the 'address' table.
     */
    @Deprecated
    public static final String COLNAME_ADDRESS2 = "address2";

    /**
     * The name of the 'postalCode' column in the 'address' table.
     */
    @Deprecated
    public static final String COLNAME_POSTALCODE = "postalCode";

    /**
     * The name of the 'phone' column in the 'address' table.
     */
    @Deprecated
    public static final String COLNAME_PHONE = "phone";
    
    @Deprecated
    public static final String SQL_JOIN_CITY = String.format("LEFT JOIN %1$s %2$s ON %3$s.%4$s = %2$s.%4$s %5$s", TABLENAME_CITY, TABLEALIAS_CITY,
            TABLEALIAS_ADDRESS, COLNAME_CITYID, SQL_JOIN_COUNTRY);
    
    @Deprecated
    public static final String SQL_ADDRESS_SELECT_FIELDS = String.format(".%1$s as %1$s, %2$s.%3$s as %3$s, %s2$.%4$s as %4$s, %2$s%5$s,"
            + " %2$s.%6$s as %6$s, %2$s.%7$s as %7$s",
            COLNAME_ADDRESSID, TABLEALIAS_ADDRESS, COLNAME_ADDRESS, COLNAME_ADDRESS2, SQL_CITY_SELECT_FIELDS, COLNAME_POSTALCODE, COLNAME_PHONE);
}
