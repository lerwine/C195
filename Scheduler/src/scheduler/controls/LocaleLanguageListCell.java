package scheduler.controls;

import java.util.Locale;
import javafx.scene.control.ListCell;

/**
 *
 * @author lerwi
 */
public class LocaleLanguageListCell extends ListCell<Locale> {

    @Override
    protected void updateItem(Locale item, boolean empty) {
        super.updateItem(item, empty);
        String s;
        setText((item == null || (s = item.getDisplayLanguage(item)) == null) ? "" : s);
    }
}
