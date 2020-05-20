package scheduler.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.model.ui.UserModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserListCellFactory implements Callback<ListView<UserModel>, ListCell<UserModel>> {

    @Override
    public ListCell<UserModel> call(ListView<UserModel> param) {
        return new UserListCell();
    }
}
