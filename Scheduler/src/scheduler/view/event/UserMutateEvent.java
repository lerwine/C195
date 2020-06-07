package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;
import static scheduler.view.event.ItemMutateEvent.ITEM_MUTATE_EVENT;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserMutateEvent extends ItemMutateEvent<UserModel> {

    private static final long serialVersionUID = -4702962471823130721L;

    public static final EventType<UserMutateEvent> USER_MUTATE_EVENT = new EventType<>(
            ITEM_MUTATE_EVENT,
            "USER_MUTATE_EVENT");

    public static final EventType<UserMutateEvent> USER_INSERT_EVENT = new EventType<>(
            USER_MUTATE_EVENT,
            "USER_INSERT_EVENT");

    public static final EventType<UserMutateEvent> USER_UPDATE_EVENT = new EventType<>(
            USER_MUTATE_EVENT,
            "USER_UPDATE_EVENT");

    public static final EventType<UserMutateEvent> USER_DELETE_EVENT = new EventType<>(
            USER_MUTATE_EVENT,
            "USER_DELETE_EVENT");

    public UserMutateEvent(UserModel source, EventType<UserMutateEvent> type, Event fxEvent) {
        super(source, type, fxEvent);
    }

    @Override
    public UserDAO getTarget() {
        return (UserDAO) super.getTarget();
    }

}
