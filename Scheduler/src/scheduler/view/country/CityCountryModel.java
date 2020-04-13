package scheduler.view.country;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.CountryElement;
import scheduler.view.model.ElementModel;

/**
 * Models a country data access object.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link CountryElement} data access object.
 */
public interface CityCountryModel<T extends CountryElement> extends ElementModel<T> {

    String getName();

    ReadOnlyProperty<String> nameProperty();
    
    CountryOptionModel getOptionModel();
}
