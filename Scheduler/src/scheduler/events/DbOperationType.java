package scheduler.events;

/**
 * Represents a database operation type. This is primarily used by the {@link DbOperationEvent} object.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @deprecated
 */
@Deprecated
public enum DbOperationType {
    /**
     * Request to open an {@link scheduler.model.ui.FxRecordModel} for editing.
     * <ul>
     * <li>{@link AppointmentEvent#EDIT_REQUEST_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#EDIT_REQUEST_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#EDIT_REQUEST_EVENT_TYPE}</li>
     * <li>{@link CityEvent#EDIT_REQUEST_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#EDIT_REQUEST_EVENT_TYPE}</li>
     * <li>{@link UserEvent#EDIT_REQUEST_EVENT_TYPE}</li>
     * </ul>
     * @deprecated
     */
    @Deprecated
    EDIT_REQUEST,
    /**
     * Request to open a confirmation dialog for deleting an {@link scheduler.model.ui.FxRecordModel}.
     * <ul>
     * <li>{@link AppointmentEvent#DELETE_REQUEST_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#DELETE_REQUEST_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#DELETE_REQUEST_EVENT_TYPE}</li>
     * <li>{@link CityEvent#DELETE_REQUEST_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#DELETE_REQUEST_EVENT_TYPE}</li>
     * <li>{@link UserEvent#DELETE_REQUEST_EVENT_TYPE}</li>
     * </ul>
     * @deprecated
     */
    @Deprecated
    DELETE_REQUEST,
    /**
     * Pending database insert for a {@link scheduler.model.DataRecord}.
     * <ul>
     * <li>{@link AppointmentEvent#INSERT_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#INSERT_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#INSERT_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link CityEvent#INSERT_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#INSERT_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link UserEvent#INSERT_VALIDATION_EVENT_TYPE}</li>
     * </ul>
     * @deprecated
     */
    @Deprecated
    INSERT_VALIDATION,
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
     * @deprecated
     */
    @Deprecated
    DB_INSERT,
    /**
     * Pending database update for a {@link scheduler.model.DataRecord}.
     * <ul>
     * <li>{@link AppointmentEvent#UPDATE_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#UPDATE_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#UPDATE_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link CityEvent#UPDATE_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#UPDATE_VALIDATION_EVENT_TYPE}</li>
     * <li>{@link UserEvent#UPDATE_VALIDATION_EVENT_TYPE}</li>
     * </ul>
     * @deprecated
     */
    @Deprecated
    UPDATE_VALIDATION,
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
     * @deprecated
     */
    @Deprecated
    DB_UPDATE,
    /**
     * Pending database delete for a {@link scheduler.model.DataRecord}.
     * <ul>
     * <li>{@link AppointmentEvent#DELETING_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#DELETING_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#DELETING_EVENT_TYPE}</li>
     * <li>{@link CityEvent#DELETING_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#DELETING_EVENT_TYPE}</li>
     * <li>{@link UserEvent#DELETING_EVENT_TYPE}</li>
     * </ul>
     * @deprecated
     */
    @Deprecated
    DELETE_VALIDATION,
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
     * @deprecated
     */
    @Deprecated
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
     * @deprecated
     */
    @Deprecated
    NONE;
}
