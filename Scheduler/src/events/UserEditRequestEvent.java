package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;


public final class UserEditRequestEvent extends ModelEditRequestEvent<UserModel, UserDAO> {

    /**
     * {@link EventType} for all {@code UserEditRequestEvent}s.
     */
    public static final EventType<UserEditRequestEvent> USER_EDIT_REQUEST = new EventType<>(ModelEditRequestEvent.EDIT_REQUEST_EVENT, "SCHEDULER_USER_EDIT_REQUEST");

    private UserEditRequestEvent(UserEditRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public UserEditRequestEvent(Object source, EventTarget target, EventType<UserEditRequestEvent> eventType, UserModel model) {
        super(source, target, eventType, model);
    }

    public UserEditRequestEvent(Object source, EventTarget target, EventType<UserEditRequestEvent> eventType, UserDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public UserEditRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserEditRequestEvent(this, newSource, newTarget);
    }

    @Override
    public UserBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.UserEditRequestEvent#toBeginOperationEvent
    }
    
}
