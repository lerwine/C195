package scheduler.view.customer;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.DataRowState;
import scheduler.model.ModelHelper;
import scheduler.model.db.AddressRowData;
import scheduler.model.db.CustomerRowData;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.CustomerItem;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.observables.RowStateProperty;
import scheduler.model.ui.RelatedAddress;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCustomer implements CustomerItem<CustomerRowData> {

    private final ReadOnlyObjectWrapper<CustomerRowData> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final RowStateProperty rowState;
    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<RelatedAddress> address;
    private final NestedStringBindingProperty<RelatedAddress> address1;
    private final NestedStringBindingProperty<RelatedAddress> address2;
    private final NestedStringBindingProperty<RelatedAddress> cityName;
    private final NestedStringBindingProperty<RelatedAddress> countryName;
    private final NestedStringBindingProperty<RelatedAddress> postalCode;
    private final NestedStringBindingProperty<RelatedAddress> phone;
    private final NestedStringBindingProperty<RelatedAddress> cityZipCountry;
    private final AddressTextProperty addressText;
    private final ReadOnlyBooleanWrapper active;
    private final StringBinding multiLineAddress;

    public RelatedCustomer(CustomerRowData rowData) {
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", rowData.getPrimaryKey());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", rowData);
        rowState = new RowStateProperty(this, "rowState", ModelHelper.getRowState(rowData));
        name = new ReadOnlyStringWrapper(this, "name", rowData.getName());
        AddressRowData a = rowData.getAddress();
        address = new ReadOnlyObjectWrapper<>((null == a) ? null : new RelatedAddress(a));
        address1 = new NestedStringBindingProperty<>(this, "address1", address, (c) -> c.address1Property());
        address2 = new NestedStringBindingProperty<>(this, "address2", address, (c) -> c.address2Property());
        cityName = new NestedStringBindingProperty<>(this, "cityName", address, (c) -> c.cityNameProperty());
        countryName = new NestedStringBindingProperty<>(this, "countryName", address, (c) -> c.countryNameProperty());
        postalCode = new NestedStringBindingProperty<>(this, "postalCode", address, (c) -> c.postalCodeProperty());
        phone = new NestedStringBindingProperty<>(this, "phone", address, (c) -> c.phoneProperty());
        cityZipCountry = new NestedStringBindingProperty<>(this, "cityZipCountry", address, (t) -> t.cityZipCountryProperty());
        addressText = new AddressTextProperty(this, "addressText", this);
        active = new ReadOnlyBooleanWrapper(this, "active", rowData.isActive());
        multiLineAddress = AddressItem.createMultiLineAddressBinding(address1, address2, cityZipCountry, phone);
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
    public RelatedAddress getAddress() {
        return address.get();
    }

    @Override
    public ReadOnlyObjectProperty<RelatedAddress> addressProperty() {
        return address.getReadOnlyProperty();
    }

    @Override
    public String getAddressText() {
        return addressText.get();
    }

    @Override
    public ReadOnlyProperty<String> addressTextProperty() {
        return addressText;
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    @Override
    public CustomerRowData getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<CustomerRowData> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public NestedStringBindingProperty<RelatedAddress> address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public NestedStringBindingProperty<RelatedAddress> address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public NestedStringBindingProperty<RelatedAddress> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public NestedStringBindingProperty<RelatedAddress> cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public NestedStringBindingProperty<RelatedAddress> countryNameProperty() {
        return countryName;
    }

    @Override
    public StringBinding getMultiLineAddress() {
        return multiLineAddress;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public NestedStringBindingProperty<RelatedAddress> phoneProperty() {
        return phone;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public NestedStringBindingProperty<RelatedAddress> postalCodeProperty() {
        return postalCode;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyProperty();
    }

    @Override
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyProperty<? extends DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

}
