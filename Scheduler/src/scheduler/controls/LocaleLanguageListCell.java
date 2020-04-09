package scheduler.controls;

import javafx.scene.control.ListCell;
import scheduler.util.ResourceBundleLoader;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class LocaleLanguageListCell extends ListCell<ResourceBundleLoader.SupportedLocale> {

    @Override
    protected void updateItem(ResourceBundleLoader.SupportedLocale item, boolean empty) {
        super.updateItem(item, empty);
        String s;
        setText((item == null || (s = item.getLocaleDisplayName()) == null) ? "" : s);
    }
}
