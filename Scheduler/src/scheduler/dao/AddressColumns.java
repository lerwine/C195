/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

/**
 *
 * @author lerwi
 */
public interface AddressColumns extends CityColumns {
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
     * The name of the 'postalCode' column in the 'address' table.
     */
    public static final String COLNAME_POSTALCODE = "postalCode";

    /**
     * The name of the 'phone' column in the 'address' table.
     */
    public static final String COLNAME_PHONE = "phone";
    
    public static final String SQL_JOIN_CITY = String.format("LEFT JOIN %s %s ON %s.%s = %s.%s %s", TABLENAME_CITY, TABLEALIAS_CITY,
            TABLEALIAS_ADDRESS, COLNAME_CITYID, TABLEALIAS_CITY, COLNAME_CITYID, SQL_JOIN_COUNTRY);
    
    public static final String SQL_ADDRESS_SELECT_FIELDS = String.format(".%s as %s, %s.%s as %s, %s.%s as %s, %s%s, %s.%s as %s, %s.%s as %s",
            COLNAME_ADDRESSID, COLNAME_ADDRESSID, TABLEALIAS_ADDRESS, COLNAME_ADDRESS, COLNAME_ADDRESS,
            TABLEALIAS_ADDRESS, COLNAME_ADDRESS2, COLNAME_ADDRESS2, TABLEALIAS_ADDRESS, SQL_CITY_SELECT_FIELDS,
            TABLEALIAS_ADDRESS, COLNAME_POSTALCODE, COLNAME_POSTALCODE, TABLEALIAS_ADDRESS, COLNAME_PHONE, COLNAME_PHONE);
}
