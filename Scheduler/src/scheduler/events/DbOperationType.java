package scheduler.events;

/**
 * Represents a database operation type. This is primarily used by the {@link DbOperationEvent} object.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum DbOperationType {
    /**
     * A new {@link scheduler.model.DataRecord} has been inserted into the database.
     * <ul>
     * <li>{@link AppointmentEvent#DB_INSERT_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#DB_INSERT_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#DB_INSERT_EVENT_TYPE}</li>
     * <li>{@link CityEvent#DB_INSERT_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#DB_INSERT_EVENT_TYPE}</li>
     * <li>{@link UserEvent#DB_INSERT_EVENT_TYPE}</li>
     * </ul>
     */
    DB_INSERT,
    /**
     * Changes to a {@link scheduler.model.DataRecord} has been saved to the database.
     * <ul>
     * <li>{@link AppointmentEvent#UPDATED_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#UPDATED_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#UPDATED_EVENT_TYPE}</li>
     * <li>{@link CityEvent#UPDATED_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#UPDATED_EVENT_TYPE}</li>
     * <li>{@link UserEvent#UPDATED_EVENT_TYPE}</li>
     * </ul>
     */
    DB_UPDATE,
    /**
     * A {@link scheduler.model.DataRecord} has been deleted from the database.
     * <ul>
     * <li>{@link AppointmentEvent#DB_DELETE_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#DB_DELETE_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#DB_DELETE_EVENT_TYPE}</li>
     * <li>{@link CityEvent#DB_DELETE_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#DB_DELETE_EVENT_TYPE}</li>
     * <li>{@link UserEvent#DB_DELETE_EVENT_TYPE}</li>
     * </ul>
     */
    DB_DELETE,
    /**
     * No (any) database operation.
     * <ul>
     * <li>{@link AppointmentEvent#APPOINTMENT_MODEL_EVENT}</li>
     * <li>{@link CustomerEvent#CUSTOMER_MODEL_EVENT}</li>
     * <li>{@link AddressEvent#ADDRESS_MODEL_EVENT_TYPE}</li>
     * <li>{@link CityEvent#CITY_MODEL_EVENT}</li>
     * <li>{@link CountryEvent#COUNTRY_MODEL_EVENT}</li>
     * <li>{@link UserEvent#USER_MODEL_EVENT_TYPE}</li>
     * </ul>
     */
    NONE;
}
