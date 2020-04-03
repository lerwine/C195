package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.dao.schema.DbColumn;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Represents a data row from the "customer" database table.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface CustomerElement extends DataElement {

    public static boolean areEqual(CustomerElement a, CustomerElement b) {
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
                        && AddressElement.areEqual(a.getAddress(), b.getAddress()) && a.isActive() == b.isActive();
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
    public static CustomerElement of(int pk, String nameValue, AddressElement address, boolean active) {
        Objects.requireNonNull(address, "Address cannot be null");
        return new CustomerElement() {
            private final String name = asNonNullAndTrimmed(nameValue);

            @Override
            public String getName() {
                return name;
            }

            @Override
            public AddressElement getAddress() {
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
                return null != obj && obj instanceof CustomerElement && CustomerElement.areEqual(this, (CustomerElement) obj);
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
    public static CustomerElement of(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString(DbColumn.CITY_NAME.toString());
        if (resultSet.wasNull()) {
            name = "";
        }
        return of(resultSet.getInt(DbColumn.CUSTOMER_ID.toString()), name, AddressElement.of(resultSet), resultSet.getBoolean(DbColumn.ACTIVE.toString()));
    }

    /**
     * Gets the name of the current customer. This corresponds to the "customerName" database column.
     *
     * @return the name of the current customer.
     */
    String getName();

    /**
     * Gets the {@link AddressElement} for the current customer. This corresponds to the "address" data row referenced by the "addressId" database
     * column.
     *
     * @return The {@link AddressElement} for the current customer.
     */
    AddressElement getAddress();

    /**
     * Gets a value that indicates whether the current customer is active. This corresponds to the "active" database column.
     *
     * @return {@code true} if the current customer is active; otherwise, {@code false}.
     */
    boolean isActive();

}
