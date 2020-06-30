package scheduler.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Interface for objects that contain either partial or complete information from the {@code appointment} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of object for date/time values
 */
public interface Appointment<T extends Serializable & Comparable<? super T>> extends DataObject {

    /**
     * The name of the 'customer' property.
     */
    public static final String PROP_CUSTOMER = "customer";

    /**
     * The name of the 'user' property.
     */
    public static final String PROP_USER = "user";

    public static final int MAX_LENGTH_TITLE = 255;

    /**
     * The name of the 'title' property.
     */
    public static final String PROP_TITLE = "title";

    public static final int MAX_LENGTH_DESCRIPTION = 65535;

    /**
     * The name of the 'description' property.
     */
    public static final String PROP_DESCRIPTION = "description";

    public static final int MAX_LENGTH_LOCATION = 65535;

    /**
     * The name of the 'location' property.
     */
    public static final String PROP_LOCATION = "location";

    public static final int MAX_LENGTH_CONTACT = 65535;

    /**
     * The name of the 'contact' property.
     */
    public static final String PROP_CONTACT = "contact";

    public static final int MAX_LENGTH_TYPE = 65535;

    /**
     * The name of the 'type' property.
     */
    public static final String PROP_TYPE = "type";

    public static final int MAX_LENGTH_URL = 255;

    /**
     * The name of the 'url' property.
     */
    public static final String PROP_URL = "url";

    /**
     * The name of the 'start' property.
     */
    public static final String PROP_START = "start";

    /**
     * The name of the 'end' property.
     */
    public static final String PROP_END = "end";

    public static int compare(Appointment<?> a, Appointment<?> b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = a.compareStart(b.getStart());
        if (result == 0 && (result = a.compareEnd(b.getEnd())) == 0 && (result = Customer.compare(a.getCustomer(), b.getCustomer())) == 0
                && (result = User.compare(a.getUser(), b.getUser())) == 0) {
            return a.getPrimaryKey() - b.getPrimaryKey();
        }
        return result;
    }

    public static int compareByDates(Appointment<?> a, Appointment<?> b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = a.compareStart(b.getStart());
        return (result == 0) ? a.compareEnd(b.getEnd()) : result;
    }

    public static boolean arePropertiesEqual(Appointment<?> a, Appointment<?> b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || (ModelHelper.areSameRecord(a.getCustomer(), b.getCustomer())
                && ModelHelper.areSameRecord(a.getUser(), b.getUser())
                && a.getContact().equalsIgnoreCase(b.getContact())
                && a.getDescription().equalsIgnoreCase(b.getDescription())
                && a.getLocation().equalsIgnoreCase(b.getLocation())
                && a.getType() == b.getType()
                && a.getTitle().equalsIgnoreCase(b.getTitle())
                && a.getUrl().equalsIgnoreCase(b.getUrl())
                && a.startEquals(b.getStart())
                && a.endEquals(b.getEnd())));
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
     * <dl>
     * <dt>{@link #getType()} = {@link AppointmentType#PHONE}</dt>
     * <dd>Required - contains phone number.</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#VIRTUAL}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#CUSTOMER_SITE}</dt>
     * <dd>{@code =} {@link scheduler.model.ui.AddressModel#calculateMultiLineAddress(java.lang.String, java.lang.String, java.lang.String)} &lArr;
     * {@link scheduler.dao.CustomerDAO#address}</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#CORPORATE_LOCATION}</dt>
     * <dd>{@code =} {@link scheduler.model.CorporateAddress#name}</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#OTHER}</dt>
     * <dd>Required - Contains address text.</dd>
     * </dl>
     * 
     * @return The explicit location of the current appointment.
     */
    String getLocation();

    /**
     * Gets the contact for the current appointment. This corresponds to the "contact" database column. Column definition:
     * <code>`contact` text NOT NULL</code>
     * <dl>
     * <dt>{@link #getType()} = {@link AppointmentType#PHONE}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#VIRTUAL}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#CUSTOMER_SITE}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#CORPORATE_LOCATION}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#OTHER}</dt>
     * <dd>Required</dd>
     * </dl>
     *
     * @return The contact for the current appointment.
     */
    String getContact();

    /**
     * Gets the contact for the current appointment. This corresponds to the "contact" database column. Column definition:
     * <code>`type` text NOT NULL</code>
     *
     * @return The current appointment type.
     */
    AppointmentType getType();

    /**
     * Gets the URL of the current appointment. This corresponds to the "url" database column. Column definition:
     * <code>`url` varchar(255) NOT NULL</code>
     * <dl>
     * <dt>{@link #getType()} = {@link AppointmentType#PHONE}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#VIRTUAL}</dt>
     * <dd>Required</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#CUSTOMER_SITE}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#CORPORATE_LOCATION}</dt>
     * <dd>Optional</dd>
     * <dt>{@link #getType()} = {@link AppointmentType#OTHER}</dt>
     * <dd>Optional</dd>
     * </dl>
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
