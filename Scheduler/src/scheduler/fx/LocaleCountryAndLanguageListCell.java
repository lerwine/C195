package scheduler.fx;

import java.util.Locale;
import javafx.scene.control.ListCell;
import scheduler.model.CountryProperties;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LocaleCountryAndLanguageListCell extends ListCell<Locale> {

    @Override
    protected void updateItem(Locale item, boolean empty) {
        super.updateItem(item, empty);
        setText(CountryProperties.getCountryAndLanguageDisplayText(item));
    }

}
