package scheduler.fx;

import java.time.Month;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class MonthNameListCellFactory implements Callback<ListView<Month>, ListCell<Month>> {

    @Override
    public ListCell<Month> call(ListView<Month> param) {
        return new MonthNameListCell();
    }

}
