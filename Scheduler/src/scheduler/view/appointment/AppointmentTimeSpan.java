package scheduler.view.appointment;

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
        this.start = Objects.requireNonNull(value);
        if (!oldValue.equals(value)) {
            firePropertyChange(PROP_START, oldValue, value);
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
        this.duration = Objects.requireNonNull(value);
        if (!oldValue.equals(value)) {
            firePropertyChange(PROP_DURATION, oldValue, value);
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

            firePropertyChange(PROP_END, oldValue, value);
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

}
