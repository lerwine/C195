package scheduler.view.country;

import javafx.beans.property.StringProperty;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
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
    protected void refreshFromDAO(CountryImpl dao) {
        name.set(dao.getName());
    }

    @Override
    public Factory<CountryImpl, CountryModel> getDaoFactory() {
        return CountryImpl.getFactory();
    }

    @Override
    public String toString() {
        return name.get();
    }

}
