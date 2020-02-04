package scheduler.controls;

import javafx.scene.control.TableCell;
import scheduler.view.customer.AppointmentCustomer;

/**
 *
 * @author lerwi
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class CustomerTableCell<S, T  extends AppointmentCustomer<?>> extends TableCell<S, T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getName());
    }
}
