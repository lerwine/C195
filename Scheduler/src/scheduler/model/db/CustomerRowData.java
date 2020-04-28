package scheduler.model.db;

import scheduler.model.Customer;
import scheduler.model.RelatedRecord;

/**
 * Represents a data row from the "customer" database table.
 * <dl>
 * <dt>{@link scheduler.dao.CustomerDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.CustomerItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerRowData extends Customer, RelatedRecord {

//    /**
//     * Creates a read-only CustomerElement object from object values.
//     *
//     * @param pk The value of the primary key.
//     * @param nameValue The customer name.
//     * @param address The customer's address
//     * @param active {@code true} if the current customer is active; otherwise, {@code false}.
//     * @return The read-only CustomerElement object.
//     */
//    public static CustomerRowData of(int pk, String nameValue, AddressRowData address, boolean active) {
//        Objects.requireNonNull(address, "Address cannot be null");
//        return new CustomerRowData() {
//            private final String name = asNonNullAndTrimmed(nameValue);
//
//            @Override
//            public String getName() {
//                return name;
//            }
//
//            @Override
//            public AddressRowData getAddress() {
//                return address;
//            }
//
//            @Override
//            public boolean isActive() {
//                return active;
//            }
//
//            @Override
//            public int getPrimaryKey() {
//                return pk;
//            }
//
//            @Override
//            public boolean equals(Object obj) {
//                return null != obj && obj instanceof Customer && DataModel.areSameRecord(this, (Customer)obj);
//            }
//
//            @Override
//            public int hashCode() {
//                return pk;
//            }
//
//        };
//    }

    @Override
    public AddressRowData getAddress();

}
