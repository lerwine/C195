package scheduler.model;

/**
 * Interface for objects that contain either partial or complete information from the {@code customer} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Customer extends DataObject {

    public static boolean arePropertiesEqual(Customer a, Customer b) {
        if (null == a) {
            return null == b;
        }
        if (a == b) {
            return true;
        }
        return null != b && a.getName().equalsIgnoreCase(b.getName()) && a.isActive() == b.isActive()
                && ModelHelper.areSameRecord(a.getAddress(), b.getAddress());
    }

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
     * Gets the {@link Address} for the current customer. This corresponds to the "address" data row referenced by the "addressId" database column.
     *
     * @return The {@link Address} for the current customer.
     */
    Address getAddress();
}
