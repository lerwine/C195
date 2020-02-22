package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.ListCell;
import scheduler.dao.UserImpl;

/**
 *
 * @author lerwi
 */
public class UserStatusListCell extends ListCell<Integer> {

    private final ObservableMap<Integer, String> userStatusMap;

    UserStatusListCell(ObservableMap<Integer, String> userStatusMap) {
        this.userStatusMap = (null == userStatusMap) ? UserImpl.getUserStatusMap() : userStatusMap;
    }

    public UserStatusListCell() {
        this(null);
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : ((userStatusMap.containsKey(item)) ? userStatusMap.get(item) : item.toString()));
    }
}
