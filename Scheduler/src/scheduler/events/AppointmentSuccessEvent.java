package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;

public final class AppointmentSuccessEvent extends AppointmentEvent {

    private static final String BASE_EVENT_NAME = "SCHEDULER_APPOINTMENT_SUCCESS_EVENT";

    /**
     * Base {@link EventType} for all {@code AppointmentSuccessEvent}s.
     */
    public static final EventType<AppointmentSuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code AppointmentSuccessEvent}s.
     */
    public static final EventType<AppointmentSuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_APPOINTMENT_SAVE_SUCCESS");

    /**
     * {@link EventType} for delete {@code AppointmentSuccessEvent}s.
     */
    public static final EventType<AppointmentSuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_APPOINTMENT_DELETE_SUCCESS");

    private static EventType<AppointmentSuccessEvent> assertValidEventType(EventType<AppointmentSuccessEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    public AppointmentSuccessEvent(AppointmentEvent event, Object source, EventTarget target, EventType<AppointmentSuccessEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
    }

    public AppointmentSuccessEvent(AppointmentEvent event, EventType<AppointmentSuccessEvent> eventType) {
        super(event, assertValidEventType(eventType));
    }

    public AppointmentSuccessEvent(AppointmentModel fxRecordModel, Object source, EventTarget target, EventType<AppointmentSuccessEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
    }

    public AppointmentSuccessEvent(AppointmentDAO dao, Object source, EventTarget target, EventType<AppointmentSuccessEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
    }

}
