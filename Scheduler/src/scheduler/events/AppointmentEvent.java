package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link AppointmentModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentEvent extends DbOperationEvent<AppointmentModel, AppointmentDAO> {

    private static final long serialVersionUID = -1145658585716643269L;

    public static final String APPOINTMENT_MODEL_EVENT_NAME = "SCHEDULER_APPOINTMENT_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_APPOINTMENT_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETE_REQUEST";
    public static final String INSERTING_EVENT_NAME = "SCHEDULER_APPOINTMENT_INSERTING";
    public static final String INSERTED_EVENT_NAME = "SCHEDULER_APPOINTMENT_INSERTED";
    public static final String UPDATING_EVENT_NAME = "SCHEDULER_APPOINTMENT_UPDATING";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_APPOINTMENT_UPDATED";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETING";
    public static final String DELETED_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETED";

    /**
     * Base {@link EventType} for all {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> APPOINTMENT_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, APPOINTMENT_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTING} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> INSERTING_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, INSERTING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTED} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> INSERTED_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, INSERTED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATING} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> UPDATING_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, UPDATING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATED} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> UPDATED_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETING} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> DELETING_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETED} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> DELETED_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, DELETED_EVENT_NAME);

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
    public static EventType<AppointmentEvent> toEventType(DbOperationType operation) {
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
        return APPOINTMENT_MODEL_EVENT_TYPE;
    }

    private AppointmentEvent(AppointmentEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    private AppointmentEvent(AppointmentEvent copyFrom, EventTarget target, DbOperationType operation) {
        super(copyFrom, target, Objects.requireNonNull(toEventType(operation)), operation);
    }

    public AppointmentEvent(AppointmentModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AppointmentEvent(AppointmentModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public AppointmentEvent(Object source, EventTarget target, AppointmentDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AppointmentEvent(Object source, EventTarget target, AppointmentDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public FxRecordModel.ModelFactory<AppointmentDAO, AppointmentModel, ? extends DbOperationEvent<AppointmentModel, AppointmentDAO>> getModelFactory() {
        return AppointmentModel.FACTORY;
    }

    @Override
    public synchronized AppointmentEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AppointmentEvent> getEventType() {
        return (EventType<AppointmentEvent>) super.getEventType();
    }

    @Override
    public AppointmentEvent toDbOperationType(DbOperationType operation) {
        return new AppointmentEvent(this, getTarget(), operation);
    }

}
