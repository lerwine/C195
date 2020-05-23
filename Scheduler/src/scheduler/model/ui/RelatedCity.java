package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.PredefinedData;
import scheduler.observables.NestedStringProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCity extends RelatedModel<ICityDAO> implements CityItem<ICityDAO> {

    private final ReadOnlyStringWrapper name;
    private final NestedStringProperty<CountryItem<? extends ICountryDAO>> countryName;
    private final ReadOnlyObjectWrapper<CountryItem<? extends ICountryDAO>> country;
    private final ReadOnlyObjectWrapper<CityDAO.PredefinedElement> predefinedElement;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;

    public RelatedCity(ICityDAO dao) {
        super(dao);
        CityDAO.PredefinedElement c = dao.getPredefinedElement();
        predefinedElement = new ReadOnlyObjectWrapper<>(this, "predefinedData", c);
        name = new ReadOnlyStringWrapper(this, "name", PredefinedData.getCityDisplayName(c.getKey()));
        CountryDAO.PredefinedElement n = c.getCountry();
        country = new ReadOnlyObjectWrapper<>(this, "country", CountryItem.createModel(dao.getCountry()));
        countryName = new NestedStringProperty<>(this, "countryName", country, (t) -> t.nameProperty());
        zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId", City.getZoneIdOf(dao));
        language = new ReadOnlyStringWrapper(this, "language", Country.getLanguageOf(dao.getCountry()));
        predefinedElement.addListener(this::onPredefinedDataChanged);
        onPredefinedDataChanged(predefinedElement, null, predefinedElement.get());
    }

    @SuppressWarnings("unchecked")
    private void onPredefinedDataChanged(ObservableValue<? extends CityDAO.PredefinedElement> observable, CityDAO.PredefinedElement oldValue,
            CityDAO.PredefinedElement newValue) {
        if (null != newValue) {
            CountryDAO.PredefinedElement n = newValue.getCountry();
            country.set(CountryItem.createModel(PredefinedData.lookupCountry(n.getLocale().getCountry())));
            name.set(PredefinedData.getCityDisplayName(newValue.getKey()));
            zoneId.set(ZoneId.of(newValue.getZoneId()));
            language.set(n.getLocale().getDisplayLanguage());
        } else {
            country.set(null);
            name.set("");
            zoneId.set(ZoneId.systemDefault());
            language.set("");
        }
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public CountryItem<? extends ICountryDAO> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryItem<? extends ICountryDAO>> countryProperty() {
        return country.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName.getReadOnlyStringProperty();
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId;
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyProperty();
    }

    @Override
    public CityDAO.PredefinedElement getPredefinedElement() {
        return predefinedElement.get();
    }

    @Override
    public ReadOnlyObjectProperty<CityDAO.PredefinedElement> predefinedDataProperty() {
        return predefinedElement.getReadOnlyProperty();
    }

}
