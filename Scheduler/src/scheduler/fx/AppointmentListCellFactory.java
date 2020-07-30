package scheduler.fx;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.appointment.ManageAppointments;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentListCellFactory implements Callback<ListView<AppointmentModel>, ListCell<AppointmentModel>> {

    private final DateTimeFormatter formatter;

    public AppointmentListCellFactory() {
        formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
    }

    @Override
    public AppointmentListCell call(ListView<AppointmentModel> param) {
        return new AppointmentListCell(formatter);
    }

}
