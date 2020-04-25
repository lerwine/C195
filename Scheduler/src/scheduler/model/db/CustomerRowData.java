package scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.dao.DataRowState;
import scheduler.dao.schema.DbColumn;
import scheduler.model.Customer;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Represents a data row from the "customer" database table.
 * <dl>
 * <dt>{@link scheduler.dao.CustomerDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.CustomerItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerRowData extends Customer, RowData {

    public static boolean areEqual(CustomerRowData a, CustomerRowData b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        if (a == b || a.getPrimaryKey() != b.getPrimaryKey()) {
            return true;
        }
        switch (a.getRowState()) {
            case MODIFIED:
            case UNMODIFIED:
                switch (b.getRowState()) {
                    case MODIFIED:
                    case UNMODIFIED:
                        return true;
                    default:
                        return false;
                }
            case NEW:
                return b.getRowState() == DataRowState.NEW && a.getName().equalsIgnoreCase(b.getName())
                        && AddressRowData.areEqual(a.getAddress(), b.getAddress()) && a.isActive() == b.isActive();
            default:
                return b.getRowState() == DataRowState.DELETED;
        }
    }

    /**
     * Creates a read-only CustomerElement object from object values.
     *
     * @param pk The value of the primary key.
     * @param nameValue The customer name.
     * @param address The customer's address
     * @param active {@code true} if the current customer is active; otherwise, {@code false}.
     * @return The read-only CustomerElement object.
     */
    public static CustomerRowData of(int pk, String nameValue, AddressRowData address, boolean active) {
        Objects.requireNonNull(address, "Address cannot be null");
        return new CustomerRowData() {
            private final String name = asNonNullAndTrimmed(nameValue);

            @Override
            public String getName() {
                return name;
            }

            @Override
            public AddressRowData getAddress() {
                return address;
            }

            @Override
            public boolean isActive() {
                return active;
            }

            @Override
            public int getPrimaryKey() {
                return pk;
            }

            @Override
            public DataRowState getRowState() {
                return DataRowState.UNMODIFIED;
            }

            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof CustomerRowData && CustomerRowData.areEqual(this, (CustomerRowData) obj);
            }

            @Override
            public int hashCode() {
                return pk;
            }

        };
    }

    /**
     * Creates a read-only CustomerElement object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @return The read-only CustomerElement object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static CustomerRowData of(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString(DbColumn.CITY_NAME.toString());
        if (resultSet.wasNull()) {
            name = "";
        }
        return of(resultSet.getInt(DbColumn.CUSTOMER_ID.toString()), name, AddressRowData.of(resultSet), resultSet.getBoolean(DbColumn.ACTIVE.toString()));
    }
//
//    /**
//     * Gets the {@link AddressElement} for the current customer. This corresponds to the "address" data row referenced by the "addressId" database
//     * column.
//     *
//     * @return The {@link AddressElement} for the current customer.
//     */
//    AddressElement getAddress();

    @Override
    public AddressRowData getAddress();

}
