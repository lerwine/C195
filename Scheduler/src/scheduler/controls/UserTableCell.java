package scheduler.controls;

import javafx.scene.control.TableCell;
import scheduler.view.user.AppointmentUser;

/**
 * Gets the {@link scheduler.dao.UserImpl#userName} for an {@link AppointmentUser} object.
 * @author lerwi
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class UserTableCell<S, T extends AppointmentUser<?>> extends TableCell<S, T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getUserName());
    }
}
