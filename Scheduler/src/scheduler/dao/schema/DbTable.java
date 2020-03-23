/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

import java.util.Iterator;

/**
 *
 * @author lerwi
 */
public enum DbTable {
    COUNTRY(DbName.COUNTRY, "n", DbName.COUNTRY_ID),
    CITY(DbName.CITY, "c", DbName.CITY_ID),
    ADDRESS(DbName.ADDRESS, "l", DbName.ADDRESS_ID),
    CUSTOMER(DbName.CUSTOMER, "p", DbName.CUSTOMER_ID),
    APPOINTMENT(DbName.APPOINTMENT, "a", DbName.APPOINTMENT_ID),
    USER(DbName.USER_ID, "u", DbName.USER_ID);
    private final DbName dbName;
    private final String alias;
    private final DbName pkColName;

    public DbName getDbName() {
        return dbName;
    }

    public String getAlias() {
        return alias;
    }

    public DbName getPkColName() {
        return pkColName;
    }
    
    private DbTable(DbName dbName, String alias, DbName pkColName) {
        this.dbName = dbName;
        this.alias = alias;
        this.pkColName = pkColName;
    }
    
    @Override
    public String toString() {
        return alias;
    }

}
