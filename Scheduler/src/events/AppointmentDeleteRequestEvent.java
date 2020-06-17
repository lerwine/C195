package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;


public final class AppointmentDeleteRequestEvent extends ModelDeleteRequestEvent<AppointmentModel, AppointmentDAO> {

    /**
     * {@link EventType} for all {@code AppointmentDeleteRequestEvent}s.
     */
    public static final EventType<AppointmentDeleteRequestEvent> APPOINTMENT_DELETE_REQUEST = new EventType<>(ModelDeleteRequestEvent.DELETE_REQUEST_EVENT, "SCHEDULER_APPOINTMENT_DELETE_REQUEST");

    private AppointmentDeleteRequestEvent(AppointmentDeleteRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AppointmentDeleteRequestEvent(Object source, EventTarget target, EventType<AppointmentDeleteRequestEvent> eventType, AppointmentModel model) {
        super(source, target, eventType, model);
    }

    public AppointmentDeleteRequestEvent(Object source, EventTarget target, EventType<AppointmentDeleteRequestEvent> eventType, AppointmentDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public AppointmentDeleteRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentDeleteRequestEvent(this, newSource, newTarget);
    }

    @Override
    public AppointmentBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AppointmentDeleteRequestEvent#toBeginOperationEvent
    }
    
}
