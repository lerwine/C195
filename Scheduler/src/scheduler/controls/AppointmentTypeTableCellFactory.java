package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 * @param <S> The row item type.
 */
public class AppointmentTypeTableCellFactory<S> implements Callback<TableColumn<S, String>, TableCell<S, String>> {

    // TODO: Check if the generic arguments are backwares on the return value.
    @Override
    public TableCell<S, AppointmentType> call(TableColumn<S, String> param) {
        return new AppointmentTypeTableCell();
    }
}
