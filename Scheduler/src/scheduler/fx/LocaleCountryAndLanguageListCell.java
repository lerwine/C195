package scheduler.fx;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ListCell;
import scheduler.model.CountryProperties;
import scheduler.util.LogHelper;

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
