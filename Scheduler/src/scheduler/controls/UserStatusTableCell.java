package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import scheduler.dao.UserStatus;

/**
 *
 * @author lerwi
 * @param <S> The item type.
 */
public class UserStatusTableCell<S> extends TableCell<S, UserStatus> {

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
