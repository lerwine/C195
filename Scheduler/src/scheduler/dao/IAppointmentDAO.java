package scheduler.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import scheduler.model.Appointment;
import scheduler.model.PredefinedData;
import scheduler.util.DB;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IAppointmentDAO extends DbObject, Appointment<Timestamp> {

    public static <T extends IAppointmentDAO> T assertValidAppointment(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Appointment has already been deleted");
        }
        ICustomerDAO customer = target.getCustomer();
        if (null == customer) {
            throw new IllegalStateException("Customer not specified");
        }
        ICustomerDAO.assertValidCustomer(customer);
        IUserDAO user = target.getUser();
        if (null == user) {
            throw new IllegalStateException("User not specified");
        }
        IUserDAO.assertValidUser(user);
        if (Values.isNullWhiteSpaceOrEmpty(target.getTitle())) {
            throw new IllegalStateException("Title not defined");
        }
        Timestamp start = target.getStart();
        if (null == start) {
            throw new IllegalStateException("Start date/time not defined");
        }
        Timestamp end = target.getEnd();
        if (null == end) {
            throw new IllegalStateException("End date/time not defined");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalStateException("Start is after end date/time");
        }
        switch (target.getType()) {
            case CORPORATE_LOCATION:
                if (Values.isNullWhiteSpaceOrEmpty(target.getLocation())) {
                    throw new IllegalStateException("Location not defined");
                }
                if (!PredefinedData.getAddressMap().containsKey(target.getLocation())) {
                    throw new IllegalStateException("Invalid corporate location key");
                }
                break;
            case VIRTUAL:
                if (Values.isNullWhiteSpaceOrEmpty(target.getUrl())) {
                    throw new IllegalStateException("URL not defined");
                }
                break;
            case CUSTOMER_SITE:
                if (Values.isNullWhiteSpaceOrEmpty(target.getContact())) {
                    throw new IllegalStateException("Contact not defined");
                }
                break;
            default:
                if (Values.isNullWhiteSpaceOrEmpty(target.getLocation())) {
                    throw new IllegalStateException("Location not defined");
                }
                break;
        }
        return target;
    }

    @Override
    public ICustomerDAO getCustomer();

    @Override
    public IUserDAO getUser();
    
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
