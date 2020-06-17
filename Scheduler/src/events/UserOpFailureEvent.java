package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;


public class UserOpFailureEvent extends OperationFailureEvent<UserModel, UserDAO> {

    /**
     * Base {@link EventType} for all {@code UserOpFailureEvent}s.
     */
    public static final EventType<UserOpFailureEvent> USER_OP_FAILURE = new EventType<>(OperationFailureEvent.OP_FAILURE_EVENT, "SCHEDULER_USER_OP_FAILURE");
    
    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code UserOpFailureEvent}s.
     */
    public static final EventType<UserOpFailureEvent> _INSERT_FAILURE = new EventType<>(UserOpFailureEvent.USER_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code UserOpFailureEvent}s.
     */
    public static final EventType<UserOpFailureEvent> _UPDATE_FAILURE = new EventType<>(UserOpFailureEvent.USER_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code UserOpFailureEvent}s.
     */
    public static final EventType<UserOpFailureEvent> _DELETE_FAILURE = new EventType<>(UserOpFailureEvent.USER_OP_FAILURE, "SCHEDULER_");

    static final EventType<UserOpFailureEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_FAILURE;
            case DELETE:
                return _DELETE_FAILURE;
            default:
                return _UPDATE_FAILURE;
        }
    }
    
    private UserOpFailureEvent(OperationFailureEvent<UserModel, UserDAO> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public UserOpFailureEvent(Object source, EventTarget target, UserModel model, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), model, fault, operation, message);
    }

    public UserOpFailureEvent(Object source, EventTarget target, UserModel model, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), model, operation, canceled, message);
    }

    public UserOpFailureEvent(Object source, EventTarget target, UserDAO dao, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), dao, fault, operation, message);
    }

    public UserOpFailureEvent(Object source, EventTarget target, UserDAO dao, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), dao, operation, canceled, message);
    }

    UserOpFailureEvent(UserBeginOpEvent failedEvent, EventType<OperationFailureEvent<UserModel, UserDAO>> eventType) {
        super(failedEvent, eventType);
    }

    UserOpFailureEvent(UserValidatingEvent failedEvent, EventType<OperationFailureEvent<UserModel, UserDAO>> eventType) {
        super(failedEvent, eventType);
    }

    @Override
    public UserOpFailureEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserOpFailureEvent(this, newSource, newTarget);
    }
    
}
