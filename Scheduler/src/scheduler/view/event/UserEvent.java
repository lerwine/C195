package scheduler.view.event;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserModel;

/**
 * Event that is fired when a {@link UserModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserEvent extends ModelItemEvent<UserModel, UserDAO> {

    private static final long serialVersionUID = -4702962471823130721L;

    public static final EventType<UserEvent> USER_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, "USER_MODEL_EVENT");

    public static final EventType<UserEvent> USER_EDIT_REQUEST_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_EDIT_REQUEST_EVENT");

    public static final EventType<UserEvent> USER_DELETE_REQUEST_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_DELETE_REQUEST_EVENT");

    public static final EventType<UserEvent> USER_INSERTING_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_INSERTING_EVENT");

    public static final EventType<UserEvent> USER_INSERTED_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_INSERTED_EVENT");

    public static final EventType<UserEvent> USER_UPDATING_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_UPDATING_EVENT");

    public static final EventType<UserEvent> USER_UPDATED_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_UPDATED_EVENT");

    public static final EventType<UserEvent> USER_DELETING_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_DELETING_EVENT");

    public static final EventType<UserEvent> USER_DELETED_EVENT = new EventType<>(USER_MODEL_EVENT, "USER_DELETED_EVENT");

    private UserEvent(UserEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public UserEvent(UserModel model, Object source, EventTarget target, EventType<UserEvent> type) {
        super(model, source, target, type);
    }

    public UserEvent(Object source, UserDAO target, EventType<UserEvent> type) {
        super(source, target, type);
    }

    @Override
    public FxRecordModel.ModelFactory<UserDAO, UserModel> getModelFactory() {
        return UserModel.getFactory();
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

    @Override
    public boolean isDeleteRequest() {
        return getEventType().getName().equals(USER_DELETE_REQUEST_EVENT.getName());
    }

}
