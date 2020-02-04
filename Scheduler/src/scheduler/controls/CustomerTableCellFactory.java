package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.view.customer.AppointmentCustomer;

/**
 *
 * @author Leonard T. Erwine
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class CustomerTableCellFactory<S, T  extends AppointmentCustomer<?>> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) { return new CustomerTableCell<>(); }
}
