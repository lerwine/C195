package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTypeTableCellFactory implements Callback<TableColumn<String, AppointmentType>, TableCell<String, AppointmentType>> {

    @Override
    public TableCell<String, AppointmentType> call(TableColumn<String, AppointmentType> param) {
        return new AppointmentTypeTableCell();
    }
}
