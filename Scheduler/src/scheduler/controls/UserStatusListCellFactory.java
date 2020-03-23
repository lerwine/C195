package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.dao.UserStatus;

/**
 *
 * @author lerwi
 */
public class UserStatusListCellFactory implements Callback<ListView<UserStatus>, ListCell<UserStatus>> {

    @Override
    public ListCell<UserStatus> call(ListView<UserStatus> param) {
        return new UserStatusListCell();
    }
}
