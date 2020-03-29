package scheduler.dao.schema;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum DbTable {
    COUNTRY(DbName.COUNTRY, "n", DbName.COUNTRY_ID),
    CITY(DbName.CITY, "c", DbName.CITY_ID),
    ADDRESS(DbName.ADDRESS, "l", DbName.ADDRESS_ID),
    CUSTOMER(DbName.CUSTOMER, "p", DbName.CUSTOMER_ID),
    APPOINTMENT(DbName.APPOINTMENT, "a", DbName.APPOINTMENT_ID),
    USER(DbName.USER_ID, "u", DbName.USER_ID);
    private final DbName dbName;
    private final String defaultAlias;
    private final DbName pkColName;

    public DbName getDbName() {
        return dbName;
    }

    public DbName getPkColName() {
        return pkColName;
    }
    
    private DbTable(DbName dbName, String defaultAlias, DbName pkColName) {
        this.dbName = dbName;
        this.defaultAlias = defaultAlias;
        this.pkColName = pkColName;
    }
    
    /**
     * Gets the default alias for this table.
     * 
     * @return The default table alias for use in SQL query strings.
     */
    @Override
    public String toString() {
        return defaultAlias;
    }

}
