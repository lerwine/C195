package scheduler.dao;

import java.util.Optional;
import java.util.ResourceBundle;
import scheduler.AppResources;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public enum AppointmentType {
    PHONE("phone", AppointmentLocationSource.URL_FIELD, FieldUsage.ENCODED_PHONE_NUMER),
    VIRTUAL("virtual", AppointmentLocationSource.URL_FIELD, FieldUsage.REQUIRED),
    CUSTOMER_SITE("customer", AppointmentLocationSource.CUSTOMER_ADDRESS, FieldUsage.OPTIONAL),
    CORPORATE_HQ_MEETING("hq", AppointmentLocationSource.CORPORATE_HQ, FieldUsage.OPTIONAL),
    GERMANY_SITE_MEETING("germany", AppointmentLocationSource.GERMANY_OFFICE, FieldUsage.OPTIONAL),
    INDIA_SITE_MEETING("india", AppointmentLocationSource.INDIA_OFFICE, FieldUsage.OPTIONAL),
    HONDURAS_SITE_MEETING("honduras", AppointmentLocationSource.HONDURAS_OFFICE, FieldUsage.OPTIONAL),
    OTHER("other", AppointmentLocationSource.LOCATION_FIELD, FieldUsage.OPTIONAL);

    public static AppointmentType of(String dbValue, AppointmentType defaultValue) {
        if (null != dbValue) {
            for (AppointmentType t : AppointmentType.values()) {
                if (t.getDbValue().equalsIgnoreCase(dbValue)) {
                    return t;
                }
            }
        }
        return defaultValue;
    }

    public static Optional<AppointmentType> of(String dbValue) {
        if (null != dbValue) {
            for (AppointmentType t : AppointmentType.values()) {
                if (t.getDbValue().equalsIgnoreCase(dbValue)) {
                    return Optional.of(t);
                }
            }
        }
        return Optional.empty();
    }
    
    private final String dbValue;
    private final AppointmentLocationSource locationSource;
    private final FieldUsage urlFieldusage;

    private AppointmentType(String dbValue, AppointmentLocationSource locationSource, FieldUsage urlFieldusage) {
        this.dbValue = dbValue;
        this.locationSource = locationSource;
        this.urlFieldusage = urlFieldusage;
    }

    public String getDbValue() {
        return dbValue;
    }

    public AppointmentLocationSource getLocationSource() {
        return locationSource;
    }

    public FieldUsage getUrlFieldusage() {
        return urlFieldusage;
    }

    public static String toAppointmentTypeDisplay(AppointmentType type) {
        if (null == type) {
            return AppResources.getResourceString(AppResources.RESOURCEKEY_NONE);
        }
        ResourceBundle rb = AppResources.getResources();
        String key = type.getDbValue();
        return (rb.containsKey(key)) ? rb.getString(key) : key;
    }

}
