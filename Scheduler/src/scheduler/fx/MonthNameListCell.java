package scheduler.fx;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class MonthNameListCell extends ListCell<Month> {

    @Override
    protected void updateItem(Month item, boolean empty) {
        super.updateItem(item, empty);
        setText((empty || null == item) ? "" : item.getDisplayName(TextStyle.FULL, Locale.getDefault(Locale.Category.DISPLAY)));
    }

}
