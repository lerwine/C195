package scheduler.view.appointment.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class AppointmentEditEvent extends Event {

    private static final long serialVersionUID = 6659379566344934808L;
    
    /**
     * Base {@link EventType} for all {@code AppointmentEditEvent}s.
     */
    public static final EventType<AppointmentEditEvent> APPOINTMENT_EDIT_EVENT_TYPE = new EventType<>(ANY, "SCHEDULER_APPOINTMENT_EDIT_EVENT");

    protected AppointmentEditEvent(Object source, EventTarget target, EventType<? extends AppointmentEditEvent> eventType) {
        super(source, target, eventType);
    }

}
