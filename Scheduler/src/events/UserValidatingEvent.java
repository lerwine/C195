package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;


public final class UserValidatingEvent extends DbValidatingEvent<UserModel, UserDAO> {

    /**
     * Base {@link EventType} for all {@code UserValidatingEvent}s.
     */
    public static final EventType<UserValidatingEvent> USER_DB_VALIDATING = new EventType<>(DbValidatingEvent.DB_VALIDATING_EVENT, "SCHEDULER_USER_DB_VALIDATING");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code UserValidatingEvent}s.
     */
    public static final EventType<UserValidatingEvent> _VALIDATING_INSERT = new EventType<>(UserValidatingEvent.USER_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code UserValidatingEvent}s.
     */
    public static final EventType<UserValidatingEvent> _VALIDATING_UPDATE = new EventType<>(UserValidatingEvent.USER_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code UserValidatingEvent}s.
     */
    public static final EventType<UserValidatingEvent> _VALIDATING_DELETE = new EventType<>(UserValidatingEvent.USER_DB_VALIDATING, "SCHEDULER_");

    static final EventType<UserValidatingEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _VALIDATING_INSERT;
            case DELETE:
                return _VALIDATING_DELETE;
            default:
                return _VALIDATING_UPDATE;
        }
    }
    
    UserValidatingEvent(UserBeginOpEvent precedingEvent, EventType<UserValidatingEvent> newType) {
        super(precedingEvent, newType);
    }

    private UserValidatingEvent(UserValidatingEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public UserValidatingEvent(Object source, EventTarget target, UserModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public UserValidatingEvent(Object source, EventTarget target, UserDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public UserValidatingEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserValidatingEvent(this, newSource, newTarget);
    }

    @Override
    public UserOpCompletedEvent toOperationCompletedEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserValidatingEvent#toOperationCompletedEvent
    }

    @Override
    public UserOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserValidatingEvent#toOperationFaultEvent
    }

    @Override
    public UserOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserValidatingEvent#toOperationCanceledEvent
    }
    
}
