package scheduler.model;

/**
 * Interface for objects that contain either partial or complete information from the {@code customer} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.model.Customer}
 */
public interface Customer extends DbDataModel {

    /**
     * Gets the name of the current customer. This corresponds to the "customerName" database column.
     *
     * @return the name of the current customer.
     */
    String getName();

    /**
     * Gets a value that indicates whether the current customer is active. This corresponds to the "active" database column.
     *
     * @return {@code true} if the current customer is active; otherwise, {@code false}.
     */
    boolean isActive();

    /**
     * Gets the {@link AddressElement} for the current customer. This corresponds to the "address" data row referenced by the "addressId" database
     * column.
     *
     * @return The {@link AddressElement} for the current customer.
     */
    Address getAddress();

}
