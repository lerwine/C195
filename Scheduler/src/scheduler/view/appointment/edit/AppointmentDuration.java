package scheduler.view.appointment.edit;

import java.time.Duration;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentDuration extends HourAndMinute implements Comparable<AppointmentDuration> {

    public static final long MAX_DURATION_SECONDS = 185_542_587_187_199L;
    public static final String PROP_DAYS = "days";

    public static AppointmentDuration of(Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("TimeSpan cannot be negative");
        }
        long t = duration.getSeconds();
        long s = t % 60L;
        long u = (t - s) / 60L;
        long m = u % 60L;
        t = (u - m) / 60L;
        long h = t % 24L;
        long d = (t - h) / 24L;
        if (duration.getNano() > 499_999_999L) {
            if (++s == 60) {
                s = 0;
                if (++m == 60) {
                    m = 0;
                    if (++h == 24) {
                        h = 0;
                        d++;
                    }
                }
            }
        }
        if (s > 29) {
            if (++m == 60) {
                m = 0;
                if (++h == 24) {
                    h = 0;
                    d++;
                }
            }
        }

        if (d > (long) (Integer.MAX_VALUE)) {
            throw new ArithmeticException("Duration too large");
        }
        return new AppointmentDuration((int) d, (int) h, (int) m);
    }

    private int days;

    public AppointmentDuration(int days, int hours, int minutes) {
        super(hours, minutes);
        if (days < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }
        this.days = days;
    }

    public AppointmentDuration(int hours, int minutes) {
        this(0, hours, minutes);
    }

    /**
     * Get the value of days
     *
     * @return the value of days
     */
    public int getDays() {
        return days;
    }

    /**
     * Set the value of days
     *
     * @param value new value of days
     */
    protected void setDays(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }
        int oldValue = days;
        days = value;
        if (value != oldValue) {
            firePropertyChange(PROP_DAYS, oldValue, days);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + days;
        hash = 29 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof AppointmentDuration && (this == obj || (super.equals(obj) && ((AppointmentDuration) obj).days == days));
    }

    public Duration toDuration() {
        return Duration.ofSeconds((((long) getDays() * 24 + (long) getHours()) * 60L + (long) getMinutes()) * 60L);
    }

    @Override
    public int compareTo(AppointmentDuration o) {
        if (null == o) {
            return -1;
        }
        int result = getDays() - o.getDays();
        if (result == 0 && (result = getHours() - o.getHours()) == 0) {
            return getMinutes() - o.getMinutes();
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[days=" + days + "; hours=" + getHours() + "; minutes=" + getMinutes() + "]";
    }

}
