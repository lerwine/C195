package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The item type.
 */
public class CustomerListCellFactory<T extends scheduler.view.customer.CustomerModel<?>> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new CustomerListCell<>();
    }
}
