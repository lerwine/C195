package scheduler.view.event;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link AppointmentModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentEvent extends ModelItemEvent<AppointmentModel, AppointmentDAO> {

    private static final long serialVersionUID = -1145658585716643269L;

    public static final EventType<AppointmentEvent> APPOINTMENT_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, "APPOINTMENT_MODEL_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_EDIT_REQUEST_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT,
            "APPOINTMENT_EDIT_REQUEST_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_DELETE_REQUEST_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT,
            "APPOINTMENT_DELETE_REQUEST_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_INSERTING_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT,
            "APPOINTMENT_INSERTING_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_INSERTED_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT,
            "APPOINTMENT_INSERTED_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_UPDATING_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT,
            "APPOINTMENT_UPDATING_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_UPDATED_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT, "APPOINTMENT_UPDATED_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_DELETING_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT,
            "APPOINTMENT_DELETING_EVENT");

    public static final EventType<AppointmentEvent> APPOINTMENT_DELETED_EVENT = new EventType<>(APPOINTMENT_MODEL_EVENT, "APPOINTMENT_DELETED_EVENT");

    private AppointmentEvent(AppointmentEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public AppointmentEvent(AppointmentModel model, Object source, EventTarget target, EventType<AppointmentEvent> type) {
        super(model, source, target, type);
    }

    public AppointmentEvent(Object source, AppointmentDAO target, EventType<AppointmentEvent> type) {
        super(source, target, type);
    }

    @Override
    public FxRecordModel.ModelFactory<AppointmentDAO, AppointmentModel> getModelFactory() {
        return AppointmentModel.getFactory();
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

    @Override
    public boolean isDeleteRequest() {
        return getEventType().getName().equals(APPOINTMENT_DELETE_REQUEST_EVENT.getName());
    }

}
