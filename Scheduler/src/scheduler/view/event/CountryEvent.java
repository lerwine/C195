package scheduler.view.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;

/**
 * Event that is fired when a {@link CountryModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryEvent extends ModelItemEvent<CountryModel, CountryDAO> {

    private static final long serialVersionUID = 6220482582846221386L;
    public static final String COUNTRY_MODEL_EVENT_NAME = "COUNTRY_MODEL_EVENT";
    public static final String EDIT_REQUEST_EVENT_NAME = "COUNTRY_EDIT_REQUEST_EVENT";
    public static final String DELETE_REQUEST_EVENT_NAME = "COUNTRY_DELETE_REQUEST_EVENT";
    public static final String INSERTING_EVENT_NAME = "COUNTRY_INSERTING_EVENT";
    public static final String INSERTED_EVENT_NAME = "COUNTRY_INSERTED_EVENT";
    public static final String UPDATING_EVENT_NAME = "COUNTRY_UPDATING_EVENT";
    public static final String UPDATED_EVENT_NAME = "COUNTRY_UPDATED_EVENT";
    public static final String DELETING_EVENT_NAME = "COUNTRY_DELETING_EVENT";
    public static final String DELETED_EVENT_NAME = "COUNTRY_DELETED_EVENT";

    public static final EventType<CountryEvent> COUNTRY_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, COUNTRY_MODEL_EVENT_NAME);

    public static final EventType<CountryEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, EDIT_REQUEST_EVENT_NAME);

    public static final EventType<CountryEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, DELETE_REQUEST_EVENT_NAME);

    public static final EventType<CountryEvent> INSERTING_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, INSERTING_EVENT_NAME);

    public static final EventType<CountryEvent> INSERTED_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, INSERTED_EVENT_NAME);

    public static final EventType<CountryEvent> UPDATING_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, UPDATING_EVENT_NAME);

    public static final EventType<CountryEvent> UPDATED_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, UPDATED_EVENT_NAME);

    public static final EventType<CountryEvent> DELETING_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, DELETING_EVENT_NAME);

    public static final EventType<CountryEvent> DELETED_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT, DELETED_EVENT_NAME);

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

    public static EventType<CountryEvent> toEventType(ActivityType action) {
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

    private CountryEvent(CountryEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CountryEvent(CountryModel model, Object source, EventTarget target, ActivityType action, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public CountryEvent(CountryModel model, Object source, EventTarget target, ActivityType action) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, false);
    }

    public CountryEvent(Object source, EventTarget target, CountryDAO dao, ActivityType action, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public CountryEvent(Object source, EventTarget target, CountryDAO dao, ActivityType action) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, false);
    }

    @Override
    public CountryModel.Factory getModelFactory() {
        return CountryModel.FACTORY;
    }

    @Override
    public synchronized CountryEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CountryEvent> getEventType() {
        return (EventType<CountryEvent>) super.getEventType();
    }

}
