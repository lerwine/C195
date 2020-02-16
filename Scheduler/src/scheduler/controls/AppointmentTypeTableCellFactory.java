package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.AppointmentImpl;

/**
 *
 * @author Leonard T. Erwine
 * @param <S> The row item type.
 */
public class AppointmentTypeTableCellFactory<S> implements Callback<TableColumn<S, String>, TableCell<S, String>> {
    @Override
    public TableCell<S, String> call(TableColumn<S, String> param) { return new AppointmentTypeTableCell(); }
}
