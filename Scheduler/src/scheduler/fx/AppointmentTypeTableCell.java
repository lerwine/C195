package scheduler.fx;

import javafx.scene.control.TableCell;
import scheduler.model.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTypeTableCell extends TableCell<String, AppointmentType> {

    @Override
    protected void updateItem(AppointmentType item, boolean empty) {
        super.updateItem(item, empty);
        setWrapText(true);
        setText(AppointmentType.toDisplayText(item));
    }
}
