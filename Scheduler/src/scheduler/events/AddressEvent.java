package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.dao.OperationFailureException;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.AddressModel;

/**
 * Base {@link ModelEvent} for appointment events.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr; {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link #ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_OP_EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link AddressSuccessEvent#BASE_EVENT_NAME "SCHEDULER_ADDRESS_SUCCESS_EVENT"}</dt>
 * <dd>&rarr; {@link AddressSuccessEvent}</dd>
 * <dt>(inherit) {@link AddressFailedEvent#BASE_EVENT_NAME "SCHEDULER_ADDRESS_FAILED_EVENT"}</dt>
 * <dd>&rarr; {@link AddressFailedEvent}</dd>
 * </dl>
 * </dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class AddressEvent extends ModelEvent<AddressDAO, AddressModel> {

    private static final long serialVersionUID = -3650516330020602507L;

    /**
     * Base {@link EventType} for all {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> ADDRESS_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_ADDRESS_EVENT");

    /**
     * Base {@link EventType} for all change {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> CHANGE_EVENT_TYPE = new EventType<>(ADDRESS_EVENT_TYPE, "SCHEDULER_ADDRESS_CHANGE_EVENT");

    public static final boolean isSuccess(AddressEvent event) {
        return event instanceof AddressSuccessEvent;
    }

    public static final boolean isInvalid(AddressEvent event) {
        return event instanceof AddressFailedEvent && ((AddressFailedEvent) event).getFailKind() == FailKind.INVALID;
    }

    public static final boolean isCanceled(AddressEvent event) {
        return event instanceof AddressFailedEvent && ((AddressFailedEvent) event).getFailKind() == FailKind.CANCELED;
    }

    public static final boolean isFaulted(AddressEvent event) {
        return event instanceof AddressFailedEvent && ((AddressFailedEvent) event).getFailKind() == FailKind.FAULT;
    }

    public static final AddressEvent createInsertSuccessEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source) {
        return new AddressSuccessEvent(target, source, AddressSuccessEvent.INSERT_SUCCESS);
    }

    public static final AddressEvent createUpdateSuccessEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source) {
        return new AddressSuccessEvent(target, source, AddressSuccessEvent.UPDATE_SUCCESS);
    }

    public static final AddressEvent createDeleteSuccessEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source) {
        return new AddressSuccessEvent(target, source, AddressSuccessEvent.DELETE_SUCCESS);
    }

    public static final AddressEvent createInsertInvalidEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, String message) {
        return new AddressFailedEvent(target, message, null, source, AddressFailedEvent.INSERT_INVALID);
    }

    public static final AddressEvent createUpdateInvalidEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, String message) {
        return new AddressFailedEvent(target, message, null, source, AddressFailedEvent.UPDATE_INVALID);
    }

    public static final AddressEvent createDeleteInvalidEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, String message) {
        return new AddressFailedEvent(target, message, null, source, AddressFailedEvent.DELETE_INVALID);
    }

    public static final AddressEvent createInsertFaultedEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new AddressFailedEvent(target, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_FAULTED);
        }
        return new AddressFailedEvent(target, null, ex, source, AddressFailedEvent.INSERT_INVALID);
    }

    public static final AddressEvent createUpdateFaultedEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new AddressFailedEvent(target, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_FAULTED);
        }
        return new AddressFailedEvent(target, null, ex, source, AddressFailedEvent.UPDATE_FAULTED);
    }

    public static final AddressEvent createDeleteFaultedEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new AddressFailedEvent(target, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_FAULTED);
        }
        return new AddressFailedEvent(target, null, ex, source, AddressFailedEvent.DELETE_FAULTED);
    }

    public static final AddressEvent createInsertCanceledEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new AddressFailedEvent(target, null, null, source, AddressFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AddressFailedEvent(target, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_CANCELED);
        }
        return new AddressFailedEvent(target, ex.getMessage(), ex, source, AddressFailedEvent.INSERT_CANCELED);
    }

    public static final AddressEvent createUpdateCanceledEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new AddressFailedEvent(target, null, null, source, AddressFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AddressFailedEvent(target, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_CANCELED);
        }
        return new AddressFailedEvent(target, ex.getMessage(), ex, source, AddressFailedEvent.UPDATE_CANCELED);
    }

    public static final AddressEvent createDeleteCanceledEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new AddressFailedEvent(target, null, null, source, AddressFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AddressFailedEvent(target, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_CANCELED);
        }
        return new AddressFailedEvent(target, ex.getMessage(), ex, source, AddressFailedEvent.DELETE_CANCELED);
    }

    public static final AddressEvent createInsertCanceledEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final AddressEvent createUpdateCanceledEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source) {
        return createUpdateCanceledEvent(target, source, null);
    }

    public static final AddressEvent createDeleteCanceledEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source) {
        return createDeleteCanceledEvent(target, source, null);
    }

    public static AddressEvent createInsertInvalidEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, CityFailedEvent event) {
        return new AddressFailedEvent(target, source, AddressFailedEvent.INSERT_INVALID, event);
    }

    public static AddressEvent createUpdateInvalidEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, CityFailedEvent event) {
        return new AddressFailedEvent(target, source, AddressFailedEvent.UPDATE_INVALID, event);
    }

    protected AddressEvent(AddressEvent event, Object source, EventTarget target, EventType<? extends AddressEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected AddressEvent(AddressEvent event, EventType<? extends AddressEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected AddressEvent(RecordModelContext<AddressDAO, AddressModel> target, Object source, EventType<? extends AddressEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
