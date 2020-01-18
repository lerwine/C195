package scheduler.dao;

import java.net.URL;
import java.sql.Timestamp;

/**
 * Represents a data row from the "appointment" database table.
 * Table definition: <code>CREATE TABLE `appointment` (
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
 * @author erwinel
 */
public interface Appointment extends DataObject {
    //<editor-fold defaultstate="collapsed" desc="Appointment type values">
    
    /**
     * The value of {@link #getType()} when the appointment is a phone-based meeting.
     * {@link #getUrl()} returns the telephone number encoded as a URL using the format "tel:+" + international_code + "-" + phone_number
     * and {@link #getLocation()} returns an empty string for this appointment type.
     */
    public final String APPOINTMENTTYPE_PHONE = "phone";
    
    /**
     * The value of {@link #getType()} when the appointment is an online virtual meeting.
     * {@link #getUrl()} returns the internet address of the virtual meeting and {@link #getLocation()} returns an empty string for this appointment type.
     */
    public final String APPOINTMENTTYPE_VIRTUAL = "virtual";
    
    /**
     * The value of {@link #getType()} when the appointment located at the customer address.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public final String APPOINTMENTTYPE_CUSTOMER = "customer";
    
    /**
     * The value of {@link #getType()} when the appointment is at the home (USA) office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public final String APPOINTMENTTYPE_HOME = "home";
    
    /**
     * The value of {@link #getType()} when the appointment is at the Germany office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public final String APPOINTMENTTYPE_GERMANY = "germany";
    
    /**
     * The value of {@link #getType()} when the appointment is at the India office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public final String APPOINTMENTTYPE_INDIA = "india";
    
    /**
     * The value of {@link #getType()} when the appointment is at the Honduras office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public final String APPOINTMENTTYPE_HONDURAS = "honduras";
    
    /**
     * The value of {@link #getType()} when the appointment is at an explicit address returned by {@link #getLocation()}.
     * {@link #getUrl()} returns an empty string for this appointment type.
     */
    public final String APPOINTMENTTYPE_OTHER = "other";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    /**
     * The name of the 'appointmentId' column in the 'appointment' table, which is also the primary key.
     */
    public static final String COLNAME_APPOINTMENTID = "appointmentId";
    
    /**
     * The name of the 'customerId' column in the 'appointment' table.
     */
    public static final String COLNAME_CUSTOMERID = "customerId";
    
    /**
     * The name of the 'userId' column in the 'appointment' table.
     */
    public static final String COLNAME_USERID = "userId";
    
    /**
     * The name of the 'title' column in the 'appointment' table.
     */
    public static final String COLNAME_TITLE = "title";
    
    /**
     * The name of the 'description' column in the 'appointment' table.
     */
    public static final String COLNAME_DESCRIPTION = "description";
    
    /**
     * The name of the 'location' column in the 'appointment' table.
     */
    public static final String COLNAME_LOCATION = "location";
    
    /**
     * The name of the 'contact' column in the 'appointment' table.
     */
    public static final String COLNAME_CONTACT = "contact";
    
    /**
     * The name of the 'type' column in the 'appointment' table.
     */
    public static final String COLNAME_TYPE = "type";
    
    /**
     * The name of the 'url' column in the 'appointment' table.
     */
    public static final String COLNAME_URL = "url";
    
    /**
     * The name of the 'start' column in the 'appointment' table.
     */
    public static final String COLNAME_START = "start";
    
    /**
     * The name of the 'end' column in the 'appointment' table.
     */
    public static final String COLNAME_END = "end";
    
    //</editor-fold>
    
    public static String asValidAppointmentType(String value) {
        if (value != null) {
            if ((value = value.trim()).equalsIgnoreCase(Appointment.APPOINTMENTTYPE_CUSTOMER))
                return Appointment.APPOINTMENTTYPE_CUSTOMER;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_GERMANY))
                return Appointment.APPOINTMENTTYPE_GERMANY;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_HOME))
                return Appointment.APPOINTMENTTYPE_HOME;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_HONDURAS))
                return Appointment.APPOINTMENTTYPE_HONDURAS;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_INDIA))
                return Appointment.APPOINTMENTTYPE_INDIA;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_PHONE))
                return Appointment.APPOINTMENTTYPE_PHONE;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_VIRTUAL))
                return Appointment.APPOINTMENTTYPE_VIRTUAL;
        }
        return Appointment.APPOINTMENTTYPE_OTHER;
    }
    
    /**
     * Gets the {@link Customer} for the current appointment.
     * This corresponds to the "customer" data row referenced by the "customerId" database column.
     * Column definition: <code>`customerId` int(10) NOT NULL</code>
     * Key constraint definition: <code>CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`customerId`)</code>
     * @return The {@link Customer} for the current appointment.
     */
    Customer getCustomer();
    
    /**
     * Gets the {@link User} for the current appointment.
     * This corresponds to the "user" data row referenced by the "userId" database column.
     * Column definition: <code>`userId` int(11) NOT NULL</code>
     * Key constraint definition: <code>CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)</code>
     * @return The {@link User} for the current appointment.
     */
    User getUser();
    
    /**
     * Gets the title of the current appointment.
     * This corresponds to the "title" database column.
     * Column definition: <code>`title` varchar(255) NOT NULL</code>
     * @return The title of the current appointment.
     */
    String getTitle();
    
    /**
     * Gets the description of the current appointment.
     * This corresponds to the "description" database column.
     * Column definition: <code>`description` text NOT NULL</code>
     * @return The description of the current appointment.
     */
    String getDescription();
    
    /**
     * Gets the explicit location of the current appointment.
     * This corresponds to the "location" database column.
     * If the appointment location is not a physical location or is an implicit location, then it will be encoded into the {@link url} field.
     * Column definition: <code>`location` text NOT NULL</code>
     * @return The explicit location of the current appointment.
     */
    String getLocation();
    
    /**
     * Gets the contact for the current appointment.
     * This corresponds to the "contact" database column.
     * Column definition: <code>`contact` text NOT NULL</code>
     * @return The contact for the current appointment.
     */
    String getContact();
    
    /**
     * Gets the type of the current appointment.
     * This corresponds to the "type" database column.
     * Column definition: <code>`type` text NOT NULL</code>
     * @return {@link #APPOINTMENTTYPE_PHONE}, {@link #APPOINTMENTTYPE_VIRTUAL}, {@link #APPOINTMENTTYPE_CUSTOMER}, {@link #APPOINTMENTTYPE_HOME},
     * {@link #APPOINTMENTTYPE_GERMANY}, {@link #APPOINTMENTTYPE_INDIA}, {@link #APPOINTMENTTYPE_HONDURAS}, or {@link #APPOINTMENTTYPE_OTHER}.
     */
    String getType();
    
    /**
     * Gets the URL of the current appointment.
     * This corresponds to the "url" database column.
     * Column definition: <code>`url` varchar(255) NOT NULL</code>
     * @return The URL of the current appointment.
     */
    String getUrl();
    
    /**
     * Gets the start date and time of the current appointment.
     * This corresponds to the "start" database column.
     * Column definition: <code>`start` datetime NOT NULL</code>
     * @return The start date and time of the current appointment.
     */
    Timestamp getStart();
    
    /**
     * Gets the end date and time of the current appointment.
     * This corresponds to the "end" database column.
     * Column definition: <code>`end` datetime NOT NULL</code>
     * @return The end date and time of the current appointment.
     */
    Timestamp getEnd();
}
