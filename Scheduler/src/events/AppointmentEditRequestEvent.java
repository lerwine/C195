package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;


public final class AppointmentEditRequestEvent extends ModelEditRequestEvent<AppointmentModel, AppointmentDAO> {

    /**
     * {@link EventType} for all {@code AppointmentEditRequestEvent}s.
     */
    public static final EventType<AppointmentEditRequestEvent> APPOINTMENT_EDIT_REQUEST = new EventType<>(ModelEditRequestEvent.EDIT_REQUEST_EVENT, "SCHEDULER_APPOINTMENT_EDIT_REQUEST");

    private AppointmentEditRequestEvent(AppointmentEditRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AppointmentEditRequestEvent(Object source, EventTarget target, EventType<AppointmentEditRequestEvent> eventType, AppointmentModel model) {
        super(source, target, eventType, model);
    }

    public AppointmentEditRequestEvent(Object source, EventTarget target, EventType<AppointmentEditRequestEvent> eventType, AppointmentDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public AppointmentEditRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentEditRequestEvent(this, newSource, newTarget);
    }

    @Override
    public AppointmentBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentEditRequestEvent#toBeginOperationEvent
    }
    
}
