package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ZonedDateAndTimeSelection extends DateAndTimeSelection {

    public static final String PROP_ZONE = "zone";

    public static ZonedDateAndTimeSelection of(ZonedDateTime dateTime) {
        return new ZonedDateAndTimeSelection(dateTime.toLocalDate(), AppointmentTime.of(dateTime.toLocalTime()), dateTime.getZone());
    }

    public static int compare(DateAndTimeSelection x, DateAndTimeSelection y) {
        if (null == x) {
            return (null == y) ? 0 : -1;
        }
        if (null == y) {
            return 1;
        }
        if (x instanceof ZonedDateAndTimeSelection) {
            ZoneId a = ((ZonedDateAndTimeSelection) x).zone;
            if (y instanceof ZonedDateAndTimeSelection) {
                ZonedDateAndTimeSelection b = (ZonedDateAndTimeSelection) y;
                if (!b.zone.equals(a)) {
                    y = b.withZoneSameInstant(a);
                }
            } else {
                ZoneId b = ZoneId.systemDefault();
                if (!a.equals(b)) {
                    y = y.atZone(b).withZoneSameInstant(a);
                }
            }
        } else if (y instanceof ZonedDateAndTimeSelection) {
            ZoneId a = ((ZonedDateAndTimeSelection) y).zone;
            ZoneId b = ZoneId.systemDefault();
            if (!a.equals(b)) {
                x = x.atZone(b).withZoneSameInstant(a);
            }
        }
        int result = x.getDate().compareTo(y.getDate());
        if (result == 0) {
            return x.getTime().compareTo(y.getTime());
        }
        return result;
    }
    private ZoneId zone;

    public ZonedDateAndTimeSelection(LocalDate date, AppointmentTime time, ZoneId zone) {
        super(date, time);
        this.zone = Objects.requireNonNull(zone);
    }

    /**
     * Get the value of zone
     *
     * @return the value of zone
     */
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Set the value of zone
     *
     * @param value new value of zone
     */
    protected void setZone(ZoneId value) {
        ZoneId oldValue = this.zone;
        this.zone = Objects.requireNonNull(value);
        if (!oldValue.equals(value)) {
            firePropertyChange(PROP_ZONE, oldValue, value);
        }
    }

    public ZonedDateTime toZonedDateTime() {
        return ZonedDateTime.of(super.toLocalDateTime(), zone);
    }

    public ZonedDateAndTimeSelection withZoneSameLocal(ZoneId zone) {
        return new ZonedDateAndTimeSelection(super.getDate(), super.getTime(), zone);
    }

    public ZonedDateAndTimeSelection withZoneSameInstant(ZoneId zone) {
        ZonedDateTime zonedDateTime = toZonedDateTime().withZoneSameInstant(zone);
        AppointmentTime time = AppointmentTime.of(zonedDateTime.toLocalTime());
        LocalDate date = zonedDateTime.toLocalDate();
        return new ZonedDateAndTimeSelection((date.equals(getDate())) ? getDate() : date, (time.equals(getTime())) ? getTime() : time,
                zonedDateTime.getZone());
    }

}
