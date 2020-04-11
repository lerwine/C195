package scheduler.dao.schema;

import scheduler.util.ReadOnlyList;

/**
 * Refers to {@link DbTable} columns defined in the application database schema.
 * <p>
 * This provides constant naming definitions as well as values for programmatic compatibility checking. {@link SchemaHelper} can be used to get column
 * reference information.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum DbColumn {
    /**
     * The {@code "customerId"} column in the {@link DbTable#APPOINTMENT} data table.
     * <p>
     * Refers to the {@link DbTable#CUSTOMER} table using the {@link DbColumn#CUSTOMER_ID} column.</p>
     */
    APPOINTMENT_CUSTOMER(DbName.CUSTOMER_ID, ColumnType.INT, 10, DbTable.APPOINTMENT, ColumnCategory.FOREIGN_KEY,
            new ForeignKey("appointment_ibfk_1", DbTable.CUSTOMER, DbName.CUSTOMER_ID)),
    /**
     * The {@code "customerName"} column in the {@link DbTable#CUSTOMER} data table.
     */
    CUSTOMER_NAME(DbName.CUSTOMER_NAME, ColumnType.VARCHAR, 45, DbTable.CUSTOMER, ColumnCategory.UNIQUE_KEY),
    /**
     * The {@code "addressId"} column in the {@link DbTable#CUSTOMER} data table.
     * <p>
     * Refers to the {@link DbTable#ADDRESS} table using the {@link DbColumn#ADDRESS_ID} column.</p>
     */
    CUSTOMER_ADDRESS(DbName.ADDRESS_ID, ColumnType.INT, 10, DbTable.CUSTOMER, ColumnCategory.FOREIGN_KEY,
            new ForeignKey("customer_ibfk_1", DbTable.ADDRESS, DbName.ADDRESS_ID)),
    /**
     * The {@code "address"} column in the {@link DbTable#ADDRESS} data table.
     */
    ADDRESS1(DbName.ADDRESS, ColumnType.VARCHAR, 50, DbTable.ADDRESS, ColumnCategory.DATA),
    /**
     * The {@code "address1"} column in the {@link DbTable#ADDRESS} data table.
     */
    ADDRESS2(DbName.ADDRESS2, ColumnType.VARCHAR, 50, DbTable.ADDRESS, ColumnCategory.DATA),
    /**
     * The {@code "cityId"} column in the {@link DbTable#ADDRESS} data table.
     * <p>
     * Refers to the {@link DbTable#CITY} table using the {@link DbColumn#CITY_ID} column.</p>
     */
    ADDRESS_CITY(DbName.CITY_ID, ColumnType.INT, 10, DbTable.ADDRESS, ColumnCategory.FOREIGN_KEY,
            new ForeignKey("address_ibfk_1", DbTable.CITY, DbName.CITY_ID)),
    /**
     * The {@code "city"} column in the {@link DbTable#CITY} data table.
     */
    CITY_NAME(DbName.CITY, ColumnType.VARCHAR, 50, DbTable.CITY, ColumnCategory.DATA),
    /**
     * The {@code "countryId"} column in the {@link DbTable#CITY} data table.
     * <p>
     * Refers to the {@link DbTable#COUNTRY} table using the {@link DbColumn#COUNTRY_ID} column.</p>
     */
    CITY_COUNTRY(DbName.COUNTRY_ID, ColumnType.INT, 10, DbTable.CITY, ColumnCategory.FOREIGN_KEY,
            new ForeignKey("address_ibfk_1", DbTable.COUNTRY, DbName.COUNTRY_ID)),
    /**
     * The {@code "country"} column in the {@link DbTable#COUNTRY} data table.
     */
    COUNTRY_NAME(DbName.COUNTRY, ColumnType.VARCHAR, 50, DbTable.COUNTRY, ColumnCategory.DATA),
    /**
     * The {@code "postalCode"} column in the {@link DbTable#ADDRESS} data table.
     */
    POSTAL_CODE(DbName.POSTAL_CODE, ColumnType.VARCHAR, 10, DbTable.ADDRESS, ColumnCategory.DATA),
    /**
     * The {@code "phone"} column in the {@link DbTable#ADDRESS} data table.
     */
    PHONE(DbName.PHONE, ColumnType.VARCHAR, 20, DbTable.ADDRESS, ColumnCategory.DATA),
    /**
     * The {@code "active"} column in the {@link DbTable#CUSTOMER} data table.
     */
    ACTIVE(DbName.ACTIVE, ColumnType.TINYINT_BOOLEAN, DbTable.CUSTOMER, ColumnCategory.DATA),
    /**
     * The {@code "userId"} column in the {@link DbTable#CUSTOMER} data table.
     * <p>
     * Refers to the {@link DbTable#USER} table using the {@link DbColumn#USER_ID} column.</p>
     */
    APPOINTMENT_USER(DbName.USER_ID, ColumnType.INT, 11, DbTable.APPOINTMENT, ColumnCategory.FOREIGN_KEY,
            new ForeignKey("appointment_ibfk_1", DbTable.USER, DbName.USER_ID)),
    /**
     * The {@code "userName"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    USER_NAME(DbName.USER_NAME, ColumnType.VARCHAR, 50, DbTable.USER, ColumnCategory.UNIQUE_KEY),
    /**
     * The {@code "password"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    PASSWORD(DbName.PASSWORD, ColumnType.VARCHAR_PWD_HASH, 50, DbTable.USER, ColumnCategory.CRYPTO_HASH),
    /**
     * The {@code "active"} column in the {@link DbTable#APPOINTMENT} data table, usually referred to by the {@code "status"} alias.
     */
    STATUS(DbName.ACTIVE, ColumnType.TINYINT, "status", 4, DbTable.USER, ColumnCategory.DATA),
    /**
     * The {@code "title"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    TITLE(DbName.TITLE, ColumnType.VARCHAR, 255, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "description"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    DESCRIPTION(DbName.DESCRIPTION, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "location"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    LOCATION(DbName.LOCATION, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "contact"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    CONTACT(DbName.CONTACT, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "type"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    TYPE(DbName.TYPE, ColumnType.TEXT, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "url"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    URL(DbName.URL, ColumnType.VARCHAR, 255, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "start"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    START(DbName.START, ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "end"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    END(DbName.END, ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnCategory.DATA),
    /**
     * The {@code "appointmentId"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    APPOINTMENT_ID(DbName.APPOINTMENT_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.APPOINTMENT, ColumnCategory.PRIMARY_KEY),
    /**
     * The {@code "createDate"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    APPOINTMENT_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnCategory.AUDIT),
    /**
     * The {@code "createdBy"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    APPOINTMENT_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.APPOINTMENT, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdate"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    APPOINTMENT_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.APPOINTMENT, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdateBy"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    APPOINTMENT_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.APPOINTMENT, ColumnCategory.AUDIT),
    /**
     * The {@code "customerId"} column in the {@link DbTable#APPOINTMENT} data table.
     */
    CUSTOMER_ID(DbName.CUSTOMER_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.CUSTOMER, ColumnCategory.PRIMARY_KEY),
    /**
     * The {@code "createDate"} column in the {@link DbTable#CUSTOMER} data table.
     */
    CUSTOMER_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.CUSTOMER, ColumnCategory.AUDIT),
    /**
     * The {@code "createdBy"} column in the {@link DbTable#CUSTOMER} data table.
     */
    CUSTOMER_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.CUSTOMER, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdate"} column in the {@link DbTable#CUSTOMER} data table.
     */
    CUSTOMER_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.CUSTOMER, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdateBy"} column in the {@link DbTable#CUSTOMER} data table.
     */
    CUSTOMER_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.CUSTOMER, ColumnCategory.AUDIT),
    /**
     * The {@code "addressId"} column in the {@link DbTable#ADDRESS} data table.
     */
    ADDRESS_ID(DbName.ADDRESS_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.ADDRESS, ColumnCategory.PRIMARY_KEY),
    /**
     * The {@code "createDate"} column in the {@link DbTable#ADDRESS} data table.
     */
    ADDRESS_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.ADDRESS, ColumnCategory.AUDIT),
    /**
     * The {@code "createdBy"} column in the {@link DbTable#ADDRESS} data table.
     */
    ADDRESS_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.ADDRESS, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdate"} column in the {@link DbTable#ADDRESS} data table.
     */
    ADDRESS_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.ADDRESS, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdateBy"} column in the {@link DbTable#ADDRESS} data table.
     */
    ADDRESS_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.ADDRESS, ColumnCategory.AUDIT),
    /**
     * The {@code "cityId"} column in the {@link DbTable#CITY} data table.
     */
    CITY_ID(DbName.CITY_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.CITY, ColumnCategory.PRIMARY_KEY),
    /**
     * The {@code "createDate"} column in the {@link DbTable#CITY} data table.
     */
    CITY_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.CITY, ColumnCategory.AUDIT),
    /**
     * The {@code "createdBy"} column in the {@link DbTable#CITY} data table.
     */
    CITY_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.CITY, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdate"} column in the {@link DbTable#CITY} data table.
     */
    CITY_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.CITY, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdateBy"} column in the {@link DbTable#CITY} data table.
     */
    CITY_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.CITY, ColumnCategory.AUDIT),
    /**
     * The {@code "countryId"} column in the {@link DbTable#COUNTRY} data table.
     */
    COUNTRY_ID(DbName.COUNTRY_ID, ColumnType.AUTO_INCREMENT, 10, DbTable.COUNTRY, ColumnCategory.PRIMARY_KEY),
    /**
     * The {@code "createDate"} column in the {@link DbTable#COUNTRY} data table.
     */
    COUNTRY_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.COUNTRY, ColumnCategory.AUDIT),
    /**
     * The {@code "createdBy"} column in the {@link DbTable#COUNTRY} data table.
     */
    COUNTRY_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.COUNTRY, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdate"} column in the {@link DbTable#COUNTRY} data table.
     */
    COUNTRY_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.COUNTRY, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdateBy"} column in the {@link DbTable#COUNTRY} data table.
     */
    COUNTRY_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.COUNTRY, ColumnCategory.AUDIT),
    /**
     * The {@code "userId"} column in the {@link DbTable#USER} data table.
     */
    USER_ID(DbName.USER_ID, ColumnType.AUTO_INCREMENT, 11, DbTable.USER, ColumnCategory.PRIMARY_KEY),
    /**
     * The {@code "createDate"} column in the {@link DbTable#USER} data table.
     */
    USER_CREATE_DATE(DbName.CREATE_DATE, ColumnType.DATETIME, DbTable.USER, ColumnCategory.AUDIT),
    /**
     * The {@code "createdBy"} column in the {@link DbTable#USER} data table.
     */
    USER_CREATED_BY(DbName.CREATED_BY, ColumnType.VARCHAR, 40, DbTable.USER, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdate"} column in the {@link DbTable#USER} data table.
     */
    USER_LAST_UPDATE(DbName.LAST_UPDATE, ColumnType.TIMESTAMP, DbTable.USER, ColumnCategory.AUDIT),
    /**
     * The {@code "lastUpdateBy"} column in the {@link DbTable#USER} data table.
     */
    USER_LAST_UPDATE_BY(DbName.LAST_UPDATE_BY, ColumnType.VARCHAR, 40, DbTable.USER, ColumnCategory.AUDIT);

    private final DbName dbName;
    private final ColumnType type;
    private final String defaultAlias;
    private final DbTable table;
    private final int maxLength;
    private final ColumnCategory usageCategory;
    private final ReadOnlyList<ForeignKey> foreignKeys;

    private DbColumn(DbName dbName, ColumnType type, String alias, int maxLength, DbTable table, ColumnCategory usage, ForeignKey... foreignKeys) {
        this.dbName = dbName;
        this.defaultAlias = alias;
        this.type = type;
        this.maxLength = maxLength;
        this.table = table;
        this.usageCategory = usage;
        this.foreignKeys = (null == foreignKeys) ? ReadOnlyList.empty() : ReadOnlyList.of(foreignKeys);
    }

    private DbColumn(DbName dbName, ColumnType type, int maxLength, DbTable table, ColumnCategory usage, ForeignKey... foreignKeys) {
        this(dbName, type, dbName.toString(), maxLength, table, usage, foreignKeys);
    }

    private DbColumn(DbName dbName, ColumnType type, DbTable table, ColumnCategory usage, ForeignKey... foreignKeys) {
        this(dbName, type, -1, table, usage, foreignKeys);
    }

    /**
     * Gets the name of the column.
     * <p>
     * This refers to the name of the column in the database schema definition.</p>
     *
     * @return The {@link DbName} value that refers to the name of the column as defined in the database schema.
     */
    public DbName getDbName() {
        return dbName;
    }

    /**
     * Gets the database column type.
     * <p>
     * This is for checking compatibility with database column types. Refer to {@link ColumnType#getValueType()} for Java type compatibility.</p>
     *
     * @return The {@link ColumnType} value that refers to the database column type.
     */
    public ColumnType getType() {
        return type;
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
     * <p>
     * A value of {@code -1} indicates that there is no maximum length specified or it is not applicable.</p>
     *
     * @return The maximum length of values for this column or {@code -1} if not applicable.
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Calculates the maximum value for this column.
     * <p>
     * This is calculated by raising {@code 10} to the power of the column's {@link #maxLength}. A value of {@code -1} indicates that there is no
     * maximum length specified from which the maximum value could be calculated.</p>
     *
     * @return The maximum value for this column or {@code -1} if not applicable.
     */
    public int calculateMaxValue() {
        if (maxLength > 0 && type.getValueType() == ValueType.INT) {
            return (int) Math.pow(10, maxLength);
        }
        return -1;
    }

    /**
     * Gets the usage category for this column.
     *
     * @return A {@link ColumnCategory} value that indicates the usage category of the column.
     */
    public ColumnCategory getUsageCategory() {
        return usageCategory;
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

    /**
     * Gets the default alias for this column.
     *
     * @return The default column alias for use in SQL query strings.
     */
    @Override
    public String toString() {
        return defaultAlias;
    }

}
