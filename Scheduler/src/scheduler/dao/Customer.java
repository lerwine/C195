package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Represents a data row from the "customer" database table. Table definition: <code>CREATE TABLE `customer` (
 *   `customerId` int(10) NOT NULL AUTO_INCREMENT,
 *   `customerName` varchar(45) NOT NULL,
 *   `addressId` int(10) NOT NULL,
 *   `active` tinyint(1) NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`customerId`),
 *   KEY `addressId` (`addressId`),
 *   CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`addressId`) REFERENCES `address` (`addressId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;</code>
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link Address} data access object.
 */
public interface Customer<T extends Address<? extends City>> extends DataObject {

    /**
     * Gets the name of the current customer. This corresponds to the "customerName" database column.
     *
     * @return the name of the current customer.
     */
    String getName();

    /**
     * Gets the {@link Address} for the current customer. This corresponds to the "address" data row referenced by the "addressId" database column.
     *
     * @return The {@link Address} for the current customer.
     */
    T getAddress();

    /**
     * Gets a value that indicates whether the current customer is active. This corresponds to the "active" database column.
     *
     * @return {@code true} if the current customer is active; otherwise, {@code false}.
     */
    boolean isActive();

    /**
     * Creates a read-only Customer object from object values.
     *
     * @param <T> The type of {@link Address} data access object.
     * @param pk The value of the primary key.
     * @param name The customer name.
     * @param address The customer's address
     * @param active {@code true} if the current customer is active; otherwise, {@code false}.
     * @return The read-only Customer object.
     */
    public static <T extends Address<? extends City>> Customer<T> of(int pk, String name, T address, boolean active) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new Customer<T>() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public T getAddress() {
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

        };
    }

    /**
     * Creates a read-only Customer object from a result set.
     *
     * @param <T>
     * @param resultSet The data retrieved from the database.
     * @return The read-only Customer object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static <T extends Address<? extends City>> Customer<T> of(ResultSet resultSet) throws SQLException {
        // TODO: Implement this
        throw new UnsupportedOperationException();
    }
}
