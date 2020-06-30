package scheduler.view.appointment.event;

import java.util.Objects;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(getSource());
        hash = 97 * hash + Objects.hashCode(getTarget());
        hash = 97 * hash + Objects.hashCode(getEventType().getName());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AppointmentEditEvent other = (AppointmentEditEvent) obj;
        return Objects.equals(getEventType(), other.getEventType()) && Objects.equals(this.source, other.source) && Objects.equals(this.target, other.target);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName()).append("[type=").append(getEventType().getName());
        if (isConsumed()) {
            sb.append("; consumed=true");
        }
        EventTarget t = getTarget();
        if (null != t) {
            sb.append("; target=").append(t);
        }
        Object s = getSource();
        if (null != s) {
            return sb.append("; source=").append(s).append("]").toString();
        }
        return sb.append("]").toString();
    }

}
