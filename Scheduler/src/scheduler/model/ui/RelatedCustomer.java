package scheduler.model.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICustomerDAO;
import scheduler.model.AddressProperties;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCustomer extends RelatedModel<ICustomerDAO> implements CustomerItem<ICustomerDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedCustomer.class.getName());

    private final ReadOnlyJavaBeanStringProperty name;
    private final ReadOnlyJavaBeanObjectProperty<IAddressDAO> addressDAO;
    private final ReadOnlyObjectBindingProperty<AddressItem<? extends IAddressDAO>> address;
    private final ReadOnlyStringBindingProperty address1;
    private final ReadOnlyStringBindingProperty address2;
    private final ReadOnlyStringBindingProperty cityName;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty postalCode;
    private final ReadOnlyStringBindingProperty phone;
    private final ReadOnlyStringBindingProperty cityZipCountry;
    private final ReadOnlyStringBindingProperty addressText;
    private final ReadOnlyJavaBeanBooleanProperty active;
    private final ReadOnlyStringBindingProperty multiLineAddress;

    public RelatedCustomer(ICustomerDAO rowData) {
        super(rowData);
        try {
            name = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(PROP_NAME).build();
            addressDAO = ReadOnlyJavaBeanObjectPropertyBuilder.<IAddressDAO>create().bean(rowData).name(PROP_ADDRESS).build();
            active = ReadOnlyJavaBeanBooleanPropertyBuilder.create().bean(rowData).name(PROP_ACTIVE).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        address = new ReadOnlyObjectBindingProperty<>(this, PROP_ADDRESS, () -> AddressItem.createModel(addressDAO.get()), addressDAO);
        address1 = new ReadOnlyStringBindingProperty(this, PROP_ADDRESS1, Bindings.selectString(address, AddressProperties.PROP_ADDRESS1));
        address2 = new ReadOnlyStringBindingProperty(this, PROP_ADDRESS2, Bindings.selectString(address, AddressProperties.PROP_ADDRESS2));
        cityName = new ReadOnlyStringBindingProperty(this, PROP_CITYNAME, Bindings.selectString(address, AddressItem.PROP_CITYNAME));
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(address, AddressItem.PROP_COUNTRYNAME));
        postalCode = new ReadOnlyStringBindingProperty(this, PROP_POSTALCODE, Bindings.selectString(address, AddressProperties.PROP_POSTALCODE));
        phone = new ReadOnlyStringBindingProperty(this, PROP_PHONE, Bindings.selectString(address, AddressProperties.PROP_PHONE));
        cityZipCountry = new ReadOnlyStringBindingProperty(this, PROP_CITYZIPCOUNTRY, Bindings.selectString(address, AddressItem.PROP_CITYZIPCOUNTRY));
        addressText = new ReadOnlyStringBindingProperty(this, PROP_ADDRESSTEXT,
                () -> AddressModel.calculateSingleLineAddress(address1.get(), address2.get(), cityZipCountry.get(), phone.get()));
        multiLineAddress = new ReadOnlyStringBindingProperty(this, PROP_MULTILINEADDRESS,
                () -> AddressModel.calculateMultiLineAddress(AddressModel.calculateAddressLines(address1.get(), address2.get()),
                        cityZipCountry.get(), phone.get()));
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public AddressItem<? extends IAddressDAO> getAddress() {
        return address.get();
    }

    @Override
    public ReadOnlyObjectProperty<AddressItem<? extends IAddressDAO>> addressProperty() {
        return address;
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

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ReadOnlyStringProperty cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyStringProperty cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName;
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
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ReadOnlyStringProperty postalCodeProperty() {
        return postalCode;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public ReadOnlyBooleanProperty activeProperty() {
        return active;
    }

    @Override
    public String getMultiLineAddress() {
        return multiLineAddress.get();
    }

    @Override
    public ReadOnlyProperty<String> multiLineAddressProperty() {
        return multiLineAddress;
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        return ToStringPropertyBuilder.create(this)
                .addNumber(primaryKeyProperty())
                .addString(name)
                .addDataObject(address)
                .addBoolean(active);
    }

}
