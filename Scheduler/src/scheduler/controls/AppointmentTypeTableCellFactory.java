package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine
 * @param <T> The row item type.
 */
public class AppointmentTypeTableCellFactory<T extends AppointmentType> implements Callback<TableColumn<String, T>, TableCell<String, T>> {

    @Override
    public TableCell<String, T> call(TableColumn<String, T> param) {
        return new AppointmentTypeTableCell<>();
    }
}
