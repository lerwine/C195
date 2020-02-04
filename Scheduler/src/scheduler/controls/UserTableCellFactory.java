package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Creates a {@link TableCell} that gets the {@link scheduler.dao.UserImpl#userName} for an {@link AppointmentUser} object.
 * @author Leonard T. Erwine
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class UserTableCellFactory<S, T extends scheduler.view.user.AppointmentUser<?>> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) { return new UserTableCell<>(); }
}
