package scheduler.fx;

import javafx.scene.control.ListCell;
import scheduler.model.ui.UserModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserListCell extends ListCell<UserModel> {

    @Override
    protected void updateItem(UserModel item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getUserName());
    }
}
