package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The item type.
 */
public class UserStatusTableCellFactory<T extends UserStatus> implements Callback<TableColumn<String, T>, TableCell<String, T>> {

    @Override
    public TableCell<String, T> call(TableColumn<String, T> param) {
        return new UserStatusTableCell<>();
    }
}
