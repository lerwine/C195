package scheduler.fx;

import java.util.logging.Logger;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.util.LogHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class WrappingTextTableCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {

    private static final Logger LOG = Logger.getLogger(WrappingTextTableCellFactory.class.getName());

    @Override
    public TableCell<T, String> call(TableColumn<T, String> param) {
        return new TableCell<T, String>() {
            {
                this.setWrapText(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    LOG.finer(() -> String.format("wrapText is %s when setting %s", this.wrapTextProperty().get(), LogHelper.toLogText(item)));
                    setText(item);
                }
            }
        };
    }

}
