package scheduler.fx;

import javafx.scene.control.ListCell;
import scheduler.dao.PartialCountryDAO;
import scheduler.model.ModelHelper.CountryHelper;
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
            setText(CountryHelper.getCountryAndLanguageDisplayText(item.getLocale()));
        }
    }

}
