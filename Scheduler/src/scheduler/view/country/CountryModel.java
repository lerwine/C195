/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import java.sql.SQLException;
import javafx.beans.property.StringProperty;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.ItemModel;

/**
 *
 * @author erwinel
 */
public final class CountryModel extends ItemModel<CountryImpl> implements CountryReferenceModel<CountryImpl> {

    private final NonNullableStringProperty name;

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    public CountryModel(CountryImpl dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
    }

    @Override
    protected void refreshFromDAO(CountryImpl dao) throws SQLException, ClassNotFoundException {
        name.set(dao.getName());
    }

    @Override
    public Factory<CountryImpl> getDaoFactory() { return CountryImpl.getFactory(); }
}
