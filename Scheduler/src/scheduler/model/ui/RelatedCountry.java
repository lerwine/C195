package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.predefined.PredefinedCountry;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCountry extends RelatedModel<ICountryDAO> implements CountryItem<ICountryDAO> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<PredefinedCountry> predefinedData;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;

    public RelatedCountry(ICountryDAO rowData) {
        super(rowData);
        PredefinedCountry c = rowData.getPredefinedData();
        name = new ReadOnlyStringWrapper(this, "name", c.getName());
        predefinedData = new ReadOnlyObjectWrapper<>(this, "predefinedData", c);
        zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId");
        language = new ReadOnlyStringWrapper(this, "language");
        predefinedData.addListener(this::onPredefinedDataChange);
        onPredefinedDataChange(predefinedData);
    }

    @SuppressWarnings("unchecked")
    private void onPredefinedDataChange(Observable observable) {
        PredefinedCountry country = ((ReadOnlyObjectWrapper<PredefinedCountry>) observable).get();
        name.set(country.getName());
        zoneId.set(country.getZoneId());
        language.set(country.getLocale().getDisplayLanguage());
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
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyProperty();
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
    public PredefinedCountry getPredefinedData() {
        return predefinedData.get();
    }

    public ReadOnlyObjectProperty<PredefinedCountry> predefinedDataProperty() {
        return predefinedData;
    }

    @Override
    protected void onDataObjectPropertyChanged(ICountryDAO dao, String propertyName) {
        if (propertyName.equals(CountryDAO.PROP_PREDEFINEDCOUNTRY)) {
            predefinedData.set(dao.getPredefinedData());
        }
    }

}
