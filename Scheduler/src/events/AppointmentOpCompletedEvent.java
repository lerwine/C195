package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;


public final class AppointmentOpCompletedEvent extends OperationCompletedEvent<AppointmentModel, AppointmentDAO> {

    /**
     * Base {@link EventType} for all {@code AppointmentOpCompletedEvent}s.
     */
    public static final EventType<AppointmentOpCompletedEvent> APPOINTMENT_OP_COMPLETED = new EventType<>(OperationCompletedEvent.OP_COMPLETED_EVENT, "SCHEDULER_APPOINTMENT_OP_COMPLETED");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AppointmentOpCompletedEvent}s.
     */
    public static final EventType<AppointmentOpCompletedEvent> _INSERT_COMPLETED = new EventType<>(AppointmentOpCompletedEvent.APPOINTMENT_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AppointmentOpCompletedEvent}s.
     */
    public static final EventType<AppointmentOpCompletedEvent> _UPDATE_COMPLETED = new EventType<>(AppointmentOpCompletedEvent.APPOINTMENT_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AppointmentOpCompletedEvent}s.
     */
    public static final EventType<AppointmentOpCompletedEvent> _DELETE_COMPLETED = new EventType<>(AppointmentOpCompletedEvent.APPOINTMENT_OP_COMPLETED, "SCHEDULER_");

    static final EventType<AppointmentOpCompletedEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_COMPLETED;
            case DELETE:
                return _DELETE_COMPLETED;
            default:
                return _UPDATE_COMPLETED;
        }
    }
    
    AppointmentOpCompletedEvent(AppointmentBeginOpEvent skippedEvent, EventType<AppointmentOpCompletedEvent> eventType) {
        super(skippedEvent, eventType);
    }

    AppointmentOpCompletedEvent(AppointmentValidatingEvent validatedEvent, EventType<AppointmentOpCompletedEvent> eventType) {
        super(validatedEvent, eventType);
    }

    private AppointmentOpCompletedEvent(AppointmentOpCompletedEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AppointmentOpCompletedEvent(Object source, EventTarget target, AppointmentModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public AppointmentOpCompletedEvent(Object source, EventTarget target, AppointmentDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public AppointmentOpCompletedEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentOpCompletedEvent(this, newSource, newTarget);
    }
    
}
