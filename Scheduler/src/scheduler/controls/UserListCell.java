package scheduler.controls;

import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The item type.
 */
public class UserListCell<T extends scheduler.view.user.UserModel<?>> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getUserName());
    }
}
