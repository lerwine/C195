package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.view.customer.CustomerModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CustomerListCellFactory implements Callback<ListView<CustomerModel>, ListCell<CustomerModel>> {

    @Override
    public ListCell<CustomerModel> call(ListView<CustomerModel> param) {
        return new CustomerListCell();
    }
}
