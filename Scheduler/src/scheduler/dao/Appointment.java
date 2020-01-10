package scheduler.dao;

import java.net.URL;
import java.sql.Timestamp;

/**
 * Represents a data row from the "appointment" database table.
 * 
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
    
    /**
     * Gets the {@link Customer} for the current appointment.
     * This corresponds to the "customer" data row referenced by the "customerId" database column.
     * 
     * @return The {@link Customer} for the current appointment.
     */
    Customer getCustomer();
    
    /**
     * Gets the {@link User} for the current appointment.
     * This corresponds to the "user" data row referenced by the "userId" database column.
     * 
     * @return The {@link User} for the current appointment.
     */
    User getUser();
    
    /**
     * Gets the title of the current appointment.
     * This corresponds to the "title" database column.
     * 
     * @return The title of the current appointment.
     */
    String getTitle();
    
    /**
     * Gets the description of the current appointment.
     * This corresponds to the "description" database column.
     * 
     * @return The description of the current appointment.
     */
    String getDescription();
    
    /**
     * Gets the explicit location of the current appointment.
     * This corresponds to the "location" database column.
     * If the appointment location is not a physical location or is an implicit location, then it will be encoded into the {@link url} field.
     * 
     * @return The explicit location of the current appointment.
     */
    String getLocation();
    
    /**
     * Gets the contact for the current appointment.
     * This corresponds to the "contact" database column.
     * 
     * @return The contact for the current appointment.
     */
    String getContact();
    
    /**
     * Gets the type of the current appointment.
     * This corresponds to the "type" database column.
     * 
     * @return {@link #APPOINTMENTTYPE_PHONE}, {@link #APPOINTMENTTYPE_VIRTUAL}, {@link #APPOINTMENTTYPE_CUSTOMER}, {@link #APPOINTMENTTYPE_HOME},
     * {@link #APPOINTMENTTYPE_GERMANY}, {@link #APPOINTMENTTYPE_INDIA}, {@link #APPOINTMENTTYPE_HONDURAS}, or {@link #APPOINTMENTTYPE_OTHER}.
     */
    String getType();
    
    /**
     * Gets the URL of the current appointment.
     * This corresponds to the "url" database column.
     * 
     * @return The URL of the current appointment.
     */
    URL getUrl();
    
    /**
     * Gets the start date and time of the current appointment.
     * This corresponds to the "start" database column.
     * 
     * @return The start date and time of the current appointment.
     */
    Timestamp getStart();
    
    /**
     * Gets the end date and time of the current appointment.
     * This corresponds to the "end" database column.
     * 
     * @return The end date and time of the current appointment.
     */
    Timestamp getEnd();
}
