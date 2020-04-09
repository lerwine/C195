package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
interface CustomerColumns extends AddressColumns {
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    public static final String COLNAME_CUSTOMERNAME = "customerName";
    
    public static final String COLNAME_ACTIVE = "active";
    
    public static final String SQL_JOIN_ADDRESS = String.format("LEFT JOIN %s %s ON %s.%s = %s.%s %s", TABLENAME_ADDRESS, TABLEALIAS_ADDRESS,
            TABLEALIAS_CUSTOMER, COLNAME_ADDRESSID, TABLEALIAS_ADDRESS, COLNAME_ADDRESSID, SQL_JOIN_CITY);
    
    public static final String SQL_CUSTOMER_SELECT_FIELDS = String.format(".%s as %s, %s.%s as %s, %s.%s as %s, %s%s",
            COLNAME_CUSTOMERID, COLNAME_CUSTOMERID, TABLEALIAS_CUSTOMER, COLNAME_CUSTOMERNAME, COLNAME_CUSTOMERNAME,
            TABLEALIAS_CUSTOMER, COLNAME_ACTIVE, COLNAME_ACTIVE, TABLEALIAS_CUSTOMER, SQL_ADDRESS_SELECT_FIELDS);
}
