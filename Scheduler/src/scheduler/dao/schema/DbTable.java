package scheduler.dao.schema;

/**
 * Database tables defined in the database schema.
 * <p>
 * This provides constant naming definitions for database tables. Methods from {@link SchemaHelper} can be used to get the {@link DbColumn}s
 * associated with a specific table.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum DbTable {
    /**
     * The {@code "country"} data table.
     */
    COUNTRY(DbName.COUNTRY, "n", DbName.COUNTRY_ID),
    /**
     * The {@code "city"} data table.
     */
    CITY(DbName.CITY, "c", DbName.CITY_ID),
    /**
     * The {@code "address"} data table.
     */
    ADDRESS(DbName.ADDRESS, "l", DbName.ADDRESS_ID),
    /**
     * The {@code "customer"} data table.
     */
    CUSTOMER(DbName.CUSTOMER, "p", DbName.CUSTOMER_ID),
    /**
     * The {@code "appointment"} data table.
     */
    APPOINTMENT(DbName.APPOINTMENT, "a", DbName.APPOINTMENT_ID),
    /**
     * The {@code "user"} data table.
     */
    USER(DbName.USER, "u", DbName.USER_ID);
    private final DbName dbName;
    private final String defaultAlias;
    private final DbName pkColName;

    private DbTable(DbName dbName, String defaultAlias, DbName pkColName) {
        this.dbName = dbName;
        this.defaultAlias = defaultAlias;
        this.pkColName = pkColName;
    }

    /**
     * Gets the name of the table referenced by the enumerated value.
     * <p>
     * This is the name of the table in the database schema definition.</p>
     *
     * @return The name of the table as defined in the database schema.
     */
    public DbName getDbName() {
        return dbName;
    }

    /**
     * Gets the name of the primary key column for the data table.
     * <p>
     * Use {@link SchemaHelper#getPrimaryKey(scheduler.dao.schema.DbTable)} to get the actual {@link DbColumn} definition.</p>
     *
     * @return The name of the primary key column for the data table referenced by the enumerated value.
     */
    public DbName getPkColName() {
        return pkColName;
    }

    /**
     * Gets the default alias for the data table referenced by the enumerated value.
     * <p>
     * This is provided so table joins can be programmatically created more easily.</p>
     *
     * @return The default table alias for use with SQL query join statements.
     */
    @Override
    public String toString() {
        return defaultAlias;
    }

}
