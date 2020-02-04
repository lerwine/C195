package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import scheduler.dao.AppointmentFactory;

/**
 *
 * @author lerwi
 * @param <S> The row item type.
 */
public class AppointmentTypeTableCell<S> extends TableCell<S, String> {
    private final ObservableMap<String, String> map;
    AppointmentTypeTableCell(ObservableMap<String, String> map) { this.map = (null == map) ? AppointmentFactory.getAppointmentTypes() : map; }
    public AppointmentTypeTableCell() { this(null); }
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText((null == item) ? "" : (map.containsKey(item)) ? map.get(item) : item);
    }
}
