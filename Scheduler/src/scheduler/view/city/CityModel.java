package scheduler.view.city;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.country.CityCountryModel;
import scheduler.view.country.CityOptionModel;
import scheduler.view.model.ElementModel;
import scheduler.model.db.CityRowData;
import scheduler.model.db.CountryRowData;

/**
 * Models a {@link CityRowData} data object.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The city data object type.
 * @deprecated Use {@link scheduler.model.ui.CityItem}, instead.
 */
public interface CityModel<T extends CityRowData> extends ElementModel<T> {

    String getName();

    ReadOnlyProperty<String> nameProperty();

    CityCountryModel<? extends CountryRowData> getCountry();

    ReadOnlyProperty<CityCountryModel<? extends CountryRowData>> countryProperty();

    String getCountryName();

    ChildPropertyWrapper<String, CityCountryModel<? extends CountryRowData>> countryNameProperty();
    
    CityOptionModel getOptionModel();
}
