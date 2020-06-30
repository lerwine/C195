package scheduler.view.appointment.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class ConflictStateChangedEvent extends AppointmentEditEvent {

    private static final long serialVersionUID = -977052401530592970L;

    /**
     * {@link EventType} for {@code ConflictStateChangedEvent}s.
     */
    public static final EventType<ConflictStateChangedEvent> CONFLICT_STATE_CHANGED_EVENT_TYPE = new EventType<>(AppointmentEditEvent.APPOINTMENT_EDIT_EVENT_TYPE,
            "SCHEDULER_APPOINTMENT_EDIT_CONFLICT_STATE_CHANGED");

    private String message;
    private boolean conflictCheckingCurrent;
    private boolean conflicting;

    public ConflictStateChangedEvent(Object source, EventTarget target, String message, boolean conflictCheckingCurrent, boolean conflicting) {
        super(source, target, CONFLICT_STATE_CHANGED_EVENT_TYPE);
        this.message = message;
        this.conflictCheckingCurrent = conflictCheckingCurrent;
        this.conflicting = conflicting;
    }

    @Override
    public ConflictStateChangedEvent copyFor(Object newSource, EventTarget newTarget) {
        ConflictStateChangedEvent event = (ConflictStateChangedEvent) super.copyFor(newSource, newTarget);
        event.message = message;
        event.conflictCheckingCurrent = conflictCheckingCurrent;
        event.conflicting = conflicting;
        return event;
    }

    public String getMessage() {
        return message;
    }

    public boolean isConflictCheckingCurrent() {
        return conflictCheckingCurrent;
    }

    public boolean isConflicting() {
        return conflicting;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(message);
        hash = 53 * hash + (conflictCheckingCurrent ? 1 : 0);
        hash = 53 * hash + (conflicting ? 1 : 0);
        hash = 53 * hash + Objects.hashCode(getSource());
        hash = 53 * hash + Objects.hashCode(getTarget());
        hash = 53 * hash + Objects.hashCode(getEventType().getName());
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
        final ConflictStateChangedEvent other = (ConflictStateChangedEvent) obj;
        return conflictCheckingCurrent == other.conflictCheckingCurrent && conflicting == other.conflicting && Objects.equals(message, other.message) && super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName()).append("[type=").append(getEventType().getName());
        if (conflictCheckingCurrent) {
            sb.append("; conflictCheckingCurrent=true");
        }
        if (conflicting) {
            sb.append("; conflicting=true");
        }
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
