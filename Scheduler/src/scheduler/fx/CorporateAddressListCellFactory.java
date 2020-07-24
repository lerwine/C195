package scheduler.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.model.CorporateAddress;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CorporateAddressListCellFactory implements Callback<ListView<CorporateAddress>, ListCell<CorporateAddress>> {

    @Override
    public ListCell<CorporateAddress> call(ListView<CorporateAddress> param) {
        return new CorporateAddressListCell();
    }

}
