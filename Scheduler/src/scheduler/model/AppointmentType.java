package scheduler.model;

import java.util.Optional;
import java.util.ResourceBundle;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CORPORATE;
import static scheduler.AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER;
import static scheduler.AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_OTHER;
import static scheduler.AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_PHONE;
import static scheduler.AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL;
import scheduler.AppResources;
import scheduler.dao.AppointmentLocationSource;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum AppointmentType {
    /**
     * Phone call appointment.
     * <ul>
     * <li>The string value {@code "phone"} is stored in the {@link scheduler.dao.AppointmentDAO#type} field.</li>
     * <li>Phone number is stored in the {@link scheduler.dao.AppointmentDAO#location} field.</li>
     * <li>{@link scheduler.dao.AppointmentDAO#url} and {@link scheduler.dao.AppointmentDAO#contact} are optional.</li>
     * </ul>
     */
    PHONE("phone", AppointmentLocationSource.LOCATION_FIELD, RESOURCEKEY_APPOINTMENTTYPE_PHONE),
    /**
     * Virtual online appointment.
     * <ul>
     * <li>The string value {@code "virtual"} is stored in the {@link scheduler.dao.AppointmentDAO#type} field.</li>
     * <li>{@link scheduler.dao.AppointmentDAO#url} is required.</li>
     * <li>{@link scheduler.dao.AppointmentDAO#location} and {@link scheduler.dao.AppointmentDAO#contact} are optional.</li>
     * </ul>
     */
    VIRTUAL("virtual", AppointmentLocationSource.URL_FIELD, RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL),
    /**
     * Appointment at address of customer.
     * <ul>
     * <li>The string value {@code "customer"} is stored in the {@link scheduler.dao.AppointmentDAO#type} field.</li>
     * <li>A copy of the customer's address is stored in the {@link scheduler.dao.AppointmentDAO#location} field.</li>
     * <li>{@link scheduler.dao.AppointmentDAO#url} and {@link scheduler.dao.AppointmentDAO#contact} are optional.</li>
     * </ul>
     */
    CUSTOMER_SITE("customer", AppointmentLocationSource.CUSTOMER_ADDRESS, RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER),
    /**
     * Appointment at a {@link scheduler.model.CorporateAddress}.
     * <ul>
     * <li>The string value {@code "corporate"} is stored in the {@link scheduler.dao.AppointmentDAO#type} field.</li>
     * <li>The value of the {@link scheduler.model.CorporateAddress#name} is stored in the
     * {@link scheduler.dao.AppointmentDAO#location} field.</li>
     * <li>{@link scheduler.dao.AppointmentDAO#url} and {@link scheduler.dao.AppointmentDAO#contact} are optional.</li>
     * </ul>
     */
    CORPORATE_LOCATION("corporate", AppointmentLocationSource.CORPORATE_LOCATION, RESOURCEKEY_APPOINTMENTTYPE_CORPORATE),
    /**
     * Appointment at a physical location.
     * <ul>
     * <li>The string value {@code "other"} is stored in the {@link scheduler.dao.AppointmentDAO#type} field.</li>
     * <li>The {@link scheduler.dao.AppointmentDAO#location} and {@link scheduler.dao.AppointmentDAO#contact} fields are required.</li>
     * <li>{@link scheduler.dao.AppointmentDAO#url} isS optional.</li>
     * </ul>
     */
    OTHER("other", AppointmentLocationSource.LOCATION_FIELD, RESOURCEKEY_APPOINTMENTTYPE_OTHER);

    public static AppointmentType of(String dbValue, AppointmentType defaultValue) {
        if (null != dbValue) {
            for (AppointmentType t : AppointmentType.values()) {
                if (t.dbValue.equalsIgnoreCase(dbValue)) {
                    return t;
                }
            }
        }
        return defaultValue;
    }

    public static Optional<AppointmentType> of(String dbValue) {
        if (null != dbValue) {
            for (AppointmentType t : AppointmentType.values()) {
                if (t.dbValue.equalsIgnoreCase(dbValue)) {
                    return Optional.of(t);
                }
            }
        }
        return Optional.empty();
    }

    public static String toDisplayText(AppointmentType type) {
        if (null == type) {
            return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_NONE);
        }
        ResourceBundle rb = AppResources.getResources();
        String key = "appointmentType_" + type.dbValue;
        return (rb.containsKey(key)) ? rb.getString(key) : key;
    }

    private final String dbValue;
    private final AppointmentLocationSource locationSource;
    private final String appResourceKey;

    private AppointmentType(String dbValue, AppointmentLocationSource locationSource, String appResourceKey) {
        this.dbValue = dbValue;
        this.locationSource = locationSource;
        this.appResourceKey = appResourceKey;
    }

    public String getAppResourceKey() {
        return appResourceKey;
    }

    public AppointmentLocationSource getLocationSource() {
        return locationSource;
    }

    @Override
    public String toString() {
        return dbValue;
    }

}
