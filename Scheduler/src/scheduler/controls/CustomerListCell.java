package scheduler.controls;

import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The item type.
 */
public class CustomerListCell<T extends scheduler.view.customer.CustomerModel<?>> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getName());
    }
}
