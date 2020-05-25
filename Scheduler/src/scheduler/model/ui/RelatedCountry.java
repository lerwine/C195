package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.observables.DerivedObjectProperty;
import scheduler.observables.DerivedStringProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCountry extends RelatedModel<ICountryDAO> implements CountryItem<ICountryDAO> {

    private final ReadOnlyObjectWrapper<CountryDAO.PredefinedCountryElement> predefinedElement;
    private final DerivedStringProperty<CountryDAO.PredefinedCountryElement> name;
    private final DerivedStringProperty<CountryDAO.PredefinedCountryElement> language;
    private final DerivedObjectProperty<CountryDAO.PredefinedCountryElement, ZoneId> zoneId;
    private final DerivedStringProperty<ZoneId> defaultTimeZoneDisplay;

    public RelatedCountry(ICountryDAO rowData) {
        super(rowData);
        predefinedElement = new ReadOnlyObjectWrapper<>(this, "predefinedElement", rowData.getPredefinedElement());
        name = new DerivedStringProperty<>(this, "name", predefinedElement, CountryModel::toCountryName);
        zoneId = new DerivedObjectProperty<>(this, "zoneId", predefinedElement, CountryModel::toZoneId);
        language = new DerivedStringProperty<>(this, "language", predefinedElement, CountryModel::toLanguage);
        defaultTimeZoneDisplay = new DerivedStringProperty<>(this, "defaultTimeZoneDisplay", zoneId, CountryModel::toTimeZoneDisplay);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyStringProperty();
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyObjectProperty();
    }

    @Override
    public String getDefaultTimeZoneDisplay() {
        return defaultTimeZoneDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty defaultTimeZoneDisplayProperty() {
        return defaultTimeZoneDisplay.getReadOnlyStringProperty();
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyStringProperty();
    }

    @Override
    public CountryDAO.PredefinedCountryElement getPredefinedElement() {
        return predefinedElement.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryDAO.PredefinedCountryElement> predefinedElementProperty() {
        return predefinedElement;
    }

}
