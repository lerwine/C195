package scheduler.model.ui;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.IAppointmentDAO;
import scheduler.model.Appointment;
import scheduler.model.AppointmentType;
import scheduler.model.Customer;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.util.DB;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface AppointmentItem<T extends IAppointmentDAO> extends Appointment<LocalDateTime>, FxDbModel<T> {

    @Override
    CustomerItem<? extends Customer> getCustomer();

    ReadOnlyObjectProperty<? extends CustomerItem<? extends Customer>> customerProperty();

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

    ReadOnlyObjectProperty<? extends UserItem<? extends User>> userProperty();

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
            return start.compareTo((ChronoLocalDateTime<?>) value);
        }

        return -1;
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
            return end.compareTo((ChronoLocalDateTime<?>) value);
        }

        return -1;
    }

}
