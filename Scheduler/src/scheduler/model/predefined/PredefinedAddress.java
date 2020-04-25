package scheduler.model.predefined;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.model.ui.AddressItem;

/**
 * Represents a pre-defined address that is loaded with the application.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.model.predefined.PredefinedAddress}
 */
public class PredefinedAddress implements AddressItem {

    private final ReadOnlyBooleanWrapper mainOffice;
    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final ReadOnlyObjectWrapper<PredefinedCity> city;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;

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

    PredefinedAddress(AddressElement source, PredefinedCity city) {
        this.mainOffice = new ReadOnlyBooleanWrapper(source.isMainOffice());
        this.address1 = new ReadOnlyStringWrapper(source.getAddress1());
        this.address2 = new ReadOnlyStringWrapper(source.getAddress2());
        this.city = new ReadOnlyObjectWrapper<>(city);
        this.postalCode = new ReadOnlyStringWrapper(source.getPhone());
        this.phone = new ReadOnlyStringWrapper(source.getPostalCode());
    }

}
