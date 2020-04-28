package scheduler.controls;

import java.util.Locale;
import javafx.scene.control.ListCell;
import scheduler.SupportedLocale;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LocaleLanguageListCell extends ListCell<SupportedLocale> {

    @Override
    protected void updateItem(SupportedLocale item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item)
            setText("");
        else {
            Locale l = item.getLocale();
            setText(l.getDisplayLanguage(l));
        }
    }
}
