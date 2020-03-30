package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import scheduler.AppResourceBundleConstants;
import scheduler.AppResources;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;

public class CustomerImpl extends DataObjectImpl implements Customer<Address<? extends City>> {

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'address' property.
     */
    public static final String PROP_ADDRESS = "address";

    /**
     * The name of the 'active' property.
     */
    public static final String PROP_ACTIVE = "active";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private Address<? extends City> address;
    private boolean active;

    /**
     * Initializes a {@link DataRowState#NEW} customer object.
     */
    public CustomerImpl() {
        super();
        name = "";
        address = null;
        active = true;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = (name == null) ? "" : name;
        firePropertyChange(PROP_NAME, oldValue, this.name);
    }

    @Override
    public Address<? extends City> getAddress() {
        return address;
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(Address<? extends City> address) {
        Address oldValue = this.address;
        this.address = address;
        firePropertyChange(PROP_ADDRESS, oldValue, this.address);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Set the value of active
     *
     * @param active new value of active
     */
    public void setActive(boolean active) {
        boolean oldValue = this.active;
        this.active = active;
        firePropertyChange(PROP_ADDRESS, oldValue, this.active);
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.address);
        hash = 29 * hash + (this.active ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof Customer) {
            Customer other = (Customer) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && name.equals(other.getName()) && address.equals(other.getAddress())
                        && active == other.isActive();
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    public static final class FactoryImpl extends DataObjectImpl.DaoFactory<CustomerImpl> {

        @Override
        public boolean isAssignableFrom(DataObjectImpl dao) {
            return null != dao && dao instanceof CustomerImpl;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.CUSTOMER;
        }

        @Override
        public DbColumn getPrimaryKeyColumn() {
            return DbColumn.CUSTOMER_ID;
        }

        @Override
        public CustomerImpl createNew() {
            return new CustomerImpl();
        }

        @Override
        public DaoFilter<CustomerImpl> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_LOADINGCUSTOMERS));
        }

        @Override
        public DaoFilter<CustomerImpl> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public StringBuilder getBaseSelectQuery() {
            StringBuilder sb = new StringBuilder();
            CityImpl.getFactory().appendSelectColumns(sb.append("SELECT ")
                    .append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_ID).append(" AS ").append(DbColumn.CUSTOMER_ID)
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_NAME).append(" AS ").append(DbColumn.CUSTOMER_NAME)
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_ADDRESS).append(" AS ").append(DbColumn.CUSTOMER_ADDRESS));
            CityImpl.getFactory().appendJoinStatement(sb
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.ACTIVE).append(" AS ").append(DbColumn.ACTIVE)
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_CREATE_DATE).append(" AS ").append(DbColumn.CUSTOMER_CREATE_DATE)
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_CREATED_BY).append(" AS ").append(DbColumn.CUSTOMER_CREATED_BY)
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_LAST_UPDATE).append(" AS ").append(DbColumn.CUSTOMER_LAST_UPDATE)
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_LAST_UPDATE_BY).append(" AS ").append(DbColumn.CUSTOMER_LAST_UPDATE_BY)
                    .append(" FROM ").append(DbTable.CUSTOMER.getDbName()).append(" ").append(DbTable.CUSTOMER));
            return sb;
        }

        void appendSelectColumns(StringBuilder sb) {
            CityImpl.getFactory().appendSelectColumns(sb
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_NAME).append(" AS ").append(DbColumn.CUSTOMER_NAME)
                    .append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_ADDRESS).append(" AS ").append(DbColumn.CUSTOMER_ADDRESS));
            sb.append(", ").append(DbTable.CUSTOMER).append(".").append(DbColumn.ACTIVE).append(" AS ").append(DbColumn.ACTIVE);
        }

        void appendJoinStatement(StringBuilder sb) {
            CityImpl.getFactory().appendJoinStatement(sb.append(" LEFT JOIN ").append(DbTable.CUSTOMER.getDbName()).append(" ").append(DbTable.CUSTOMER)
                    .append(" ON ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_CUSTOMER).append(" = ")
                    .append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_ID));
        }

        @Override
        protected void onInitializeFromResultSet(CustomerImpl dao, ResultSet rs) throws SQLException {
            String oldName = dao.name;
            dao.name = rs.getString(DbColumn.CUSTOMER_NAME.toString());
            Address<? extends City> oldAddress = dao.address;
            dao.address = AddressImpl.getFactory().fromJoinedResultSet(rs);
            boolean oldActive = dao.active;
            dao.active = rs.getBoolean(DbColumn.ACTIVE.toString());
            dao.firePropertyChange(PROP_NAME, oldName, dao.name);
            dao.firePropertyChange(PROP_ADDRESS, oldAddress, dao.address);
            dao.firePropertyChange(PROP_ACTIVE, oldActive, dao.active);
        }

        Customer<? extends Address> fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Customer<Address<? extends City>>() {
                private final String name = rs.getString(DbColumn.CUSTOMER_NAME.toString());
                private final Address<? extends City> address = AddressImpl.getFactory().fromJoinedResultSet(rs);
                private final boolean active = rs.getBoolean(DbColumn.ACTIVE.toString());
                private final int primaryKey = rs.getInt(DbColumn.APPOINTMENT_CUSTOMER.toString());

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Address<? extends City> getAddress() {
                    return address;
                }

                @Override
                public boolean isActive() {
                    return active;
                }

                @Override
                public int getPrimaryKey() {
                    return primaryKey;
                }

                @Override
                public DataRowState getRowState() {
                    return DataRowState.UNMODIFIED;
                }

                @Override
                public boolean isExisting() {
                    return true;
                }

                @Override
                public int hashCode() {
                    return primaryKey;
                }

                @Override
                public boolean equals(Object obj) {
                    if (null != obj && obj instanceof Customer) {
                        Customer<? extends Address> other = (Customer<? extends Address>) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        public Optional<CustomerImpl> findByName(Connection connection, String value) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int countByAddress(Connection connection, int addressId) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Class<? extends CustomerImpl> getDaoClass() {
            return CustomerImpl.class;
        }

        @Override
        public String getDeleteDependencyMessage(CustomerImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getSaveConflictMessage(CustomerImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ArrayList<CustomerImpl> getAll(Connection connection) {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
