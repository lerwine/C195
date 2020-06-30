package scheduler.view.appointment.event;

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

}
