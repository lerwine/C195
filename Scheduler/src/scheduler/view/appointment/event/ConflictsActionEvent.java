package scheduler.view.appointment.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class ConflictsActionEvent extends AppointmentEditEvent {

    private static final long serialVersionUID = 6242626865301510765L;

    private static final String CHECK_CONFLICTS_EVENT_NAME = "SCHEDULER_APPOINTMENT_EDIT_CHECK_CONFLICTS";

    /**
     * Base {@link EventType} for all {@code ConflictsActionEvent}s.
     */
    public static final EventType<ConflictsActionEvent> CONFLICTS_ACTION_EVENT_TYPE = new EventType<>(AppointmentEditEvent.APPOINTMENT_EDIT_EVENT_TYPE,
            "SCHEDULER_APPOINTMENT_EDIT_CONFLICTS_ACTION");

    /**
     * Check conflicts {@link EventType}.
     */
    public static final EventType<ConflictsActionEvent> CHECK_CONFLICTS = new EventType<>(CONFLICTS_ACTION_EVENT_TYPE, CHECK_CONFLICTS_EVENT_NAME);

    /**
     * Check conflicts {@link EventType}.
     */
    public static final EventType<ConflictsActionEvent> SHOW_CONFLICTS = new EventType<>(CONFLICTS_ACTION_EVENT_TYPE, "SCHEDULER_APPOINTMENT_EDIT_SHOW_CONFLICTS");

    private boolean checkConflicts;

    public ConflictsActionEvent(Object source, EventTarget target, boolean checkConflicts) {
        super(source, target, (checkConflicts) ? CHECK_CONFLICTS : SHOW_CONFLICTS);
        this.checkConflicts = checkConflicts;
    }

    public boolean isCheckConflicts() {
        return checkConflicts;
    }

    @Override
    public ConflictsActionEvent copyFor(Object newSource, EventTarget newTarget) {
        ConflictsActionEvent event = (ConflictsActionEvent) super.copyFor(newSource, newTarget);
        event.checkConflicts = checkConflicts;
        return event;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(getSource());
        hash = 67 * hash + Objects.hashCode(getTarget());
        hash = 67 * hash + Objects.hashCode(getEventType().getName());
        hash = 67 * hash + (checkConflicts ? 1 : 0);
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
        final ConflictsActionEvent other = (ConflictsActionEvent) obj;
        return checkConflicts == other.checkConflicts && super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName()).append("[type=").append(getEventType().getName());
        if (checkConflicts) {
            sb.append("; checkConflicts=true");
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
