package scheduler.view.appointment.event;

import java.util.Objects;
import java.util.Optional;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.view.appointment.ZonedAppointmentTimeSpan;

public final class DateRangeChangedEvent extends AppointmentEditEvent {

    private static final long serialVersionUID = 7552343803860931682L;

    /**
     * {@link EventType} for {@code DateRangeChangedEvent}s.
     */
    public static final EventType<DateRangeChangedEvent> DATE_RANGE_CHANGED_EVENT_TYPE = new EventType<>(AppointmentEditEvent.APPOINTMENT_EDIT_EVENT_TYPE,
            "SCHEDULER_APPOINTMENT_EDIT_DATE_RANGE_CHANGED");

    private Optional<ZonedAppointmentTimeSpan> zonedAppointmentTimeSpan;

    public DateRangeChangedEvent(Object source, EventTarget target, Optional<ZonedAppointmentTimeSpan> zonedAppointmentTimeSpan) {
        super(source, target, DATE_RANGE_CHANGED_EVENT_TYPE);
        this.zonedAppointmentTimeSpan = Objects.requireNonNull(zonedAppointmentTimeSpan);
    }

    public Optional<ZonedAppointmentTimeSpan> getZonedAppointmentTimeSpan() {
        return zonedAppointmentTimeSpan;
    }

    @Override
    public DateRangeChangedEvent copyFor(Object newSource, EventTarget newTarget) {
        DateRangeChangedEvent event = (DateRangeChangedEvent) super.copyFor(newSource, newTarget);
        event.zonedAppointmentTimeSpan = zonedAppointmentTimeSpan;
        return event;
    }

}
