package events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;

/**
 * Event that is fired when a {@link AddressModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressEvent extends DbOperationEvent<AddressModel, AddressDAO> {

    private static final long serialVersionUID = 8261622802802373344L;
    public static final String ADDRESS_MODEL_EVENT_NAME = "SCHEDULER_ADDRESS_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_ADDRESS_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_ADDRESS_DELETE_REQUEST";
    public static final String INSERTING_EVENT_NAME = "SCHEDULER_ADDRESS_INSERTING";
    public static final String INSERTED_EVENT_NAME = "SCHEDULER_ADDRESS_INSERTED";
    public static final String UPDATING_EVENT_NAME = "SCHEDULER_ADDRESS_UPDATING";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_ADDRESS_UPDATED";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_ADDRESS_DELETING";
    public static final String DELETED_EVENT_NAME = "SCHEDULER_ADDRESS_DELETED";

    /**
     * Base {@link EventType} for all {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> ADDRESS_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, ADDRESS_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTING} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> INSERTING_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, INSERTING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTED} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> INSERTED_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, INSERTED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATING} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> UPDATING_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, UPDATING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATED} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> UPDATED_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETING} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> DELETING_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETED} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> DELETED_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETED_EVENT_NAME);

    public static DbOperationType toDbOperationType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return DbOperationType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return DbOperationType.DELETE_REQUEST;
                case INSERTING_EVENT_NAME:
                    return DbOperationType.INSERTING;
                case INSERTED_EVENT_NAME:
                    return DbOperationType.INSERTED;
                case UPDATING_EVENT_NAME:
                    return DbOperationType.UPDATING;
                case UPDATED_EVENT_NAME:
                    return DbOperationType.UPDATED;
                case DELETING_EVENT_NAME:
                    return DbOperationType.DELETING;
                case DELETED_EVENT_NAME:
                    return DbOperationType.DELETED;
            }
        }
        return DbOperationType.NONE;
    }

    @SuppressWarnings("incomplete-switch")
    public static EventType<AddressEvent> toEventType(DbOperationType operation) {
        if (null != operation) {
            switch (operation) {
                case EDIT_REQUEST:
                    return EDIT_REQUEST_EVENT_TYPE;
                case DELETE_REQUEST:
                    return DELETE_REQUEST_EVENT_TYPE;
                case INSERTING:
                    return INSERTING_EVENT_TYPE;
                case INSERTED:
                    return INSERTED_EVENT_TYPE;
                case UPDATING:
                    return UPDATING_EVENT_TYPE;
                case UPDATED:
                    return UPDATED_EVENT_TYPE;
                case DELETING:
                    return DELETING_EVENT_TYPE;
                case DELETED:
                    return DELETED_EVENT_TYPE;
            }
        }
        return ADDRESS_MODEL_EVENT_TYPE;
    }

    private AddressEvent(AddressEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public AddressEvent(AddressModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AddressEvent(AddressModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public AddressEvent(Object source, EventTarget target, AddressDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AddressEvent(Object source, EventTarget target, AddressDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public AddressModel.Factory getModelFactory() {
        return AddressModel.FACTORY;
    }

    @Override
    public synchronized AddressEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AddressEvent> getEventType() {
        return (EventType<AddressEvent>) super.getEventType();
    }

}
