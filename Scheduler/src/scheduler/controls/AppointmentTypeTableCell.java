package scheduler.controls;

import javafx.scene.control.TableCell;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The row item type.
 */
public class AppointmentTypeTableCell<T extends AppointmentType> extends TableCell<String, T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(AppointmentType.toAppointmentTypeDisplay(item));
    }
}
