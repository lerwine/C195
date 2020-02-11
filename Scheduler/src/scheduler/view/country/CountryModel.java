/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.CountryFactory;

/**
 *
 * @author erwinel
 */
public class CountryModel extends scheduler.view.ItemModel<CountryFactory.CountryImpl> implements CityCountry<CountryFactory.CountryImpl> {

    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() { return name.get(); }

    @Override
    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }

    public CountryModel(CountryFactory.CountryImpl dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(dao.getName());
    }

    @Override
    protected void refreshFromDAO(CountryFactory.CountryImpl dao) {
        name.set(dao.getName());
    }
}
