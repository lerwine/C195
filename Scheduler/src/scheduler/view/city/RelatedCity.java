package scheduler.view.city;

import java.time.ZoneId;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.CityDAO;
import scheduler.dao.ICityDAO;
import scheduler.model.ui.RelatedModel;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.ui.CityDbItem;
import scheduler.model.ui.CountryItem;
import scheduler.observables.NestedStringProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCity extends RelatedModel<ICityDAO> implements CityDbItem<ICityDAO> {

    private final ReadOnlyStringWrapper name;
    private final NestedStringProperty<CountryItem> countryName;
    private final ReadOnlyObjectWrapper<CountryItem> country;
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
    protected void onDataObjectPropertyChanged(ICityDAO dao, String propertyName) {
        if (propertyName.equals(CityDAO.PROP_PREDEFINEDCITY)) {
            predefinedData.set(dao.getPredefinedData());
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
    public CountryItem getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryItem> countryProperty() {
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

    public ReadOnlyObjectProperty<PredefinedCity> predefinedDataProperty() {
        return predefinedData.getReadOnlyProperty();
    }

}
