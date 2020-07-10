package scheduler.fx;

import java.util.TimeZone;
import javafx.scene.control.TableCell;
import scheduler.util.DateTimeUtil;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class TimeZoneTableCell<T> extends TableCell<T, TimeZone> {

    @Override
    protected void updateItem(TimeZone item, boolean empty) {
        super.updateItem(item, empty);
        setWrapText(true);
        setText(DateTimeUtil.getTimeZoneDisplayText(item));
    }

}
