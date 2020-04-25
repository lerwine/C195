package scheduler.model.ui;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import scheduler.model.Appointment;
import scheduler.model.AppointmentType;
import scheduler.model.db.AppointmentRowData;
import scheduler.model.db.CustomerRowData;
import scheduler.model.db.UserRowData;
import scheduler.observables.AppointmentTypeProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface AppointmentItem<T extends AppointmentRowData> extends Appointment<LocalDateTime>, UIDbModel<T> {

    @Override
    CustomerItem<? extends CustomerRowData> getCustomer();

    ReadOnlyObjectProperty<CustomerItem<? extends CustomerRowData>> customerProperty();

    @Override
    public UserItem<? extends UserRowData> getUser();

    ReadOnlyObjectProperty<CustomerItem<? extends UserRowData>> userProperty();
    
    StringProperty titleProperty();
    
    StringProperty descriptionProperty();
    
    StringProperty locationProperty();
    
    String getEffectiveLocation();

    ReadOnlyProperty<String> effectiveLocationProperty();

    StringProperty contactProperty();
    
    ReadOnlyObjectProperty<AppointmentType> typeProperty();
    
    String getTypeDisplay();

    ReadOnlyProperty<String> typeDisplayProperty();

    StringProperty urlProperty();
    
    ReadOnlyProperty<LocalDateTime> startProperty();
    
    ReadOnlyProperty<LocalDateTime> endProperty();
    
}
