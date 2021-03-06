package devhelper;

import java.util.Locale;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class LocaleLanguageTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>() {
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem((T)item, empty);
                if (item == null) {
                    super.setText("");
                } else if (item instanceof Locale)
                    super.setText(((Locale)item).toLanguageTag());
                else if (item instanceof String)
                    super.setText((String)item);
                else
                    super.setText(item.toString());
            }
        };
    }
    
}
