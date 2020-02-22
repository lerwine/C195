package scheduler.controls;

import javafx.scene.control.ListCell;
import scheduler.util.Values;

/**
 *
 * @author lerwi
 */
public class AppointmentTypeListCell extends ListCell<String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(Values.toAppointmentTypeDisplay(item));
    }
}
