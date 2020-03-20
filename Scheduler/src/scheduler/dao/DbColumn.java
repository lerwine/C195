package scheduler.dao;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author lerwi
 */
public enum DbColumn {
    APPOINTMENT_CUSTOMER("customerId", DbColType.INT, 10, TableName.APPOINTMENT),
    CUSTOMER_NAME("customerName", DbColType.VARCHAR, 45, TableName.CUSTOMER),
    CUSTOMER_ADDRESS("addressId", DbColType.INT, 10, TableName.CUSTOMER),
    ADDRESS1("address", DbColType.VARCHAR, 50, TableName.ADDRESS),
    ADDRESS2("address2", DbColType.VARCHAR, 50, TableName.ADDRESS),
    ADDRESS_CITY("cityId", DbColType.INT, 10, TableName.ADDRESS),
    CITY_NAME("city", DbColType.VARCHAR, 50, TableName.CITY),
    CITY_COUNTRY("countryId", DbColType.INT, 10, TableName.CITY),
    COUNTRY_NAME("country", DbColType.VARCHAR, 50, TableName.COUNTRY),
    POSTAL_CODE("postalCode", DbColType.VARCHAR, 10, TableName.ADDRESS),
    PHONE("phone", DbColType.VARCHAR, 20, TableName.ADDRESS),
    ACTIVE("active", DbColType.TINYINT_BOOLEAN, TableName.CUSTOMER),
    APPOINTMENT_USER("userId", DbColType.INT, 11, TableName.APPOINTMENT),
    USER_NAME("userName", DbColType.VARCHAR, 50, TableName.USER),
    PASSWORD("password", DbColType.VARCHAR_PWD_HASH, 50, TableName.USER),
    STATUS("active", DbColType.TINYINT, "status", 4, TableName.USER),
    TITLE("title", DbColType.VARCHAR, 255, TableName.APPOINTMENT),
    DESCRIPTION("description", DbColType.TEXT, TableName.APPOINTMENT),
    LOCATION("location", DbColType.TEXT, TableName.APPOINTMENT),
    CONTACT("contact", DbColType.TEXT, TableName.APPOINTMENT),
    TYPE("type", DbColType.TEXT, TableName.APPOINTMENT),
    URL("url", DbColType.VARCHAR, 255, TableName.APPOINTMENT),
    START("start", DbColType.DATETIME, TableName.APPOINTMENT),
    END("end", DbColType.DATETIME, TableName.APPOINTMENT),
    APPOINTMENT_ID("appointmentId", DbColType.AUTO_INCREMENT, 10, TableName.APPOINTMENT),
    APPOINTMENT_CREATE_DATE("createDate", DbColType.DATETIME, TableName.APPOINTMENT, true),
    APPOINTMENT_CREATED_BY("createdBy", DbColType.VARCHAR, 40, TableName.APPOINTMENT, true),
    APPOINTMENT_LAST_UPDATE("lastUpdate", DbColType.TIMESTAMP, TableName.APPOINTMENT, true),
    APPOINTMENT_LAST_UPDATE_BY("lastUpdateBy", DbColType.VARCHAR, 40, TableName.APPOINTMENT, true),
    CUSTOMER_ID("customerId", DbColType.AUTO_INCREMENT, 10, TableName.CUSTOMER),
    CUSTOMER_CREATE_DATE("createDate", DbColType.DATETIME, TableName.CUSTOMER, true),
    CUSTOMER_CREATED_BY("createdBy", DbColType.VARCHAR, 40, TableName.CUSTOMER, true),
    CUSTOMER_LAST_UPDATE("lastUpdate", DbColType.TIMESTAMP, TableName.CUSTOMER, true),
    CUSTOMER_LAST_UPDATE_BY("lastUpdateBy", DbColType.VARCHAR, 40, TableName.CUSTOMER, true),
    ADDRESS_ID("addressId", DbColType.AUTO_INCREMENT, 10, TableName.ADDRESS),
    ADDRESS_CREATE_DATE("createDate", DbColType.DATETIME, TableName.ADDRESS, true),
    ADDRESS_CREATED_BY("createdBy", DbColType.VARCHAR, 40, TableName.ADDRESS, true),
    ADDRESS_LAST_UPDATE("lastUpdate", DbColType.TIMESTAMP, TableName.ADDRESS, true),
    ADDRESS_LAST_UPDATE_BY("lastUpdateBy", DbColType.VARCHAR, 40, TableName.ADDRESS, true),
    CITY_ID("cityId", DbColType.AUTO_INCREMENT, 10, TableName.CITY),
    CITY_CREATE_DATE("createDate", DbColType.DATETIME, TableName.CITY, true),
    CITY_CREATED_BY("createdBy", DbColType.VARCHAR, 40, TableName.CITY, true),
    CITY_LAST_UPDATE("lastUpdate", DbColType.TIMESTAMP, TableName.CITY, true),
    CITY_LAST_UPDATE_BY("lastUpdateBy", DbColType.VARCHAR, 40, TableName.CITY, true),
    COUNTRY_ID("countryId", DbColType.AUTO_INCREMENT, 10, TableName.COUNTRY),
    COUNTRY_CREATE_DATE("createDate", DbColType.DATETIME, TableName.COUNTRY, true),
    COUNTRY_CREATED_BY("createdBy", DbColType.VARCHAR, 40, TableName.COUNTRY, true),
    COUNTRY_LAST_UPDATE("lastUpdate", DbColType.TIMESTAMP, TableName.COUNTRY, true),
    COUNTRY_LAST_UPDATE_BY("lastUpdateBy", DbColType.VARCHAR, 40, TableName.COUNTRY, true),
    USER_ID("userId", DbColType.AUTO_INCREMENT, 11, TableName.USER),
    USER_CREATE_DATE("createDate", DbColType.DATETIME, TableName.USER, true),
    USER_CREATED_BY("createdBy", DbColType.VARCHAR, 40, TableName.USER, true),
    USER_LAST_UPDATE("lastUpdate", DbColType.TIMESTAMP, TableName.USER, true),
    USER_LAST_UPDATE_BY("lastUpdateBy", DbColType.VARCHAR, 40, TableName.USER, true);

    public static Stream<DbColumn> getColumns(TableName tableName) {
        return Arrays.stream(DbColumn.values()).filter((t) -> t.getTable() == tableName);
    }

    public static Stream<DbColumn> getColumns(TableName tableName, Predicate<DbColumn> predicate) {
        return Arrays.stream(DbColumn.values()).filter((t) -> t.getTable() == tableName && predicate.test(t));
    }

    public static Stream<DbColumn> getColumns(Predicate<DbColumn> predicate) {
        return Arrays.stream(DbColumn.values()).filter(predicate);
    }

    private final String dbName;
    private final DbColType type;
    private final String alias;
    private final TableName table;
    private final int maxLength;
    private final boolean auditColumn;

    private DbColumn(String dbName, DbColType type, String alias, int maxLength, TableName table, boolean auditColumn) {
        if (null == dbName) {
            this.dbName = this.alias = name();
        } else {
            this.dbName = dbName;
            this.alias = alias;
        }
        this.type = type;
        this.maxLength = maxLength;
        this.table = table;
        this.auditColumn = auditColumn;
    }

    private DbColumn(String dbName, DbColType type, int maxLength, TableName table, boolean auditColumn) {
        this(dbName, type, dbName, maxLength, table, auditColumn);
    }

    private DbColumn(String dbName, DbColType type, String alias, int maxLength, TableName table) {
        this(dbName, type, alias, maxLength, table, false);
    }

    private DbColumn(String dbName, DbColType type, int maxLength, TableName table) {
        this(dbName, type, maxLength, table, false);
    }

    private DbColumn(String dbName, DbColType type, TableName table, boolean auditColumn) {
        this(dbName, type, -1, table, auditColumn);
    }

    private DbColumn(String dbName, DbColType type, TableName table) {
        this(dbName, type, table, false);
    }

    public String getDbName() {
        return dbName;
    }

    public DbColType getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    public TableName getTable() {
        return table;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isAuditColumn() {
        return auditColumn;
    }

}
