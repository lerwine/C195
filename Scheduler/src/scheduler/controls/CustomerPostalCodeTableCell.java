package scheduler.controls;

import javafx.scene.control.TableCell;
import scheduler.view.address.CustomerAddress;

/**
 *
 * @author lerwi
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class CustomerPostalCodeTableCell<S, T  extends CustomerAddress<?>> extends TableCell<S, T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item)
            setText("");
        else {
            String s = item.getPostalCode();
            setText((null == s) ? "" : s);
        }
    }
}
