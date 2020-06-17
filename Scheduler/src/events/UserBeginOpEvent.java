package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;


public final class UserBeginOpEvent extends BeginOperationEvent<UserModel, UserDAO> {

    /**
     * Base {@link EventType} for all {@code UserBeginOpEvent}s.
     */
    public static final EventType<UserBeginOpEvent> BEGIN_USER_OP = new EventType<>(BeginOperationEvent.BEGIN_OP_EVENT, "SCHEDULER_BEGIN_USER_OP");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code UserBeginOpEvent}s.
     */
    public static final EventType<UserBeginOpEvent> BEGIN__INSERT = new EventType<>(UserBeginOpEvent.BEGIN_USER_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code UserBeginOpEvent}s.
     */
    public static final EventType<UserBeginOpEvent> BEGIN__UPDATE = new EventType<>(UserBeginOpEvent.BEGIN_USER_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code UserBeginOpEvent}s.
     */
    public static final EventType<UserBeginOpEvent> BEGIN__DELETE = new EventType<>(UserBeginOpEvent.BEGIN_USER_OP, "SCHEDULER_");

    static final EventType<UserBeginOpEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return BEGIN__INSERT;
            case DELETE:
                return BEGIN__DELETE;
            default:
                return BEGIN__UPDATE;
        }
    }
    
    UserBeginOpEvent(UserEditRequestEvent precedingEvent, EventType<UserBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    UserBeginOpEvent(UserDeleteRequestEvent precedingEvent, EventType<UserBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    private UserBeginOpEvent(UserBeginOpEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public UserBeginOpEvent(Object source, EventTarget target, UserModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public UserBeginOpEvent(Object source, EventTarget target, UserDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public UserBeginOpEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserBeginOpEvent(this, newSource, newTarget);
    }

    @Override
    public UserValidatingEvent toDbValidatingEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserBeginOpEvent#toDbValidatingEvent
    }

    @Override
    public UserOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserBeginOpEvent#toOperationFaultEvent
    }

    @Override
    public UserOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserBeginOpEvent#toOperationCanceledEvent
    }
    
}
