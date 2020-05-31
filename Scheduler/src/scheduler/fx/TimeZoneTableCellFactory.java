package scheduler.fx;

import java.util.TimeZone;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class TimeZoneTableCellFactory<T> implements Callback<TableColumn<T, TimeZone>, TableCell<T, TimeZone>> {

    @Override
    public TableCell<T, TimeZone> call(TableColumn<T, TimeZone> param) {
        return new TimeZoneTableCell();
    }

}
