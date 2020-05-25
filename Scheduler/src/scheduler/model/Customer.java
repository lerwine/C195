package scheduler.model;

import java.util.Objects;

/**
 * Interface for objects that contain either partial or complete information from the {@code customer} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Customer extends DataObject {

    public static int compare(Customer a, Customer b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        String x = a.getName();
        String y = b.getName();
        int result = x.compareToIgnoreCase(y);
        if (result == 0) {
            return x.compareTo(y);
        }
        return result;
    }

    public static boolean arePropertiesEqual(Customer a, Customer b) {
        if (null == a) {
            return null == b;
        }
        
        return null != b && (a == b || (a.getName().equalsIgnoreCase(b.getName()) && a.isActive() == b.isActive()
                && ModelHelper.areSameRecord(a.getAddress(), b.getAddress())));
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
