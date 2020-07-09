package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableBooleanValue;
import scheduler.dao.CustomerDAO;
import scheduler.model.Customer;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCustomerDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface PartialCustomerModel<T extends PartialCustomerDAO> extends Customer, PartialEntityModel<T> {

    /**
     * The name of the 'address1' property.
     */
    public static final String PROP_ADDRESS1 = "address1";

    /**
     * The name of the 'address2' property.
     */
    public static final String PROP_ADDRESS2 = "address2";

    /**
     * The name of the 'cityName' property.
     */
    public static final String PROP_CITYNAME = "cityName";

    /**
     * The name of the 'countryName' property.
     */
    public static final String PROP_COUNTRYNAME = "countryName";

    /**
     * The name of the 'cityZipCountry' property.
     */
    public static final String PROP_CITYZIPCOUNTRY = "cityZipCountry";

    /**
     * The name of the 'addressText' property.
     */
    public static final String PROP_ADDRESSTEXT = "addressText";

    /**
     * The name of the 'multiLineAddress' property.
     */
    public static final String PROP_MULTILINEADDRESS = "multiLineAddress";

    /**
     * The name of the 'postalCode' property.
     */
    public static final String PROP_POSTALCODE = "postalCode";

    /**
     * The name of the 'phone' property.
     */
    public static final String PROP_PHONE = "phone";

    public static PartialCustomerModel<? extends PartialCustomerDAO> createModel(PartialCustomerDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof CustomerDAO) {
            return new CustomerModel((CustomerDAO) t);
        }

        return new PartialCustomerModelImpl(t);
    }

    /**
     * Gets the property that contains the name of the customer.
     *
     * @return The property that contains the name of the customer.
     */
    ReadOnlyProperty<String> nameProperty();

    @Override
    public PartialAddressModel<? extends PartialAddressDAO> getAddress();

    /**
     * Gets the property that contains the address model associated with the customer.
     *
     * @return The property that contains the address model associated with the customer.
     */
    ReadOnlyProperty<? extends PartialAddressModel<? extends PartialAddressDAO>> addressProperty();

    String getAddressText();

    ReadOnlyProperty<String> addressTextProperty();

    String getAddress1();

    ReadOnlyProperty<String> address1Property();

    String getAddress2();

    ReadOnlyProperty<String> address2Property();

    String getCityName();

    ReadOnlyProperty<String> cityNameProperty();

    String getCityZipCountry();

    ReadOnlyProperty<String> cityZipCountryProperty();

    String getCountryName();

    ReadOnlyProperty<String> countryNameProperty();

    String getMultiLineAddress();

    ReadOnlyProperty<String> multiLineAddressProperty();

    String getPhone();

    ReadOnlyProperty<String> phoneProperty();

    String getPostalCode();

    ReadOnlyProperty<String> postalCodeProperty();

    /**
     * Gets the property that contains the value that indicates whether the customer is active.
     *
     * @return The property that contains the value that indicates whether the customer is active.
     */
    ObservableBooleanValue activeProperty();

}
