package scheduler.controls;

import javafx.scene.control.ListCell;
import scheduler.dao.AppointmentType;

/**
 *
 * @author lerwi
 */
public class AppointmentTypeListCell extends ListCell<AppointmentType> {

    @Override
    protected void updateItem(AppointmentType item, boolean empty) {
        super.updateItem(item, empty);
        setText(AppointmentType.toAppointmentTypeDisplay(item));
    }
}
