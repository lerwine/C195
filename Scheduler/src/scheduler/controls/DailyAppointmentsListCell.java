package scheduler.controls;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import scheduler.view.report.DailyAppointmentsBorderPane;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DailyAppointmentsListCell extends ListCell<DailyAppointmentsBorderPane> {

    @Override
    protected void updateItem(DailyAppointmentsBorderPane item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || null == item) {
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(item);
        }
    }

}
