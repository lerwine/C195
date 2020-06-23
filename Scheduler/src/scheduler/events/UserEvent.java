package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;

public abstract class UserEvent extends ModelEvent<UserDAO, UserModel> {

    private static final long serialVersionUID = -4220071150094259420L;

    /**
     * Base {@link EventType} for all {@code UserEvent}s.
     */
    public static final EventType<UserEvent> USER_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_USER_EVENT");

    /**
     * Base {@link EventType} for all operational {@code UserEvent}s.
     */
    public static final EventType<UserEvent> OP_EVENT_TYPE = new EventType<>(USER_EVENT_TYPE, "SCHEDULER_USER_OP_EVENT");

    protected UserEvent(UserEvent event, Object source, EventTarget target, EventType<? extends UserEvent> eventType) {
        super(event, source, target, eventType);
    }

    protected UserEvent(UserEvent event, EventType<? extends UserEvent> eventType) {
        super(event, eventType);
    }

    protected UserEvent(UserModel fxRecordModel, Object source, EventTarget target, EventType<? extends UserEvent> eventType) {
        super(fxRecordModel, source, target, eventType);
    }

    protected UserEvent(UserDAO dao, Object source, EventTarget target, EventType<? extends UserEvent> eventType) {
        super(dao, source, target, eventType);
    }

}
