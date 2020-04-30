package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import scheduler.util.PropertyBindable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DateAndTimeSelection extends PropertyBindable implements Comparable<DateAndTimeSelection> {

    public static final String PROP_DATE = "date";
    public static final String PROP_TIME = "time";

    public static DateAndTimeSelection of(LocalDateTime dateTime) {
        return new DateAndTimeSelection(dateTime.toLocalDate(), AppointmentTime.of(dateTime.toLocalTime()));
    }

    private LocalDate date;
    private AppointmentTime time;

    public DateAndTimeSelection(LocalDate date, AppointmentTime time) {
        this.date = Objects.requireNonNull(date);
        this.time = Objects.requireNonNull(time);
    }

    /**
     * Get the value of date
     *
     * @return the value of date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Set the value of date
     *
     * @param value new value of date
     */
    protected void setDate(LocalDate value) {
        LocalDate oldDate = date;
        date = Objects.requireNonNull(value);
        if (!oldDate.equals(date)) {
            firePropertyChange(PROP_DATE, oldDate, date);
        }
    }

    /**
     * Get the value of time
     *
     * @return the value of time
     */
    public AppointmentTime getTime() {
        return time;
    }

    /**
     * Set the value of time
     *
     * @param value new value of time
     */
    protected void setTime(AppointmentTime value) {
        AppointmentTime oldTime = time;
        time = Objects.requireNonNull(value);
        if (!oldTime.equals(time)) {
            firePropertyChange(PROP_TIME, oldTime, time);
        }
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(date, time.toTemporal());
    }

    public ZonedDateAndTimeSelection atZone(ZoneId zone) {
        return new ZonedDateAndTimeSelection(date, time, zone);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.date);
        hash = 53 * hash + Objects.hashCode(this.time);
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
        final DateAndTimeSelection other = (DateAndTimeSelection) obj;
        return date.equals(other.date) && time.equals(other.time);
    }

    DateAndTimeSelection plus(AppointmentDuration duration) {
        LocalDateTime localDateTime = toLocalDateTime();
        if (duration.getDays() > 0) {
            localDateTime = localDateTime.plusDays(duration.getDays());
        }
        if (duration.getHours() > 0) {
            localDateTime = localDateTime.plusHours(duration.getHours());
        }
        if (duration.getMinutes() > 0) {
            localDateTime = localDateTime.plusMinutes(duration.getMinutes());
        }
        return DateAndTimeSelection.of(localDateTime);
    }

    @Override
    public int compareTo(DateAndTimeSelection o) {
        return ZonedDateAndTimeSelection.compare(this, o);
    }

}
