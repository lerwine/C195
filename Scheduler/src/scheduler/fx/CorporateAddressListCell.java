package scheduler.fx;

import javafx.scene.control.ListCell;
import scheduler.model.CorporateAddress;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CorporateAddressListCell extends ListCell<CorporateAddress> {

    @Override
    protected void updateItem(CorporateAddress item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item) {
            setText("");
        } else {
            setText(item.getName());
        }
    }

}
