package scheduler.model.predefined;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.AddressDbRecord;
import scheduler.dao.ICityDAO;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.IFxRecordModel;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.NestedObjectProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.ObservableStringDerivitive;
import scheduler.observables.WrappedStringObservableProperty;

/**
 * Represents a pre-defined address that is loaded with the application.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedAddress extends PredefinedItem<AddressDbRecord> implements IFxRecordModel<AddressDbRecord>, AddressItem<AddressDbRecord> {

    private final ReadOnlyBooleanWrapper mainOffice;
    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final WrappedStringObservableProperty addressLines;
    private final ReadOnlyObjectWrapper<PredefinedCity> city;
    private final NestedStringProperty<PredefinedCity> cityName;
    private final NestedStringProperty<PredefinedCity> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final WrappedStringObservableProperty cityZipCountry;
    private final NestedStringProperty<PredefinedCity> language;
    private final NestedObjectProperty<PredefinedCity, ZoneId> zoneId;
    private final ReadOnlyStringWrapper referenceKey;
    private final WrappedStringObservableProperty multiLineAddress;
    private final ReadOnlyObjectProperty<LocalDateTime> createDate;
    private final ReadOnlyStringProperty createdBy;
    private final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringProperty lastModifiedBy;

    PredefinedAddress(AddressElement source, PredefinedCity city) {
        setDataObject(new PlaceHolderDAO());
        createDate = createReadOnlyDaoDateTimeProperty("createDate", (t) -> t.getCreateDate());
        createdBy = createReadOnlyDaoStringProperty("createdBy", (t) -> t.getCreatedBy());
        lastModifiedDate = createReadOnlyDaoDateTimeProperty("lastModifiedDate", (t) -> t.getLastModifiedDate());
        lastModifiedBy = createReadOnlyDaoStringProperty("lastModifiedBy", (t) -> t.getLastModifiedBy());
        referenceKey = new ReadOnlyStringWrapper(this, "referenceKey", source.getKey());
        mainOffice = new ReadOnlyBooleanWrapper(this, "mainOffice", source.isMainOffice());
        address1 = new ReadOnlyStringWrapper(this, "address1", source.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", source.getAddress2());
        addressLines = new WrappedStringObservableProperty(this, "addressLines",
                ObservableStringDerivitive.of(address1, address2, AddressModel::calculateAddressLines)
        );
        this.city = new ReadOnlyObjectWrapper<>(this, "city", city);
        cityName = new NestedStringProperty<>(this, "cityName", this.city, (t) -> t.nameProperty());
        countryName = new NestedStringProperty<>(this, "countryName", this.city, (t) -> t.countryNameProperty());
        postalCode = new ReadOnlyStringWrapper(this, "postalCode", source.getPostalCode());
        phone = new ReadOnlyStringWrapper(this, "phone", source.getPhone());
        cityZipCountry = new WrappedStringObservableProperty(this, "cityZipCountry",
                ObservableStringDerivitive.of(cityName, countryName, postalCode, AddressModel::calculateCityZipCountry)
        );

        multiLineAddress = new WrappedStringObservableProperty(this, "multiLineAddress",
                ObservableStringDerivitive.of(addressLines, cityZipCountry, phone, AddressModel::calculateMultiLineAddress)
        );
        language = new NestedStringProperty<>(this, "language", this.city, (t) -> t.languageProperty());
        zoneId = new NestedObjectProperty<>(this, "zoneId", this.city, (t) -> t.zoneIdProperty());
    }

    public String getReferenceKey() {
        return referenceKey.get();
    }

    public ReadOnlyStringProperty referenceKeyProperty() {
        return referenceKey.getReadOnlyProperty();
    }

    public boolean isMainOffice() {
        return mainOffice.get();
    }

    public ReadOnlyBooleanProperty mainOfficeProperty() {
        return mainOffice.getReadOnlyProperty();
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ReadOnlyStringProperty address1Property() {
        return address1.getReadOnlyProperty();
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ReadOnlyStringProperty address2Property() {
        return address2.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyStringProperty addressLinesProperty() {
        return addressLines.getReadOnlyStringProperty();
    }

    @Override
    public PredefinedCity getCity() {
        return city.get();
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCity> cityProperty() {
        return city.getReadOnlyProperty();
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ReadOnlyStringProperty postalCodeProperty() {
        return postalCode.getReadOnlyProperty();
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ReadOnlyStringProperty phoneProperty() {
        return phone.getReadOnlyProperty();
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
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyStringProperty cityZipCountryProperty() {
        return cityZipCountry.getReadOnlyStringProperty();
    }

    @Override
    public String toString() {
        return AddressTextProperty.convertToString(this);
    }

    @Override
    public PredefinedAddress getPredefinedData() {
        return this;
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
    public LocalDateTime getCreateDate() {
        return createDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return createDate;
    }

    @Override
    public String getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public ReadOnlyStringProperty createdByProperty() {
        return createdBy;
    }

    @Override
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate;
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy.get();
    }

    @Override
    public ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy;
    }

    class PlaceHolderDAO extends BasePlaceHolderDAO implements AddressDbRecord {

        @Override
        public ICityDAO getCity() {
            return PredefinedAddress.this.getCity().getDataObject();
        }

        @Override
        public String getAddress1() {
            return PredefinedAddress.this.getAddress1();
        }

        @Override
        public String getAddress2() {
            return PredefinedAddress.this.getAddress2();
        }

        @Override
        public String getPostalCode() {
            return PredefinedAddress.this.getPostalCode();
        }

        @Override
        public String getPhone() {
            return PredefinedAddress.this.getPhone();
        }

    }

}
