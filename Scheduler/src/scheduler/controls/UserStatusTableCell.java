package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The item type.
 */
public class UserStatusTableCell<T extends UserStatus> extends TableCell<String, T> {

    UserStatusTableCell(ObservableMap<UserStatus, String> userStatusMap) {
    }

    public UserStatusTableCell() {
        this(null);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : UserStatus.toDisplayValue(item));
    }
}
