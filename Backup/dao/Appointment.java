package scheduler.dao;

import java.sql.Timestamp;

/**
 * Represents a data row from the "appointment" database table. Table definition: <code>CREATE TABLE `appointment` (
 *   `appointmentId` int(10) NOT NULL AUTO_INCREMENT,
 *   `customerId` int(10) NOT NULL,
 *   `userId` int(11) NOT NULL,
 *   `title` varchar(255) NOT NULL,
 *   `description` text NOT NULL,
 *   `location` text NOT NULL,
 *   `contact` text NOT NULL,
 *   `type` text NOT NULL,
 *   `url` varchar(255) NOT NULL,
 *   `start` datetime NOT NULL,
 *   `end` datetime NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`appointmentId`),
 *   KEY `userId` (`userId`),
 *   KEY `appointment_ibfk_1` (`customerId`),
 *   CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`customerId`),
 *   CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;</code>
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface Appointment extends DataObject {

    /**
     * Gets the {@link Customer} for the current appointment. This corresponds to the "customer" data row referenced by the "customerId" database column. Column definition:
     * <code>`customerId` int(10) NOT NULL</code> Key constraint definition:
     * <code>CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`customerId`)</code>
     *
     * @return The {@link Customer} for the current appointment.
     */
    DataObjectReference<CustomerImpl, Customer> getCustomer();

    /**
     * Gets the {@link User} for the current appointment. This corresponds to the "user" data row referenced by the "userId" database column. Column definition:
     * <code>`userId` int(11) NOT NULL</code> Key constraint definition: <code>CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)</code>
     *
     * @return The {@link User} for the current appointment.
     */
    DataObjectReference<UserImpl, User> getUser();

    /**
     * Gets the title of the current appointment. This corresponds to the "title" database column. Column definition: <code>`title` varchar(255) NOT NULL</code>
     *
     * @return The title of the current appointment.
     */
    String getTitle();

    /**
     * Gets the description of the current appointment. This corresponds to the "description" database column. Column definition: <code>`description` text NOT NULL</code>
     *
     * @return The description of the current appointment.
     */
    String getDescription();

    /**
     * Gets the explicit location of the current appointment. This corresponds to the "location" database column. If the appointment location is not a physical location or is an
     * implicit location, then it will be encoded into the url field. Column definition: <code>`location` text NOT NULL</code>
     *
     * @return The explicit location of the current appointment.
     */
    String getLocation();

    /**
     * Gets the contact for the current appointment. This corresponds to the "contact" database column. Column definition: <code>`contact` text NOT NULL</code>
     *
     * @return The contact for the current appointment.
     */
    String getContact();

    /**
     * Gets the type of the current appointment. This corresponds to the "type" database column. Column definition: <code>`type` text NOT NULL</code>
     *
     * @return {@link scheduler.util.Values#APPOINTMENTTYPE_PHONE}, {@link scheduler.util.Values#APPOINTMENTTYPE_VIRTUAL},
     * {@link scheduler.util.Values#APPOINTMENTTYPE_CUSTOMER}, {@link scheduler.util.Values#APPOINTMENTTYPE_HOME},
     * {@link scheduler.util.Values#APPOINTMENTTYPE_GERMANY}, {@link scheduler.util.Values#APPOINTMENTTYPE_INDIA},
     * {@link scheduler.util.Values#APPOINTMENTTYPE_HONDURAS}, or {@link scheduler.util.Values#APPOINTMENTTYPE_OTHER}.
     */
    String getType();

    /**
     * Gets the URL of the current appointment. This corresponds to the "url" database column. Column definition: <code>`url` varchar(255) NOT NULL</code>
     *
     * @return The URL of the current appointment.
     */
    String getUrl();

    /**
     * Gets the start date and time of the current appointment. This corresponds to the "start" database column. Column definition: <code>`start` datetime NOT NULL</code>
     *
     * @return The start date and time of the current appointment.
     */
    Timestamp getStart();

    /**
     * Gets the end date and time of the current appointment. This corresponds to the "end" database column. Column definition: <code>`end` datetime NOT NULL</code>
     *
     * @return The end date and time of the current appointment.
     */
    Timestamp getEnd();
}