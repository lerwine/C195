/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Country;
import scheduler.dao.CountryFactory;
import scheduler.view.ChildModel;

/**
 *
 * @author erwinel
 * @param <T>
 */
public interface CityCountry<T extends Country> extends ChildModel<T> {
    String getName();
    ReadOnlyStringProperty nameProperty();

    public static CityCountry<?> of(Country dao) {
        if (null == dao)
            return null;
        if (dao instanceof CountryFactory.CountryImpl)
            return new CountryModel((CountryFactory.CountryImpl)dao);
        return new CityCountry<Country>() {
            private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(dao.getName());
            @Override
            public String getName() { return name.get(); }
            @Override
            public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
            @Override
            public Country getDataObject() { return dao; }
        };
    }
}
