package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.Customer;
import scheduler.model.db.CustomerRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CustomerItem<T extends CustomerRowData> extends Customer, UIDbModel<T> {

    /**
     * Gets the property that contains the name of the customer.
     * 
     * @return The property that contains the name of the customer.
     */
    ReadOnlyProperty<String> nameProperty();

    @Override
    public AddressItem getAddress();

    /**
     * Gets the property that contains the address model associated with the customer.
     * 
     * @return The property that contains the address model associated with the customer.
     */
    ReadOnlyProperty<AddressItem> addressProperty();

    /**
     * Gets the property that contains the value that indicates whether the customer is active.
     * 
     * @return The property that contains the value that indicates whether the customer is active.
     */
    ReadOnlyProperty<Boolean> activeProperty();

}
