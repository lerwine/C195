package scheduler.model.predefined;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.AddressDAO;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.model.Address;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.IFxRecordModel;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.CalculatedStringExpression;
import static scheduler.observables.CalculatedStringExpression.calculateAddressLines;
import static scheduler.observables.CalculatedStringExpression.calculateCityZipCountry;
import static scheduler.observables.CalculatedStringExpression.calculateMultiLineAddress;
import scheduler.observables.CalculatedStringProperty;
import scheduler.observables.NestedObjectValueProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.ObservableTriplet;
import scheduler.observables.ObservableTuple;
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.util.Values;

/**
 * Represents a pre-defined address that is loaded with the application.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedAddress extends PredefinedItem implements IFxRecordModel<AddressDAO>, AddressItem, Address {

    private final ReadOnlyBooleanWrapper mainOffice;
    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final CalculatedStringProperty<Tuple<String, String>> addressLines;
    private final ReadOnlyObjectWrapper<PredefinedCity> city;
    private final NestedStringProperty<PredefinedCity> cityName;
    private final NestedStringProperty<PredefinedCity> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final CalculatedStringProperty<Triplet<String, String, String>> cityZipCountry;
    private final NestedStringProperty<PredefinedCity> language;
    private final NestedObjectValueProperty<PredefinedCity, ZoneId> zoneId;
    private final ReadOnlyStringWrapper referenceKey;
    private final CalculatedStringProperty<Triplet<String, String, String>> multiLineAddress;
//    private DAO dao;
    private final ReadOnlyObjectWrapper<AddressDAO> dataObject = new ReadOnlyObjectWrapper<>();

    PredefinedAddress(AddressElement source, PredefinedCity city) {
        referenceKey = new ReadOnlyStringWrapper(this, "referenceKey", source.getKey());
        mainOffice = new ReadOnlyBooleanWrapper(this, "mainOffice", source.isMainOffice());
        address1 = new ReadOnlyStringWrapper(this, "address1", source.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", source.getAddress2());
        addressLines = new CalculatedStringProperty<>(this, "addressLines",
                new ObservableTuple<>(
                        new CalculatedStringExpression<>(address1, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(address2, Values::asNonNullAndWsNormalized)
                ), (t) -> calculateAddressLines(t.getValue1(), t.getValue2())
        );
        this.city = new ReadOnlyObjectWrapper<>(this, "city", city);
        cityName = new NestedStringProperty<>(this, "cityName", this.city, (t) -> t.nameProperty());
        countryName = new NestedStringProperty<>(this, "countryName", this.city, (t) -> t.countryNameProperty());
        postalCode = new ReadOnlyStringWrapper(this, "postalCode", source.getPostalCode());
        phone = new ReadOnlyStringWrapper(this, "phone", source.getPhone());
        cityZipCountry = new CalculatedStringProperty<>(this, "cityZipCountry",
                new ObservableTriplet<>(
                        new CalculatedStringExpression<>(cityName, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(countryName, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(postalCode, Values::asNonNullAndWsNormalized)
                ), (t) -> calculateCityZipCountry(t.getValue1(), t.getValue2(), t.getValue3())
        );

        multiLineAddress = new CalculatedStringProperty<>(this, "cityZipCountry",
                new ObservableTriplet<>(
                        addressLines,
                        cityZipCountry,
                        new CalculatedStringExpression<>(phone, Values::asNonNullAndWsNormalized)
                ), (t) -> calculateMultiLineAddress(t.getValue1(), t.getValue2(), t.getValue3())
        );
        language = new NestedStringProperty<>(this, "language", this.city, (t) -> t.languageProperty());
        zoneId = new NestedObjectValueProperty<>(this, "zoneId", this.city, (t) -> t.zoneIdProperty());
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

    public AddressDAO getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<? extends AddressDAO> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedAddress#createDateProperty
    }

    @Override
    public ReadOnlyStringProperty createdByProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedAddress#createdByProperty
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedAddress#lastModifiedDateProperty
    }

    @Override
    public ReadOnlyStringProperty lastModifiedByProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedAddress#lastModifiedByProperty
    }

    class PlaceHolderDAO extends BasePlaceHolderDAO implements IAddressDAO {

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
