package scheduler.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Creates a {@link ListCell} which formats a zero-padded 2-digit integer.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ZeroPadDigitListCellFactory implements Callback<ListView<Integer>, ListCell<Integer>> {

    @Override
    public ListCell<Integer> call(ListView<Integer> param) {
        return new ZeroPadDigitListCell();
    }
}
