package scheduler.model;

import java.io.Serializable;

/**
 * Interface for objects that contain either partial or complete information from the {@code appointment} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of object for date/time values
 */
public interface Appointment<T extends Serializable & Comparable<? super T>> extends DataObject {

    public static boolean arePropertiesEqual(Appointment<?> a, Appointment<?> b) {
        if (null == a) {
            return null == b;
        }
        if (a == b) {
            return true;
        }
        return null != b && ModelHelper.areSameRecord(a.getCustomer(), b.getCustomer())
                && ModelHelper.areSameRecord(a.getUser(), b.getUser())
                && a.getContact().equalsIgnoreCase(b.getContact())
                && a.getDescription().equalsIgnoreCase(b.getDescription())
                && a.getLocation().equalsIgnoreCase(b.getLocation())
                && a.getType() == b.getType()
                && a.getTitle().equalsIgnoreCase(b.getTitle())
                && a.getUrl().equalsIgnoreCase(b.getUrl())
                && a.startEquals(b.getStart())
                && a.endEquals(b.getEnd());
    }

    public static int compareByDates(Appointment<?> a, Appointment<?> b) {
        if (null == a) {
            return (null == b) ? 0 : 1;
        }
        if (null == b) {
            return -1;
        }

        int result = a.compareStart(b.getStart());
        return (result == 0) ? a.compareEnd(b.getEnd()) : result;
    }

    /**
     * Gets the {@link Customer} for the current appointment. This corresponds to the "customer" data row referenced by the "customerId" database
     * column.
     *
     * @return The {@link Customer} for the current appointment.
     */
    Customer getCustomer();

    /**
     * Gets the {@link User} for the current appointment. This corresponds to the "user" data row referenced by the "userId" database column.
     *
     * @return The {@link User} for the current appointment.
     */
    User getUser();

    /**
     * Gets the title of the current appointment. This corresponds to the "title" database column. Column definition:
     * <code>`title` varchar(255) NOT NULL</code>
     *
     * @return The title of the current appointment.
     */
    String getTitle();

    /**
     * Gets the description of the current appointment. This corresponds to the "description" database column. Column definition:
     * <code>`description` text NOT NULL</code>
     *
     * @return The description of the current appointment.
     */
    String getDescription();

    /**
     * Gets the explicit location of the current appointment. This corresponds to the "location" database column. If the appointment location is not a
     * physical location or is an implicit location, then it will be encoded into the url field. Column definition:
     * <code>`location` text NOT NULL</code>
     *
     * @return The explicit location of the current appointment.
     */
    String getLocation();

    /**
     * Gets the contact for the current appointment. This corresponds to the "contact" database column. Column definition:
     * <code>`contact` text NOT NULL</code>
     *
     * @return The contact for the current appointment.
     */
    String getContact();

    AppointmentType getType();

    /**
     * Gets the URL of the current appointment. This corresponds to the "url" database column. Column definition:
     * <code>`url` varchar(255) NOT NULL</code>
     *
     * @return The URL of the current appointment.
     */
    String getUrl();

    /**
     * Gets the start date and time of the current appointment. This corresponds to the "start" database column. Column definition:
     * <code>`start` datetime NOT NULL</code>
     *
     * @return The start date and time of the current appointment.
     */
    T getStart();

    /**
     * Gets the end date and time of the current appointment. This corresponds to the "end" database column. Column definition:
     * <code>`end` datetime NOT NULL</code>
     *
     * @return The end date and time of the current appointment.
     */
    T getEnd();

    boolean startEquals(Object value);

    int compareStart(Object value);

    boolean endEquals(Object value);

    int compareEnd(Object value);

}
