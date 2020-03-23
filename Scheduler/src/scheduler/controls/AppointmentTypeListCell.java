package scheduler.controls;

import javafx.scene.control.ListCell;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class AppointmentTypeListCell extends ListCell<AppointmentType> {

    @Override
    protected void updateItem(AppointmentType item, boolean empty) {
        super.updateItem(item, empty);
        setText(AppointmentType.toAppointmentTypeDisplay(item));
    }
}
