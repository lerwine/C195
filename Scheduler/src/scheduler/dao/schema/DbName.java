package scheduler.dao.schema;

/**
 * Names used for tables and columns in the database.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum DbName {
    CUSTOMER("customer"),
    CUSTOMER_ID("customerId"),
    CUSTOMER_NAME("customerName"),
    ADDRESS_ID("addressId"),
    ADDRESS("address"),
    ADDRESS2("address2"),
    CITY_ID("cityId"),
    CITY("city"),
    COUNTRY_ID("countryId"),
    COUNTRY("country"),
    POSTAL_CODE("postalCode"),
    PHONE("phone"),
    ACTIVE("active"),
    USER("user"),
    USER_ID("userId"),
    USER_NAME("userName"),
    PASSWORD("password"),
    TITLE("title"),
    DESCRIPTION("description"),
    LOCATION("location"),
    CONTACT("contact"),
    TYPE("type"),
    URL("url"),
    START("start"),
    END("end"),
    APPOINTMENT("appointment"),
    APPOINTMENT_ID("appointmentId"),
    CREATE_DATE("createDate"),
    CREATED_BY("createdBy"),
    LAST_UPDATE("lastUpdate"),
    LAST_UPDATE_BY("lastUpdateBy");
    
    private final String value;

    private DbName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    
}
