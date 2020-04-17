package scheduler.controls;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * For drop-downs that selects AM or PM in the current user's language.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AmPmListCellFactory implements Callback<ListView<Boolean>, ListCell<Boolean>> {

    private final String amText;
    private final String pmText;

    public AmPmListCellFactory() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("a");
        amText = LocalTime.of(0, 0).format(dtf);
        pmText = LocalTime.of(12, 0).format(dtf);
    }

    @Override
    public ListCell<Boolean> call(ListView<Boolean> param) {
        return new AmPmListCell(amText, pmText);
    }

}
