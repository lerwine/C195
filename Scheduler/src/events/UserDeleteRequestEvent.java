package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;


public final class UserDeleteRequestEvent extends ModelDeleteRequestEvent<UserModel, UserDAO> {

    /**
     * {@link EventType} for all {@code UserDeleteRequestEvent}s.
     */
    public static final EventType<UserDeleteRequestEvent> USER_DELETE_REQUEST = new EventType<>(ModelDeleteRequestEvent.DELETE_REQUEST_EVENT, "SCHEDULER_USER_DELETE_REQUEST");

    private UserDeleteRequestEvent(UserDeleteRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public UserDeleteRequestEvent(Object source, EventTarget target, EventType<UserDeleteRequestEvent> eventType, UserModel model) {
        super(source, target, eventType, model);
    }

    public UserDeleteRequestEvent(Object source, EventTarget target, EventType<UserDeleteRequestEvent> eventType, UserDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public UserDeleteRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserDeleteRequestEvent(this, newSource, newTarget);
    }

    @Override
    public UserBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserDeleteRequestEvent#toBeginOperationEvent
    }
    
}
