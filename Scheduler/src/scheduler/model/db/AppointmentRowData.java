package scheduler.model.db;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import scheduler.model.Appointment;
import scheduler.model.RelatedRecord;
import scheduler.util.DB;

/**
 * Represents a data row from the "appointment" database table.
 * <dl>
 * <dt>{@link scheduler.dao.AppointmentDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.AppointmentItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AppointmentRowData extends Appointment<Timestamp>, RelatedRecord {

    @Override
    public CustomerRowData getCustomer();

    @Override
    public UserRowData getUser();

    @Override
    public default boolean startEquals(Object value) {
        Timestamp start = getStart();
        if (null == start) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof Date) {
            return start.equals((Date) value);
        }

        Timestamp other;
        if (value instanceof ZonedDateTime) {
            other = DB.toUtcTimestamp((ZonedDateTime) value);
        } else if (value instanceof LocalDateTime) {
            other = DB.toUtcTimestamp((LocalDateTime) value);
        } else {
            return false;
        }
        return start.equals(other);
    }

    @Override
    public default int compareStart(Object value) {
        Timestamp start = getStart();
        if (null == start) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof Date) {
            return start.compareTo((Date) value);
        }

        Timestamp other;
        if (value instanceof ZonedDateTime) {
            other = DB.toUtcTimestamp((ZonedDateTime) value);
        } else if (value instanceof LocalDateTime) {
            other = DB.toUtcTimestamp((LocalDateTime) value);
        } else {
            return -1;
        }
        return start.compareTo(other);
    }

    @Override
    public default boolean endEquals(Object value) {
        Timestamp end = getEnd();
        if (null == end) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof Date) {
            return end.equals((Date) value);
        }

        Timestamp other;
        if (value instanceof ZonedDateTime) {
            other = DB.toUtcTimestamp((ZonedDateTime) value);
        } else if (value instanceof LocalDateTime) {
            other = DB.toUtcTimestamp((LocalDateTime) value);
        } else {
            return false;
        }
        return end.equals(other);
    }

    @Override
    public default int compareEnd(Object value) {
        Timestamp end = getEnd();
        if (null == end) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof Date) {
            return end.compareTo((Date) value);
        }

        Timestamp other;
        if (value instanceof ZonedDateTime) {
            other = DB.toUtcTimestamp((ZonedDateTime) value);
        } else if (value instanceof LocalDateTime) {
            other = DB.toUtcTimestamp((LocalDateTime) value);
        } else {
            return -1;
        }
        return end.compareTo(other);
    }

}
