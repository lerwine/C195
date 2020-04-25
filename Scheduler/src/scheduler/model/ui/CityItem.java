package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.City;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityItem extends City, UIModel {

    ReadOnlyProperty<String> nameProperty();

    @Override
    public CountryItem getCountry();

    ReadOnlyProperty<? extends CountryItem> countryProperty();

}
