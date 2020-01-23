/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.city;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.City;
import scheduler.dao.CityImpl;
import scheduler.dao.Country;
import scheduler.view.country.CityCountry;
import scheduler.view.ChildModel;

/**
 *
 * @author erwinel
 * @param <T>
 */
public interface AddressCity<T extends City> extends ChildModel<T> {
    String getName();
    ReadOnlyStringProperty nameProperty();
    CityCountry<?> getCountry();
    ReadOnlyObjectProperty<CityCountry<?>> countryProperty();

    public static AddressCity<?> of(City dao) {
        if (null == dao)
            return null;
        if (dao instanceof CityImpl)
            return new CityModel((CityImpl)dao);
        return new AddressCity<City>() {
            private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(dao.getName());
            @Override
            public String getName() { return name.get(); }
            @Override
            public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
            private final ReadOnlyObjectWrapper<CityCountry<? extends Country>> country = new ReadOnlyObjectWrapper<>(CityCountry.of(dao.getCountry()));
            @Override
            public CityCountry<? extends Country> getCountry() { return country.get(); }
            @Override
            public ReadOnlyObjectProperty<CityCountry<? extends Country>> countryProperty() { return country.getReadOnlyProperty(); }
            @Override
            public City getDataObject() { return dao; }
        };
    }
}
