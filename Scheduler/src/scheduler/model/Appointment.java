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
     * The name of the {@link #getCustomer() 'customer'} property.
     */
    public static final String PROP_CUSTOMER = "customer";

    /**
     * The name of the {@link #getUser() 'user'} property.
     */
    public static final String PROP_USER = "user";

    /**
     * The maximum allowed length for the {@link #getTitle() 'title'} property.
     */
    public static final int MAX_LENGTH_TITLE = 255;

    /**
     * The name of the {@link #getTitle() 'title'} property.
     */
    public static final String PROP_TITLE = "title";

    /**
     * The maximum allowed length for the {@link #getDescription() 'description'} property.
     */
    public static final int MAX_LENGTH_DESCRIPTION = 65535;

    /**
     * The name of the {@link #getDescription() 'description'} property.
     */
    public static final String PROP_DESCRIPTION = "description";

    /**
     * The maximum allowed length for the {@link #getLocation() 'location'} property.
     */
    public static final int MAX_LENGTH_LOCATION = 65535;

    /**
     * The name of the {@link #getLocation() 'location'} property.
     */
    public static final String PROP_LOCATION = "location";

    /**
     * The maximum allowed length for the {@link #getContact() 'contact'} property.
     */
    public static final int MAX_LENGTH_CONTACT = 65535;

    /**
     * The name of the {@link #getContact() 'contact'} property.
     */
    public static final String PROP_CONTACT = "contact";

    /**
     * The maximum allowed length for the {@link #getType() 'type'} property.
     */
    public static final int MAX_LENGTH_TYPE = 65535;

    /**
     * The name of the {@link #getType() 'type'} property.
     */
    public static final String PROP_TYPE = "type";

    /**
     * The maximum allowed length for the {@link #getUrl() 'url'} property.
     */
    public static final int MAX_LENGTH_URL = 255;

    /**
     * The name of the {@link #getUrl() 'url'} property.
     */
    public static final String PROP_URL = "url";

    /**
     * The name of the {@link #getStart() 'start'} property.
     */
    public static final String PROP_START = "start";

    /**
     * The name of the {@link #getEnd() 'end'} property.
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
     * Gets the {@link Customer} for the current appointment. This corresponds to the "customer" data row referenced by the "customerId" database column.
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
     * Gets the title of the current appointment. Associated database column definition: <code>`title` varchar(255) NOT NULL</code>
     *
     * @return The title of the current appointment.
     */
    String getTitle();

    /**
     * Gets the description of the current appointment. Associated database column definition: <code>`description` text NOT NULL</code>
     *
     * @return The description of the current appointment.
     */
    String getDescription();

    /**
     * Gets the location of the current appointment. The usage of this field depends upon the value in the {@link #getType() type} field. Associated database column definition:
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
     * Gets the contact for the current appointment. Associated database column definition: <code>`contact` text NOT NULL</code>
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
     * Gets the contact for the current appointment. Associated database column definition: <code>`type` text NOT NULL</code>
     * <table border="1" cellspacing="0">
     * <tr>
     * <th scope="col" rowspan="2">Value</th>
     * <th scope="col" colspan="3">Field Content / Validation Constraints</th>
     * </tr>
     * <tr>
     * <th scope="col">{@link #getLocation() Location}</th>
     * <th scope="col">{@link #getContact() Contact}</th>
     * <th scope="col">{@link Appointment#getUrl() URL}</th>
     * </tr>
     * <tr>
     * <th scope="row" align="right">{@link AppointmentType#PHONE PHONE}:</th>
     * <td>Required - Contains phone number (single-line)</td>
     * <td>Optional</td>
     * <td>Optional</td>
     * </tr>
     * <tr>
     * <th scope="row" align="right">{@link AppointmentType#VIRTUAL VIRTUAL}:</th>
     * <td>Optional</td>
     * <td>Optional</td>
     * <td>Required</td>
     * </tr>
     * <tr>
     * <th scope="row" align="right">{@link AppointmentType#CUSTOMER_SITE CUSTOMER_SITE}:</th>
     * <td>{@code =} {@link scheduler.model.ui.AddressModel#calculateMultiLineAddress(String, String, String)} &lArr; {@link Customer#getAddress() Customer#address}</td>
     * <td>Optional</td>
     * <td>Optional</td>
     * </tr>
     * <tr>
     * <th scope="row" align="right">{@link AppointmentType#CORPORATE_LOCATION CORPORATE_LOCATION}:</th>
     * <td>{@code =} {@link CorporateAddress#name}</td>
     * <td>Optional</td>
     * <td>Optional</td>
     * </tr>
     * <tr>
     * <th scope="row" align="right">{@link AppointmentType#OTHER OTHER}:</th>
     * <td>Required Address (multi-line)</td>
     * <td>Required</td>
     * <td>Optional</td>
     * </tr>
     * </table>
     *
     * @return The current appointment type.
     */
    AppointmentType getType();

    /**
     * Gets the URL of the current appointment. Associated database column definition: <code>`url` varchar(255) NOT NULL</code>
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
     * Gets the start date and time of the current appointment. Associated database column definition: <code>`start` datetime NOT NULL</code>
     *
     * @return The start date and time of the current appointment.
     */
    T getStart();

    /**
     * Gets the end date and time of the current appointment. Associated database column definition: <code>`end` datetime NOT NULL</code>
     *
     * @return The end date and time of the current appointment.
     */
    T getEnd();

    /**
     * Tests whether the current {@link #getStart() start} date/time represents the same point in time as a
     * {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date} value.
     *
     * @param value The {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date} to compare to.
     * @return {@code true} if the current {@link #getStart() start} date/time represents the same point in time as the given {@code value}; otherwise, {@code false}.
     */
    boolean startEquals(Object value);

    /**
     * Compares the current {@link #getStart() start} date is equal to another {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or
     * {@link java.util.Date} value.
     *
     * @param value The {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date} to compare to.
     * @return Less than zero if the current {@link #getStart() start} date/time is older; Greater than zero if the current {@link #getStart() start} date/time is newer; otherwise,
     * zero if given {@code value} represents the same point in time as the current {@link #getStart() start} date/time.
     * @throws IllegalArgumentException if the given {@code value} is not a {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date}
     * object.
     */
    int compareStart(Object value);

    /**
     * Tests whether the current {@link #getEnd() end} date/time represents the same point in time as a
     * {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date} value.
     *
     * @param value The {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date} to compare to.
     * @return {@code true} if the current {@link #getEnd() end} date/time represents the same point in time as the given {@code value}; otherwise, {@code false}.
     */
    boolean endEquals(Object value);

    /**
     * Compares the current {@link #getEnd() end} date is equal to another {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date}
     * value.
     *
     * @param value The {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date} to compare to.
     * @return Less than zero if the current {@link #getEnd() end} date/time is older; Greater than zero if the current {@link #getEnd() end} date/time is newer; otherwise, zero if
     * given {@code value} represents the same point in time as the current {@link #getEnd() end} date/time.
     * @throws IllegalArgumentException if the given {@code value} is not a {@link java.time.chrono.ChronoLocalDateTime}, {@link java.time.ZonedDateTime} or {@link java.util.Date}
     * object.
     */
    int compareEnd(Object value);

}
