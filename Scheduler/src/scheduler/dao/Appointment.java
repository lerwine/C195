package scheduler.dao;

import java.sql.Timestamp;

/**
 * Represents a data row from the "appointment" database table.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link Customer} data access object.
 * @param <U> The type of {@link User} data access object.
 */
public interface Appointment<T extends Customer<? extends Address>, U extends User> extends DataObject {

    /**
     * Gets the {@link Customer} for the current appointment. This corresponds to the "customer" data row referenced by the "customerId" database column.
     *
     * @return The {@link Customer} for the current appointment.
     */
    T getCustomer();

    /**
     * Gets the {@link User} for the current appointment. This corresponds to the "user" data row referenced by the "userId" database column.
     *
     * @return The {@link User} for the current appointment.
     */
    U getUser();

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
    Timestamp getStart();

    /**
     * Gets the end date and time of the current appointment. This corresponds to the "end" database column. Column definition:
     * <code>`end` datetime NOT NULL</code>
     *
     * @return The end date and time of the current appointment.
     */
    Timestamp getEnd();
}
