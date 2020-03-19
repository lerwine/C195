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
interface CustomerColumns extends AddressColumns {
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    public static final String COLNAME_CUSTOMERNAME = "customerName";
    
    public static final String COLNAME_ACTIVE = "active";
    
    public static final String SQL_JOIN_ADDRESS = String.format("LEFT JOIN %1$s %2$s ON %3$s.%4$s = %2$s.%4$s %5$s", TABLENAME_ADDRESS,
            TABLEALIAS_ADDRESS, TABLEALIAS_CUSTOMER, COLNAME_ADDRESSID, SQL_JOIN_CITY);
    
    public static final String SQL_CUSTOMER_SELECT_FIELDS = String.format(".%1$s as %1$s, %2$s.%3$s as %3$s, %2$s.%4$s as %4$s, %2$s%s",
            COLNAME_CUSTOMERID, TABLEALIAS_CUSTOMER, COLNAME_CUSTOMERNAME,
            COLNAME_ACTIVE, COLNAME_ACTIVE, SQL_ADDRESS_SELECT_FIELDS);
}
