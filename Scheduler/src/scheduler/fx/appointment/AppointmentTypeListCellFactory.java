package scheduler.fx.appointment;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.model.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTypeListCellFactory implements Callback<ListView<AppointmentType>, ListCell<AppointmentType>> {

    @Override
    public ListCell<AppointmentType> call(ListView<AppointmentType> param) {
        return new AppointmentTypeListCell();
    }
}
