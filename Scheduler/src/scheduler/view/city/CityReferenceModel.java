package scheduler.view.city;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.City;
import scheduler.dao.Country;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.country.CountryReferenceModel;

/**
 * Models a {@link City} data object.
 *
 * @author lerwi
 * @param <T> The city data object type.
 */
public interface CityReferenceModel<T extends City> extends DataObjectReferenceModel<T> {

    String getName();

    ReadOnlyProperty<String> nameProperty();

    CountryReferenceModel<? extends Country> getCountry();

    ReadOnlyProperty<CountryReferenceModel<? extends Country>> countryProperty();

    String getCountryName();

    ChildPropertyWrapper<String, CountryReferenceModel<? extends Country>> countryNameProperty();
}
