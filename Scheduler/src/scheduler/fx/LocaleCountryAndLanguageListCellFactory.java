package scheduler.fx;

import java.util.Locale;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LocaleCountryAndLanguageListCellFactory implements Callback<ListView<Locale>, ListCell<Locale>> {

    @Override
    public ListCell<Locale> call(ListView<Locale> param) {
        LocaleCountryAndLanguageListCell listCell = new LocaleCountryAndLanguageListCell();
        listCell.setWrapText(true);
        return listCell;
    }

}
