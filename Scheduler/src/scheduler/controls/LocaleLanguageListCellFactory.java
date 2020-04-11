package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.util.ResourceBundleLoader;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LocaleLanguageListCellFactory implements Callback<ListView<ResourceBundleLoader.SupportedLocale>, ListCell<ResourceBundleLoader.SupportedLocale>> {

    @Override
    public ListCell<ResourceBundleLoader.SupportedLocale> call(ListView<ResourceBundleLoader.SupportedLocale> param) {
        return new LocaleLanguageListCell();
    }
}
