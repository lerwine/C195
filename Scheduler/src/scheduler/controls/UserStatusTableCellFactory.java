package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.UserFactory;

/**
 *
 * @author lerwi
 * @param <S> The item type.
 */
public class UserStatusTableCellFactory<S> implements Callback<TableColumn<S, Integer>, TableCell<S, Integer>> {
    private final ObservableMap<Integer, String> userStatusMap = UserFactory.getUserStatusMap();
    @Override
    public TableCell<S, Integer> call(TableColumn<S, Integer> param) { return new UserStatusTableCell(userStatusMap); }
}
