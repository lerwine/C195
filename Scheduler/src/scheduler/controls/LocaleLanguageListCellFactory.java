package scheduler.controls;

import java.util.Locale;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class LocaleLanguageListCellFactory implements Callback<ListView<Locale>, ListCell<Locale>> {
    @Override
    public ListCell<Locale> call(ListView<Locale> param) { return new LocaleLanguageListCell(); }
}
