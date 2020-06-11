package scheduler.fx;

import java.util.logging.Logger;
import javafx.scene.control.ListCell;
import scheduler.dao.ICityDAO;
import scheduler.model.ui.CityItem;
import scheduler.util.LogHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class CityListCell<T extends CityItem<? extends ICityDAO>> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item) {
            setText("");
        } else {
            String name = item.getName();
            Logger.getLogger(getClass().getName()).fine(String.format("Setting city list cell to %s", LogHelper.toLogText(name)));
            setText(item.getName());
        }
    }

}
