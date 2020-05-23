package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCountry extends RelatedModel<ICountryDAO> implements CountryItem<ICountryDAO> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<CountryDAO.PredefinedElement> predefinedData;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;

    public RelatedCountry(ICountryDAO rowData) {
        super(rowData);
        name = new ReadOnlyStringWrapper(this, "name");
        predefinedData = new ReadOnlyObjectWrapper<>(this, "predefinedData", rowData.getPredefinedElement());
        zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId");
        language = new ReadOnlyStringWrapper(this, "language");
        predefinedData.addListener(this::onPredefinedDataChange);
        onPredefinedDataChange(predefinedData, null, predefinedData.get());
    }

    @SuppressWarnings("unchecked")
    private void onPredefinedDataChange(ObservableValue<? extends CountryDAO.PredefinedElement> observable, CountryDAO.PredefinedElement oldValue, CountryDAO.PredefinedElement newValue) {
        if (null != newValue) {
            name.set(newValue.getLocale().getDisplayCountry());
            zoneId.set(ZoneId.of(newValue.getDefaultZoneId()));
            language.set(newValue.getLocale().getDisplayLanguage());
        } else {
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
    public CountryDAO.PredefinedElement getPredefinedElement() {
        return predefinedData.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryDAO.PredefinedElement> predefinedElementProperty() {
        return predefinedData;
    }

}
