package scheduler.controls;

import javafx.scene.control.ListCell;

/**
 * Formats a zero-padded 2-digit integer.
 * @author lerwi
 */
public class ZeroPadDigitListCell extends ListCell<Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        setText((empty || item == null) ? "" : ((item < 10) ? "0" + item.toString() : item.toString()));
    }
}