package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 * @param <T> The item type.
 */
public class CustomerListCellFactory<T extends scheduler.view.customer.CustomerReferenceModel<?>> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new CustomerListCell<>();
    }
}
