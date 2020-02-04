package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.ListCell;
import scheduler.dao.AppointmentFactory;

/**
 *
 * @author lerwi
 */
public class AppointmentTypeListCell extends ListCell<String> {
    private final ObservableMap<String, String> map;
    AppointmentTypeListCell(ObservableMap<String, String> map) { this.map = (null == map) ? AppointmentFactory.getAppointmentTypes() : map; }
    public AppointmentTypeListCell() { this(null); }
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText((null == item) ? "" : (map.containsKey(item)) ? map.get(item) : item);
    }
}
