package scheduler.fx;

import javafx.collections.ObservableMap;
import javafx.scene.control.ListCell;
import scheduler.model.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
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
