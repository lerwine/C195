package scheduler.view.appointment.edit;

import java.time.LocalTime;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTime extends HourAndMinute implements Comparable<AppointmentTime> {

    public static final String PROP_PM = "pm";

    public static AppointmentTime of(LocalTime time) {
        if (time.getNano() > 499_999_999L) {
            if (time.equals(LocalTime.MAX)) {
                time = LocalTime.MIDNIGHT;
            } else {
                time = time.withNano(0).plusSeconds(1L);
            }
        } else if (time.getNano() > 0) {
            time = time.withNano(0);
        }
        if (time.getSecond() > 29) {
            if (time.equals(LocalTime.MAX.withNano(0))) {
                time = LocalTime.MIDNIGHT;
            } else {
                time = time.withSecond(0).plusMinutes(1L);
            }
        } else if (time.getSecond() > 0) {
            time = time.withSecond(0);
        }
        return new AppointmentTime(time.getHour(), time.getMinute());
    }

    public static int to12HourValue(int hours24) {
        if (hours24 == 0) {
            return 12;
        }
        if (hours24 > 23) {
            throw new IllegalArgumentException("Hours cannot be equal to or greater than 24");
        }
        if (hours24 > 12) {
            return hours24 - 12;
        }
        if (hours24 < 0) {
            throw new IllegalArgumentException("Hours cannot be less than zero");
        }
        return hours24;
    }

    public static int require12HourValue(int hours) {
        if (hours < 1) {
            throw new IllegalArgumentException("Hours cannot be less than 1");
        }
        if (hours > 12) {
            throw new IllegalArgumentException("Hours cannot be greater than 12");
        }
        return hours;
    }
    private boolean pm;

    public AppointmentTime(int hours24, int minutes) {
        super(to12HourValue(hours24), minutes);
        pm = hours24 > 11;
    }

    public AppointmentTime(int hours12, int minutes, boolean isPm) {
        super(require12HourValue(hours12), minutes);
        pm = isPm;
    }

    /**
     * Get the value of pm
     *
     * @return the value of pm
     */
    public boolean isPm() {
        return pm;
    }

    /**
     * Set the value of pm
     *
     * @param value new value of pm
     */
    protected void setPm(boolean value) {
        boolean oldValue = pm;
        pm = oldValue;
        if (pm != oldValue) {
            firePropertyChange(PROP_PM, oldValue, pm);
        }
    }

    public LocalTime toTemporal() {
        return LocalTime.of(getHours(), getMinutes(), 0, 0);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (pm ? 1 : 0);
        hash = 89 * hash + super.hashCode();
        return hash;
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return this == obj || (super.equals(obj) && ((AppointmentTime) obj).pm == pm);
    }

    @Override
    public int compareTo(AppointmentTime o) {
        if (null == o) {
            return -1;
        }
        if (pm != o.pm) {
            return (pm) ? 1 : -1;
        }
        int result = getHours() - o.getHours();
        return (result == 0) ? getMinutes() - o.getMinutes() : result;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[hours=" + getHours() + "; minutes=" + getMinutes() + "; pm=" + ((pm) ? "true" : "false") + "]";
    }

}
