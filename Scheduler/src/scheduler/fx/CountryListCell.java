package scheduler.fx;

import javafx.scene.control.ListCell;
import scheduler.model.CountryProperties;
import scheduler.dao.PartialCountryDAO;
import scheduler.model.fx.PartialCountryModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class CountryListCell<T extends PartialCountryModel<? extends PartialCountryDAO>> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item) {
            setText("");
        } else {
            setText(CountryProperties.getCountryAndLanguageDisplayText(item.getLocale()));
        }
    }

}
