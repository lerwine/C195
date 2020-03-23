package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.ListCell;
import scheduler.dao.UserImpl;
import scheduler.dao.UserStatus;

/**
 *
 * @author lerwi
 */
public class UserStatusListCell extends ListCell<UserStatus> {

    UserStatusListCell(ObservableMap<UserStatus, String> userStatusMap) {
    }

    public UserStatusListCell() {
        this(null);
    }

    @Override
    protected void updateItem(UserStatus item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : UserStatus.toDisplayValue(item));
    }
}
