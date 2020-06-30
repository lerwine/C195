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
     * <dl>
     * <dt>{@link scheduler.dao.AppointmentDAO#type}</dt>
     * <dd>{@code = "phone"}</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#location}</dt>
     * <dd>Required - Contains phone number</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#contact}</dt>
     * <dd>Optional</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#url}</dt>
     * <dd>Optional</dd>
     * </dl>
     */
    PHONE("phone", AppointmentLocationSource.LOCATION_FIELD, RESOURCEKEY_APPOINTMENTTYPE_PHONE),
    /**
     * Virtual online appointment.
     * <dl>
     * <dt>{@link scheduler.dao.AppointmentDAO#type}</dt>
     * <dd>{@code = "virtual"}</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#location}</dt>
     * <dd>Optional</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#contact}</dt>
     * <dd>Optional</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#url}</dt>
     * <dd>Required</dd>
     * </dl>
     */
    VIRTUAL("virtual", AppointmentLocationSource.URL_FIELD, RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL),
    /**
     * Appointment at address of customer.
     * <dl>
     * <dt>{@link scheduler.dao.AppointmentDAO#type}</dt>
     * <dd>{@code = "customer"}</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#location}</dt>
     * <dd>{@code =} {@link scheduler.model.ui.AddressModel#calculateMultiLineAddress(java.lang.String, java.lang.String, java.lang.String)} &lArr;
     * {@link scheduler.dao.CustomerDAO#address}</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#contact}</dt>
     * <dd>Optional</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#url}</dt>
     * <dd>Optional</dd>
     * </dl>
     */
    CUSTOMER_SITE("customer", AppointmentLocationSource.CUSTOMER_ADDRESS, RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER),
    /**
     * Appointment at a {@link scheduler.model.CorporateAddress}.
     * <dl>
     * <dt>{@link scheduler.dao.AppointmentDAO#type}</dt>
     * <dd>{@code = "corporate"}</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#location}</dt>
     * <dd>{@code =} {@link scheduler.model.CorporateAddress#name}</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#contact}</dt>
     * <dd>Optional</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#url}</dt>
     * <dd>Optional</dd>
     * </dl>
     */
    CORPORATE_LOCATION("corporate", AppointmentLocationSource.CORPORATE_LOCATION, RESOURCEKEY_APPOINTMENTTYPE_CORPORATE),
    /**
     * Appointment at a physical location.
     * <dl>
     * <dt>{@link scheduler.dao.AppointmentDAO#type}</dt>
     * <dd>{@code = "other"}</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#location}</dt>
     * <dd>Required</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#contact}</dt>
     * <dd>Required</dd>
     * <dt>{@link scheduler.dao.AppointmentDAO#url}</dt>
     * <dd>Optional</dd>
     * </dl>
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
