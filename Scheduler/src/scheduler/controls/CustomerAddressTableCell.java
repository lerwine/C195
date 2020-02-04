package scheduler.controls;

import javafx.scene.control.TableCell;
import scheduler.view.address.CustomerAddress;

/**
 *
 * @author lerwi
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class CustomerAddressTableCell<S, T  extends CustomerAddress<?>> extends TableCell<S, T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item)
            setText("");
        else {
            String a = item.getAddress1();
            String s = item.getAddress2();
            if (null == a || a.trim().isEmpty())
                setText((null == s) ? "" : s);
            else
                setText((null == s || s.trim().isEmpty()) ? a : a + "\n" + s);
        }
    }
}
