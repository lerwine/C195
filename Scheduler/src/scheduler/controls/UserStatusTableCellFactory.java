package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <S> The item type.
 */
public class UserStatusTableCellFactory<S> implements Callback<TableColumn<S, UserStatus>, TableCell<S, UserStatus>> {

    @Override
    public TableCell<S, UserStatus> call(TableColumn<S, UserStatus> param) {
        return new UserStatusTableCell<S>();
    }
}
