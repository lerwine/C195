package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class UserStatusTableCell extends TableCell<String, UserStatus> {

    UserStatusTableCell(ObservableMap<UserStatus, String> userStatusMap) {
    }

    public UserStatusTableCell() {
        this(null);
    }

    @Override
    protected void updateItem(UserStatus item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : UserStatus.toDisplayValue(item));
    }
}
