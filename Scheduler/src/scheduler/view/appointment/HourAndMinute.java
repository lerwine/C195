package scheduler.view.appointment;

import scheduler.util.PropertyBindable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class HourAndMinute extends PropertyBindable {

    public static final String PROP_HOURS = "hours";
    public static final String PROP_MINUTES = "minutes";

    private int hours;

    private int minutes;

    public HourAndMinute(int hours, int minutes) {
        if (hours < 0) {
            throw new IllegalArgumentException("Hours cannot be negative");
        }
        if (minutes < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative");
        }
        if (minutes > 59) {
            throw new IllegalArgumentException("Minutes cannot be equal to or greater than 60");
        }
        this.hours = hours;
        this.minutes = minutes;
    }

    /**
     * Get the value of hours
     *
     * @return the value of hours
     */
    public int getHours() {
        return hours;
    }

    /**
     * Set the value of minutes
     *
     * @param value new value of minutes
     */
    protected void setHours(int value) {
        if (hours < 0) {
            throw new IllegalArgumentException("Hours cannot be negative");
        }
        int oldValue = hours;
        hours = value;
        if (value != oldValue) {
            firePropertyChange(PROP_HOURS, oldValue, hours);
        }
    }

    /**
     * Get the value of minutes
     *
     * @return the value of minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Set the value of minutes
     *
     * @param value new value of minutes
     */
    protected void setMinutes(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative");
        }
        if (value > 59) {
            throw new IllegalArgumentException("Minutes cannot be equal to or greater than 60");
        }
        int oldValue = minutes;
        minutes = value;
        if (value != oldValue) {
            firePropertyChange(PROP_MINUTES, oldValue, minutes);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.hours;
        hash = 89 * hash + this.minutes;
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
        final HourAndMinute other = (HourAndMinute) obj;
        return hours == other.hours && minutes == other.minutes;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[hours=" + hours + "; minutes=" + minutes + "]";
    }

}
