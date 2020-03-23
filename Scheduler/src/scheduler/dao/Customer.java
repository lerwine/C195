package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;

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
 * @author erwinel
 */
public interface Customer extends DataObject {

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
    DataObjectReference<AddressImpl, Address> getAddressReference();

    Address getAddress();

    /**
     * Gets a value that indicates whether the current customer is active. This corresponds to the "active" database column.
     *
     * @return {@code true} if the current customer is active; otherwise, {@code false}.
     */
    boolean isActive();

    /**
     * Creates a read-only Customer object from object values.
     *
     * @param pk The value of the primary key.
     * @param name The customer name.
     * @param address The customer's address
     * @param active {@code true} if the current customer is active; otherwise, {@code false}.
     * @return The read-only Customer object.
     */
    public static Customer of(int pk, String name, DataObjectReference<AddressImpl, Address> address, boolean active) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new Customer() {
            private final DataObjectReference<AddressImpl, Address> addressReference = (null == address) ? DataObjectReference.of(null) : address;

            @Override
            public String getName() {
                return name;
            }

            @Override
            public DataObjectReference<AddressImpl, Address> getAddressReference() {
                return addressReference;
            }

            @Override
            public Address getAddress() {
                return addressReference.getPartial();
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
     * @param resultSet The data retrieved from the database.
     * @param columns The {@link TableColumnList} that created the current lookup query.
     * @return The read-only Customer object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static Customer of(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
        Optional<Integer> id = columns.tryGetInt(resultSet, DbName.CUSTOMER_ID);
        if (id.isPresent()) {
            return Customer.of(id.get(), columns.getString(resultSet, DbColumn.CUSTOMER_NAME, ""),
                    DataObjectReference.of(Address.of(resultSet, columns)), columns.getBoolean(resultSet, DbColumn.ACTIVE, false));
        }
        return null;
    }
}
