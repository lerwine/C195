/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.city;

import expressions.NonNullableStringProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.CityImpl;
import scheduler.dao.Country;
import view.country.CityCountry;
import view.ChildModel;

/**
 *
 * @author erwinel
 */
public class CityModel extends view.ModelBase<CityImpl> implements AddressCity<CityImpl> {
    
    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() { return name.get(); }

    @Override
    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CityCountry<? extends Country>> country = new ReadOnlyObjectWrapper<>();

    @Override
    public CityCountry<?> getCountry() { return country.get(); }

    @Override
    public ReadOnlyObjectProperty<CityCountry<?>> countryProperty() { return country.getReadOnlyProperty(); }
    
    public CityModel(CityImpl dao) {
        super(dao);
        this.name = new ReadOnlyStringWrapper(dao.getName());
    }
}
