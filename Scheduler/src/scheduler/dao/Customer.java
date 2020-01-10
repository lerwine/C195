package scheduler.dao;

/**
 * Represents a data row from the "customer" database table.
 * 
 * @author erwinel
 */
public interface Customer extends DataObject {

    /**
     * Gets the name of the current customer.
     * This corresponds to the "customerName" database column.
     * 
     * @return the name of the current customer.
     */
    String getName();

    /**
     * Gets the {@link Address} for the current customer.
     * This corresponds to the "address" data row referenced by the "addressId" database column.
     * 
     * @return The {@link Address} for the current customer.
     */
    Address getAddress();

    /**
     * Gets a value that indicates whether the current customer is active.
     * This corresponds to the "active" database column.
     * 
     * @return {@code true} if the current customer is active; otherwise, {@code false}.
     */
    boolean isActive();
}
