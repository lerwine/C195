package scheduler.view.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;

/**
 * Event that is fired when a {@link UserModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserEvent extends ModelItemEvent<UserModel, UserDAO> {

    private static final long serialVersionUID = -4702962471823130721L;
    public static final String USER_MODEL_EVENT_NAME = "USER_MODEL_EVENT";

    public static final EventType<UserEvent> USER_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, USER_MODEL_EVENT_NAME);
    public static final String EDIT_REQUEST_EVENT_NAME = "USER_EDIT_REQUEST_EVENT";

    public static final EventType<UserEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, EDIT_REQUEST_EVENT_NAME);
    public static final String DELETE_REQUEST_EVENT_NAME = "USER_DELETE_REQUEST_EVENT";

    public static final EventType<UserEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, DELETE_REQUEST_EVENT_NAME);
    public static final String INSERTING_EVENT_NAME = "USER_INSERTING_EVENT";

    public static final EventType<UserEvent> INSERTING_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, INSERTING_EVENT_NAME);
    public static final String INSERTED_EVENT_NAME = "USER_INSERTED_EVENT";

    public static final EventType<UserEvent> INSERTED_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, INSERTED_EVENT_NAME);
    public static final String UPDATING_EVENT_NAME = "USER_UPDATING_EVENT";

    public static final EventType<UserEvent> UPDATING_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, UPDATING_EVENT_NAME);
    public static final String UPDATED_EVENT_NAME = "USER_UPDATED_EVENT";

    public static final EventType<UserEvent> UPDATED_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, UPDATED_EVENT_NAME);
    public static final String DELETING_EVENT_NAME = "USER_DELETING_EVENT";

    public static final EventType<UserEvent> DELETING_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, DELETING_EVENT_NAME);
    public static final String DELETED_EVENT_NAME = "USER_DELETED_EVENT";

    public static final EventType<UserEvent> DELETED_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT, DELETED_EVENT_NAME);

    public static ActivityType toActivityType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return ActivityType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return ActivityType.DELETE_REQUEST;
                case INSERTING_EVENT_NAME:
                    return ActivityType.INSERTING;
                case INSERTED_EVENT_NAME:
                    return ActivityType.INSERTED;
                case UPDATING_EVENT_NAME:
                    return ActivityType.UPDATING;
                case UPDATED_EVENT_NAME:
                    return ActivityType.UPDATED;
                case DELETING_EVENT_NAME:
                    return ActivityType.DELETING;
                case DELETED_EVENT_NAME:
                    return ActivityType.DELETED;
            }
        }
        return ActivityType.NONE;
    }

    @SuppressWarnings("incomplete-switch")
    public static EventType<UserEvent> toEventType(ActivityType action) {
        if (null != action) {
            switch (action) {
                case EDIT_REQUEST:
                    return EDIT_REQUEST_EVENT_TYPE;
                case DELETE_REQUEST:
                    return DELETE_REQUEST_EVENT_TYPE;
                case INSERTING:
                    return INSERTING_EVENT_TYPE;
                case INSERTED:
                    return INSERTED_EVENT_TYPE;
                case UPDATING:
                    return UPDATING_EVENT_TYPE;
                case UPDATED:
                    return UPDATED_EVENT_TYPE;
                case DELETING:
                    return DELETING_EVENT_TYPE;
                case DELETED:
                    return DELETED_EVENT_TYPE;
            }
        }
        return null;
    }

    private UserEvent(UserEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public UserEvent(UserModel model, Object source, EventTarget target, ActivityType action, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public UserEvent(UserModel model, Object source, EventTarget target, ActivityType action) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, false);
    }

    public UserEvent(Object source, EventTarget target, UserDAO dao, ActivityType action, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public UserEvent(Object source, EventTarget target, UserDAO dao, ActivityType action) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserModel.Factory getModelFactory() {
        return UserModel.FACTORY;
    }

    @Override
    public synchronized UserEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<UserEvent> getEventType() {
        return (EventType<UserEvent>) super.getEventType();
    }

}
