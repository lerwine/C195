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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(getSource());
        hash = 43 * hash + Objects.hashCode(getTarget());
        hash = 43 * hash + Objects.hashCode(getEventType().getName());
        hash = 43 * hash + Objects.hashCode(zonedAppointmentTimeSpan);
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
        final DateRangeChangedEvent other = (DateRangeChangedEvent) obj;
        return Objects.equals(zonedAppointmentTimeSpan, other.zonedAppointmentTimeSpan) && super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName()).append("[type=").append(getEventType().getName());
        if (null != zonedAppointmentTimeSpan) {
            sb.append("; zonedAppointmentTimeSpan=").append(zonedAppointmentTimeSpan);
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
