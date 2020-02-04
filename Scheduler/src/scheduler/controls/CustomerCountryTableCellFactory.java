package scheduler.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.view.address.CustomerAddress;

/**
 *
 * @author lerwi
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class CustomerCountryTableCellFactory<S, T  extends CustomerAddress<?>> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) { return new CustomerCountryTableCell(); }
}