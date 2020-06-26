package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.RecordModelContext;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;

public final class UserOpRequestEvent extends OperationRequestEvent<UserDAO, UserModel> {

    private static final long serialVersionUID = -7155468722667631823L;

    /**
     * Base {@link EventType} for all {@code UserOpRequestEvent}s.
     */
    public static final EventType<UserOpRequestEvent> USER_OP_REQUEST = new EventType<>(OP_REQUEST_EVENT, "SCHEDULER_USER_OP_REQUEST");

    /**
     * {@link EventType} for edit {@code UserOpRequestEvent}s.
     */
    public static final EventType<UserOpRequestEvent> EDIT_REQUEST = new EventType<>(USER_OP_REQUEST, "SCHEDULER_USER_EDIT_REQUEST");

    /**
     * {@link EventType} for delete {@code UserOpRequestEvent}s.
     */
    public static final EventType<UserOpRequestEvent> DELETE_REQUEST = new EventType<>(USER_OP_REQUEST, "SCHEDULER_USER_DELETE_REQUEST");

    public UserOpRequestEvent(ModelEvent<UserDAO, UserModel> event, Object source, EventTarget target, boolean isDelete) {
        super(event, source, target, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public UserOpRequestEvent(ModelEvent<UserDAO, UserModel> event, boolean isDelete) {
        super(event, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public UserOpRequestEvent(RecordModelContext<UserDAO, UserModel> target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public UserOpRequestEvent(UserModel target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

}
