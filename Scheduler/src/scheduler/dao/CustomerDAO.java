package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.dao.event.CustomerDaoEvent;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DbChangeType;
import scheduler.dao.filter.CustomerFilter;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.value.StringValueFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.model.Address;
import scheduler.model.Customer;
import scheduler.model.CustomerRecord;
import scheduler.model.ModelHelper;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Data access object for the {@code customer} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.CUSTOMER)
public final class CustomerDAO extends DataAccessObject implements ICustomerDAO, CustomerRecord<Timestamp> {

    public static final int MAX_LENGTH_NAME = 45;

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

    public static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private final OriginalValues originalValues;
    private String name;
    private IAddressDAO address;
    private boolean active;

    /**
     * Initializes a {@link DataRowState#NEW} customer object.
     */
    public CustomerDAO() {
        super();
        name = "";
        address = null;
        active = true;
        originalValues = new OriginalValues();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param value new value of name
     */
    public void setName(String value) {
        String oldValue = this.name;
        this.name = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_NAME, oldValue, this.name);
    }

    @Override
    public IAddressDAO getAddress() {
        return address;
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(IAddressDAO address) {
        IAddressDAO oldValue = this.address;
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
    protected void onAcceptChanges() {
        originalValues.name = name;
        originalValues.address = address;
        originalValues.active = active;
    }

    @Override
    protected void onRejectChanges() {
        String oldName = name;
        IAddressDAO oldAddress = address;
        boolean oldActive = active;
        name = originalValues.name;
        address = originalValues.address;
        active = originalValues.active;
        firePropertyChange(PROP_NAME, oldName, name);
        firePropertyChange(PROP_ADDRESS, oldAddress, address);
        firePropertyChange(PROP_ACTIVE, oldActive, active);
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
        return null != obj && obj instanceof Customer && ModelHelper.areSameRecord(this, (Customer) obj);
    }

    @Override
    public String toString() {
        IAddressDAO a = address;
        if (getRowState() == DataRowState.NEW) {
            return String.format("CustomerDAO{name=%s, address=%s, active=%s}", name, (null == a) ? "null" : a.toString(),
                    (active) ? "true}" : "false}");
        }
        return String.format("CustomerDAO{primaryKey=%d, name=%s, address=%s, active=%s}", getPrimaryKey(), name, (null == a) ? "null" : a.toString(),
                (active) ? "true}" : "false}");
    }

    /**
     * Factory implementation for {@link CustomerDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CustomerDAO> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected void applyColumnValue(CustomerDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case CUSTOMER_NAME:
                    ps.setString(index, dao.name);
                    break;
                case CUSTOMER_ADDRESS:
                    ps.setInt(index, dao.getAddress().getPrimaryKey());
                    break;
                case ACTIVE:
                    ps.setBoolean(index, dao.active);
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        public CustomerDAO createNew() {
            return new CustomerDAO();
        }

        @Override
        public DaoFilter<CustomerDAO> getAllItemsFilter() {
            return CustomerFilter.of(DaoFilterExpression.empty());
        }

        public DaoFilter<CustomerDAO> getActiveStatusFilter(boolean active) {
            return CustomerFilter.of(CustomerFilter.expressionOf(active));
        }

        public DaoFilter<CustomerDAO> getByAddressFilter(Address address) {
            return CustomerFilter.of(CustomerFilter.expressionOf(address));
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            DmlSelectQueryBuilder builder = new DmlSelectQueryBuilder(DbTable.CUSTOMER, SchemaHelper.getTableColumns(DbTable.CUSTOMER));
            builder.join(DbColumn.CUSTOMER_ADDRESS, TableJoinType.LEFT, DbColumn.ADDRESS_ID,
                    SchemaHelper.getTableColumns(DbTable.ADDRESS, SchemaHelper::isForJoinedData))
                    .join(DbColumn.ADDRESS_CITY, TableJoinType.LEFT, DbColumn.CITY_ID,
                            SchemaHelper.getTableColumns(DbTable.CITY, SchemaHelper::isForJoinedData))
                    .join(DbColumn.CITY_COUNTRY, TableJoinType.LEFT, DbColumn.COUNTRY_ID,
                            SchemaHelper.getTableColumns(DbTable.COUNTRY, SchemaHelper::isForJoinedData));
            return builder;
        }

        @Override
        protected void onCloneProperties(CustomerDAO fromDAO, CustomerDAO toDAO) {
            String oldName = toDAO.name;
            IAddressDAO oldAddress = toDAO.address;
            boolean oldActive = toDAO.active;
            toDAO.name = fromDAO.name;
            toDAO.address = fromDAO.address;
            toDAO.active = fromDAO.active;
            toDAO.originalValues.name = fromDAO.originalValues.name;
            toDAO.originalValues.address = fromDAO.originalValues.address;
            toDAO.originalValues.active = fromDAO.originalValues.active;
            toDAO.firePropertyChange(PROP_NAME, oldName, toDAO.name);
            toDAO.firePropertyChange(PROP_ADDRESS, oldAddress, toDAO.address);
            toDAO.firePropertyChange(PROP_ACTIVE, oldActive, toDAO.active);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CustomerDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                private final Address oldAddress = dao.address;
                private final boolean oldActive = dao.active;

                @Override
                public void accept(PropertyChangeSupport t) {
                    if (!dao.name.equals(oldName)) {
                        t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    }
                    if (!Objects.equals(dao.address, oldAddress)) {
                        t.firePropertyChange(PROP_ADDRESS, oldAddress, dao.address);
                    }
                    if (dao.active != oldActive) {
                        t.firePropertyChange(PROP_NAME, oldActive, dao.active);
                    }
                }
            };
            dao.name = asNonNullAndTrimmed(rs.getString(DbColumn.CUSTOMER_NAME.toString()));
            dao.address = AddressDAO.FACTORY.fromJoinedResultSet(rs);
            dao.active = rs.getBoolean(DbColumn.ACTIVE.toString());
            if (rs.wasNull()) {
                dao.active = false;
            }
            return propertyChanges;
        }

        ICustomerDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Related(rs.getInt(DbColumn.APPOINTMENT_CUSTOMER.toString()),
                    asNonNullAndTrimmed(rs.getString(DbColumn.CUSTOMER_NAME.toString())),
                    AddressDAO.FACTORY.fromJoinedResultSet(rs), rs.getBoolean(DbColumn.ACTIVE.toString()));
        }

        public Optional<CustomerDAO> findByName(Connection connection, String value) throws SQLException {
            if (null != value && !value.isEmpty()) {
                String sql = createDmlSelectQueryBuilder().build().append(" WHERE LOWER(").append(DbColumn.CUSTOMER_NAME).append(")=?").toString();
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, StringValueFilter.encodeLikeString(value));
                    LOG.fine(() -> String.format("findByName", "Executing DML query: %s", sql));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            Optional<CustomerDAO> result = Optional.of(fromResultSet(rs));
                            SQLWarning sqlWarning = connection.getWarnings();
                            if (null != sqlWarning) {
                                do {
                                    LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                                } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                            }
                            return result;
                        }
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                    }
                }
            }
            return Optional.empty();
        }

        public int countByAddress(Connection connection, int addressId) throws SQLException {
            String sql = "SELECT COUNT(" + DbColumn.CUSTOMER_ID.getDbName() + ") FROM " + DbTable.CUSTOMER.getDbName()
                    + " WHERE " + DbColumn.CUSTOMER_ADDRESS.getDbName() + "=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, addressId);
                LOG.fine(() -> String.format("countByAddress", "Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int result = rs.getInt(1);
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        return result;
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        public Class<? extends CustomerDAO> getDaoClass() {
            return CustomerDAO.class;
        }

        @Override
        public String getDeleteDependencyMessage(CustomerDAO dao, Connection connection) throws SQLException {
            if (null == dao || !DataRowState.existsInDb(dao.getRowState())) {
                return "";
            }
            int count = AppointmentDAO.FACTORY.countByCustomer(connection, dao.getPrimaryKey(), null, null);
            switch (count) {
                case 0:
                    return "";
                case 1:
                    return "Customer is referenced by one appointment.";
                default:
                    return String.format("Customer is referenced by %d other appointments", count);
            }
        }

        @Override
        public void save(CustomerDAO dao, Connection connection, boolean force) throws SQLException {
            Address address = ICustomerDAO.assertValidCustomer(dao).getAddress();
            if (address instanceof AddressDAO && (force || address.getRowState() != DataRowState.UNMODIFIED)) {
                AddressDAO.FACTORY.save((AddressDAO) address, connection, force);
            }
            super.save(dao, connection, force);
        }

        @Override
        @SuppressWarnings("incomplete-switch")
        public String getSaveDbConflictMessage(CustomerDAO dao, Connection connection) throws SQLException {
            IAddressDAO address;
            switch (dao.getRowState()) {
                case DELETED:
                    throw new IllegalStateException("Data access object already deleted");
                case UNMODIFIED:
                    address = dao.getAddress();
                    if (address instanceof AddressDAO) {
                        return AddressDAO.FACTORY.getSaveDbConflictMessage((AddressDAO) address, connection);
                    }
                    return "";
            }

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.CUSTOMER_ID.getDbName())
                    .append(") FROM ").append(DbTable.CUSTOMER.getDbName())
                    .append(" WHERE LOWER(").append(DbColumn.CUSTOMER_NAME.getDbName()).append(")=?");
            if (dao.getRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.CUSTOMER_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, dao.getName());
                if (dao.getRowState() != DataRowState.NEW) {
                    ps.setInt(1, dao.getPrimaryKey());
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            }
            if (count > 0) {
                return "Another customer has the same name";
            }
            address = dao.getAddress();
            if (address instanceof AddressDAO) {
                return AddressDAO.FACTORY.getSaveDbConflictMessage((AddressDAO) address, connection);
            }
            return "";
        }

        @Override
        protected DataObjectEvent<? extends CustomerDAO> createDataObjectEvent(Object source, CustomerDAO dataAccessObject,
                DbChangeType changeAction) {
            return new CustomerDaoEvent(source, dataAccessObject, changeAction);
        }

    }

    public static class Related extends PropertyBindable implements ICustomerDAO {

        private final String name;
        private final IAddressDAO address;
        private final boolean active;
        private final int primaryKey;

        private Related(int primaryKey, String name, IAddressDAO address, boolean active) {
            this.primaryKey = primaryKey;
            this.name = name;
            this.address = address;
            this.active = active;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public IAddressDAO getAddress() {
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
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof Customer && ModelHelper.areSameRecord(this, (Customer) obj);
        }

    }

    private class OriginalValues {

        private String name;
        private IAddressDAO address;
        private boolean active;

        private OriginalValues() {
            this.name = CustomerDAO.this.name;
            this.address = CustomerDAO.this.address;
            this.active = CustomerDAO.this.active;
        }
    }
}
