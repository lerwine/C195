package scheduler.dao;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_GERMANY;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_GUATEMALA;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_HQ;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_INDIA;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_OTHER;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_PHONE;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL;
import scheduler.AppResources;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.city.SupportedLocale;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum AppointmentType {
    PHONE("phone", AppointmentLocationSource.LOCATION_FIELD, RESOURCEKEY_APPOINTMENTTYPE_PHONE),
    VIRTUAL("virtual", AppointmentLocationSource.URL_FIELD, RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL),
    CUSTOMER_SITE("customer", AppointmentLocationSource.CUSTOMER_ADDRESS, RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER),
    CORPORATE_HQ_MEETING("hq", AppointmentLocationSource.CORPORATE_HQ, RESOURCEKEY_APPOINTMENTTYPE_HQ, SupportedLocale.EN),
    GERMANY_SITE_MEETING("germany", AppointmentLocationSource.GERMANY_OFFICE, RESOURCEKEY_APPOINTMENTTYPE_GERMANY, SupportedLocale.DE),
    INDIA_SITE_MEETING("india", AppointmentLocationSource.INDIA_OFFICE, RESOURCEKEY_APPOINTMENTTYPE_INDIA, SupportedLocale.HI),
    GUATEMALA_SITE_MEETING("guatemala", AppointmentLocationSource.GUATEMALA_OFFICE, RESOURCEKEY_APPOINTMENTTYPE_GUATEMALA, SupportedLocale.ES),
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

    private final String dbValue;
    private final AppointmentLocationSource locationSource;
    private final String appResourceKey;
    private final Optional<SupportedLocale> defaultLocale;

    private AppointmentType(String dbValue, AppointmentLocationSource locationSource, String appResourceKey, SupportedLocale defaultLocale) {
        this.dbValue = dbValue;
        this.locationSource = locationSource;
        this.appResourceKey = appResourceKey;
        this.defaultLocale = (null == defaultLocale) ? Optional.empty() : Optional.of(defaultLocale);
    }

    private AppointmentType(String dbValue, AppointmentLocationSource locationSource, String appResourceKey) {
        this(dbValue, locationSource, appResourceKey, null);
    }
    
    public String getAppResourceKey() {
        return appResourceKey;
    }

    public AppointmentLocationSource getLocationSource() {
        return locationSource;
    }

    public Optional<SupportedLocale> getDefaultLocale() {
        return defaultLocale;
    }

    public static SupportedLocale getDefaultLocale(AppointmentModel model) {
        if (null != model) {
            AppointmentType t = model.getType();
            if (t.defaultLocale.isPresent()) {
                return t.defaultLocale.get();
            }
            if (model.getType() == CUSTOMER_SITE) {
                return SupportedLocale.getDefaultLocale(model.getCustomer());
            }
        }
        return SupportedLocale.fromLocale(Locale.getDefault());
    }
    
    public static SupportedLocale getDefaultLocale(AppointmentElement appointment) {
        if (null != appointment) {
            AppointmentType t = appointment.getType();
            if (t.defaultLocale.isPresent()) {
                return t.defaultLocale.get();
            }
            if (appointment.getType() == CUSTOMER_SITE) {
                return SupportedLocale.getDefaultLocale(appointment.getCustomer());
            }
        }
        return SupportedLocale.fromLocale(Locale.getDefault());
    }

    @Override
    public String toString() {
        return dbValue;
    }

    public static String toDisplayText(AppointmentType type) {
        if (null == type) {
            return AppResources.getResourceString(AppResources.RESOURCEKEY_NONE);
        }
        ResourceBundle rb = AppResources.getResources();
        String key = type.dbValue;
        return (rb.containsKey(key)) ? rb.getString(key) : key;
    }

}
