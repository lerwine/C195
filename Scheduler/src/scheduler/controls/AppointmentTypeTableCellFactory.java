package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.AppointmentFactory;

/**
 *
 * @author Leonard T. Erwine
 * @param <S> The row item type.
 */
public class AppointmentTypeTableCellFactory<S> implements Callback<TableColumn<S, String>, TableCell<S, String>> {
    private final ObservableMap<String, String> map = AppointmentFactory.getAppointmentTypes();
    @Override
    public TableCell<S, String> call(TableColumn<S, String> param) { return new AppointmentTypeTableCell(map); }
}
