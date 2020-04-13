package scheduler.view.city;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.CityElement;
import scheduler.dao.CountryElement;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.country.CityCountryModel;
import scheduler.view.country.CityOptionModel;
import scheduler.view.model.ElementModel;

/**
 * Models a {@link CityElement} data object.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The city data object type.
 */
public interface CityModel<T extends CityElement> extends ElementModel<T> {

    String getName();

    ReadOnlyProperty<String> nameProperty();

    CityCountryModel<? extends CountryElement> getCountry();

    ReadOnlyProperty<CityCountryModel<? extends CountryElement>> countryProperty();

    String getCountryName();

    ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryNameProperty();
    
    CityOptionModel getOptionModel();
}
