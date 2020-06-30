package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.OperationFailureException;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.AppointmentModel;

/**
 * Base {@link ModelEvent} for appointment events.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr;
 * {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link #CHANGE_EVENT_TYPE "SCHEDULER_APPOINTMENT_OP_EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link AppointmentSuccessEvent#BASE_EVENT_NAME "SCHEDULER_APPOINTMENT_SUCCESS_EVENT"}</dt>
 * <dd>&rarr; {@link AppointmentSuccessEvent}</dd>
 * <dt>(inherit) {@link AppointmentFailedEvent#BASE_EVENT_NAME "SCHEDULER_APPOINTMENT_FAILED_EVENT"}</dt>
 * <dd>&rarr; {@link AppointmentFailedEvent}</dd>
 * </dl>
 * </dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class AppointmentEvent extends ModelEvent<AppointmentDAO, AppointmentModel> {

    private static final long serialVersionUID = -3677443789026319836L;

    /**
     * Base {@link EventType} for all {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> APPOINTMENT_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_APPOINTMENT_EVENT");

    /**
     * Base {@link EventType} for all change {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> CHANGE_EVENT_TYPE = new EventType<>(APPOINTMENT_EVENT_TYPE, "SCHEDULER_APPOINTMENT_CHANGE_EVENT");

    public static final boolean isSuccess(AppointmentEvent event) {
        return event instanceof AppointmentSuccessEvent;
    }

    public static final boolean isInvalid(AppointmentEvent event) {
        return event instanceof AppointmentFailedEvent && ((AppointmentFailedEvent) event).getFailKind() == FailKind.INVALID;
    }

    public static final boolean isCanceled(AppointmentEvent event) {
        return event instanceof AppointmentFailedEvent && ((AppointmentFailedEvent) event).getFailKind() == FailKind.CANCELED;
    }

    public static final boolean isFaulted(AppointmentEvent event) {
        return event instanceof AppointmentFailedEvent && ((AppointmentFailedEvent) event).getFailKind() == FailKind.FAULT;
    }

    public static final AppointmentEvent createInsertSuccessEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source) {
        return new AppointmentSuccessEvent(target, source, AppointmentSuccessEvent.INSERT_SUCCESS);
    }

    public static final AppointmentEvent createUpdateSuccessEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source) {
        return new AppointmentSuccessEvent(target, source, AppointmentSuccessEvent.UPDATE_SUCCESS);
    }

    public static final AppointmentEvent createDeleteSuccessEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source) {
        return new AppointmentSuccessEvent(target, source, AppointmentSuccessEvent.DELETE_SUCCESS);
    }

    public static final AppointmentEvent createInsertInvalidEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, String message) {
        return new AppointmentFailedEvent(target, message, null, source, AppointmentFailedEvent.INSERT_INVALID);
    }

    public static final AppointmentEvent createUpdateInvalidEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, String message) {
        return new AppointmentFailedEvent(target, message, null, source, AppointmentFailedEvent.UPDATE_INVALID);
    }

    public static final AppointmentEvent createDeleteInvalidEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, String message) {
        return new AppointmentFailedEvent(target, message, null, source, AppointmentFailedEvent.DELETE_INVALID);
    }

    public static final AppointmentEvent createInsertFaultedEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new AppointmentFailedEvent(target, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_FAULTED);
        }
        return new AppointmentFailedEvent(target, null, ex, source, AppointmentFailedEvent.INSERT_INVALID);
    }

    public static final AppointmentEvent createUpdateFaultedEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new AppointmentFailedEvent(target, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_FAULTED);
        }
        return new AppointmentFailedEvent(target, null, ex, source, AppointmentFailedEvent.UPDATE_FAULTED);
    }

    public static final AppointmentEvent createDeleteFaultedEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new AppointmentFailedEvent(target, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_FAULTED);
        }
        return new AppointmentFailedEvent(target, null, ex, source, AppointmentFailedEvent.DELETE_FAULTED);
    }

    public static final AppointmentEvent createInsertCanceledEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new AppointmentFailedEvent(target, null, null, source, AppointmentFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AppointmentFailedEvent(target, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_CANCELED);
        }
        return new AppointmentFailedEvent(target, ex.getMessage(), ex, source, AppointmentFailedEvent.INSERT_CANCELED);
    }

    public static final AppointmentEvent createUpdateCanceledEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new AppointmentFailedEvent(target, null, null, source, AppointmentFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AppointmentFailedEvent(target, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_CANCELED);
        }
        return new AppointmentFailedEvent(target, ex.getMessage(), ex, source, AppointmentFailedEvent.UPDATE_CANCELED);
    }

    public static final AppointmentEvent createDeleteCanceledEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new AppointmentFailedEvent(target, null, null, source, AppointmentFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AppointmentFailedEvent(target, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_CANCELED);
        }
        return new AppointmentFailedEvent(target, ex.getMessage(), ex, source, AppointmentFailedEvent.DELETE_CANCELED);
    }

    public static final AppointmentEvent createInsertCanceledEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final AppointmentEvent createUpdateCanceledEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source) {
        return createUpdateCanceledEvent(target, source, null);
    }

    public static final AppointmentEvent createDeleteCanceledEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source) {
        return createDeleteCanceledEvent(target, source, null);
    }

    public static AppointmentEvent createInsertInvalidEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, CustomerFailedEvent event) {
        return new AppointmentFailedEvent(target, source, AppointmentFailedEvent.INSERT_INVALID, event);
    }

    public static AppointmentEvent createInsertInvalidEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, UserFailedEvent event) {
        return new AppointmentFailedEvent(target, source, AppointmentFailedEvent.INSERT_INVALID, event);
    }

    public static AppointmentEvent createUpdateInvalidEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, CustomerFailedEvent event) {
        return new AppointmentFailedEvent(target, source, AppointmentFailedEvent.UPDATE_INVALID, event);
    }

    public static AppointmentEvent createUpdateInvalidEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, UserFailedEvent event) {
        return new AppointmentFailedEvent(target, source, AppointmentFailedEvent.UPDATE_INVALID, event);
    }

    protected AppointmentEvent(AppointmentEvent event, Object source, EventTarget target, EventType<? extends AppointmentEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected AppointmentEvent(AppointmentEvent event, EventType<? extends AppointmentEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected AppointmentEvent(RecordModelContext<AppointmentDAO, AppointmentModel> target, Object source, EventType<? extends AppointmentEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
