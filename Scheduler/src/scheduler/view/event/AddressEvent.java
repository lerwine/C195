package scheduler.view.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;

/**
 * Event that is fired when a {@link AddressModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressEvent extends ModelItemEvent<AddressModel, AddressDAO> {

    private static final long serialVersionUID = 8261622802802373344L;
    public static final String ADDRESS_MODEL_EVENT_NAME = "ADDRESS_MODEL_EVENT";
    public static final String EDIT_REQUEST_EVENT_NAME = "ADDRESS_EDIT_REQUEST_EVENT";
    public static final String DELETE_REQUEST_EVENT_NAME = "ADDRESS_DELETE_REQUEST_EVENT";
    public static final String INSERTING_EVENT_NAME = "ADDRESS_INSERTING_EVENT";
    public static final String INSERTED_EVENT_NAME = "ADDRESS_INSERTED_EVENT";
    public static final String UPDATING_EVENT_NAME = "ADDRESS_UPDATING_EVENT";
    public static final String UPDATED_EVENT_NAME = "ADDRESS_UPDATED_EVENT";
    public static final String DELETING_EVENT_NAME = "ADDRESS_DELETING_EVENT";
    public static final String DELETED_EVENT_NAME = "ADDRESS_DELETED_EVENT";

    public static final EventType<AddressEvent> ADDRESS_MODEL_EVENT_TYPE = new EventType<>(MODEL_ITEM_EVENT, ADDRESS_MODEL_EVENT_NAME);

    public static final EventType<AddressEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    public static final EventType<AddressEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    public static final EventType<AddressEvent> INSERTING_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, INSERTING_EVENT_NAME);

    public static final EventType<AddressEvent> INSERTED_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, INSERTED_EVENT_NAME);

    public static final EventType<AddressEvent> UPDATING_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, UPDATING_EVENT_NAME);

    public static final EventType<AddressEvent> UPDATED_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    public static final EventType<AddressEvent> DELETING_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    public static final EventType<AddressEvent> DELETED_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETED_EVENT_NAME);

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
    public static EventType<AddressEvent> toEventType(ActivityType action) {
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

    private AddressEvent(AddressEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public AddressEvent(AddressModel model, Object source, EventTarget target, ActivityType action, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public AddressEvent(AddressModel model, Object source, EventTarget target, ActivityType action) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, false);
    }

    public AddressEvent(Object source, EventTarget target, AddressDAO dao, ActivityType action, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public AddressEvent(Object source, EventTarget target, AddressDAO dao, ActivityType action) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AddressModel.Factory getModelFactory() {
        return AddressModel.FACTORY;
    }

    @Override
    public synchronized AddressEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AddressEvent> getEventType() {
        return (EventType<AddressEvent>) super.getEventType();
    }

}
