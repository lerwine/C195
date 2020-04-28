package scheduler.controls;

import javafx.scene.control.ListCell;
import scheduler.view.customer.CustomerModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CustomerListCell extends ListCell<CustomerModel> {

    @Override
    protected void updateItem(CustomerModel item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getName());
    }
}
