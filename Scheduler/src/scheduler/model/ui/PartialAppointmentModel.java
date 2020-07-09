package scheduler.model.ui;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.model.Appointment;
import scheduler.model.AppointmentType;
import scheduler.model.Customer;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.util.DB;
import scheduler.dao.PartialAppointmentDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface PartialAppointmentModel<T extends PartialAppointmentDAO> extends Appointment<LocalDateTime>, PartialEntityModel<T> {

    public static final String PROP_EFFECTIVELOCATION = "effectiveLocation";
    public static final String PROP_TYPEDISPLAY = "typeDisplay";
    public static final String PROP_USERSTATUSDISPLAY = "userStatusDisplay";
    public static final String PROP_USERSTATUS = "userStatus";
    public static final String PROP_USERNAME = "userName";
    public static final String PROP_CUSTOMERACTIVE = "customerActive";
    public static final String PROP_CUSTOMERADDRESSTEXT = "customerAddressText";
    public static final String PROP_CUSTOMERCITYZIPCOUNTRY = "customerCityZipCountry";
    public static final String PROP_CUSTOMERPHONE = "customerPhone";
    public static final String PROP_CUSTOMERPOSTALCODE = "customerPostalCode";
    public static final String PROP_CUSTOMERCOUNTRYNAME = "customerCountryName";
    public static final String PROP_CUSTOMERCITYNAME = "customerCityName";
    public static final String PROP_CUSTOMERADDRESS2 = "customerAddress2";
    public static final String PROP_CUSTOMERADDRESS1 = "customerAddress1";
    public static final String PROP_CUSTOMERNAME = "customerName";
    
    @Override
    PartialCustomerModel<? extends Customer> getCustomer();

    ReadOnlyObjectProperty<? extends PartialCustomerModel<? extends Customer>> customerProperty();

    ReadOnlyStringProperty customerNameProperty();

    String getCustomerName();

    String getCustomerAddress1();

    ReadOnlyStringProperty customerAddress1Property();

    String getCustomerAddress2();

    ReadOnlyStringProperty customerAddress2Property();

    String getCustomerCityName();

    ReadOnlyStringProperty customerCityNameProperty();

    String getCustomerCountryName();

    ReadOnlyStringProperty customerCountryNameProperty();

    String getCustomerCityZipCountry();

    ReadOnlyStringProperty customerCityZipCountryProperty();

    String getCustomerAddressText();

    String getCustomerPhone();

    ReadOnlyStringProperty customerPhoneProperty();

    String getCustomerPostalCode();

    ReadOnlyStringProperty customerPostalCodeProperty();

    ReadOnlyStringProperty customerAddressTextProperty();

    boolean isCustomerActive();

    ReadOnlyBooleanProperty customerActiveProperty();

    ReadOnlyObjectProperty<? extends PartialUserModel<? extends User>> userProperty();

    String getUserName();

    ReadOnlyStringProperty userNameProperty();

    UserStatus getUserStatus();

    ReadOnlyStringProperty userStatusDisplayProperty();

    ReadOnlyObjectProperty<UserStatus> userStatusProperty();

    String getUserStatusDisplay();

    ReadOnlyStringProperty titleProperty();

    ReadOnlyObjectProperty<AppointmentType> typeProperty();

    String getTypeDisplay();

    ReadOnlyStringProperty typeDisplayProperty();

    ReadOnlyStringProperty contactProperty();

    ReadOnlyStringProperty descriptionProperty();

    ReadOnlyStringProperty locationProperty();

    ReadOnlyObjectProperty<LocalDateTime> startProperty();

    ReadOnlyObjectProperty<LocalDateTime> endProperty();

    ReadOnlyStringProperty urlProperty();

    String getEffectiveLocation();

    ReadOnlyStringProperty effectiveLocationProperty();

    @Override
    public default boolean startEquals(Object value) {
        LocalDateTime start = getStart();
        if (null == start) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof ChronoLocalDateTime) {
            return start.equals((ChronoLocalDateTime<?>) value);
        }

        if (value instanceof ZonedDateTime) {
            return start.equals(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }
        return value instanceof Timestamp && start.equals(DB.toLocalDateTime((Timestamp) value));
    }

    @Override
    public default int compareStart(Object value) {
        LocalDateTime start = getStart();
        if (null == start) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof ChronoLocalDateTime) {
            return start.compareTo((ChronoLocalDateTime<?>) value);
        }

        if (value instanceof ZonedDateTime) {
            return start.compareTo(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }

        if (value instanceof Timestamp) {
            return start.compareTo(((Timestamp) value).toLocalDateTime());
        }

        if (value instanceof Date) {
            return start.compareTo(new Timestamp(((Date) value).getTime()).toLocalDateTime());
        }

        throw new IllegalArgumentException();
    }

    @Override
    public default boolean endEquals(Object value) {
        LocalDateTime end = getEnd();
        if (null == end) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof ChronoLocalDateTime) {
            return end.equals((ChronoLocalDateTime<?>) value);
        }

        if (value instanceof ZonedDateTime) {
            return end.equals(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }
        return value instanceof Timestamp && end.equals(DB.toLocalDateTime((Timestamp) value));
    }

    @Override
    public default int compareEnd(Object value) {
        LocalDateTime end = getEnd();
        if (null == end) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof ChronoLocalDateTime) {
            return end.compareTo((ChronoLocalDateTime<?>) value);
        }

        if (value instanceof ZonedDateTime) {
            return end.compareTo(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }

        if (value instanceof Timestamp) {
            return end.compareTo(((Timestamp) value).toLocalDateTime());
        }

        if (value instanceof Date) {
            return end.compareTo(new Timestamp(((Date) value).getTime()).toLocalDateTime());
        }

        throw new IllegalArgumentException();
    }

}
