package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;


public final class AppointmentOpFailureEvent extends OperationFailureEvent<AppointmentModel, AppointmentDAO> {

    /**
     * Base {@link EventType} for all {@code AppointmentOpFailureEvent}s.
     */
    public static final EventType<AppointmentOpFailureEvent> APPOINTMENT_OP_FAILURE = new EventType<>(OperationFailureEvent.OP_FAILURE_EVENT, "SCHEDULER_APPOINTMENT_OP_FAILURE");
    
    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AppointmentOpFailureEvent}s.
     */
    public static final EventType<AppointmentOpFailureEvent> _INSERT_FAILURE = new EventType<>(AppointmentOpFailureEvent.APPOINTMENT_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AppointmentOpFailureEvent}s.
     */
    public static final EventType<AppointmentOpFailureEvent> _UPDATE_FAILURE = new EventType<>(AppointmentOpFailureEvent.APPOINTMENT_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AppointmentOpFailureEvent}s.
     */
    public static final EventType<AppointmentOpFailureEvent> _DELETE_FAILURE = new EventType<>(AppointmentOpFailureEvent.APPOINTMENT_OP_FAILURE, "SCHEDULER_");

    static final EventType<AppointmentOpFailureEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_FAILURE;
            case DELETE:
                return _DELETE_FAILURE;
            default:
                return _UPDATE_FAILURE;
        }
    }
    
    private AppointmentOpFailureEvent(AppointmentOpFailureEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AppointmentOpFailureEvent(Object source, EventTarget target, AppointmentModel model, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), model, fault, operation, message);
    }

    public AppointmentOpFailureEvent(Object source, EventTarget target, AppointmentModel model, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), model, operation, canceled, message);
    }

    public AppointmentOpFailureEvent(Object source, EventTarget target, AppointmentDAO dao, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), dao, fault, operation, message);
    }

    public AppointmentOpFailureEvent(Object source, EventTarget target, AppointmentDAO dao, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), dao, operation, canceled, message);
    }

    AppointmentOpFailureEvent(AppointmentBeginOpEvent failedEvent, EventType<AppointmentOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    AppointmentOpFailureEvent(AppointmentValidatingEvent failedEvent, EventType<AppointmentOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    @Override
    public AppointmentOpFailureEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentOpFailureEvent(this, newSource, newTarget);
    }
    
}
