package scheduler.view.appointment.event;

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

}
