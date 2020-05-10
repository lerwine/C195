package scheduler.fx;

import javafx.scene.control.ListCell;
import scheduler.model.ui.CityItem;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class CityListCell<T extends CityItem> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item) {
            setText("");
        } else {
            setText(item.getName());
        }
    }

}
