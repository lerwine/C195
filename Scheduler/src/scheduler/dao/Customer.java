package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.util.Values;

/**
 * Represents a data row from the "customer" database table.
 * Table definition: <code>CREATE TABLE `customer` (
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
    DataObjectReference<AddressImpl, Address> getAddress();

    /**
     * Gets a value that indicates whether the current customer is active.
     * This corresponds to the "active" database column.
     * 
     * @return {@code true} if the current customer is active; otherwise, {@code false}.
     */
    boolean isActive();
    
    /**
     * Creates a read-only Customer object from object values.
     * @param pk The value of the primary key.
     * @param name The customer name.
     * @param address The customer's address
     * @param active {@code true} if the current customer is active; otherwise, {@code false}.
     * @return The read-only Customer object.
     */
    public static Customer of(int pk, String name, DataObjectReference<AddressImpl, Address> address, boolean active) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new Customer() {
            @Override
            public String getName() { return name; }
            @Override
            public DataObjectReference<AddressImpl, Address> getAddress() { return address; }
            @Override
            public boolean isActive() { return active; }
            @Override
            public int getPrimaryKey() { return pk; }
            @Override
            public int getRowState() { return Values.ROWSTATE_UNMODIFIED; }
        };
    }
    
    /**
     * Creates a read-only Customer object from a result set.
     * @param resultSet The data retrieved from the database.
     * @param pkColName The name of the column containing the value of the primary key.
     * @return The read-only Customer object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static Customer of(ResultSet resultSet, String pkColName) throws SQLException {
        Objects.requireNonNull(pkColName, "Primary key column name cannot be null");
        int id = resultSet.getInt(pkColName);
        if (resultSet.wasNull())
            return null;
        boolean active = resultSet.getBoolean(CustomerImpl.COLNAME_ACTIVE) && !resultSet.wasNull();
        Address a = Address.of(resultSet, CustomerImpl.COLNAME_ADDRESSID);
        String name = resultSet.getString(CustomerImpl.COLNAME_CUSTOMERNAME);
        return Customer.of(id, (resultSet.wasNull()) ? "" : name, DataObjectReference.of(a), active);
    }
}
