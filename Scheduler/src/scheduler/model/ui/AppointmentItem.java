package scheduler.model.ui;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.Appointment;
import scheduler.model.AppointmentType;
import scheduler.model.UserStatus;
import scheduler.model.db.AppointmentRowData;
import scheduler.model.db.CustomerRowData;
import scheduler.model.db.UserRowData;
import scheduler.util.DB;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface AppointmentItem<T extends AppointmentRowData> extends Appointment<LocalDateTime>, FxDbModel<T> {

    @Override
    CustomerItem<? extends CustomerRowData> getCustomer();

    ReadOnlyProperty<? extends CustomerItem<? extends CustomerRowData>> customerProperty();

    ReadOnlyProperty<String> customerNameProperty();

    String getCustomerName();

    String getCustomerAddress1();

    ReadOnlyProperty<String> customerAddress1Property();

    String getCustomerAddress2();

    ReadOnlyProperty<String> customerAddress2Property();

    String getCustomerCityName();

    ReadOnlyProperty<String> customerCityNameProperty();

    String getCustomerCountryName();

    ReadOnlyProperty<String> customerCountryNameProperty();

    String getCustomerCityZipCountry();

    ReadOnlyProperty<String> customerCityZipCountryProperty();

    String getCustomerAddressText();

    String getCustomerPhone();

    ReadOnlyProperty<String> customerPhoneProperty();

    String getCustomerPostalCode();

    ReadOnlyProperty<String> customerPostalCodeProperty();

    ReadOnlyProperty<String> customerAddressTextProperty();

    boolean isCustomerActive();

    ReadOnlyProperty<Boolean> customerActiveProperty();

    ReadOnlyProperty<? extends UserItem<? extends UserRowData>> userProperty();

    String getUserName();

    ReadOnlyProperty<String> userNameProperty();

    UserStatus getUserStatus();

    ReadOnlyProperty<String> userStatusDisplayProperty();

    ReadOnlyProperty<UserStatus> userStatusProperty();

    String getUserStatusDisplay();

    ReadOnlyProperty<String> titleProperty();

    ReadOnlyProperty<AppointmentType> typeProperty();

    String getTypeDisplay();

    ReadOnlyProperty<String> typeDisplayProperty();

    ReadOnlyProperty<String> contactProperty();

    ReadOnlyProperty<String> descriptionProperty();

    ReadOnlyProperty<String> locationProperty();

    ReadOnlyProperty<LocalDateTime> startProperty();

    ReadOnlyProperty<LocalDateTime> endProperty();

    ReadOnlyProperty<String> urlProperty();

    String getEffectiveLocation();

    ReadOnlyProperty<String> effectiveLocationProperty();

    @Override
    public default boolean startEquals(Object value) {
        LocalDateTime start = getStart();
        if (null == start)
            return null == value;
        
        if (null == value)
            return false;
        
        if (value instanceof ChronoLocalDateTime)
            return start.equals((ChronoLocalDateTime<?>)value);
        
        if (value instanceof ZonedDateTime)
            return start.equals(((ZonedDateTime)value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        return value instanceof Timestamp && start.equals(DB.toLocalDateTime((Timestamp)value));
    }

    @Override
    public default int compareStart(Object value) {
        LocalDateTime start = getStart();
        if (null == start)
            return (null == value) ? 0 : 1;
        
        if (null == value)
            return -1;
        
        if (value instanceof ChronoLocalDateTime)
            return start.compareTo((ChronoLocalDateTime<?>)value);
        
        if (value instanceof ZonedDateTime)
            return start.compareTo(((ZonedDateTime)value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());

        if (value instanceof Timestamp)
            return start.compareTo((ChronoLocalDateTime<?>)value);

        return -1;
    }

    @Override
    public default boolean endEquals(Object value) {
        LocalDateTime end = getEnd();
        if (null == end)
            return null == value;
        
        if (null == value)
            return false;
        
        if (value instanceof ChronoLocalDateTime)
            return end.equals((ChronoLocalDateTime<?>)value);
        
        if (value instanceof ZonedDateTime)
            return end.equals(((ZonedDateTime)value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        return value instanceof Timestamp && end.equals(DB.toLocalDateTime((Timestamp)value));
    }

    @Override
    public default int compareEnd(Object value) {
        LocalDateTime end = getEnd();
        if (null == end)
            return (null == value) ? 0 : 1;
        
        if (null == value)
            return -1;
        
        if (value instanceof ChronoLocalDateTime)
            return end.compareTo((ChronoLocalDateTime<?>)value);
        
        if (value instanceof ZonedDateTime)
            return end.compareTo(((ZonedDateTime)value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());

        if (value instanceof Timestamp)
            return end.compareTo((ChronoLocalDateTime<?>)value);

        return -1;
    }

}
