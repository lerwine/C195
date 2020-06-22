package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventDispatchChain;
import javafx.event.WeakEventHandler;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.IntColumnValueFilter;
import scheduler.dao.filter.value.IntValueFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.events.AddressEvent;
import scheduler.events.CityEvent;
import scheduler.model.Address;
import scheduler.model.City;
import scheduler.model.ModelHelper;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ToStringPropertyBuilder;
import static scheduler.util.Values.asNonNullAndWsNormalized;

/**
 * Data access object for the {@code address} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.ADDRESS)
public final class AddressDAO extends DataAccessObject implements AddressDbRecord {

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AddressDAO.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AddressDAO.class.getName());

    private String address1;
    private String address2;
    private ICityDAO city;
    private String postalCode;
    private String phone;
    private final OriginalPropertyValues originalValues;
    private WeakEventHandler<CityEvent> cityChangeHandler;

    /**
     * Initializes a {@link DataRowState#NEW} address object.
     */
    public AddressDAO() {
        address1 = "";
        address2 = "";
        city = null;
        postalCode = "";
        phone = "";
        originalValues = new OriginalPropertyValues();
    }

    @Override
    public String getAddress1() {
        return address1;
    }

    /**
     * Set the value of address1
     *
     * @param value new value of address1
     */
    public synchronized void setAddress1(String value) {
        String oldValue = address1;
        address1 = asNonNullAndWsNormalized(value);
        firePropertyChange(PROP_ADDRESS1, oldValue, address1);
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    /**
     * Set the value of address2
     *
     * @param value new value of address2
     */
    public synchronized void setAddress2(String value) {
        String oldValue = address2;
        address2 = asNonNullAndWsNormalized(value);
        firePropertyChange(PROP_ADDRESS2, oldValue, address2);
    }

    @Override
    public ICityDAO getCity() {
        return city;
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public synchronized void setCity(ICityDAO city) {
        ICityDAO oldValue = this.city;
        this.city = city;
        firePropertyChange(PROP_CITY, oldValue, this.city);
        if (null == city || city instanceof CityDAO) {
            if (null != cityChangeHandler) {
                CityDAO.FACTORY.removeEventHandler(CityEvent.OP_EVENT, cityChangeHandler);
                cityChangeHandler = null;
            }
        } else if (null == cityChangeHandler) {
            cityChangeHandler = new WeakEventHandler<>(this::onCityEvent);
            CityDAO.FACTORY.addEventHandler(CityEvent.OP_EVENT, cityChangeHandler);
        }
    }

    private void onCityEvent(CityEvent event) {
        ICityDAO newValue = event.getDataAccessObject();
        if (newValue.getPrimaryKey() == city.getPrimaryKey()) {
            CityDAO.FACTORY.removeEventHandler(CityEvent.OP_EVENT, cityChangeHandler);
            cityChangeHandler = null;
            ICityDAO oldValue = city;
            city = newValue;
            firePropertyChange(PROP_CITY, oldValue, city);
        }
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Set the value of postalCode
     *
     * @param value new value of postalCode
     */
    public synchronized void setPostalCode(String value) {
        String oldValue = postalCode;
        postalCode = asNonNullAndWsNormalized(value);
        firePropertyChange(PROP_POSTALCODE, oldValue, postalCode);
    }

    @Override
    public String getPhone() {
        return phone;
    }

    /**
     * Set the value of phone
     *
     * @param value new value of phone
     */
    public synchronized void setPhone(String value) {
        String oldValue = phone;
        phone = asNonNullAndWsNormalized(value);
        firePropertyChange(PROP_PHONE, oldValue, phone);
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.address1 = address1;
        originalValues.address2 = address2;
        originalValues.city = city;
        originalValues.postalCode = postalCode;
        originalValues.phone = phone;
    }

    @Override
    protected void onRejectChanges() {
        String oldAddress1 = address1;
        String oldAddress2 = address2;
        ICityDAO oldCity = city;
        String oldPostalCode = postalCode;
        String oldPhone = phone;
        address1 = originalValues.address1;
        address2 = originalValues.address2;
        city = originalValues.city;
        postalCode = originalValues.postalCode;
        phone = originalValues.phone;
        firePropertyChange(PROP_ADDRESS1, oldAddress1, address1);
        firePropertyChange(PROP_ADDRESS2, oldAddress2, address2);
        firePropertyChange(PROP_CITY, oldCity, city);
        firePropertyChange(PROP_POSTALCODE, oldPostalCode, postalCode);
        firePropertyChange(PROP_PHONE, oldPhone, phone);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.fine(() -> String.format("Adding %s to dispatch chain", FACTORY.getClass().getName()));
        return FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(address1);
        hash = 79 * hash + Objects.hashCode(address2);
        hash = 79 * hash + Objects.hashCode(city);
        hash = 79 * hash + Objects.hashCode(postalCode);
        hash = 79 * hash + Objects.hashCode(phone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(PROP_PRIMARYKEY, getPrimaryKey());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(PROP_ADDRESS1, address1)
                .addString(PROP_ADDRESS2, address2)
                .addDataObject(PROP_CITY, city)
                .addString(PROP_POSTALCODE, postalCode)
                .addString(PROP_PHONE, phone)
                .addTimestamp(PROP_CREATEDATE, getCreateDate())
                .addString(PROP_CREATEDBY, getCreatedBy())
                .addTimestamp(PROP_LASTMODIFIEDDATE, getLastModifiedDate())
                .addString(PROP_LASTMODIFIEDBY, getLastModifiedBy());
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    /**
     * Factory implementation for {@link AddressDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<AddressDAO, AddressEvent> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(AddressDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case ADDRESS1:
                    ps.setString(index, dao.address1);
                    break;
                case ADDRESS2:
                    ps.setString(index, dao.address2);
                    break;
                case ADDRESS_CITY:
                    ps.setInt(index, dao.city.getPrimaryKey());
                    break;
                case POSTAL_CODE:
                    ps.setString(index, dao.postalCode);
                    break;
                case PHONE:
                    ps.setString(index, dao.phone);
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public AddressDAO createNew() {
            return new AddressDAO();
        }

        @Override
        public DaoFilter<AddressDAO> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGADDRESSES));
        }

        public DaoFilter<AddressDAO> getByCityFilter(int pk) {
            return DaoFilter.of(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES),
                    IntColumnValueFilter.of(DbColumn.ADDRESS_CITY, IntValueFilter.of(pk, ComparisonOperator.EQUALS),
                            (AddressDAO t) -> ModelHelper.getPrimaryKey(t.getCity())));
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            DmlSelectQueryBuilder builder = new DmlSelectQueryBuilder(DbTable.ADDRESS, SchemaHelper.getTableColumns(DbTable.ADDRESS));
            builder.join(DbColumn.ADDRESS_CITY, TableJoinType.LEFT, DbColumn.CITY_ID,
                    SchemaHelper.getTableColumns(DbTable.CITY, SchemaHelper::isForJoinedData))
                    .join(DbColumn.CITY_COUNTRY, TableJoinType.LEFT, DbColumn.COUNTRY_ID,
                            SchemaHelper.getTableColumns(DbTable.COUNTRY, SchemaHelper::isForJoinedData));
            return builder;
        }

        @Override
        protected void onCloneProperties(AddressDAO fromDAO, AddressDAO toDAO) {
            String oldAddress1 = toDAO.address1;
            String oldAddress2 = toDAO.address2;
            City oldCity = toDAO.city;
            String oldPostalCode = toDAO.postalCode;
            String oldPhone = toDAO.phone;
            toDAO.address1 = fromDAO.address1;
            toDAO.address2 = fromDAO.address2;
            toDAO.city = fromDAO.city;
            toDAO.postalCode = fromDAO.postalCode;
            toDAO.phone = fromDAO.phone;
            toDAO.originalValues.address1 = fromDAO.originalValues.address1;
            toDAO.originalValues.address2 = fromDAO.originalValues.address2;
            toDAO.originalValues.city = fromDAO.originalValues.city;
            toDAO.originalValues.postalCode = fromDAO.originalValues.postalCode;
            toDAO.originalValues.phone = fromDAO.originalValues.phone;
            toDAO.firePropertyChange(PROP_ADDRESS1, oldAddress1, toDAO.address1);
            toDAO.firePropertyChange(PROP_ADDRESS2, oldAddress2, toDAO.address2);
            toDAO.firePropertyChange(PROP_CITY, oldCity, toDAO.city);
            toDAO.firePropertyChange(PROP_POSTALCODE, oldPostalCode, toDAO.postalCode);
            toDAO.firePropertyChange(PROP_POSTALCODE, oldPhone, toDAO.phone);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(AddressDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldAddress1 = dao.address1;
                private final String oldAddress2 = dao.address2;
                private final City oldCity = dao.city;
                private final String oldPostalCode = dao.postalCode;
                private final String oldPhone = dao.phone;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_ADDRESS1, oldAddress1, dao.address1);
                    t.firePropertyChange(PROP_ADDRESS2, oldAddress2, dao.address2);
                    t.firePropertyChange(PROP_CITY, oldCity, dao.city);
                    t.firePropertyChange(PROP_POSTALCODE, oldPostalCode, dao.postalCode);
                    t.firePropertyChange(PROP_POSTALCODE, oldPhone, dao.phone);
                }
            };

            dao.address1 = asNonNullAndWsNormalized(rs.getString(DbColumn.ADDRESS1.toString()));
            dao.address2 = asNonNullAndWsNormalized(rs.getString(DbColumn.ADDRESS2.toString()));
            dao.city = CityDAO.FACTORY.fromJoinedResultSet(rs);
            dao.postalCode = asNonNullAndWsNormalized(rs.getString(DbColumn.POSTAL_CODE.toString()));
            dao.phone = asNonNullAndWsNormalized(rs.getString(DbColumn.PHONE.toString()));
            return propertyChanges;
        }

        IAddressDAO fromJoinedResultSet(ResultSet resultSet) throws SQLException {
            return new Related(resultSet.getInt(DbColumn.CUSTOMER_ADDRESS.toString()),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.ADDRESS1.toString())),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.ADDRESS2.toString())),
                    CityDAO.FACTORY.fromJoinedResultSet(resultSet),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.POSTAL_CODE.toString())),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.PHONE.toString())));
        }

        @Override
        public Class<? extends AddressDAO> getDaoClass() {
            return AddressDAO.class;
        }

        int countByCity(int primaryKey, Connection connection) throws SQLException {
            String sql = "SELECT COUNT(" + DbColumn.ADDRESS_ID.getDbName() + ") FROM " + DbTable.ADDRESS.getDbName()
                    + " WHERE " + DbColumn.ADDRESS_CITY.getDbName() + "=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, primaryKey);
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
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

//        @Override
//        protected AddressEvent createDbOperationEvent(AddressEvent sourceEvent, DbOperationType operation) {
//            AddressModel model = sourceEvent.getModel();
//            if (null != model) {
//                return new AddressEvent(model, sourceEvent.getSource(), this, operation);
//            }
//            return new AddressEvent(sourceEvent.getSource(), this, sourceEvent.getDataAccessObject(), operation);
//        }
        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.fine(() -> String.format("Adding %s to dispatch chain", AddressModel.FACTORY.getClass().getName()));
            return AddressModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
        }

    }

    public static class SaveTask extends SaveDaoTask<AddressDAO, AddressModel, AddressEvent> {

        public SaveTask(AddressModel fxRecordModel, FxRecordModel.ModelFactory<AddressDAO, AddressModel, AddressEvent> modelFactory, boolean alreadyValidated) {
            super(fxRecordModel, modelFactory, alreadyValidated);
        }

        public SaveTask(AddressDAO dataAccessObject, DaoFactory<AddressDAO, AddressEvent> daoFactory, boolean alreadyValidated) {
            super(dataAccessObject, daoFactory, alreadyValidated);
        }

        @Override
        protected AddressEvent createSuccessEvent() {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.SaveTask#createSuccessEvent
        }

        @Override
        protected void validate(Connection connection) throws Exception {
//            AddressEvent event = task.getValidationEvent();
//            AddressDAO dao;
//            try {
//                dao = IAddressDAO.assertValidAddress(event.getDataAccessObject());
//            } catch (IllegalArgumentException | IllegalStateException ex) {
//                event.setFaulted("Invalid Address", ex.getMessage(), ex);
//                return;
//            }
//            Connection connection = task.getConnection();
//            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.ADDRESS_ID.getDbName())
//                    .append(") FROM ").append(DbTable.ADDRESS.getDbName())
//                    .append(" WHERE ").append(DbColumn.ADDRESS_CITY.getDbName()).append("=?");
//            if (dao.address1.isEmpty()) {
//                sb.append(" AND LENGTH(").append(DbColumn.ADDRESS1.getDbName()).append(")=0");
//            } else {
//                sb.append(" AND LOWER(").append(DbColumn.ADDRESS1.getDbName()).append(")=?");
//            }
//            if (dao.address2.isEmpty()) {
//                sb.append(" AND LENGTH(").append(DbColumn.ADDRESS2.getDbName()).append(")=0");
//            } else {
//                sb.append(" AND LOWER(").append(DbColumn.ADDRESS2.getDbName()).append(")=?");
//            }
//            if (dao.postalCode.isEmpty()) {
//                sb.append(" AND LENGTH(").append(DbColumn.POSTAL_CODE.getDbName()).append(")=0");
//            } else {
//                sb.append(" AND LOWER(").append(DbColumn.POSTAL_CODE.getDbName()).append(")=?");
//            }
//            if (dao.phone.isEmpty()) {
//                sb.append(" AND LENGTH(").append(DbColumn.PHONE.getDbName()).append(")=0");
//            } else {
//                sb.append(" AND LOWER(").append(DbColumn.PHONE.getDbName()).append(")=?");
//            }
//            if (event.getOperation() != DbOperationType.INSERT_VALIDATION) {
//                sb.append(" AND ").append(DbColumn.ADDRESS_ID.getDbName()).append("<>?");
//            }
//            String sql = sb.toString();
//            int count;
//            try (PreparedStatement ps = connection.prepareStatement(sql)) {
//                ps.setInt(1, ModelHelper.getPrimaryKey(dao.getCity()));
//                int index = 2;
//                if (!dao.address1.isEmpty()) {
//                    ps.setString(index++, dao.address1.toLowerCase());
//                }
//                if (!dao.address2.isEmpty()) {
//                    ps.setString(index++, dao.address2.toLowerCase());
//                }
//                if (!dao.postalCode.isEmpty()) {
//                    ps.setString(index++, dao.postalCode.toLowerCase());
//                }
//                if (!dao.phone.isEmpty()) {
//                    ps.setString(index, dao.phone.toLowerCase());
//                }
//                if (event.getOperation() != DbOperationType.INSERT_VALIDATION) {
//                    ps.setInt(index, dao.getPrimaryKey());
//                }
//                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
//                try (ResultSet rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        count = rs.getInt(1);
//                    } else {
//                        throw new SQLException("Unexpected lack of results from database query");
//                    }
//                    LogHelper.logWarnings(connection, LOG);
//                }
//            } catch (SQLException ex) {
//                event.setFaulted("Unexpected error", "Error checking address conflicts", ex);
//                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
//                return;
//            }
//            if (count > 0) {
//                event.setInvalid("Address already exusts", "Another matching address exists");
//                return;
//            }
//
//            ICityDAO city = dao.getCity();
//            if (city instanceof CityDAO) {
//                CityEvent cityEvent;
//                switch (city.getRowState()) {
//                    case NEW:
//                        cityEvent = event.createCityEvent(DbOperationType.DB_INSERT);
//                        break;
//                    case UNMODIFIED:
//                        event.setSucceeded();
//                        return;
//                    default:
//                        cityEvent = event.createCityEvent(DbOperationType.DB_UPDATE);
//                        break;
//                }
//                new SaveTaskOld<>(cityEvent).run();
//                switch (cityEvent.getStatus()) {
//                    case SUCCEEDED:
//                        break;
//                    case FAULTED:
//                        event.setFaulted(cityEvent.getSummaryTitle(), cityEvent.getDetailMessage(), cityEvent.getFault());
//                        return;
//                    case INVALID:
//                        event.setInvalid(cityEvent.getSummaryTitle(), cityEvent.getDetailMessage());
//                        return;
//                    default:
//                        event.setCanceled();
//                        return;
//                }
//            }
//            event.setSucceeded();
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.SaveTask#validate
        }

        @Override
        protected AddressEvent createUnhandledExceptionEvent(Throwable fault) {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.SaveTask#createUnhandledExceptionEvent
        }

        @Override
        protected AddressEvent createCancelledEvent() {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.SaveTask#createCancelledEvent
        }

        @Override
        protected AddressEvent createValidationFailureEvent(ValidationFailureException ex) {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.SaveTask#createValidationFailureEvent
        }

    }

    public static class DeleteTask extends DeleteDaoTask<AddressDAO, AddressModel, AddressEvent> {

        public DeleteTask(AddressModel fxRecordModel, FxRecordModel.ModelFactory<AddressDAO, AddressModel, AddressEvent> modelFactory, boolean alreadyValidated) {
            super(fxRecordModel, modelFactory, alreadyValidated);
        }

        public DeleteTask(AddressDAO dataAccessObject, DaoFactory<AddressDAO, AddressEvent> daoFactory, boolean alreadyValidated) {
            super(dataAccessObject, daoFactory, alreadyValidated);
        }

        @Override
        protected AddressEvent createSuccessEvent() {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.DeleteTask#createSuccessEvent
        }

        @Override
        protected void validate(Connection connection) throws Exception {
//            AddressEvent event = task.getValidationEvent();
//            Connection connection = task.getConnection();
//            AddressDAO dao = task.getDataAccessObject();
//            int count;
//            try {
//                count = CustomerDAO.FACTORY.countByAddress(connection, dao.getPrimaryKey());
//            } catch (SQLException ex) {
//                event.setFaulted("Unexpected error", "Error checking dependencies", ex);
//                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
//                return;
//            }
//            switch (count) {
//                case 0:
//                    event.setSucceeded();
//                    return;
//                case 1:
//                    event.setInvalid("Address in use", "Address is referenced by one customer.");
//                    break;
//                default:
//                    event.setInvalid("Address in use", String.format("Address is referenced by %d other customers", count));
//                    break;
//            }
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.DeleteTask#validate
        }

        @Override
        protected AddressEvent createUnhandledExceptionEvent(Throwable fault) {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.DeleteTask#createUnhandledExceptionEvent
        }

        @Override
        protected AddressEvent createCancelledEvent() {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.DeleteTask#createCancelledEvent
        }

        @Override
        protected AddressEvent createValidationFailureEvent(ValidationFailureException ex) {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.DeleteTask#createValidationFailureEvent
        }

    }

    public static class Related extends PropertyBindable implements IAddressDAO {

        private final int primaryKey;
        private final String address1;
        private final String address2;
        private ICityDAO city;
        private final String postalCode;
        private final String phone;
        private WeakEventHandler<CityEvent> cityChangeHandler;

        private Related(int primaryKey, String address1, String address2, ICityDAO city, String postalCode, String phone) {
            this.primaryKey = primaryKey;
            this.address1 = asNonNullAndWsNormalized(address1);
            this.address2 = asNonNullAndWsNormalized(address2);
            this.city = city;
            this.postalCode = asNonNullAndWsNormalized(postalCode);
            this.phone = asNonNullAndWsNormalized(phone);
            if (!(null == city || city instanceof CityDAO)) {
                cityChangeHandler = new WeakEventHandler<>(this::onCityEvent);
                CityDAO.FACTORY.addEventHandler(CityEvent.OP_EVENT, cityChangeHandler);
            }
        }

        private void onCityEvent(CityEvent event) {
            ICityDAO newValue = event.getDataAccessObject();
            if (newValue.getPrimaryKey() == city.getPrimaryKey()) {
                CityDAO.FACTORY.removeEventHandler(CityEvent.OP_EVENT, cityChangeHandler);
                cityChangeHandler = null;
                ICityDAO oldValue = city;
                city = newValue;
                firePropertyChange(PROP_CITY, oldValue, city);
            }
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public ICityDAO getCity() {
            return city;
        }

        @Override
        public String getAddress1() {
            return address1;
        }

        @Override
        public String getAddress2() {
            return address2;
        }

        @Override
        public String getPostalCode() {
            return postalCode;
        }

        @Override
        public String getPhone() {
            return phone;
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
        }

        @Override
        public String toString() {
            return toStringBuilder().build();
        }

        @Override
        public ToStringPropertyBuilder toStringBuilder() {
            return ToStringPropertyBuilder.create(this)
                    .addNumber(PROP_PRIMARYKEY, getPrimaryKey())
                    .addString(PROP_ADDRESS1, address1)
                    .addString(PROP_ADDRESS2, address2)
                    .addDataObject(PROP_CITY, city)
                    .addString(PROP_POSTALCODE, postalCode)
                    .addString(PROP_PHONE, phone);
        }

    }

    private class OriginalPropertyValues {

        private String address1;
        private String address2;
        private ICityDAO city;
        private String postalCode;
        private String phone;

        private OriginalPropertyValues() {
            this.address1 = AddressDAO.this.address1;
            this.address2 = AddressDAO.this.address2;
            this.city = AddressDAO.this.city;
            this.postalCode = AddressDAO.this.postalCode;
            this.phone = AddressDAO.this.phone;
        }
    }

}
