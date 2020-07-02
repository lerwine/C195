package scheduler.view.appointment.edit;

import java.time.ZoneId;
import java.util.Objects;
import scheduler.util.PropertyBindable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTimeSpan extends PropertyBindable implements Comparable<AppointmentTimeSpan> {

    public static final String PROP_START = "start";
    public static final String PROP_DURATION = "duration";
    public static final String PROP_END = "end";

    private DateAndTimeSelection start;
    private AppointmentDuration duration;
    private DateAndTimeSelection end;

    public AppointmentTimeSpan(DateAndTimeSelection start, AppointmentDuration duration) {
        this.start = Objects.requireNonNull(start);
        this.duration = Objects.requireNonNull(duration);
        end = start.plus(duration);
    }

    /**
     * Get the value of start
     *
     * @return the value of start
     */
    public DateAndTimeSelection getStart() {
        return start;
    }

    /**
     * Set the value of start
     *
     * @param value new value of start
     */
    protected void setStart(DateAndTimeSelection value) {
        DateAndTimeSelection oldValue = this.start;
        start = Objects.requireNonNull(value);
        if (!oldValue.equals(value)) {
            AppointmentDuration oldDuration = duration;
            duration = start.until(end);
            try { firePropertyChange(PROP_START, oldValue, start); }
            finally { firePropertyChange(PROP_DURATION, oldDuration, duration); }
        }
    }

    /**
     * Get the value of duration
     *
     * @return the value of duration
     */
    public AppointmentDuration getDuration() {
        return duration;
    }

    /**
     * Set the value of duration
     *
     * @param value new value of duration
     */
    protected void setDuration(AppointmentDuration value) {
        AppointmentDuration oldValue = this.duration;
        duration = Objects.requireNonNull(value);
        if (!oldValue.equals(value)) {
            DateAndTimeSelection oldEnd = end;
            end = start.plus(duration);
            try { firePropertyChange(PROP_DURATION, oldValue, duration); }
            finally { firePropertyChange(PROP_END, oldEnd, end); }
        }
    }

    /**
     * Get the value of end
     *
     * @return the value of end
     */
    public DateAndTimeSelection getEnd() {
        return end;
    }

    /**
     * Set the value of end
     *
     * @param value new value of end
     */
    protected void setEnd(DateAndTimeSelection value) {
        DateAndTimeSelection oldValue = this.end;
        if (!oldValue.equals(Objects.requireNonNull(value))) {
            AppointmentDuration oldDuration = duration;
            duration = start.until(end);
            try { firePropertyChange(PROP_END, oldValue, end); }
            finally { firePropertyChange(PROP_DURATION, oldDuration, duration); }
        }
    }

    public ZonedAppointmentTimeSpan atZone(ZoneId zone) {
        return new ZonedAppointmentTimeSpan(start, duration, zone);
    }

    @Override
    public int compareTo(AppointmentTimeSpan o) {
        return ZonedAppointmentTimeSpan.compare(this, o);
    }

    public boolean isInRange(DateAndTimeSelection dataAndTime) {
        return ZonedAppointmentTimeSpan.isInRange(dataAndTime, this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(start);
        hash = 37 * hash + Objects.hashCode(duration);
        hash = 37 * hash + Objects.hashCode(end);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppointmentTimeSpan other = (AppointmentTimeSpan) obj;
        return Objects.equals(this.start, other.start) && Objects.equals(duration, other.duration) && Objects.equals(end, other.end);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        if (null == start) {
            if (null == end) {
                if (null == duration) {
                    return sb.append("[]").toString();
                }
                return sb.append("[duration=").append(duration).append("]").toString();
            }
            sb.append("[end=").append(end);
        } else {
            sb.append("[start=").append(start);
            if (null != end) {
                sb.append("; end=").append(end);
            }
        }
        if (null == duration) {
            return sb.append("]").toString();
        }
        return sb.append("; duration=").append(duration).append("]").toString();
    }

}
