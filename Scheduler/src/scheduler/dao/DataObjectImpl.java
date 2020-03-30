package scheduler.dao;

import scheduler.util.PropertyBindable;
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.stage.Stage;
import scheduler.Scheduler;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.dao.schema.DbTable;
import scheduler.util.DB;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.TaskWaiter;

/**
 * Base class for implementations of the {@link DataObject} interface.
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class DataObjectImpl extends PropertyBindable implements DataObject {

    public static final String PROP_PRIMARYKEY = "primaryKey";
    /**
     * The name of the 'createDate' property.
     */
    public static final String PROP_CREATEDATE = "createDate";
    /**
     * The name of the 'createdBy' property.
     */
    public static final String PROP_CREATEDBY = "createdBy";
    /**
     * The name of the 'lastModifiedDate' property.
     */
    public static final String PROP_LASTMODIFIEDDATE = "lastModifiedDate";
    /**
     * The name of the 'lastModifiedBy' property.
     */
    public static final String PROP_LASTMODIFIEDBY = "lastModifiedBy";
    /**
     * The name of the 'rowState' property.
     */
    public static final String PROP_ROWSTATE = "rowState";

    private int primaryKey;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastModifiedDate;
    private String lastModifiedBy;
    private DataRowState rowState;

    /**
     * Initializes a {@link DataRowState#NEW} data access object.
     */
    protected DataObjectImpl() {
        primaryKey = 0;
        lastModifiedDate = createDate = DB.toUtcTimestamp(LocalDateTime.now());
        lastModifiedBy = createdBy = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
        rowState = DataRowState.NEW;
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey;
    }

//    private void setPrimaryKey(int primaryKey) {
//        int oldPrimaryKey = this.primaryKey;
//        this.primaryKey = primaryKey;
//        getPropertyChangeSupport().firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, primaryKey);
//    }

    /**
     * Gets the timestamp when the data row associated with the current data object was inserted into the database.
     *
     * @return The timestamp when the data row associated with the current data object was inserted into the database.
     */
    public final Timestamp getCreateDate() {
        return createDate;
    }

    private void setCreateDate(Timestamp createDate) {
        Timestamp oldCreateDate = this.createDate;
        this.createDate = Objects.requireNonNull(createDate);
        getPropertyChangeSupport().firePropertyChange(PROP_PRIMARYKEY, oldCreateDate, createDate);
    }

    /**
     * Gets the user name of the person who inserted the data row associated with the current data object into the database.
     *
     * @return The user name of the person who inserted the data row associated with the current data object into the database.
     */
    public final String getCreatedBy() {
        return createdBy;
    }

    private void setCreatedBy(String createdBy) {
        String oldCreatedBy = this.createdBy;
        this.createdBy = Objects.requireNonNull(createdBy);
        getPropertyChangeSupport().firePropertyChange(PROP_PRIMARYKEY, oldCreatedBy, createdBy);
    }

    /**
     * Gets the timestamp when the data row associated with the current data object was last modified.
     *
     * @return The timestamp when the data row associated with the current data object was last modified.
     */
    public final Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }

    private void setLastModifiedDate(Timestamp lastModifiedDate) {
        Timestamp oldModifiedDate = this.lastModifiedDate;
        this.lastModifiedDate = Objects.requireNonNull(lastModifiedDate);
        getPropertyChangeSupport().firePropertyChange(PROP_PRIMARYKEY, oldModifiedDate, lastModifiedDate);
    }

    /**
     * Gets the user name of the person who last modified the data row associated with the current data object in the database.
     *
     * @return The user name of the person who last modified the data row associated with the current data object in the database.
     */
    public final String getLastModifiedBy() {
        return lastModifiedBy;
    }

    private void setLastModifiedBy(String lastModifiedBy) {
        String oldLastModifiedBy = this.lastModifiedBy;
        this.lastModifiedBy = Objects.requireNonNull(lastModifiedBy);
        getPropertyChangeSupport().firePropertyChange(PROP_PRIMARYKEY, oldLastModifiedBy, lastModifiedBy);
    }

    @Override
    public DataRowState getRowState() {
        return rowState;
    }

    final void setDeleted() {
        DataRowState oldRowState = this.rowState;
        rowState = DataRowState.DELETED;
        getPropertyChangeSupport().firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
    }

    public final boolean isModified() {
        return rowState != DataRowState.UNMODIFIED;
    }

    protected boolean propertyChangeModifiesState(String propertyName) {
        return true;
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent event) {
        super.onPropertyChange(event);
        if (null != event.getPropertyName()) {
            switch (event.getPropertyName()) {
                case PROP_CREATEDATE:
                case PROP_CREATEDBY:
                case PROP_LASTMODIFIEDBY:
                case PROP_LASTMODIFIEDDATE:
                case PROP_PRIMARYKEY:
                case PROP_ROWSTATE:
                    return;
                default:
                    if (propertyChangeModifiesState(event.getPropertyName())) {
                        UserImpl currentUser = Scheduler.getCurrentUser();
                        setLastModifiedBy(currentUser.getUserName());
                        setLastModifiedDate(DB.toUtcTimestamp(LocalDateTime.now()));
                        if (rowState != DataRowState.NEW && rowState != DataRowState.DELETED) {
                            DataRowState oldRowState = rowState;
                            rowState = DataRowState.MODIFIED;
                            getPropertyChangeSupport().firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
                            return;
                        }
                        setCreatedBy(lastModifiedBy);
                        setCreateDate(lastModifiedDate);
                    }
                    break;
            }
        }
    }

    /**
     * Base class for FXML models of {@link DataObject} properties.
     * This gives controlled access to the underlying {@link DataObject}.
     * 
     * @param <T> The type of {@link DataObject} being represented.
     */
    public static abstract class DataObjectReferenceModelImpl<T extends DataObject> implements DataObjectReferenceModel<T> {

        private final ReadOnlyObjectProperty<T> dataObject;
        private final ReadOnlyIntegerWrapper primaryKey;

        @Override
        public final T getDataObject() {
            return dataObject.get();
        }

        @Override
        public final ReadOnlyObjectProperty<T> dataObjectProperty() {
            return dataObject;
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey.get();
        }

        @Override
        public ReadOnlyIntegerProperty primaryKeyProperty() {
            return primaryKey.getReadOnlyProperty();
        }

        protected DataObjectReferenceModelImpl(T dao) {
            Objects.requireNonNull(dao);
            dataObject = new ReadOnlyObjectPropertyBase<T>() {
                @Override
                public T get() {
                    return dao;
                }

                @Override
                public Object getBean() {
                    return DataObjectReferenceModelImpl.this;
                }

                @Override
                public String getName() {
                    return "dataObject";
                }
            };
            primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", dao.getPrimaryKey());
        }
    }

    /**
     * Base class for FXML models of {@link DataObjectImpl} properties.
     * This gives controlled access to the DataObjectImpl {@link DataObject}.
     * 
     * @param <T> The type of {@link DataObjectImpl} being represented.
     */
    public static abstract class DataObjectModel<T extends DataObjectImpl> implements DataObjectReferenceModel<T> {

        private final ReadOnlyObjectProperty<T> dataObject;
        private final ReadOnlyIntegerWrapper primaryKey;
        private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
        private final ReadOnlyStringWrapper createdBy;
        private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
        private final ReadOnlyStringWrapper lastModifiedBy;
        private final ReadOnlyBooleanWrapper newItem;

        @Override
        public final T getDataObject() {
            return dataObject.get();
        }

        @Override
        public final ReadOnlyObjectProperty<T> dataObjectProperty() {
            return dataObject;
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey.get();
        }

        @Override
        public ReadOnlyIntegerProperty primaryKeyProperty() {
            return primaryKey.getReadOnlyProperty();
        }

        public LocalDateTime getCreateDate() {
            return createDate.get();
        }

        public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
            return createDate.getReadOnlyProperty();
        }

        public String getCreatedBy() {
            return createdBy.get();
        }

        public ReadOnlyStringProperty createdByProperty() {
            return createdBy.getReadOnlyProperty();
        }

        public LocalDateTime getLastModifiedDate() {
            return lastModifiedDate.get();
        }

        public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
            return lastModifiedDate.getReadOnlyProperty();
        }

        public String getLastModifiedBy() {
            return lastModifiedBy.get();
        }

        public ReadOnlyStringProperty lastModifiedByProperty() {
            return lastModifiedBy.getReadOnlyProperty();
        }

        /**
         * Indicates whether this represents a new item that has not been saved to the database.
         *
         * @return {@code true} if the current item has not been saved to the database; otherwise, {@code false}.
         */
        public boolean isNewItem() {
            return newItem.get();
        }

        /**
         * Gets the property that indicates whether the current item has not yet been saved to the database.
         *
         * @return The property that indicates whether the current item has not yet been saved to the database.
         */
        public ReadOnlyBooleanProperty newItemProperty() {
            return newItem.getReadOnlyProperty();
        }

        protected DataObjectModel(T dao) {
            assert Objects.requireNonNull(dao).getRowState() != DataRowState.DELETED :
                    String.format("%s has been deleted", dao.getClass().getName());
            dataObject = new ReadOnlyObjectPropertyBase<T>() {
                @Override
                public T get() {
                    return dao;
                }

                @Override
                public Object getBean() {
                    return DataObjectModel.this;
                }

                @Override
                public String getName() {
                    return "dataObject";
                }
            };
            primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", dao.getPrimaryKey());
            createDate = new ReadOnlyObjectWrapper<>(this, "createDate", DB.fromUtcTimestamp(dao.getCreateDate()));
            createdBy = new ReadOnlyStringWrapper(this, "createdBy", dao.getCreatedBy());
            lastModifiedDate = new ReadOnlyObjectWrapper<>(this, "lastModifiedDate", DB.fromUtcTimestamp(dao.getLastModifiedDate()));
            lastModifiedBy = new ReadOnlyStringWrapper(this, "lastModifiedBy", dao.getLastModifiedBy());
            newItem = new ReadOnlyBooleanWrapper(this, "newItem", dao.getRowState() == DataRowState.NEW);
        }

        protected abstract void refreshFromDAO(T dao);

        public abstract DataObjectImpl.DaoFactory<T> getDaoFactory();

        public void refreshFromDAO() throws SQLException, ClassNotFoundException {
            T dao = getDataObject();
            createDate.set(DB.fromUtcTimestamp(dao.getCreateDate()));
            createdBy.set(dao.getCreatedBy());
            lastModifiedDate.set(DB.fromUtcTimestamp(dao.getLastModifiedDate()));
            lastModifiedBy.set(dao.getLastModifiedBy());
            newItem.set(dao.getRowState() == DataRowState.NEW);
            refreshFromDAO(dao);
        }
    }

    private static class LoadTask<T extends DataObjectImpl> extends TaskWaiter<List<T>> {
        private final DaoFactory<T> factory;
        private final DaoFilter<T> filter;
        private final Consumer<List<T>> onSuccess;
        private final Consumer<Throwable> onFail;
        LoadTask(Stage stage, DaoFactory<T> factory, DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
            super(stage, filter.getLoadingTitle(), filter.getLoadingMessage());
            this.factory = Objects.requireNonNull(factory);
            this.filter = Objects.requireNonNull(filter);
            this.onSuccess = Objects.requireNonNull(onSuccess);
            this.onFail = onFail;
        }

        @Override
        protected void processResult(List<T> result, Stage stage) {
            onSuccess.accept(result);
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            if (null != onFail)
                onFail.accept(ex);
        }

        @Override
        protected List<T> getResult(Connection connection) throws SQLException {
            StringBuilder sql = factory.getBaseSelectQuery();
            filter.appendWhereClause(sql);
            ArrayList<T> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                filter.applyWhereParameters(ps, 1);
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs) {
                        while (rs.next()) {
                            T item = factory.fromResultSet(rs);
                            result.add(item);
                        }
                    }
                }
            }
            return result;
        }
    }
    
    public static abstract class DaoFactory<T extends DataObjectImpl> {
        private static final Logger LOG = Logger.getLogger(DaoFactory.class.getName());

        public void loadAsync(Stage stage, DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
            if (null == filter)
                filter = getDefaultFilter();
            TaskWaiter.execute(new LoadTask<>(stage, this, filter, onSuccess, onFail));
        }
        
        private T fromResultSet(ResultSet rs) throws SQLException {
            T item = createNew();
            initializeFromResultSet(item, rs);
            return item;
        }
        
        public abstract T createNew();
        
        public abstract DaoFilter<T> getAllItemsFilter();
        
        public abstract DaoFilter<T> getDefaultFilter();
        
        protected abstract void onInitializeFromResultSet(T dao, ResultSet rs) throws SQLException;
        
        public abstract DbTable getDbTable();
        
        public abstract DbColumn getPrimaryKeyColumn();
        
        protected final void initializeFromResultSet(T dao, ResultSet rs) throws SQLException {
            DataObjectImpl obj = (DataObjectImpl)dao;
            int oldPrimaryKey = obj.primaryKey;
            Timestamp oldCreateDate = obj.createDate;
            String oldCreatedBy = obj.createdBy;
            Timestamp oldLastModifiedDate = obj.lastModifiedDate;
            String oldLastModifiedBy = obj.lastModifiedBy;
            DataRowState oldRowState = obj.rowState;
            obj.primaryKey = rs.getInt(getPrimaryKeyColumn().toString());
            obj.createDate = rs.getTimestamp(DbName.CREATE_DATE.toString());
            obj.createdBy = rs.getString(DbName.CREATED_BY.toString());
            obj.lastModifiedDate = rs.getTimestamp(DbName.LAST_UPDATE.toString());
            obj.lastModifiedBy = rs.getString(DbName.LAST_UPDATE_BY.toString());
            obj.rowState = DataRowState.UNMODIFIED;
            try {
                onInitializeFromResultSet(dao, rs);
            } finally {
                obj.firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, obj.primaryKey);
                obj.firePropertyChange(PROP_CREATEDATE, oldCreateDate, obj.createDate);
                obj.firePropertyChange(PROP_CREATEDBY, oldCreatedBy, obj.createdBy);
                obj.firePropertyChange(PROP_LASTMODIFIEDDATE, oldLastModifiedDate, obj.lastModifiedDate);
                obj.firePropertyChange(PROP_LASTMODIFIEDBY, oldLastModifiedBy, obj.lastModifiedBy);
                obj.firePropertyChange(PROP_ROWSTATE, oldRowState, obj.rowState);
            }
        }
        
        /**
         * Gets the {@link Class} for the target {@link DataObjectImpl} type.
         * 
         * @return The {@link Class} for the target {@link DataObjectImpl} type.
         */
        public abstract Class<? extends T> getDaoClass();

        public abstract StringBuilder getBaseSelectQuery();
        
        /**
         * Saves a {@link DataObjectImpl} to the database.
         * 
         * @param dao The {@link DataObjectImpl} to be inserted or updated.
         * @param connection The database connection to use.
         * @throws SQLException If unable to perform the database operation.
         */
        public void save(T dao, Connection connection) throws SQLException {
            // TODO: Implement this.
            throw new UnsupportedOperationException();
        }
        
        /**
         * Retrieves the {@link DataObjectImpl} from the database which matches the given primary key.
         * 
         * @param connection The database connection to use.
         * @param pk The value of the primary key for the {@link DataObjectImpl}.
         * @return An {@link Optional} {@link DataObjectImpl} which will be empty if no match was found.
         * @throws SQLException If unable to perform the database operation.
         */
        public Optional<T> loadByPrimaryKey(Connection connection, int pk) throws SQLException {
//            Objects.requireNonNull(connection, "Connection cannot be null");
//            SelectColumnList dml = getSelectColumns();
//            String sql = dml.getSelectQuery().append(" WHERE p.`").append(getDbTable().getPkColName().getValue()).append("`=?").toString();
//            LOG.log(Level.SEVERE, String.format("Finalizing query \"%s\"", sql));
//            try (PreparedStatement ps = connection.prepareStatement(sql)) {
//                ps.setInt(1, pk);
//                try (ResultSet rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return Optional.of(fromResultSet(rs, dml));
//                    }
//                }
//            }
//            return Optional.empty();
            // TODO: Implement this.
            throw new UnsupportedOperationException();
        }

        /**
         * Deletes the corresponding {@link DataObjectImpl} from the database.
         * 
         * @param dao The {@link DataObjectImpl} to delete.
         * @param connection The database connection to use.
         * @throws SQLException If unable to perform the database operation.
         */
        public void delete(T dao, Connection connection) throws SQLException {
//            Objects.requireNonNull(dao, "Data access object cannot be null");
//            Objects.requireNonNull(connection, "Connection cannot be null");
//            synchronized (dao) {
//                assert dao.getRowState() != DataRowState.DELETED : String.format("%s has already been deleted", getClass().getName());
//                assert dao.getRowState() != DataRowState.NEW : String.format("%s has not been inserted into the database", getClass().getName());
//                String sql = String.format("DELETE FROM `%s` WHERE `%s` = ?", getDbTable().getDbName().getValue(), getDbTable().getPkColName().getValue());
//                LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql));
//                try (PreparedStatement ps = connection.prepareStatement(sql)) {
//                    ps.setInt(1, dao.getPrimaryKey());
//                    assert ps.executeUpdate() > 0 : String.format("Failed to delete associated database row on %s where %s = %d",
//                            getDbTable().getDbName().getValue(), getDbTable().getPkColName().getValue(), dao.getPrimaryKey());
//                }
//                dao.setDeleted();
//            }
            // TODO: Implement this.
            throw new UnsupportedOperationException();
        }

        /**
         * Checks to see if the current {@link DataObjectImpl} can safely be deleted from the database.
         * 
         * @param dao The {@link DataObjectImpl} intended for deletion.
         * @param connection The database connection to use.
         * @return A user-friendly description of the reason that the {@link DataObjectImpl} cannot be deleted or an
         * empty string if it can be safely deleted.
         * @throws SQLException If unable to perform the database operation.
         */
        // TODO: Make sure no implementations return a null value.
        public abstract String getDeleteDependencyMessage(T dao, Connection connection) throws SQLException;

        /**
         * Checks to see if any impending changes cause any database conflicts.
         * 
         * @param dao The target {@link DataObjectImpl}.
         * @param connection The database connection to use.
         * @return A user-friendly description of the reason that the changes to the {@link DataObjectImpl} cannot be saved or an
         * empty string if it can be safely deleted.
         * @throws SQLException If unable to perform the database operation.
         */
        // TODO: Make sure no implementations return a null value.
        public abstract String getSaveConflictMessage(T dao, Connection connection) throws SQLException;

        public abstract boolean isAssignableFrom(DataObjectImpl dao);

    }
    
}
