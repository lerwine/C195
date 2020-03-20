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
public enum TableName {
    COUNTRY("country", "n", "countryId"),
    CITY("city", "c", "cityId"),
    ADDRESS("address", "l", "addressId"),
    CUSTOMER("customer", "p", "customerId"),
    APPOINTMENT("appointment", "a", "appointmentId"),
    USER("user", "u", "userId");
    private final String dbName;
    private final String alias;
    private final String pkColName;

    public String getDbName() {
        return dbName;
    }

    public String getAlias() {
        return alias;
    }

    public String getPkColName() {
        return pkColName;
    }
    
    private TableName(String dbName, String alias, String pkColName) {
        this.dbName = dbName;
        this.alias = alias;
        this.pkColName = pkColName;
    }
}
