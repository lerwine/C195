package scheduler.dao.schema;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author lerwi
 */
public enum DbColumn {
    APPOINTMENT_CUSTOMER("customerId", ColumnType.INT, 10, DbTable.APPOINTMENT, ColumnUsage.FOREIGN_KEY),
    CUSTOMER_NAME("customerName", ColumnType.VARCHAR, 45, DbTable.CUSTOMER, ColumnUsage.UNIQUE_KEY),
    CUSTOMER_ADDRESS("addressId", ColumnType.INT, 10, DbTable.CUSTOMER, ColumnUsage.FOREIGN_KEY),
    ADDRESS1("address", ColumnType.VARCHAR, 50, DbTable.ADDRESS, ColumnUsage.OTHER),
    ADDRESS2("address2", ColumnType.VARCHAR, 50, DbTable.ADDRESS, ColumnUsage.OTHER),
    ADDRESS_CITY("cityId", ColumnType.INT, 10, DbTable.ADDRESS, ColumnUsage.FOREIGN_KEY),
    CITY_NAME("city", ColumnType.VARCHAR, 50, DbTable.CITY, ColumnUsage.OTHER),
    CITY_COUNTRY("countryId", ColumnType.INT, 10, DbTable.CITY, ColumnUsage.FOREIGN_KEY),
    COUNTRY_NAME("country", ColumnType.VARCHAR, 50, DbTable.COUNTRY, ColumnUsage.OTHER),
    POSTAL_CODE("postalCode", ColumnType.VARCHAR, 10, DbTable.ADDRESS, ColumnUsage.OTHER),
    PHONE("phone", ColumnType.VARCHAR, 20, DbTable.ADDRESS, ColumnUsage.OTHER),
    ACTIVE("active", ColumnType.TINYINT_BOOLEAN, DbTable.CUSTOMER, ColumnUsage.OTHER),
    APPOINTMENT_USER("userId", ColumnType.INT, 11, DbTable.APPOINTMENT, ColumnUsage.FOREIGN_KEY),
    USER_NAME("userName", ColumnType.VARCHAR, 50, DbTable.USER, ColumnUsage.UNIQUE_KEY),
    PASSWORD("password", ColumnType.VARCHAR_PWD_HASH, 50, DbTable.USER, ColumnUsage.CRYPTO_HASH),
    STATUS("active", ColumnType.TINYINT, "status", 4, DbTable.USER, ColumnUsage.OTHER),
    TITLE("title", ColumnType.VARCHAR, 255, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    DESCRIPTION("description", ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    LOCATION("location", ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    CONTACT("contact", ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    TYPE("type", ColumnType.TEXT, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    URL("url", ColumnType.VARCHAR, 255, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    START("start", ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    END("end", ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnUsage.OTHER),
    APPOINTMENT_ID("appointmentId", ColumnType.AUTO_INCREMENT, 10, DbTable.APPOINTMENT, ColumnUsage.PRIMARY_KEY),
    APPOINTMENT_CREATE_DATE("createDate", ColumnType.DATETIME, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    APPOINTMENT_CREATED_BY("createdBy", ColumnType.VARCHAR, 40, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    APPOINTMENT_LAST_UPDATE("lastUpdate", ColumnType.TIMESTAMP, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    APPOINTMENT_LAST_UPDATE_BY("lastUpdateBy", ColumnType.VARCHAR, 40, DbTable.APPOINTMENT, ColumnUsage.AUDIT),
    CUSTOMER_ID("customerId", ColumnType.AUTO_INCREMENT, 10, DbTable.CUSTOMER, ColumnUsage.PRIMARY_KEY),
    CUSTOMER_CREATE_DATE("createDate", ColumnType.DATETIME, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    CUSTOMER_CREATED_BY("createdBy", ColumnType.VARCHAR, 40, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    CUSTOMER_LAST_UPDATE("lastUpdate", ColumnType.TIMESTAMP, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    CUSTOMER_LAST_UPDATE_BY("lastUpdateBy", ColumnType.VARCHAR, 40, DbTable.CUSTOMER, ColumnUsage.AUDIT),
    ADDRESS_ID("addressId", ColumnType.AUTO_INCREMENT, 10, DbTable.ADDRESS, ColumnUsage.PRIMARY_KEY),
    ADDRESS_CREATE_DATE("createDate", ColumnType.DATETIME, DbTable.ADDRESS, ColumnUsage.AUDIT),
    ADDRESS_CREATED_BY("createdBy", ColumnType.VARCHAR, 40, DbTable.ADDRESS, ColumnUsage.AUDIT),
    ADDRESS_LAST_UPDATE("lastUpdate", ColumnType.TIMESTAMP, DbTable.ADDRESS, ColumnUsage.AUDIT),
    ADDRESS_LAST_UPDATE_BY("lastUpdateBy", ColumnType.VARCHAR, 40, DbTable.ADDRESS, ColumnUsage.AUDIT),
    CITY_ID("cityId", ColumnType.AUTO_INCREMENT, 10, DbTable.CITY, ColumnUsage.PRIMARY_KEY),
    CITY_CREATE_DATE("createDate", ColumnType.DATETIME, DbTable.CITY, ColumnUsage.AUDIT),
    CITY_CREATED_BY("createdBy", ColumnType.VARCHAR, 40, DbTable.CITY, ColumnUsage.AUDIT),
    CITY_LAST_UPDATE("lastUpdate", ColumnType.TIMESTAMP, DbTable.CITY, ColumnUsage.AUDIT),
    CITY_LAST_UPDATE_BY("lastUpdateBy", ColumnType.VARCHAR, 40, DbTable.CITY, ColumnUsage.AUDIT),
    COUNTRY_ID("countryId", ColumnType.AUTO_INCREMENT, 10, DbTable.COUNTRY, ColumnUsage.PRIMARY_KEY),
    COUNTRY_CREATE_DATE("createDate", ColumnType.DATETIME, DbTable.COUNTRY, ColumnUsage.AUDIT),
    COUNTRY_CREATED_BY("createdBy", ColumnType.VARCHAR, 40, DbTable.COUNTRY, ColumnUsage.AUDIT),
    COUNTRY_LAST_UPDATE("lastUpdate", ColumnType.TIMESTAMP, DbTable.COUNTRY, ColumnUsage.AUDIT),
    COUNTRY_LAST_UPDATE_BY("lastUpdateBy", ColumnType.VARCHAR, 40, DbTable.COUNTRY, ColumnUsage.AUDIT),
    USER_ID("userId", ColumnType.AUTO_INCREMENT, 11, DbTable.USER, ColumnUsage.PRIMARY_KEY),
    USER_CREATE_DATE("createDate", ColumnType.DATETIME, DbTable.USER, ColumnUsage.AUDIT),
    USER_CREATED_BY("createdBy", ColumnType.VARCHAR, 40, DbTable.USER, ColumnUsage.AUDIT),
    USER_LAST_UPDATE("lastUpdate", ColumnType.TIMESTAMP, DbTable.USER, ColumnUsage.AUDIT),
    USER_LAST_UPDATE_BY("lastUpdateBy", ColumnType.VARCHAR, 40, DbTable.USER, ColumnUsage.AUDIT);

    public static Stream<DbColumn> getColumns(DbTable tableName) {
        return Arrays.stream(DbColumn.values()).filter((t) -> t.getTable() == tableName);
    }

    public static Stream<DbColumn> getColumns(DbTable tableName, Predicate<DbColumn> predicate) {
        return Arrays.stream(DbColumn.values()).filter((t) -> 
                t.getTable() == tableName && 
                        predicate.test(t));
    }

    public static Stream<DbColumn> getColumns(Predicate<DbColumn> predicate) {
        return Arrays.stream(DbColumn.values()).filter(predicate);
    }

    private final String dbName;
    private final ColumnType type;
    private final String alias;
    private final DbTable table;
    private final int maxLength;
    private final ColumnUsage usage;
    private final boolean auditColumn;

    private DbColumn(String dbName, ColumnType type, String alias, int maxLength, DbTable table, ColumnUsage usage) {
        if (null == dbName) {
            this.dbName = this.alias = name();
        } else {
            this.dbName = dbName;
            this.alias = alias;
        }
        this.type = type;
        this.maxLength = maxLength;
        this.table = table;
        this.usage = usage;
        this.auditColumn = usage == ColumnUsage.AUDIT;
    }

    private DbColumn(String dbName, ColumnType type, int maxLength, DbTable table, ColumnUsage usage) {
        this(dbName, type, dbName, maxLength, table, usage);
    }

    private DbColumn(String dbName, ColumnType type, DbTable table, ColumnUsage usage) {
        this(dbName, type, -1, table, usage);
    }

    public String getDbName() {
        return dbName;
    }

    public ColumnType getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    public DbTable getTable() {
        return table;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public ColumnUsage getUsage() {
        return usage;
    }

    public boolean isAuditColumn() {
        return auditColumn;
    }

    public static boolean isEntityData(DbColumn column) {
        switch (column.usage) {
            case CRYPTO_HASH:
            case OTHER:
            case UNIQUE_KEY:
                return true;
        }
        return false;
    }
}
