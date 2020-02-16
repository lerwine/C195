/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.city;

import java.sql.SQLException;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.City;
import scheduler.dao.Country;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.country.CountryReferenceModel;
import scheduler.view.country.CountryReferenceModelImpl;


public class CityReferenceModelImpl extends DataObjectImpl.DataObjectReferenceModelImpl<City> implements CityReferenceModel<City> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<CountryReferenceModel<? extends Country>> country;
    private final ChildPropertyWrapper<String, CountryReferenceModel<? extends Country>> countryName;
    
    public CityReferenceModelImpl(City dao) throws SQLException, ClassNotFoundException {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "name", dao.getName());
        Country c = dao.getCountry().ensurePartial(CountryImpl.getFactory());
        country = new ReadOnlyObjectWrapper<>(this, "country", (null == c) ? null : new CountryReferenceModelImpl(c));
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
    public CountryReferenceModel<? extends Country> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryReferenceModel<? extends Country>> countryProperty() {
        return country.getReadOnlyProperty();
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CountryReferenceModel<? extends Country>> countryNameProperty() {
        return countryName;
    }

}
