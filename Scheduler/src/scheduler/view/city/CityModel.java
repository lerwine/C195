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
import scheduler.dao.CityFactory;
import scheduler.dao.Country;
import scheduler.view.country.CityCountry;

/**
 *
 * @author erwinel
 */
public class CityModel extends scheduler.view.ItemModel<CityFactory.CityImpl> implements AddressCity<CityFactory.CityImpl> {
    
    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() { return name.get(); }

    @Override
    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CityCountry<? extends Country>> country;

    @Override
    public CityCountry<?> getCountry() { return country.get(); }

    @Override
    public ReadOnlyObjectProperty<CityCountry<?>> countryProperty() { return country.getReadOnlyProperty(); }
    
    public CityModel(CityFactory.CityImpl dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(dao.getName());
        country = new ReadOnlyObjectWrapper<>(CityCountry.of(dao.getCountry()));
    }

    @Override
    protected void refreshFromDAO(CityFactory.CityImpl dao) {
        name.set(dao.getName());
        country.set(CityCountry.of(dao.getCountry()));
    }
    
}
