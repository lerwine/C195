package scheduler.view.appointment.edit;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ZonedAppointmentTimeSpan extends AppointmentTimeSpan {

    public static final String PROP_ZONE = "zone";

    public static ZonedAppointmentTimeSpan of(ZonedDateAndTimeSelection start, AppointmentDuration duration) {
        return new ZonedAppointmentTimeSpan(new DateAndTimeSelection(start.getDate(), start.getTime()), duration, start.getZone());
    }

    public static int compare(AppointmentTimeSpan x, AppointmentTimeSpan y) {
        if (null == x) {
            return (null == y) ? 0 : -1;
        }
        if (null == y) {
            return 1;
        }
        if (x instanceof ZonedAppointmentTimeSpan) {
            ZoneId a = ((ZonedAppointmentTimeSpan) x).zone;
            if (y instanceof ZonedAppointmentTimeSpan) {
                ZonedAppointmentTimeSpan b = (ZonedAppointmentTimeSpan) y;
                if (!b.zone.equals(a)) {
                    y = b.withZoneSameInstant(a);
                }
            } else {
                ZoneId b = ZoneId.systemDefault();
                if (!a.equals(b)) {
                    y = y.atZone(b).withZoneSameInstant(a);
                }
            }
        } else if (y instanceof ZonedAppointmentTimeSpan) {
            ZoneId a = ((ZonedAppointmentTimeSpan) y).zone;
            ZoneId b = ZoneId.systemDefault();
            if (!a.equals(b)) {
                x = x.atZone(b).withZoneSameInstant(a);
            }
        }
        int result = x.getStart().compareTo(y.getStart());
        if (result == 0) {
            return x.getEnd().compareTo(y.getEnd());
        }
        return result;
    }

    public static boolean overlap(AppointmentTimeSpan x, AppointmentTimeSpan y) {
        if (null == x) {
            return (null == y);
        }
        if (null == y) {
            return false;
        }
        if (x instanceof ZonedAppointmentTimeSpan) {
            ZoneId a = ((ZonedAppointmentTimeSpan) x).zone;
            if (y instanceof ZonedAppointmentTimeSpan) {
                ZonedAppointmentTimeSpan b = (ZonedAppointmentTimeSpan) y;
                if (!b.zone.equals(a)) {
                    y = b.withZoneSameInstant(a);
                }
            } else {
                ZoneId b = ZoneId.systemDefault();
                if (!a.equals(b)) {
                    y = y.atZone(b).withZoneSameInstant(a);
                }
            }
        } else if (y instanceof ZonedAppointmentTimeSpan) {
            ZoneId a = ((ZonedAppointmentTimeSpan) y).zone;
            ZoneId b = ZoneId.systemDefault();
            if (!a.equals(b)) {
                x = x.atZone(b).withZoneSameInstant(a);
            }
        }

        return x.getEnd().compareTo(y.getStart()) > 0 && x.getStart().compareTo(y.getEnd()) < 0;
    }

    public static boolean isInRange(DateAndTimeSelection dateAndTime, AppointmentTimeSpan timeSpan) {
        if (timeSpan instanceof ZonedAppointmentTimeSpan) {
            ZoneId a = ((ZonedAppointmentTimeSpan) timeSpan).zone;
            if (dateAndTime instanceof ZonedDateAndTimeSelection) {
                ZonedDateAndTimeSelection b = (ZonedDateAndTimeSelection) dateAndTime;
                if (!a.equals(b.getZone())) {
                    dateAndTime = b.withZoneSameInstant(a);
                }
            } else if (!a.equals(ZoneId.systemDefault())) {
                dateAndTime = dateAndTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(a);
            }
        } else if (dateAndTime instanceof ZonedDateAndTimeSelection) {
            ZonedDateAndTimeSelection b = (ZonedDateAndTimeSelection) dateAndTime;
            if (!b.getZone().equals(ZoneId.systemDefault())) {
                dateAndTime = b.withZoneSameInstant(ZoneId.systemDefault());
            }
        }
        return dateAndTime.compareTo(timeSpan.getStart()) >= 0 && dateAndTime.compareTo(timeSpan.getEnd()) < 0;
    }

    private ZoneId zone;

    public ZonedAppointmentTimeSpan(DateAndTimeSelection start, AppointmentDuration duration, ZoneId zone) {
        super(start, duration);
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

    public ZonedAppointmentTimeSpan withZoneSameLocal(ZoneId zone) {
        return new ZonedAppointmentTimeSpan(getStart(), getDuration(), zone);
    }

    public ZonedAppointmentTimeSpan withZoneSameInstant(ZoneId zone) {
        return of(ZonedDateAndTimeSelection.of(toZonedStartDateTime().withZoneSameInstant(zone)), getDuration());
    }

    public ZonedDateTime toZonedStartDateTime() {
        return ZonedDateTime.of(super.getStart().toLocalDateTime(), zone);
    }

    public ZonedDateTime toZonedEndDateTime() {
        return ZonedDateTime.of(super.getEnd().toLocalDateTime(), zone);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(getStart());
        hash = 19 * hash + Objects.hashCode(getDuration());
        hash = 19 * hash + Objects.hashCode(getEnd());
        hash = 19 * hash + Objects.hashCode(zone);
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
        final ZonedAppointmentTimeSpan other = (ZonedAppointmentTimeSpan) obj;
        return Objects.equals(zone, other.zone) && super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        if (null == getStart()) {
            if (null == getEnd()) {
                if (null == getDuration()) {
                    if (null == zone)
                        return sb.append("[]").toString();
                    return sb.append("[zone=").append(zone.getId()).append("]").toString();
                }
                sb.append("[duration=").append(getDuration());
                if (null == zone)
                    return sb.append("]").toString();
            }
            sb.append("[end=").append(getEnd());
        } else {
            sb.append("[start=").append(getStart());
            if (null != getEnd())
                sb.append("; end=").append(getEnd());
        }
        if (null != getDuration())
            sb.append("; duration=").append(getDuration());
        if (null == zone)
            return sb.append("]").toString();
        return sb.append("; zone=").append(zone.getId()).append("]").toString();
    }

}
