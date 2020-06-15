package scheduler.view.event;

/**
 * Represents a database operation type. This is primarily used by the {@link ModelItemEvent} object.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
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
     */
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
     */
    DELETE_REQUEST,
    /**
     * Pending database insert for a {@link scheduler.model.DataRecord}.
     * <ul>
     * <li>{@link AppointmentEvent#INSERTING_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#INSERTING_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#INSERTING_EVENT_TYPE}</li>
     * <li>{@link CityEvent#INSERTING_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#INSERTING_EVENT_TYPE}</li>
     * <li>{@link UserEvent#INSERTING_EVENT_TYPE}</li>
     * </ul>
     */
    INSERTING,
    /**
     * A new {@link scheduler.model.DataRecord} has been inserted into the database.
     * <ul>
     * <li>{@link AppointmentEvent#INSERTED_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#INSERTED_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#INSERTED_EVENT_TYPE}</li>
     * <li>{@link CityEvent#INSERTED_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#INSERTED_EVENT_TYPE}</li>
     * <li>{@link UserEvent#INSERTED_EVENT_TYPE}</li>
     * </ul>
     */
    INSERTED,
    /**
     * Pending database update for a {@link scheduler.model.DataRecord}.
     * <ul>
     * <li>{@link AppointmentEvent#UPDATING_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#UPDATING_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#UPDATING_EVENT_TYPE}</li>
     * <li>{@link CityEvent#UPDATING_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#UPDATING_EVENT_TYPE}</li>
     * <li>{@link UserEvent#UPDATING_EVENT_TYPE}</li>
     * </ul>
     */
    UPDATING,
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
    UPDATED,
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
     */
    DELETING,
    /**
     * A {@link scheduler.model.DataRecord} has been deleted from the database.
     * <ul>
     * <li>{@link AppointmentEvent#DELETED_EVENT_TYPE}</li>
     * <li>{@link CustomerEvent#DELETED_EVENT_TYPE}</li>
     * <li>{@link AddressEvent#DELETED_EVENT_TYPE}</li>
     * <li>{@link CityEvent#DELETED_EVENT_TYPE}</li>
     * <li>{@link CountryEvent#DELETED_EVENT_TYPE}</li>
     * <li>{@link UserEvent#DELETED_EVENT_TYPE}</li>
     * </ul>
     */
    DELETED,
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
