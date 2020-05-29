package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableBooleanValue;
import scheduler.dao.CustomerDAO;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICustomerDAO;
import scheduler.model.Customer;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CustomerItem<T extends ICustomerDAO> extends Customer, FxDbModel<T> {

    public static CustomerItem<? extends ICustomerDAO> createModel(ICustomerDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof CustomerDAO) {
            return new CustomerModel((CustomerDAO) t);
        }

        return new RelatedCustomer(t);
    }

    /**
     * Gets the property that contains the name of the customer.
     *
     * @return The property that contains the name of the customer.
     */
    ReadOnlyProperty<String> nameProperty();

    @Override
    public AddressItem<? extends IAddressDAO> getAddress();

    /**
     * Gets the property that contains the address model associated with the customer.
     *
     * @return The property that contains the address model associated with the customer.
     */
    ReadOnlyProperty<? extends AddressItem<? extends IAddressDAO>> addressProperty();

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

    ReadOnlyProperty<String> countryNameProperty();

    String getCountryName();

    ReadOnlyProperty<String> getMultiLineAddress();

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
