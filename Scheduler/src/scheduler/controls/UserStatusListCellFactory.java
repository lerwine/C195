package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.dao.UserImpl;

/**
 *
 * @author lerwi
 */
public class UserStatusListCellFactory implements Callback<ListView<Integer>, ListCell<Integer>> {
    private final ObservableMap<Integer, String> userStatusMap = UserImpl.getUserStatusMap();
    @Override
    public ListCell<Integer> call(ListView<Integer> param) { return new UserStatusListCell(userStatusMap); }
}
