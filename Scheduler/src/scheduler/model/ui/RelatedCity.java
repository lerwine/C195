package scheduler.model.ui;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.CityDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.observables.DerivedObjectProperty;
import scheduler.observables.DerivedStringProperty;
import scheduler.observables.NestedStringProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCity extends RelatedModel<ICityDAO> implements CityItem<ICityDAO> {

    private final ReadOnlyObjectWrapper<CityDAO.PredefinedCityElement> predefinedElement;
    private final DerivedStringProperty<CityDAO.PredefinedCityElement> name;
    private final DerivedObjectProperty<CityDAO.PredefinedCityElement, CountryItem<? extends ICountryDAO>> country;
    private final NestedStringProperty<CountryItem<? extends ICountryDAO>> countryName;
    private final NestedStringProperty<CountryItem<? extends ICountryDAO>> language;
    private final DerivedObjectProperty<CityDAO.PredefinedCityElement, ZoneId> zoneId;
    private final DerivedStringProperty<ZoneId> timeZoneDisplay;
    private final CountryItem<? extends ICountryDAO> originalCountryModel;

    public RelatedCity(ICityDAO dao) {
        super(dao);
        originalCountryModel = CountryItem.createModel(dao.getCountry());
        predefinedElement = new ReadOnlyObjectWrapper<>(this, "predefinedElement", dao.getPredefinedElement());
        name = new DerivedStringProperty<>(this, "name", predefinedElement, CityModel::toCityName);
        country = new DerivedObjectProperty<>(this, "country", predefinedElement, this::toCountryModel);
        countryName = new NestedStringProperty<>(this, "countryName", country, (t) -> t.nameProperty());
        language = new NestedStringProperty<>(this, "language", country, (t) -> t.languageProperty());
        zoneId = new DerivedObjectProperty<>(this, "zoneId", predefinedElement, CityModel::toZoneId);
        timeZoneDisplay = new DerivedStringProperty<>(this, "timeZoneDisplay", zoneId, CountryModel::toTimeZoneDisplay);
    }

    private CountryItem<? extends ICountryDAO> toCountryModel(CityDAO.PredefinedCityElement element) {
        if (null != element && !Objects.equals(originalCountryModel.getPredefinedElement(), element.getCountry())) {
            return CountryItem.createModel(element.getCountry().getDataAccessObject());
        }
        return originalCountryModel;
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
    public CountryItem<? extends ICountryDAO> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryItem<? extends ICountryDAO>> countryProperty() {
        return country.getReadOnlyObjectProperty();
    }

    @Override
    public String getCountryName() {
        return countryNameProperty().get();
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
        return zoneId.getReadOnlyObjectProperty();
    }

    @Override
    public String getTimeZoneDisplay() {
        return timeZoneDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty timeZoneDisplayProperty() {
        return timeZoneDisplay.getReadOnlyStringProperty();
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
    public CityDAO.PredefinedCityElement getPredefinedElement() {
        return predefinedElement.get();
    }

    @Override
    public ReadOnlyObjectProperty<CityDAO.PredefinedCityElement> predefinedElementProperty() {
        return predefinedElement.getReadOnlyProperty();
    }

}
