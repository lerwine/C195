package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;

public abstract class AppointmentEvent extends ModelEvent<AppointmentDAO, AppointmentModel> {

    private static final long serialVersionUID = -3677443789026319836L;

    /**
     * Base {@link EventType} for all {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> APPOINTMENT_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_APPOINTMENT_EVENT");

    /**
     * Base {@link EventType} for all operational {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> OP_EVENT_TYPE = new EventType<>(APPOINTMENT_EVENT_TYPE, "SCHEDULER_APPOINTMENT_OP_EVENT");

    protected AppointmentEvent(AppointmentEvent event, Object source, EventTarget target, EventType<? extends AppointmentEvent> eventType) {
        super(event, source, target, eventType);
    }

    protected AppointmentEvent(AppointmentEvent event, EventType<? extends AppointmentEvent> eventType) {
        super(event, eventType);
    }

    protected AppointmentEvent(AppointmentModel fxRecordModel, Object source, EventTarget target, EventType<? extends AppointmentEvent> eventType) {
        super(fxRecordModel, source, target, eventType);
    }

    protected AppointmentEvent(AppointmentDAO dao, Object source, EventTarget target, EventType<? extends AppointmentEvent> eventType) {
        super(dao, source, target, eventType);
    }

}
