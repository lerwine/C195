/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.country;

import expressions.NonNullableStringProperty;
import java.sql.Connection;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Country;
import scheduler.dao.CountryImpl;
import view.ChildModel;

/**
 *
 * @author erwinel
 */
public class CountryModel extends view.ItemModel<CountryImpl> implements CityCountry<CountryImpl> {

    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() { return name.get(); }

    @Override
    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }

    public CountryModel(CountryImpl dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(dao.getName());
    }

    @Override
    public void refreshFromDAO() {
        super.refreshFromDAO(); //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean delete(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveChanges(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
