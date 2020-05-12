package scheduler.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.view.report.DailyAppointmentsBorderPane;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DailyAppointmentsListCellFactory implements Callback<ListView<DailyAppointmentsBorderPane>, ListCell<DailyAppointmentsBorderPane>> {

    @Override
    public DailyAppointmentsListCell call(ListView<DailyAppointmentsBorderPane> param) {
        return new DailyAppointmentsListCell();
    }

}
