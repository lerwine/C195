package scheduler.events;

/**
 * Represents a database operation type. This is primarily used by the {@link OperationRequestEvent} object.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum DbOperationType {
    /**
     * Insert a new {@link scheduler.model.DataRecord} into the database.
     * <ul>
     * <li>{@link AppointmentSuccessEvent#INSERT_EVENT_NAME}</li>
     * <li>{@link AppointmentFailedEvent#INSERT_FAILED}</li>
     * <li>{@link CustomerSuccessEvent#INSERT_EVENT_NAME}</li>
     * <li>{@link CustomerFailedEvent#INSERT_FAILED}</li>
     * <li>{@link AddressSuccessEvent#INSERT_EVENT_NAME}</li>
     * <li>{@link AddressFailedEvent#INSERT_FAILED}</li>
     * <li>{@link CitySuccessEvent#INSERT_EVENT_NAME}</li>
     * <li>{@link CityFailedEvent#INSERT_FAILED}</li>
     * <li>{@link CountrySuccessEvent#INSERT_EVENT_NAME}</li>
     * <li>{@link CountryFailedEvent#INSERT_FAILED}</li>
     * <li>{@link UserSuccessEvent#INSERT_EVENT_NAME}</li>
     * <li>{@link UserFailedEvent#INSERT_FAILED}</li>
     * </ul>
     */
    DB_INSERT,
    /**
     * Changes to a {@link scheduler.model.DataRecord} has been saved to the database.
     * <ul>
     * <li>{@link AppointmentSuccessEvent#UPDATE_EVENT_NAME}</li>
     * <li>{@link AppointmentFailedEvent#UPDATE_FAILED}</li>
     * <li>{@link CustomerSuccessEvent#UPDATE_EVENT_NAME}</li>
     * <li>{@link CustomerFailedEvent#UPDATE_FAILED}</li>
     * <li>{@link AddressSuccessEvent#UPDATE_EVENT_NAME}</li>
     * <li>{@link AddressFailedEvent#UPDATE_FAILED}</li>
     * <li>{@link CitySuccessEvent#UPDATE_EVENT_NAME}</li>
     * <li>{@link CityFailedEvent#UPDATE_FAILED}</li>
     * <li>{@link CountrySuccessEvent#UPDATE_EVENT_NAME}</li>
     * <li>{@link CountryFailedEvent#UPDATE_FAILED}</li>
     * <li>{@link UserSuccessEvent#UPDATE_EVENT_NAME}</li>
     * <li>{@link UserFailedEvent#UPDATE_FAILED}</li>
     * </ul>
     */
    DB_UPDATE,
    /**
     * A {@link scheduler.model.DataRecord} has been deleted from the database.
     * <ul>
     * <li>{@link AppointmentOpRequestEvent#DELETE_REQUEST}</li>
     * <li>{@link CustomerOpRequestEvent#DELETE_REQUEST}</li>
     * <li>{@link AddressOpRequestEvent#DELETE_REQUEST}</li>
     * <li>{@link CityOpRequestEvent#DELETE_REQUEST}</li>
     * <li>{@link CountryOpRequestEvent#DELETE_REQUEST}</li>
     * <li>{@link UserOpRequestEvent#DELETE_REQUEST}</li>
     * <li>{@link AppointmentSuccessEvent#DELETE_EVENT_NAME}</li>
     * <li>{@link AppointmentFailedEvent#DELETE_FAILED}</li>
     * <li>{@link CustomerSuccessEvent#DELETE_EVENT_NAME}</li>
     * <li>{@link CustomerFailedEvent#DELETE_FAILED}</li>
     * <li>{@link AddressSuccessEvent#DELETE_EVENT_NAME}</li>
     * <li>{@link AddressFailedEvent#DELETE_FAILED}</li>
     * <li>{@link CitySuccessEvent#DELETE_EVENT_NAME}</li>
     * <li>{@link CityFailedEvent#DELETE_FAILED}</li>
     * <li>{@link CountrySuccessEvent#DELETE_EVENT_NAME}</li>
     * <li>{@link CountryFailedEvent#DELETE_FAILED}</li>
     * <li>{@link UserSuccessEvent#DELETE_EVENT_NAME}</li>
     * <li>{@link UserFailedEvent#DELETE_FAILED}</li>
     * </ul>
     */
    DB_DELETE,
    /**
     * No (any) database operation.
     * <ul>
     * <li>{@link AppointmentOpRequestEvent#EDIT_REQUEST}</li>
     * <li>{@link CustomerOpRequestEvent#EDIT_REQUEST}</li>
     * <li>{@link AddressOpRequestEvent#EDIT_REQUEST}</li>
     * <li>{@link CityOpRequestEvent#EDIT_REQUEST}</li>
     * <li>{@link CountryOpRequestEvent#EDIT_REQUEST}</li>
     * <li>{@link UserOpRequestEvent#EDIT_REQUEST}</li>
     * </ul>
     */
    NONE;
}
