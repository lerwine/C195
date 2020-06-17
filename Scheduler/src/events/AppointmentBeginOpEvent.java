package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;


public final class AppointmentBeginOpEvent extends BeginOperationEvent<AppointmentModel, AppointmentDAO> {

    /**
     * Base {@link EventType} for all {@code AppointmentBeginOpEvent}s.
     */
    public static final EventType<AppointmentBeginOpEvent> BEGIN_APPOINTMENT_OP = new EventType<>(BeginOperationEvent.BEGIN_OP_EVENT, "SCHEDULER_BEGIN_APPOINTMENT_OP");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AppointmentBeginOpEvent}s.
     */
    public static final EventType<AppointmentBeginOpEvent> BEGIN__INSERT = new EventType<>(AppointmentBeginOpEvent.BEGIN_APPOINTMENT_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AppointmentBeginOpEvent}s.
     */
    public static final EventType<AppointmentBeginOpEvent> BEGIN__UPDATE = new EventType<>(AppointmentBeginOpEvent.BEGIN_APPOINTMENT_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AppointmentBeginOpEvent}s.
     */
    public static final EventType<AppointmentBeginOpEvent> BEGIN__DELETE = new EventType<>(AppointmentBeginOpEvent.BEGIN_APPOINTMENT_OP, "SCHEDULER_");

    static final EventType<AppointmentBeginOpEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return BEGIN__INSERT;
            case DELETE:
                return BEGIN__DELETE;
            default:
                return BEGIN__UPDATE;
        }
    }
    
    AppointmentBeginOpEvent(AppointmentEditRequestEvent precedingEvent, EventType<AppointmentBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    AppointmentBeginOpEvent(AppointmentDeleteRequestEvent precedingEvent, EventType<AppointmentBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    private AppointmentBeginOpEvent(AppointmentBeginOpEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AppointmentBeginOpEvent(Object source, EventTarget target, EventType<AppointmentBeginOpEvent> eventType, AppointmentModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public AppointmentBeginOpEvent(Object source, EventTarget target, EventType<AppointmentBeginOpEvent> eventType, AppointmentDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public AppointmentBeginOpEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentBeginOpEvent(this, newSource, newTarget);
    }

    @Override
    public AppointmentValidatingEvent toDbValidatingEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentBeginOpEvent#toDbValidatingEvent
    }

    @Override
    public AppointmentOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentBeginOpEvent#toOperationFaultEvent
    }

    @Override
    public AppointmentOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentBeginOpEvent#toOperationCanceledEvent
    }
    
}
