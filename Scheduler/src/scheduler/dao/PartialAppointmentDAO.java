package scheduler.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import scheduler.model.Appointment;
import scheduler.util.DB;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface PartialAppointmentDAO extends PartialDataAccessObject, Appointment<Timestamp> {

    @Override
    public PartialCustomerDAO getCustomer();

    @Override
    public PartialUserDAO getUser();

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

        if (value instanceof ZonedDateTime) {
            return start.compareTo(DB.toUtcTimestamp((ZonedDateTime) value));
        }

        if (value instanceof LocalDateTime) {
            return start.compareTo(DB.toUtcTimestamp((LocalDateTime) value));
        }

        throw new IllegalArgumentException();
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

        if (value instanceof ZonedDateTime) {
            return end.compareTo(DB.toUtcTimestamp((ZonedDateTime) value));
        }

        if (value instanceof LocalDateTime) {
            return end.compareTo(DB.toUtcTimestamp((LocalDateTime) value));
        }

        throw new IllegalArgumentException();
    }

}
