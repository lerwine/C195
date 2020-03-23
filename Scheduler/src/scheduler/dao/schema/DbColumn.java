package scheduler.dao.schema;

import scheduler.util.ReadOnlyList;

/**
 * Database table columns defined in the application database.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum DbColumn {
    /**
     * The {@code customerId} column in the {@code appointment} data table.
     * Refers to the {@link DbTable#CUSTOMER} table using the {@link DbColumn#CUSTOMER_ID} column.
     */
    APPOINTMENT_CUSTOMER(DbName.CUSTOMER_ID, ColumnType.INT, 10, DbTable.APPOINTMENT, ColumnUsage.FOREIGN_KEY,
            new ForeignKey("appointment_ibfk_1", DbTable.CUSTOMER, DbName.CUSTOMER_ID)),
    /**
     * The {@code customerName} column in the {@code customer} data table.
     */
    CUSTOMER_NAME(DbName.CUSTOMER_NAME, ColumnType.VARCHAR, 45, DbTable.CUSTOMER, ColumnUsage.UNIQUE_KEY),
    /**
     * The {@code addressId} column in the {@code customer} data table.
     * Refers to the {@link DbTable#ADDRESS} table using the {@link DbColumn#ADDRESS_ID} column.
     */
    CUSTOMER_ADDRESS(DbName.ADDRESS_ID, ColumnType.INT, 10, DbTable.CUSTOMER, ColumnUsage.FOREIGN_KEY,
            new ForeignKey("customer_ibfk_1", DbTable.ADDRESS, DbName.ADDRESS_ID)),
    /**
     * The {@code address} column in the {@code address} data table.
     */
    ADDRESS1(DbName.ADDRESS, ColumnType.VARCHAR, 50, DbTable.ADDRESS, ColumnUsage.DATA),
    /**
     * The {@code address1} column in the {@code address} data table.
     */
    ADDRESS2(DbName.ADDRESS2, ColumnType.VARCHAR, 50, DbTable.ADDRESS, ColumnUsage.DATA),
    /**
     * The {@code cityId} column in the {@code address} data table.
     * Refers to the {@link DbTable#CITY} table using the {@link DbColumn#CITY_ID} column.
     */
    ADDRESS_CITY(DbName.CITY_ID, ColumnType.INT, 10, DbTable.ADDRESS, ColumnUsage.FOREIGN_KEY,
            new ForeignKey("address_ibfk_1", DbTable.CITY, DbName.CITY_ID)),
    /**
     * The {@code city} column in the {@code city} data table.
     */
    CITY_NAME(DbName.CITY, ColumnType.VARCHAR, 50, DbTable.CITY, ColumnUsage.DATA),
    /**
     * The {@code countryId} column in the {@code city} data table.
     * Refers to the {@link DbTable#COUNTRY} table using the {@link DbColumn#COUNTRY_ID} column.
     */
    CITY_COUNTRY(DbName.COUNTRY_ID, ColumnType.INT, 10, DbTable.CITY, ColumnUsage.FOREIGN_KEY,
            new ForeignKey("address_ibfk_1", DbTable.COUNTRY, DbName.COUNTRY_ID)),
    /**
     * The {@code country} column in the {@code country} data table.
     */
    COUNTRY_NAME(DbName.COUNTRY, ColumnType.VARCHAR, 50, DbTable.COUNTRY, ColumnUsage.DATA),
    /**
     * The {@code postalCode} column in the {@code address} data table.
     */
    POSTAL_CODE(DbName.POSTAL_CODE, ColumnType.VARCHAR, 10, DbTable.ADDRESS, ColumnUsage.DATA),
    /**
     * The {@code phone} column in the {@code address} data table.
     */
    PHONE(DbName.PHONE, ColumnType.VARCHAR, 20, DbTable.ADDRESS, ColumnUsage.DATA),
    /**
     * The {@code active} column in the {@code customer} data table.
     */
    ACTIVE(DbName.ACTIVE, ColumnType.TINYINT_BOOLEAN, DbTable.CUSTOMER, ColumnUsage.DATA),
    /**
     * The {@code userId} column in the {@code appointment} data table.
     * Refers to the {@link DbTable#USER} table using the {@link DbColumn#USER_ID} column.
     */
    APPOINTMENT_USER(DbName.USER_ID, ColumnType.INT, 11, DbTable.APPOINTMENT, ColumnUsage.FOREIGN_KEY,
            new ForeignKey("appointment_ibfk_1", DbTable.USER, DbName.USER_ID)),
    /**
     * The {@code userName} column in the {@code user} data table.
     */
    USER_NAME(DbName.USER_NAME, ColumnType.VARCHAR, 50, DbTable.USER, ColumnUsage.UNIQUE_KEY),
    /**
     * The {@code password} column in the {@code user} data table.
     */
    PASSWORD(DbName.PASSWORD, ColumnType.VARCHAR_PWD_HASH, 50, DbTable.USER, ColumnUsage.CRYPTO_HASH),
    /**
     * The {@code active} column in the {@code user} data table, usually referred to by the {@code status} alias.
     */
    STATUS(DbName.ACTIVE, ColumnType.TINYINT, "status", 4, DbTable.USER, ColumnUsage.DATA),
    /**
     * The {@code title} column in the {@code appointment} data table.
     */
    TITLE(DbName.TITLE, ColumnType.VARCHAR, 255, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code description} column in the {@code appointment} data table.
     */
    DESCRIPTION(DbName.DESCRIPTION, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code location} column in the {@code appointment} data table.
     */
    LOCATION(DbName.LOCATION, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code contact} column in the {@code appointment} data table.
     */
    CONTACT(DbName.CONTACT, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code type} column in the {@code appointment} data table.
     */
    TYPE(DbName.TYPE, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code url} column in the {@code appointment} data table.
     */
    URL(DbName.URL, ColumnType.VARCHAR, 255, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code start} column in the {@code appointment} data table.
     */
    START(DbName.START, ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code end} column in the {@code appointment} data table.
     */
    END(DbName.END, ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnUsage.DATA),
    /**
     * The {@code appointmentId} column in the {@code appointment} data table.
     */
    APPOINTMENT_ID(DbName.APPOINTMENT_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.APPOINTMENT, ColumnUsage.PRIMARY_KEY),
    /**
     * The {@code createDate} column in the {@code appointment} data table.
     */
    APPOINTMENT_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    /**
     * The {@code createdBy} column in the {@code appointment} data table.
     */
    APPOINTMENT_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdate} column in the {@code appointment} data table.
     */
    APPOINTMENT_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdateBy} column in the {@code appointment} data table.
     */
    APPOINTMENT_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    /**
     * The {@code customerId} column in the {@code customer} data table.
     */
    CUSTOMER_ID(DbName.CUSTOMER_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.CUSTOMER, ColumnUsage.PRIMARY_KEY),
    /**
     * The {@code createDate} column in the {@code customer} data table.
     */
    CUSTOMER_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    /**
     * The {@code createdBy} column in the {@code customer} data table.
     */
    CUSTOMER_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdate} column in the {@code customer} data table.
     */
    CUSTOMER_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdateBy} column in the {@code customer} data table.
     */
    CUSTOMER_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    /**
     * The {@code addressId} column in the {@code address} data table.
     */
    ADDRESS_ID(DbName.ADDRESS_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.ADDRESS, ColumnUsage.PRIMARY_KEY),
    /**
     * The {@code createDate} column in the {@code address} data table.
     */
    ADDRESS_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.ADDRESS, ColumnUsage.AUDIT),
    /**
     * The {@code createdBy} column in the {@code address} data table.
     */
    ADDRESS_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.ADDRESS, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdate} column in the {@code address} data table.
     */
    ADDRESS_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.ADDRESS, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdateBy} column in the {@code address} data table.
     */
    ADDRESS_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.ADDRESS, ColumnUsage.AUDIT),
    /**
     * The {@code cityId} column in the {@code city} data table.
     */
    CITY_ID(DbName.CITY_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.CITY, ColumnUsage.PRIMARY_KEY),
    /**
     * The {@code createDate} column in the {@code city} data table.
     */
    CITY_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.CITY, ColumnUsage.AUDIT),
    /**
     * The {@code createdBy} column in the {@code city} data table.
     */
    CITY_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.CITY, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdate} column in the {@code city} data table.
     */
    CITY_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.CITY, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdateBy} column in the {@code city} data table.
     */
    CITY_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.CITY, ColumnUsage.AUDIT),
    /**
     * The {@code countryId} column in the {@code country} data table.
     */
    COUNTRY_ID(DbName.COUNTRY_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.COUNTRY, ColumnUsage.PRIMARY_KEY),
    /**
     * The {@code createDate} column in the {@code country} data table.
     */
    COUNTRY_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.COUNTRY, ColumnUsage.AUDIT),
    /**
     * The {@code createdBy} column in the {@code country} data table.
     */
    COUNTRY_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.COUNTRY, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdate} column in the {@code country} data table.
     */
    COUNTRY_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.COUNTRY, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdateBy} column in the {@code country} data table.
     */
    COUNTRY_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.COUNTRY, ColumnUsage.AUDIT),
    /**
     * The {@code userId} column in the {@code user} data table.
     */
    USER_ID(DbName.USER_ID, ColumnType.AUTO_INCREMENT, 11, DbTable.USER, ColumnUsage.PRIMARY_KEY),
    /**
     * The {@code createDate} column in the {@code user} data table.
     */
    USER_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.USER, ColumnUsage.AUDIT),
    /**
     * The {@code createdBy} column in the {@code user} data table.
     */
    USER_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.USER, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdate} column in the {@code user} data table.
     */
    USER_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.USER, ColumnUsage.AUDIT),
    /**
     * The {@code lastUpdateBy} column in the {@code user} data table.
     */
    USER_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.USER, ColumnUsage.AUDIT);

    private final DbName dbName;
    private final ColumnType type;
    private final String defaultAlias;
    private final DbTable table;
    private final int maxLength;
    private final ColumnUsage usage;
    private final ReadOnlyList<ForeignKey> foreignKeys;

    private DbColumn(DbName dbName, ColumnType type, String alias, int maxLength, DbTable table, ColumnUsage usage, ForeignKey ...foreignKeys) {
        this.dbName = dbName;
        this.defaultAlias = alias;
        this.type = type;
        this.maxLength = maxLength;
        this.table = table;
        this.usage = usage;
        this.foreignKeys = (null == foreignKeys) ? ReadOnlyList.empty() : ReadOnlyList.of(foreignKeys);
    }

    private DbColumn(DbName dbName, ColumnType type, int maxLength, DbTable table, ColumnUsage usage, ForeignKey ...foreignKeys) {
        this(dbName, type, dbName.getValue(), maxLength, table, usage, foreignKeys);
    }

    private DbColumn(DbName dbName, ColumnType type, DbTable table, ColumnUsage usage, ForeignKey ...foreignKeys) {
        this(dbName, type, -1, table, usage, foreignKeys);
    }

    /**
     * Gets the database name of the table.
     * 
     * @return The database name of the table.
     */
    public DbName getDbName() {
        return dbName;
    }

    /**
     * Gets the column type.
     * 
     * @return The {@link ColumnType}.
     */
    public ColumnType getType() {
        return type;
    }

    /**
     * Gets the default alias for this column.
     * 
     * @return The default column alias for use in SQL query strings.
     */
    public String getDefaultAlias() {
        return defaultAlias;
    }

    /**
     * Gets the table to which this column belongs.
     * 
     * @return The {@link DbTable} that the current column belongs to.
     */
    public DbTable getTable() {
        return table;
    }

    /**
     * Gets the maximum length of values for this column.
     * A value of {@code -1} indicates that there is no maximum value or it is not applicable.
     * 
     * @return The maximum length of values for this column or {@code -1} if not applicable.
     */
    public int getMaxLength() {
        return maxLength;
    }
    
    /**
     * Calculates the maximum value for this column.
     * A value of {@code -1} indicates that there is no maximum value or it is not applicable.
     * 
     * @return The maximum value for this column or {@code -1} if not applicable.
     */
    public int calculateMaxValue() {
        if (maxLength > 0 && type.getValueType() == ValueType.INT)
            return (int)Math.pow(10, maxLength);
        return -1;
    }

    /**
     * Specifies general purpose of the column.
     * 
     * @return A {@link ColumnUsage} value that indicates the general purpose of the column.
     */
    public ColumnUsage getUsage() {
        return usage;
    }

    /**
     * Gets foreign key relationships for the current column.
     * 
     * @return A {@link ReadOnlyList} of {@link ForeignKey} objects that define the foreign key relationships for the current column.
     */
    public ReadOnlyList<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    /**
     * Tests whether another column is the target of a foreign key relationship with the current.
     * 
     * @param other The other {@link DbColumn} to test.
     * @return {@code true} if the {@code other} column in referenced in any of the elements the {@link #foreignKeys} list; otherwise, false;
     */
    public boolean isForeignKeyTarget(DbColumn other) {
        DbTable o = other.table;
        DbName n = other.dbName;
        return foreignKeys.stream().anyMatch((t) -> t.getTable() == o && t.getColumnName() == n);
    }
    
    @Override
    public String toString() {
        return defaultAlias;
    }
    
}
