package scheduler.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.model.AddressProperties;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class AddressListCellFactory<T extends AddressProperties> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new AddressListCell<>();
    }

}
