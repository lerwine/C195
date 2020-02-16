package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import scheduler.dao.AppointmentImpl;
import scheduler.util.Values;

/**
 *
 * @author lerwi
 * @param <S> The row item type.
 */
public class AppointmentTypeTableCell<S> extends TableCell<S, String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(Values.toAppointmentTypeDisplay(item));
    }
}
