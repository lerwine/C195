package scheduler.view.city;

import java.sql.SQLException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.CityImpl;
import scheduler.dao.Country;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.country.CountryReferenceModel;
import scheduler.view.country.CountryReferenceModelImpl;

/**
 *
 * @author erwinel
 */
public final class CityModel extends scheduler.view.ItemModel<CityImpl> implements CityReferenceModel<CityImpl> {
    
    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<CountryReferenceModel<? extends Country>> country;
    private final ChildPropertyWrapper<String, CountryReferenceModel<? extends Country>> countryName;

    @Override
    public String getName() { return name.get(); }

    public void setName(String value) {
        name.set(value);
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public CountryReferenceModel<? extends Country> getCountry() {
        return country.get();
    }

    public void setCountry(CountryReferenceModel<? extends Country> value) {
        country.set(value);
    }

    @Override
    public ObjectProperty<CountryReferenceModel<? extends Country>> countryProperty() {
        return country;
    }
    
    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CountryReferenceModel<? extends Country>> countryNameProperty() {
        return countryName;
    }

    public CityModel(CityImpl dao) throws SQLException, ClassNotFoundException {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
        Country c = dao.getCountry().ensurePartial(CountryImpl.getFactory());
        country = new SimpleObjectProperty<>(this, "country", (null == c) ? null : new CountryReferenceModelImpl(c));
        countryName = new ChildPropertyWrapper<>(this, "countryName", country, (t) -> t.nameProperty());
    }

    @Override
    protected void refreshFromDAO(CityImpl dao) throws SQLException, ClassNotFoundException {
        name.set(dao.getName());
        Country c = dao.getCountry().ensurePartial(CountryImpl.getFactory());
        country.set((null == c) ? null : new CountryReferenceModelImpl(c));
    }

    @Override
    public Factory<CityImpl> getDaoFactory() { return CityImpl.getFactory(); }
    
}
