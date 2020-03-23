package scheduler.controls;

import javafx.scene.control.TableCell;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <S> The row item type.
 */
public class AppointmentTypeTableCell<S> extends TableCell<S, AppointmentType> {

    @Override
    protected void updateItem(AppointmentType item, boolean empty) {
        super.updateItem(item, empty);
        setText(AppointmentType.toAppointmentTypeDisplay(item));
    }
}
