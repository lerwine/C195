package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.model.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserStatusListCellFactory implements Callback<ListView<UserStatus>, ListCell<UserStatus>> {

    @Override
    public ListCell<UserStatus> call(ListView<UserStatus> param) {
        return new UserStatusListCell();
    }
}
