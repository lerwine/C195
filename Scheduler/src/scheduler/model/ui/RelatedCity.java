package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.observables.NestedStringProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCity extends RelatedModel<ICityDAO> implements CityItem<ICityDAO> {

    private final ReadOnlyStringWrapper name;
    private final NestedStringProperty<CountryItem<? extends ICountryDAO>> countryName;
    private final ReadOnlyObjectWrapper<CountryItem<? extends ICountryDAO>> country;
    private final ReadOnlyObjectWrapper<PredefinedCity> predefinedData;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;

    public RelatedCity(ICityDAO dao) {
        super(dao);
        PredefinedCity c = dao.getPredefinedData();
        predefinedData = new ReadOnlyObjectWrapper<>(this, "predefinedData", c);
        name = new ReadOnlyStringWrapper(this, "name", c.getName());
        PredefinedCountry n = c.getCountry();
        country = new ReadOnlyObjectWrapper<>(this, "country");
        countryName = new NestedStringProperty<>(this, "countryName", country, (t) -> t.nameProperty());
        zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId");
        language = new ReadOnlyStringWrapper(this, "language");
        predefinedData.addListener(this::onPredefinedDataChanged);
        onPredefinedDataChanged(predefinedData);
    }

    @SuppressWarnings("unchecked")
    private void onPredefinedDataChanged(Observable observable) {
        PredefinedCity c = ((ReadOnlyObjectWrapper<PredefinedCity>) observable).get();
        PredefinedCountry n = c.getCountry();
        country.set(n);
        name.set(c.getName());
        zoneId.set(c.getZoneId());
        language.set(n.getLanguage());
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
    public PredefinedCity getPredefinedData() {
        return predefinedData.get();
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCity> predefinedDataProperty() {
        return predefinedData.getReadOnlyProperty();
    }

}
