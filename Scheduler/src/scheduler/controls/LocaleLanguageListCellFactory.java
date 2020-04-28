package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.SupportedLocale;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LocaleLanguageListCellFactory implements Callback<ListView<SupportedLocale>, ListCell<SupportedLocale>> {

    @Override
    public ListCell<SupportedLocale> call(ListView<SupportedLocale> param) {
        return new LocaleLanguageListCell();
    }
}
