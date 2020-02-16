package scheduler.controls;

import javafx.scene.control.ListCell;

/**
 *
 * @author lerwi
 * @param <T> The item type.
 */
public class UserListCell<T extends scheduler.view.user.UserReferenceModel<?>> extends ListCell<T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getUserName());
    }
}
