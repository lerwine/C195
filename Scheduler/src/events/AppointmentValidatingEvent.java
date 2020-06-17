package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;


public final class AppointmentValidatingEvent extends DbValidatingEvent<AppointmentModel, AppointmentDAO> {

    /**
     * Base {@link EventType} for all {@code AppointmentValidatingEvent}s.
     */
    public static final EventType<AppointmentValidatingEvent> APPOINTMENT_DB_VALIDATING = new EventType<>(DbValidatingEvent.DB_VALIDATING_EVENT, "SCHEDULER_APPOINTMENT_DB_VALIDATING");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AppointmentValidatingEvent}s.
     */
    public static final EventType<AppointmentValidatingEvent> _VALIDATING_INSERT = new EventType<>(AppointmentValidatingEvent.APPOINTMENT_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AppointmentValidatingEvent}s.
     */
    public static final EventType<AppointmentValidatingEvent> _VALIDATING_UPDATE = new EventType<>(AppointmentValidatingEvent.APPOINTMENT_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AppointmentValidatingEvent}s.
     */
    public static final EventType<AppointmentValidatingEvent> _VALIDATING_DELETE = new EventType<>(AppointmentValidatingEvent.APPOINTMENT_DB_VALIDATING, "SCHEDULER_");

    static final EventType<AppointmentValidatingEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _VALIDATING_INSERT;
            case DELETE:
                return _VALIDATING_DELETE;
            default:
                return _VALIDATING_UPDATE;
        }
    }
    
    AppointmentValidatingEvent(AppointmentBeginOpEvent precedingEvent, EventType<AppointmentValidatingEvent> newType) {
        super(precedingEvent, newType);
    }

    private AppointmentValidatingEvent(AppointmentValidatingEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AppointmentValidatingEvent(Object source, EventTarget target, AppointmentModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public AppointmentValidatingEvent(Object source, EventTarget target, AppointmentDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public AppointmentValidatingEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentValidatingEvent(this, newSource, newTarget);
    }

    @Override
    public AppointmentOpCompletedEvent toOperationCompletedEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentValidatingEvent#toOperationCompletedEvent
    }

    @Override
    public AppointmentOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentValidatingEvent#toOperationFaultEvent
    }

    @Override
    public AppointmentOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentValidatingEvent#toOperationCanceledEvent
    }
    
}
