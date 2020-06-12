package scheduler.view.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;

/**
 * Event that is fired when a {@link CityModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityEvent extends ModelItemEvent<CityModel, CityDAO> {

    private static final long serialVersionUID = 2299267381558318300L;
    public static final String CITY_MODEL_EVENT_NAME = "CITY_MODEL_EVENT";
    public static final String EDIT_REQUEST_EVENT_NAME = "CITY_EDIT_REQUEST_EVENT";
    public static final String DELETE_REQUEST_EVENT_NAME = "CITY_DELETE_REQUEST_EVENT";
    public static final String INSERTING_EVENT_NAME = "CITY_INSERTING_EVENT";
    public static final String INSERTED_EVENT_NAME = "CITY_INSERTED_EVENT";
    public static final String UPDATING_EVENT_NAME = "CITY_UPDATING_EVENT";
    public static final String UPDATED_EVENT_NAME = "CITY_UPDATED_EVENT";
    public static final String DELETING_EVENT_NAME = "CITY_DELETING_EVENT";
    public static final String DELETED_EVENT_NAME = "CITY_DELETED_EVENT";

    public static final EventType<CityEvent> CITY_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, CITY_MODEL_EVENT_NAME);

    public static final EventType<CityEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, EDIT_REQUEST_EVENT_NAME);

    public static final EventType<CityEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, DELETE_REQUEST_EVENT_NAME);

    public static final EventType<CityEvent> INSERTING_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, INSERTING_EVENT_NAME);

    public static final EventType<CityEvent> INSERTED_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, INSERTED_EVENT_NAME);

    public static final EventType<CityEvent> UPDATING_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, UPDATING_EVENT_NAME);

    public static final EventType<CityEvent> UPDATED_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, UPDATED_EVENT_NAME);

    public static final EventType<CityEvent> DELETING_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, DELETING_EVENT_NAME);

    public static final EventType<CityEvent> DELETED_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT, DELETED_EVENT_NAME);

    public static ActivityType toActionType(String eventName) {
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

    public static EventType<CityEvent> toEventType(ActivityType action) {
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

    private CityEvent(CityEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CityEvent(CityModel model, Object source, EventTarget target, ActivityType action, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public CityEvent(CityModel model, Object source, EventTarget target, ActivityType action) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, false);
    }

    public CityEvent(Object source, EventTarget target, CityDAO dao, ActivityType action, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public CityEvent(Object source, EventTarget target, CityDAO dao, ActivityType action) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, false);
    }

    @Override
    public CityModel.Factory getModelFactory() {
        return CityModel.FACTORY;
    }

    @Override
    public synchronized CityEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CityEvent> getEventType() {
        return (EventType<CityEvent>) super.getEventType();
    }

}
