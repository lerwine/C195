package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class UserStatusTableCellFactory implements Callback<TableColumn<String, UserStatus>, TableCell<String, UserStatus>> {

    @Override
    public TableCell<String, UserStatus> call(TableColumn<String, UserStatus> param) {
        return new UserStatusTableCell();
    }
}
