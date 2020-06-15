package scheduler.view.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;

/**
 * Event that is fired when a {@link AppointmentModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentEvent extends ModelItemEvent<AppointmentModel, AppointmentDAO> {

    private static final long serialVersionUID = -1145658585716643269L;

    public static final String APPOINTMENT_MODEL_EVENT_NAME = "APPOINTMENT_MODEL_EVENT";
    public static final String EDIT_REQUEST_EVENT_NAME = "APPOINTMENT_EDIT_REQUEST_EVENT";
    public static final String DELETE_REQUEST_EVENT_NAME = "APPOINTMENT_DELETE_REQUEST_EVENT";
    public static final String INSERTING_EVENT_NAME = "APPOINTMENT_INSERTING_EVENT";
    public static final String INSERTED_EVENT_NAME = "APPOINTMENT_INSERTED_EVENT";
    public static final String UPDATING_EVENT_NAME = "APPOINTMENT_UPDATING_EVENT";
    public static final String UPDATED_EVENT_NAME = "APPOINTMENT_UPDATED_EVENT";
    public static final String DELETING_EVENT_NAME = "APPOINTMENT_DELETING_EVENT";
    public static final String DELETED_EVENT_NAME = "APPOINTMENT_DELETED_EVENT";

    public static final EventType<AppointmentEvent> APPOINTMENT_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, APPOINTMENT_MODEL_EVENT_NAME);

    public static final EventType<AppointmentEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, EDIT_REQUEST_EVENT_NAME);

    public static final EventType<AppointmentEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, DELETE_REQUEST_EVENT_NAME);

    public static final EventType<AppointmentEvent> INSERTING_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, INSERTING_EVENT_NAME);

    public static final EventType<AppointmentEvent> INSERTED_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, INSERTED_EVENT_NAME);

    public static final EventType<AppointmentEvent> UPDATING_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, UPDATING_EVENT_NAME);

    public static final EventType<AppointmentEvent> UPDATED_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, UPDATED_EVENT_NAME);

    public static final EventType<AppointmentEvent> DELETING_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, DELETING_EVENT_NAME);

    public static final EventType<AppointmentEvent> DELETED_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT, DELETED_EVENT_NAME);

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
    public static EventType<AppointmentEvent> toEventType(ActivityType action) {
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

    private AppointmentEvent(AppointmentEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public AppointmentEvent(AppointmentModel model, Object source, EventTarget target, ActivityType action, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public AppointmentEvent(AppointmentModel model, Object source, EventTarget target, ActivityType action) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, false);
    }

    public AppointmentEvent(Object source, EventTarget target, AppointmentDAO dao, ActivityType action, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public AppointmentEvent(Object source, EventTarget target, AppointmentDAO dao, ActivityType action) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AppointmentModel.Factory getModelFactory() {
        return AppointmentModel.FACTORY;
    }

    @Override
    public synchronized AppointmentEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AppointmentEvent> getEventType() {
        return (EventType<AppointmentEvent>) super.getEventType();
    }

}
