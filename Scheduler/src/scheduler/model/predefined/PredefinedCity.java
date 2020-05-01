package scheduler.model.predefined;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import scheduler.model.db.CityRowData;
import scheduler.model.ui.CityItem;
import scheduler.observables.NestedStringBindingProperty;

/**
 * Represents a pre-defined city that is loaded with the application.
 * <p>
 * This also specifies the {@link ZoneId} to be used for customers associated with the specified city</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedCity extends PredefinedItem implements CityItem, CityRowData {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyStringWrapper resourceKey;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;
    private final ReadOnlyObjectWrapper<PredefinedCountry> country;
    private final ReadOnlyListWrapper<PredefinedAddress> addresses;
    private final NestedStringBindingProperty<PredefinedCountry> countryName;

    PredefinedCity(CityElement source, PredefinedCountry country, ObservableList<PredefinedAddress> addresses) {
        String key = source.getKey();
        name = new ReadOnlyStringWrapper(this, "name", PredefinedData.getCityDisplayName(key));
        resourceKey = new ReadOnlyStringWrapper(this, "resourceKey", key);
        zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId", ZoneId.of(source.getZoneId()));
        this.country = new ReadOnlyObjectWrapper<>(this, "country", country);
        this.addresses = new ReadOnlyListWrapper<>(this, "addresses", addresses);
        countryName = new NestedStringBindingProperty<>(this, "countryName", this.country, (t) -> t.nameProperty());
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    /**
     * Gets the resource bundle key to use for getting the name of the city in the current language.
     *
     * @return The resource bundle key to use for getting the name of the city in the current language.
     */
    public String getResourceKey() {
        return resourceKey.get();
    }

    public ReadOnlyStringProperty resourceKeyProperty() {
        return resourceKey.getReadOnlyProperty();
    }

    /**
     * Gets the time zone to use for customers associated with the current city.
     *
     * @return The {@link ZoneId} to use for customers associated with the current city.
     */
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyProperty();
    }

    @Override
    public PredefinedCountry getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCountry> countryProperty() {
        return country.getReadOnlyProperty();
    }

    /**
     * Gets a list of addresses associated with the current city.
     *
     * @return A list of addresses associated with the current city.
     */
    public ObservableList<PredefinedAddress> getAddresses() {
        return addresses.get();
    }

    public ReadOnlyListProperty<PredefinedAddress> addressesProperty() {
        return addresses.getReadOnlyProperty();
    }

    @Override
    public NestedStringBindingProperty<PredefinedCountry> countryNameProperty() {
        return countryName;
    }

    @Override
    public PredefinedCity asPredefinedData() {
        return this;
    }

    @Override
    public String toString() {
        return name.get();
    }

}
