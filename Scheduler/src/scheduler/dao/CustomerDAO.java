package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.Address;
import scheduler.util.InternalException;
import scheduler.util.PropertyBindable;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Data access object for the {@code customer} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.CUSTOMER)
public final class CustomerDAO extends DataAccessObject implements ICustomerDAO {

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

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

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

    /**
     * Factory implementation for {@link scheduler.model.db.Customer} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CustomerDAO> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

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
            dao.address = AddressDAO.getFactory().fromJoinedResultSet(rs);
            dao.active = rs.getBoolean(DbColumn.ACTIVE.toString());
            if (rs.wasNull()) {
                dao.active = false;
            }
            return propertyChanges;
        }

        ICustomerDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Related(rs.getInt(DbColumn.APPOINTMENT_CUSTOMER.toString()),
                    asNonNullAndTrimmed(rs.getString(DbColumn.CUSTOMER_NAME.toString())),
                    AddressDAO.getFactory().fromJoinedResultSet(rs), rs.getBoolean(DbColumn.ACTIVE.toString()));
        }

        public Optional<CustomerDAO> findByName(Connection connection, String value) throws SQLException {
            if (null != value && !value.isEmpty()) {
                String sql = createDmlSelectQueryBuilder().build().append(" WHERE LOWER(").append(DbColumn.CUSTOMER_NAME).append(")=?").toString();
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, StringValueFilter.encodeLikeString(value));
                    LOG.log(Level.INFO, String.format("findByName", "Executing DML query: %s", sql));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return Optional.of(fromResultSet(rs));
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
                LOG.log(Level.INFO, String.format("countByAddress", "Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
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
            int count = AppointmentDAO.getFactory().countByCustomer(connection, dao.getPrimaryKey(), null, null);
            // PENDING: Internationalize these
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
            Address address = dao.getAddress();
            if (address instanceof AddressDAO && (force || ModelHelper.getRowState(address) != DataRowState.UNMODIFIED)) {
                AddressDAO.getFactory().save((AddressDAO) address, connection, force);
            }
            super.save(dao, connection, force);
        }

        @Override
        public String getSaveDbConflictMessage(CustomerDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Data access object already deleted");
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
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                }
            }
            // PENDING: Internationalize this
            if (count > 0) {
                return "Another customer has the same name";
            }
            // CURRENT: Get address conflict message if it is has been modified
            return "";
        }

    }

    private static class Related extends PropertyBindable implements ICustomerDAO {

        private final String name;
        private final IAddressDAO address;
        private final boolean active;
        private final int primaryKey;

        Related(int primaryKey, String name, IAddressDAO address, boolean active) {
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
}
