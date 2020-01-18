/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.country;

import expressions.NonNullableStringProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Country;
import scheduler.dao.CountryImpl;
import view.ChildModel;

/**
 *
 * @author erwinel
 */
public class CountryModel extends view.ModelBase<CountryImpl> implements CityCountry<CountryImpl> {

    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() { return name.get(); }

    @Override
    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }

    public CountryModel(CountryImpl dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(dao.getName());
    }
}
