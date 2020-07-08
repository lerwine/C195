package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import scheduler.model.Address;
import scheduler.model.Customer;
import scheduler.model.CustomerRecord;
import scheduler.model.ModelHelper;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CustomerModel;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Data access object for the {@code customer} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.CUSTOMER)
public final class CustomerDAO extends DataAccessObject implements ICustomerDAO, CustomerRecord<Timestamp> {

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(CustomerDAO.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(CustomerDAO.class.getName());

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
    private void setName(String value) {
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
    private void setAddress(IAddressDAO address) {
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
    private void setActive(boolean active) {
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
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(PROP_PRIMARYKEY, getPrimaryKey());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(PROP_NAME, name)
                .addDataObject(PROP_ADDRESS, address)
                .addBoolean(PROP_ACTIVE, active)
                .addTimestamp(PROP_CREATEDATE, getCreateDate())
                .addString(PROP_CREATEDBY, getCreatedBy())
                .addTimestamp(PROP_LASTMODIFIEDDATE, getLastModifiedDate())
                .addString(PROP_LASTMODIFIEDBY, getLastModifiedBy());
    }

    /**
     * Factory implementation for {@link CustomerDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CustomerDAO> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

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
                            LogHelper.logWarnings(connection, LOG);
                            return result;
                        }
                        LogHelper.logWarnings(connection, LOG);
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
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        public Class<? extends CustomerDAO> getDaoClass() {
            return CustomerDAO.class;
        }

    }

    public static class SaveTask extends SaveDaoTask<CustomerDAO, CustomerModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(SaveTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(SaveTask.class.getName());

        private static final String ERROR_CHECKING_CONFLICTS = "Error checking customer naming conflicts";
        private static final String MATCHING_ITEM_EXISTS = "Another customer has the same name";

        public SaveTask(CustomerModel model, boolean alreadyValidated) {
            super(model, CustomerModel.FACTORY, alreadyValidated);
            CustomerDAO dao = model.dataObject();
            dao.setName(model.getName());
            dao.setActive(model.isActive());
            dao.setAddress(model.getAddress().dataObject());
        }

        @Override
        public String validate(Connection connection) throws Exception {
            String message = CustomerModel.FACTORY.validateProperties(getFxRecordModel());
            if (Values.isNotNullWhiteSpaceOrEmpty(message)) {
                return message;
            }
            if (isCancelled()) {
                return null;
            }
            CustomerDAO dao = getDataAccessObject();
            StringBuilder sb = new StringBuilder("SELECT COUNT(").append(DbColumn.CUSTOMER_ID.getDbName())
                    .append(") FROM ").append(DbTable.CUSTOMER.getDbName()).append(" WHERE LOWER(").append(DbColumn.CUSTOMER_NAME.getDbName()).append(")=?");
            if (getOriginalRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.CUSTOMER_ID.getDbName()).append("<>?");
            }
            int count;
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, dao.getName());
                if (getOriginalRowState() != DataRowState.NEW) {
                    ps.setInt(2, dao.getPrimaryKey());
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        LogHelper.logWarnings(connection, LOG);
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, ERROR_CHECKING_CONFLICTS, ex);
                throw new OperationFailureException(ERROR_CHECKING_CONFLICTS, ex);
            }
            if (count > 0) {
                return MATCHING_ITEM_EXISTS;
            }

            AddressItem<? extends IAddressDAO> am = getFxRecordModel().getAddress();
            if (null == am) {
                return "Address not specified.";
            }
            if (am instanceof AddressModel) {
                AddressDAO.SaveTask saveTask = new AddressDAO.SaveTask((AddressModel) am, false);
                saveTask.run();
                return saveTask.get().orElse(null);
            }
            return null;
        }

        @Override
        protected void updateDataAccessObject(CustomerModel model) {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.dao.CustomerDAO.SaveTask#updateDataAccessObject
        }

    }

    public static final class DeleteTask extends DeleteDaoTask<CustomerDAO, CustomerModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DeleteTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DeleteTask.class.getName());

        private static final String REFERENCED_BY_ONE = "Customer is referenced by one appointment.";
        private static final String REFERENCED_BY_N = "Customer is referenced by %d other appointments.";
        private static final String ERROR_CHECKING_DEPENDENCIES = "Error checking dependencies";

        public DeleteTask(CustomerModel target) {
            super(target, CustomerModel.FACTORY);
        }

        @Override
        protected String validate(Connection connection) throws Exception {
            CustomerDAO dao = getDataAccessObject();
            int count;
            try {
                count = AppointmentDAO.FACTORY.countByCustomer(connection, dao.getPrimaryKey(), null, null);
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, ERROR_CHECKING_DEPENDENCIES, ex);
                throw new OperationFailureException(ERROR_CHECKING_DEPENDENCIES, ex);
            }
            switch (count) {
                case 0:
                    break;
                case 1:
                    return REFERENCED_BY_ONE;
                default:
                    return String.format(REFERENCED_BY_N, count);
            }
            return null;
        }

    }

    public static class Related extends PropertyBindable implements ICustomerDAO {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Related.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(Related.class.getName());

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

        @Override
        public String toString() {
            return toStringBuilder().build();
        }

        @Override
        public ToStringPropertyBuilder toStringBuilder() {
            return ToStringPropertyBuilder.create(this)
                    .addNumber(PROP_PRIMARYKEY, getPrimaryKey())
                    .addString(PROP_NAME, name)
                    .addDataObject(PROP_ADDRESS, address)
                    .addBoolean(PROP_ACTIVE, active);
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
