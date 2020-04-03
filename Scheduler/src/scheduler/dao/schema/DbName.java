package scheduler.dao.schema;

/**
 * Names used by the database schema for tables and columns.
 * <p>
 * These are constant values that can be used to programmatically refer to {@link DbColumn#dbName}, {@link DbTable#dbName} and
 * {@link DbTable#pkColName}
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum DbName {
    /**
     * The database schema name for the {@link DbTable#CUSTOMER} data table.
     */
    CUSTOMER("customer"),
    /**
     * The database schema name for the {@link DbColumn#CUSTOMER_ID} and {@link DbColumn#APPOINTMENT_CUSTOMER} columns.
     */
    CUSTOMER_ID("customerId"),
    /**
     * The database schema name for the {@link DbColumn#CUSTOMER_NAME} column.
     */
    CUSTOMER_NAME("customerName"),
    /**
     * The database schema name for the {@link DbColumn#ADDRESS_ID} and {@link DbColumn#CUSTOMER_ADDRESS} columns.
     */
    ADDRESS_ID("addressId"),
    /**
     * The database schema name for the {@link DbTable#ADDRESS} table and the {@link DbColumn#ADDRESS1} column.
     */
    ADDRESS("address"),
    /**
     * The database schema name for the {@link DbColumn#ADDRESS2} column.
     */
    ADDRESS2("address2"),
    /**
     * The database schema name for the {@link DbColumn#CITY_ID} and {@link DbColumn#ADDRESS_CITY} columns.
     */
    CITY_ID("cityId"),
    /**
     * The database schema name for the {@link DbTable#CITY} table and the {@link DbColumn#CITY_NAME} column.
     */
    CITY("city"),
    /**
     * The database schema name for the {@link DbColumn#COUNTRY_ID} and {@link DbColumn#CITY_COUNTRY} columns.
     */
    COUNTRY_ID("countryId"),
    /**
     * The database schema name for the {@link DbTable#COUNTRY} table and the {@link DbColumn#COUNTRY_NAME} column.
     */
    COUNTRY("country"),
    /**
     * The database schema name for the {@link DbColumn#POSTAL_CODE} column.
     */
    POSTAL_CODE("postalCode"),
    /**
     * The database schema name for the {@link DbColumn#PHONE} column.
     */
    PHONE("phone"),
    /**
     * The database schema name for the {@link DbColumn#ACTIVE} and {@link DbColumn#STATUS} columns.
     */
    ACTIVE("active"),
    /**
     * The database schema name for the {@link DbTable#USER} data table.
     */
    USER("user"),
    /**
     * The database schema name for the {@link DbColumn#USER_ID} and {@link DbColumn#APPOINTMENT_USER} columns.
     */
    USER_ID("userId"),
    /**
     * The database schema name for the {@link DbColumn#USER_NAME} column.
     */
    USER_NAME("userName"),
    /**
     * The database schema name for the {@link DbColumn#PASSWORD} column.
     */
    PASSWORD("password"),
    /**
     * The database schema name for the {@link DbColumn#TITLE} column.
     */
    TITLE("title"),
    /**
     * The database schema name for the {@link DbColumn#DESCRIPTION} column.
     */
    DESCRIPTION("description"),
    /**
     * The database schema name for the {@link DbColumn#LOCATION} column.
     */
    LOCATION("location"),
    /**
     * The database schema name for the {@link DbColumn#CONTACT} column.
     */
    CONTACT("contact"),
    /**
     * The database schema name for the {@link DbColumn#TYPE} column.
     */
    TYPE("type"),
    /**
     * The database schema name for the {@link DbColumn#URL} column.
     */
    URL("url"),
    /**
     * The database schema name for the {@link DbColumn#START} column.
     */
    START("start"),
    /**
     * The database schema name for the {@link DbColumn#END} column.
     */
    END("end"),
    /**
     * The database schema name for the {@link DbTable#APPOINTMENT} data table.
     */
    APPOINTMENT("appointment"),
    /**
     * The database schema name for the {@link DbColumn#APPOINTMENT_ID} column.
     */
    APPOINTMENT_ID("appointmentId"),
    /**
     * The database schema name for the {@link DbColumn#APPOINTMENT_CREATE_DATE}, {@link DbColumn#CUSTOMER_CREATE_DATE},
     * {@link DbColumn#ADDRESS_CREATE_DATE}, {@link DbColumn#CITY_CREATE_DATE}, {@link DbColumn#COUNTRY_CREATE_DATE} and
     * {@link DbColumn#USER_CREATE_DATE} columns.
     */
    CREATE_DATE("createDate"),
    /**
     * The database schema name for the {@link DbColumn#APPOINTMENT_CREATED_BY}, {@link DbColumn#CUSTOMER_CREATED_BY},
     * {@link DbColumn#ADDRESS_CREATED_BY}, {@link DbColumn#CITY_CREATED_BY}, {@link DbColumn#COUNTRY_CREATED_BY} and {@link DbColumn#USER_CREATED_BY}
     * columns.
     */
    CREATED_BY("createdBy"),
    /**
     * The database schema name for the {@link DbColumn#APPOINTMENT_LAST_UPDATE}, {@link DbColumn#CUSTOMER_LAST_UPDATE},
     * {@link DbColumn#ADDRESS_LAST_UPDATE}, {@link DbColumn#CITY_LAST_UPDATE}, {@link DbColumn#COUNTRY_LAST_UPDATE} and
     * {@link DbColumn#USER_LAST_UPDATE} columns.
     */
    LAST_UPDATE("lastUpdate"),
    /**
     * The database schema name for the {@link DbColumn#APPOINTMENT_LAST_UPDATE_BY}, {@link DbColumn#CUSTOMER_LAST_UPDATE}_BY,
     * {@link DbColumn#ADDRESS_LAST_UPDATE_BY}, {@link DbColumn#CITY_LAST_UPDATE_BY}, {@link DbColumn#COUNTRY_LAST_UPDATE_BY} and
     * {@link DbColumn#USER_LAST_UPDATE_BY} columns.
     */
    LAST_UPDATE_BY("lastUpdateBy");

    private final String value;

    private DbName(String value) {
        this.value = value;
    }

    /**
     * Gets the schema name associated with the enumerated value.
     *
     * @return The schema name associated with the enumerated value.
     */
    @Override
    public String toString() {
        return value;
    }

}
