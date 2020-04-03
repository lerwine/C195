package scheduler.view.city;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.CityElement;
import scheduler.dao.CountryElement;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.country.CityCountryModel;
import scheduler.view.country.CityCountryModelImpl;
import scheduler.view.model.RelatedItemModel;

public class RelatedCityModel extends RelatedItemModel<CityElement> implements CityModel<CityElement> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<CityCountryModel<? extends CountryElement>> country;
    private final ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryName;

    public RelatedCityModel(CityElement dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "name", dao.getName());
        CountryElement c = dao.getCountry();
        country = new ReadOnlyObjectWrapper<>(this, "country", (null == c) ? null : new CityCountryModelImpl(c));
        countryName = new ChildPropertyWrapper<>(this, "countryName", country, (t) -> t.nameProperty());
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public CityCountryModel<? extends CountryElement> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CityCountryModel<? extends CountryElement>> countryProperty() {
        return country.getReadOnlyProperty();
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryNameProperty() {
        return countryName;
    }

}
