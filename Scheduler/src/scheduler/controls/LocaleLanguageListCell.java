package scheduler.controls;

import javafx.scene.control.ListCell;
import scheduler.view.city.SupportedLocale;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LocaleLanguageListCell extends ListCell<SupportedLocale> {

    @Override
    protected void updateItem(SupportedLocale item, boolean empty) {
        super.updateItem(item, empty);
        setText(SupportedLocale.toDisplayLanguage(item));
    }
}
