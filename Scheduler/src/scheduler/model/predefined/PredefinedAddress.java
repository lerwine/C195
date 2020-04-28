package scheduler.model.predefined;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.model.db.AddressRowData;
import scheduler.model.ui.AddressItem;
import scheduler.observables.CityZipCountryProperty;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.util.Values;

/**
 * Represents a pre-defined address that is loaded with the application.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedAddress extends PredefinedItem implements AddressItem, AddressRowData {

    private final ReadOnlyBooleanWrapper mainOffice;
    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final AddressLinesProperty addressLines;
    private final ReadOnlyObjectWrapper<PredefinedCity> city;
    private final NestedStringBindingProperty<PredefinedCity> cityName;
    private final NestedStringBindingProperty<PredefinedCity> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final CityZipCountryProperty cityZipCountry;
    private final ReadOnlyStringWrapper referenceKey;

    PredefinedAddress(AddressElement source, PredefinedCity city) {
        referenceKey = new ReadOnlyStringWrapper(this, "referenceKey", source.getKey());
        mainOffice = new ReadOnlyBooleanWrapper(this, "mainOffice", source.isMainOffice());
        address1 = new ReadOnlyStringWrapper(this, "address1", source.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", source.getAddress2());
        addressLines = new AddressLinesProperty();
        this.city = new ReadOnlyObjectWrapper<>(this, "city", city);
        cityName = new NestedStringBindingProperty<>(this, "cityName", this.city, (t) -> t.nameProperty());
        countryName = new NestedStringBindingProperty<>(this, "countryName", this.city, (t) -> t.countryNameProperty());
        postalCode = new ReadOnlyStringWrapper(this, "postalCode", source.getPhone());
        phone = new ReadOnlyStringWrapper(this, "phone", source.getPostalCode());
        cityZipCountry = new CityZipCountryProperty(this, "cityZipCountry", this);
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
    public NestedStringBindingProperty<PredefinedCity> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public NestedStringBindingProperty<PredefinedCity> countryNameProperty() {
        return countryName;
    }

    @Override
    public ReadOnlyProperty<String> addressLinesProperty() {
        return addressLines;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyProperty<String> cityZipCountryProperty() {
        return cityZipCountry;
    }

    class AddressLinesProperty extends StringBinding implements ReadOnlyProperty<String> {

        AddressLinesProperty() {
            super.bind(address1, address2);
        }

        @Override
        protected String computeValue() {
            String a1 = Values.asNonNullAndWsNormalized(address1.get());
            String a2 = Values.asNonNullAndWsNormalized(address2.get());
            if (a2.isEmpty()) {
                return a1;
            }
            return a1.isEmpty() ? a2 : String.format("%s%n%s", a1, a2);
        }

        @Override
        public Object getBean() {
            return PredefinedAddress.this;
        }

        @Override
        public String getName() {
            return "addressLines";
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.observableArrayList(address1, address2);
        }

        @Override
        public void dispose() {
            super.unbind(address1, address2);
            super.dispose();
        }

    }

}
