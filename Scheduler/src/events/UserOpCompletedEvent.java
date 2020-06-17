package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;


public class UserOpCompletedEvent extends OperationCompletedEvent<UserModel, UserDAO> {

    /**
     * Base {@link EventType} for all {@code UserOpCompletedEvent}s.
     */
    public static final EventType<UserOpCompletedEvent> USER_OP_COMPLETED = new EventType<>(OperationCompletedEvent.OP_COMPLETED_EVENT, "SCHEDULER_USER_OP_COMPLETED");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@link DbOperationType#INSERT} {@code UserOpCompletedEvent}s.
     */
    public static final EventType<UserOpCompletedEvent> _INSERT_COMPLETED = new EventType<>(UserOpCompletedEvent.USER_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code UserOpCompletedEvent}s.
     */
    public static final EventType<UserOpCompletedEvent> _UPDATE_COMPLETED = new EventType<>(UserOpCompletedEvent.USER_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code UserOpCompletedEvent}s.
     */
    public static final EventType<UserOpCompletedEvent> _DELETE_COMPLETED = new EventType<>(UserOpCompletedEvent.USER_OP_COMPLETED, "SCHEDULER_");

    static final EventType<UserOpCompletedEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_COMPLETED;
            case DELETE:
                return _DELETE_COMPLETED;
            default:
                return _UPDATE_COMPLETED;
        }
    }
    
    UserOpCompletedEvent(UserBeginOpEvent skippedEvent, EventType<UserOpCompletedEvent> eventType) {
        super(skippedEvent, eventType);
    }

    UserOpCompletedEvent(UserValidatingEvent validatedEvent, EventType<UserOpCompletedEvent> eventType) {
        super(validatedEvent, eventType);
    }

    private UserOpCompletedEvent(UserOpCompletedEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public UserOpCompletedEvent(Object source, EventTarget target, UserModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public UserOpCompletedEvent(Object source, EventTarget target, UserDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public UserOpCompletedEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserOpCompletedEvent(this, newSource, newTarget);
    }
    
}
