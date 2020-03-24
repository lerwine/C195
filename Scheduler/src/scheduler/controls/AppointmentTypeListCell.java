package scheduler.controls;

import javafx.scene.control.ListCell;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of the item contained within the @{link AppointmentTypeListCell}.
 */
public class AppointmentTypeListCell<T extends AppointmentType> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(AppointmentType.toAppointmentTypeDisplay(item));
    }
}
