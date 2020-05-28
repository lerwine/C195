package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.model.CustomerAddress;
import scheduler.model.ModelHelper;
import scheduler.observables.NestedObjectProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.ObservableStringDerivitive;
import scheduler.observables.WrappedStringObservableProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedAddress extends RelatedModel<IAddressDAO> implements AddressItem<IAddressDAO> {

    private final ReadOnlyStringProperty address1;
    private final ReadOnlyStringProperty address2;
    private final WrappedStringObservableProperty addressLines;
    private final ReadOnlyObjectProperty<CityItem<? extends ICityDAO>> city;
    private final NestedStringProperty<CityItem<? extends ICityDAO>> cityName;
    private final NestedStringProperty<CityItem<? extends ICityDAO>> countryName;
    private final ReadOnlyStringProperty postalCode;
    private final ReadOnlyStringProperty phone;
    private final WrappedStringObservableProperty cityZipCountry;
    private final NestedStringProperty<CityItem<? extends ICityDAO>> language;
    private final NestedObjectProperty<CityItem<? extends ICityDAO>, ZoneId> zoneId;

    public RelatedAddress(IAddressDAO rowData) {
        super(rowData);

        address1 = createReadOnlyDaoStringProperty("address1", (t) -> t.getAddress1());
        address2 = createReadOnlyDaoStringProperty("address2", (t) -> t.getAddress2());
        addressLines = new WrappedStringObservableProperty(this, "addressLines",
                ObservableStringDerivitive.of(address1, address2, AddressModel::calculateAddressLines)
        );
        city = createReadOnlyNestedDaoModelProperty("city", (t) -> (null == t) ? null : t.getCity(), CityItem::createModel);
        cityName = new NestedStringProperty<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new NestedStringProperty<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new ReadOnlyStringWrapper(this, "postalCode", rowData.getPostalCode());
        phone = new ReadOnlyStringWrapper(this, "phone", rowData.getPhone());
        cityZipCountry = new WrappedStringObservableProperty(this, "cityZipCountry",
                ObservableStringDerivitive.of(cityName, countryName, postalCode, AddressModel::calculateCityZipCountry)
        );
        language = new NestedStringProperty<>(this, "language", city, (t) -> t.languageProperty());
        zoneId = new NestedObjectProperty<>(this, "zoneId", city, (t) -> t.zoneIdProperty());
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ReadOnlyStringProperty address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ReadOnlyStringProperty address2Property() {
        return address2;
    }

    public String getAddressLines() {
        return addressLines.get();
    }

    @Override
    public ReadOnlyStringProperty addressLinesProperty() {
        return addressLines.getReadOnlyStringProperty();
    }

    @Override
    public CityItem<? extends ICityDAO> getCity() {
        return city.get();
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ReadOnlyStringProperty cityNameProperty() {
        return cityName.getReadOnlyStringProperty();
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName.getReadOnlyStringProperty();
    }

    @Override
    public ReadOnlyObjectProperty<CityItem<? extends ICityDAO>> cityProperty() {
        return city;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ReadOnlyStringProperty postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ReadOnlyStringProperty phoneProperty() {
        return phone;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyStringProperty cityZipCountryProperty() {
        return cityZipCountry.getReadOnlyStringProperty();
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
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyStringProperty();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof CustomerAddress && ModelHelper.areSameRecord(this, (CustomerAddress) obj);
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
    }

}
