package scheduler.view.country;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.view.model.ElementModel;
import scheduler.model.db.CountryRowData;

/**
 * Models a country data access object.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link CountryRowData} data access object.
 * @deprecated Use {@link scheduler.model.ui.CountryItem}, instead.
 */
public interface CityCountryModel<T extends CountryRowData> extends ElementModel<T> {

    String getName();

    ReadOnlyProperty<String> nameProperty();
    
    CountryOptionModel getOptionModel();
}
