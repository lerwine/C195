package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.dao.AppointmentFactory;

/**
 *
 * @author Leonard T. Erwine
 */
public class AppointmentTypeListCellFactory implements Callback<ListView<String>, ListCell<String>> {
    private final ObservableMap<String, String> map = AppointmentFactory.getAppointmentTypes();
    @Override
    public ListCell<String> call(ListView<String> param) { return new AppointmentTypeListCell(map); }
}