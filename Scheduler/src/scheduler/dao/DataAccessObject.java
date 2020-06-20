package scheduler.dao;

import com.sun.javafx.event.EventHandlerManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.ColumnCategory;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.events.DbOperationEvent;
import scheduler.events.DbOperationType;
import scheduler.events.EventEvaluationStatus;
import scheduler.model.DataObject;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.AnnotationHelper;
import scheduler.util.DB;
import scheduler.util.DbConnector;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.view.MainController;
import scheduler.view.task.WaitBorderPane;

/**
 * Data access object that represents all columns from a data row.
 * <p>
 * Classes that inherit from this must use the {@link scheduler.dao.schema.DatabaseTable} annotation to indicate which data table they represent. Each
 * class must also have an associated factory singleton instance that inherits from {@link DaoFactory} that can be retrieved using a static
 * {@code getFactory()} method.</p>
 * <p>
 * The current {@link MainController} (if initialized) will be included in the event dispatch chain for events fired on this object.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class DataAccessObject extends PropertyBindable implements DbRecord, EventTarget {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DataAccessObject.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(DataAccessObject.class.getName());

    private final EventHandlerManager eventHandlerManager;
    private final OriginalValues originalValues;
    private int primaryKey;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastModifiedDate;
    private String lastModifiedBy;
    private DataRowState rowState;

    /**
     * Initializes a {@link DataRowState#NEW} data access object.
     */
    protected DataAccessObject() {
        eventHandlerManager = new EventHandlerManager(this);
        primaryKey = Integer.MIN_VALUE;
        lastModifiedDate = createDate = DB.toUtcTimestamp(LocalDateTime.now());
        lastModifiedBy = createdBy = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
        rowState = DataRowState.NEW;
        originalValues = new OriginalValues();
    }

    protected abstract void onAcceptChanges();

    private void acceptChanges() {
        onAcceptChanges();
        originalValues.createDate = createDate;
        originalValues.createdBy = createdBy;
        originalValues.lastModifiedDate = lastModifiedDate;
        originalValues.lastModifiedBy = lastModifiedBy;
    }

    protected abstract void onRejectChanges();

    @SuppressWarnings("try")
    public void rejectChanges() {
        try (ChangeEventDeferral eventDeferral = deferChangeEvents()) {
            Timestamp oldCreateDate = createDate;
            String oldCreatedBy = createdBy;
            Timestamp oldLastModifiedDate = lastModifiedDate;
            String oldLastModifiedBy = lastModifiedBy;
            DataRowState oldRowState = rowState;
            onRejectChanges();
            createDate = originalValues.createDate;
            createdBy = originalValues.createdBy;
            lastModifiedDate = originalValues.lastModifiedDate;
            lastModifiedBy = originalValues.lastModifiedBy;
            if (rowState == DataRowState.MODIFIED) {
                rowState = DataRowState.UNMODIFIED;
            }
            firePropertyChange(PROP_CREATEDATE, oldCreateDate, createDate);
            firePropertyChange(PROP_CREATEDBY, oldCreatedBy, createdBy);
            firePropertyChange(PROP_LASTMODIFIEDDATE, oldLastModifiedDate, lastModifiedDate);
            firePropertyChange(PROP_LASTMODIFIEDBY, oldLastModifiedBy, lastModifiedBy);
            firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Unxpected exception in change deferral", ex);
        }
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public final Timestamp getCreateDate() {
        return createDate;
    }

    @Override
    public final String getCreatedBy() {
        return createdBy;
    }

    @Override
    public final Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }

    @Override
    public final String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public DataRowState getRowState() {
        return rowState;
    }

    /**
     * Indicates whether any of the mutable properties need to be saved to the database.
     *
     * @return {@code false} if {@link #rowState} is {@link DataRowState#UNMODIFIED} or {@link DataRowState#UNMODIFIED}; otherwise, {@code true}.
     */
    public final boolean isModified() {
        switch (rowState) {
            case DELETED:
            case UNMODIFIED:
                return false;
            default:
                return true;
        }
    }

    /**
     * Indicates whether the specified property should change current {@link #rowState}. This is invoked when a {@link PropertyChangeEvent} is raised.
     *
     * @param propertyName The name of the target property.
     * @return {@code true} if the property change should change a {@link #rowState} of {@link DataRowState#UNMODIFIED} to
     * {@link DataRowState#MODIFIED}; otherwise, {@code false} to leave {@link #rowState} unchanged.
     */
    protected boolean propertyChangeModifiesState(String propertyName) {
        switch (propertyName) {
            case PROP_CREATEDATE:
            case PROP_CREATEDBY:
            case PROP_LASTMODIFIEDBY:
            case PROP_LASTMODIFIEDDATE:
            case PROP_PRIMARYKEY:
            case PROP_ROWSTATE:
                return false;
        }
        return true;
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent event) throws Exception {
        super.onPropertyChange(event);
        String propertyName = event.getPropertyName();
        if ((null == propertyName || propertyChangeModifiesState(propertyName)) && !arePropertyChangeEventsDeferred()) {
            UserDAO currentUser = Scheduler.getCurrentUser();
            String oldModifiedby = lastModifiedBy;
            Timestamp oldModifiedDate = lastModifiedDate;
            DataRowState oldRowState = rowState;
            lastModifiedBy = (null == currentUser) ? "admin" : currentUser.getUserName();
            lastModifiedDate = DB.toUtcTimestamp(LocalDateTime.now());
            switch (rowState) {
                case DELETED:
                case MODIFIED:
                    break;
                case UNMODIFIED:
                    rowState = DataRowState.MODIFIED;
                    break;
                default:
                    String oldCreatedby = createdBy;
                    Timestamp oldCreateDate = createDate;
                    createdBy = lastModifiedBy;
                    createDate = lastModifiedDate;
                    rowState = DataRowState.NEW;
                    firePropertyChange(PROP_CREATEDBY, oldCreatedby, createdBy);
                    firePropertyChange(PROP_CREATEDATE, oldCreateDate, createDate);
                    break;
            }
            firePropertyChange(PROP_LASTMODIFIEDBY, oldModifiedby, lastModifiedBy);
            firePropertyChange(PROP_LASTMODIFIEDDATE, oldModifiedDate, lastModifiedDate);
            firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
        }
    }

    /**
     * Registers a {@link DbOperationEvent} handler in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link DbOperationEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public <E extends DbOperationEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>>
            void addEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    /**
     * Registers a {@link DbOperationEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link DbOperationEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public <E extends DbOperationEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>>
            void addEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    /**
     * Unregisters a {@link DbOperationEvent} handler in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link DbOperationEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public <E extends DbOperationEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>>
            void removeEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    /**
     * Unregisters a {@link DbOperationEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link DbOperationEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public <E extends DbOperationEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>>
            void removeEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.append(eventHandlerManager);
    }

    /**
     * Resets {@link #rowState} to {@link DataRowState#NEW} if the current state is {@link DataRowState#DELETED}; otherwise, this has no effect.
     */
    public synchronized void resetRowState() {
        if (rowState == DataRowState.DELETED) {
            primaryKey = Integer.MIN_VALUE;
            lastModifiedDate = createDate = DB.toUtcTimestamp(LocalDateTime.now());
            lastModifiedBy = createdBy = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
            rowState = DataRowState.NEW;
        }
    }

    private static class LoadTask<T extends DataAccessObject> extends Task<List<T>> {

        private final DaoFactory<T, ? extends DbOperationEvent<? extends FxRecordModel<T>, T>> factory;
        private final DaoFilter<T> filter;
        private final Consumer<List<T>> onSuccess;
        private final Consumer<Throwable> onFail;

        LoadTask(DaoFactory<T, ? extends DbOperationEvent<? extends FxRecordModel<T>, T>> factory, DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
            updateTitle(filter.getLoadingTitle());
            this.factory = Objects.requireNonNull(factory);
            this.filter = Objects.requireNonNull(filter);
            this.onSuccess = Objects.requireNonNull(onSuccess);
            this.onFail = onFail;
        }

        @Override
        protected void succeeded() {
            onSuccess.accept(getValue());
            super.succeeded();
        }

        @Override
        protected void failed() {
            if (null != onFail) {
                onFail.accept(getException());
            }
            super.failed();
        }

        @Override
        protected List<T> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(filter.getLoadingMessage());
                return factory.load(dbConnector.getConnection(), filter);
            }
        }
    }

    public static class DataObjectCache<T extends DataObject> {

        private final HashMap<Integer, WeakReference<T>> backingMap = new HashMap<>();

        public synchronized T get(int key) {
            if (backingMap.containsKey(key)) {
                T result = backingMap.get(key).get();
                if (null != result) {
                    return result;
                }
                backingMap.remove(key);
            }
            return null;
        }

        public synchronized T get(int key, Supplier<T> orElse) {
            T result;
            if (backingMap.containsKey(key) && null != (result = backingMap.get(key).get())) {
                return result;
            }
            if (null != (result = orElse.get())) {
                backingMap.put(key, new WeakReference<>(result));
            } else if (backingMap.containsKey(key)) {
                backingMap.remove(key);
            }
            return result;
        }

        public synchronized void put(T item) {
            switch (item.getRowState()) {
                case MODIFIED:
                case UNMODIFIED:
                    int key = item.getPrimaryKey();
                    if (backingMap.containsKey(key)) {
                        T result = backingMap.get(key).get();
                        if (null != result) {
                            if (result == item) {
                                return;
                            }
                            throw new IllegalStateException("One one instance may have the same primary key");
                        }
                    }
                    backingMap.put(key, new WeakReference<>(item));
                    break;
                default:
                    throw new IllegalStateException("Item cannot be new or deleted.");
            }
        }

    }

    /**
     * Base factory class for {@link DataAccessObject} objects.
     *
     * @param <T> The type of {@link DataAccessObject} object supported.
     * @param <E> The {@link DbOperationEvent} type.
     */
    public static abstract class DaoFactory<T extends DataAccessObject, E extends DbOperationEvent<? extends FxRecordModel<T>, T>> implements EventTarget {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DaoFactory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DaoFactory.class.getName());
        private final EventHandlerManager eventHandlerManager;
        private final DataObjectCache<T> dataObjectCache = new DataObjectCache<>();

        protected DaoFactory() {
            eventHandlerManager = new EventHandlerManager(this);
        }

        abstract void validateSave(SaveTask<T, ? extends FxRecordModel<T>, E> task);

        final void saveChanges(SaveTask<T, ? extends FxRecordModel<T>, E> task) {
            E event = task.getCompletionEvent();
            Connection connection = task.getConnection();
            T dao = event.getDataAccessObject();
            DataAccessObject dataObj = (DataAccessObject) dao;
            Timestamp timeStamp = DB.toUtcTimestamp(LocalDateTime.now());
            StringBuilder sb = new StringBuilder();
            DbColumn[] columns;
            Iterator<DbColumn> iterator;
            DbName dbName;
            int colNum = 0;
            if (event.getOperation() == DbOperationType.DB_INSERT) {
                columns = SchemaHelper.getTableColumns(getDbTable(),
                        (t) -> t.getUsageCategory() != ColumnCategory.PRIMARY_KEY).toArray(DbColumn[]::new);
                iterator = Arrays.stream(columns).iterator();
                sb.append("INSERT INTO ").append(getDbTable().getDbName()).append(" (")
                        .append(iterator.next().getDbName());
                int index = 1;
                while (iterator.hasNext()) {
                    index++;
                    sb.append(", ").append(iterator.next().getDbName());
                }
                sb.append(") VALUES (?");
                for (int i = 1; i < index; i++) {
                    sb.append(", ?");
                }
                sb.append(")");
            } else {
                columns = SchemaHelper.getTableColumns(getDbTable(), (t) -> SchemaHelper.isUpdatable(t)).toArray(DbColumn[]::new);
                iterator = Arrays.stream(columns).iterator();
                dbName = iterator.next().getDbName();
                sb.append("UPDATE ").append(getDbTable().getDbName()).append(" SET ");
                LOG.fine(String.format("Appending column SQL for column %s at index %d", dbName, ++colNum));
                sb.append(dbName).append("=?");
                while (iterator.hasNext()) {
                    dbName = iterator.next().getDbName();
                    LOG.fine(String.format("Appending column SQL for %s at index %d", dbName, ++colNum));
                    sb.append(", ").append(dbName).append("=?");
                }
                dbName = getPrimaryKeyColumn().getDbName();
                LOG.fine(String.format("Appending column SQL for %s at index %d", dbName, ++colNum));
                sb.append(" WHERE ").append(dbName).append("=?");
            }
            String sql = sb.toString();
            try (PreparedStatement ps = (event.getOperation() == DbOperationType.DB_INSERT)
                    ? connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                    : connection.prepareStatement(sb.toString())) {
                iterator = Arrays.stream(columns).iterator();
                int index = 1;
                do {
                    DbColumn column = iterator.next();
                    try {
                        LOG.fine(String.format("Setting value SQL for column %s at index %d", column, index));
                        if (column.getUsageCategory() == ColumnCategory.AUDIT) {
                            switch (column.getDbName()) {
                                case CREATE_DATE:
                                    dataObj.createDate = timeStamp;
                                    ps.setTimestamp(index++, dataObj.createDate);
                                    break;
                                case CREATED_BY:
                                    dataObj.createdBy = Scheduler.getCurrentUser().getUserName();
                                    ps.setString(index++, dataObj.createdBy);
                                    break;
                                case LAST_UPDATE:
                                    dataObj.lastModifiedDate = timeStamp;
                                    ps.setTimestamp(index++, dataObj.lastModifiedDate);
                                    break;
                                case LAST_UPDATE_BY:
                                    dataObj.lastModifiedBy = Scheduler.getCurrentUser().getUserName();
                                    ps.setString(index++, dataObj.lastModifiedBy);
                                    break;
                                default:
                                    LogHelper.logWarnings(connection, LOG);
                                    throw new InternalException(String.format("Unexpected AUDIT column name %s", column.getDbName()));
                            }
                        } else {
                            applyColumnValue(dao, column, ps, index++);
                        }
                    } catch (SQLException ex) {
                        event.setFaulted("Unexpected Error", String.format("Error setting value for column %s", column.getDbName()), ex);
                        LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                        return;
                    }
                } while (iterator.hasNext());
                if (event.getOperation() != DbOperationType.DB_INSERT) {
                    try {
                        LOG.fine(String.format("Setting value primary key at index %d", index));
                        ps.setInt(index, dataObj.primaryKey);
                    } catch (SQLException ex) {
                        LogHelper.logWarnings(connection, LOG);
                        event.setFaulted("Unexpected Error", "Error setting value for primary key column", ex);
                        LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                        return;
                    }
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                if (ps.executeUpdate() < 1) {
                    LogHelper.logWarnings(connection, LOG);
                    event.setFaulted("No change", "executeUpdate unexpectedly resulted in no database changes");
                    return;
                }
                LogHelper.logWarnings(connection, LOG);
                if (event.getOperation() == DbOperationType.DB_INSERT) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            LogHelper.logWarnings(connection, LOG);
                            throw new SQLException("No primary key returned");
                        }
                        dataObj.primaryKey = rs.getInt(1);
                        dataObjectCache.put(dao);
                        LogHelper.logWarnings(connection, LOG);
                    } catch (SQLException ex) {
                        event.setFaulted("Unexpected Error", "Error getting new primary key value", ex);
                        LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                        return;
                    }
                }
            } catch (SQLException ex) {
                event.setFaulted("Unexpected Error", "Error executing DML statement", ex);
                LOG.log(Level.SEVERE, String.format("Error executing DML statement: %s", sql), ex);
                return;
            }
            event.setSucceeded();
        }

        abstract void validateDelete(DeleteTask<T, ? extends FxRecordModel<T>, E> task);

        final void deleteRecord(DeleteTask<T, ? extends FxRecordModel<T>, E> task) {
            E event = task.getCompletionEvent();
            Connection connection = task.getConnection();
            T dao = task.getDataAccessObject();
            DataAccessObject dataObj = (DataAccessObject) event.getDataAccessObject();
            DataRowState oldRowState = dataObj.rowState;
            try (ChangeEventDeferral eventDeferral = dataObj.deferChangeEvents()) {
                synchronized (dataObj) {
                    if (dataObj.rowState == DataRowState.DELETED) {
                        event.setFaulted("Already deleted", "Item has already been deleted");
                        return;
                    }
                    if (dataObj.rowState == DataRowState.NEW) {
                        event.setFaulted("Not Saved", "Item has never been saved to the database");
                        return;
                    }
                    StringBuilder sb = new StringBuilder("DELETE FROM ");
                    sb.append(getDbTable().getDbName()).append(" WHERE ").append(getPrimaryKeyColumn().getDbName()).append("=?");
                    String sql = sb.toString();
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setInt(1, dataObj.primaryKey);
                        LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                        if (ps.executeUpdate() < 1) {
                            LogHelper.logWarnings(connection, LOG);
                            event.setFaulted("No results", "executeUpdate unexpectedly resulted in no database changes");
                        }
                        LogHelper.logWarnings(connection, LOG);
                    }
                    dataObj.rowState = DataRowState.DELETED;
                }
            } catch (Exception ex) {
                event.setFaulted("Unexpected error", "Error deleting object from database", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
            } finally {
                dataObj.firePropertyChange(PROP_ROWSTATE, oldRowState, dataObj.rowState);
            }
            if (event.getStatus() == EventEvaluationStatus.EVALUATING) {
                event.setSucceeded();
            }
        }

        /**
         * Loads items from the database. {@link #save(scheduler.events.DbOperationEvent, java.sql.Connection, boolean)}
         *
         * @param connection An opened database connection.
         * @param filter The {@link DaoFilter} that is used to build the WHERE clause of the SQL query.
         * @return A list of items loaded.
         * @throws SQLException if unable to read data from the database.
         */
        public final List<T> load(Connection connection, DaoFilter<T> filter) throws SQLException {
            DmlSelectQueryBuilder builder = createDmlSelectQueryBuilder();
            StringBuffer sb = builder.build();
            if (null != filter && !filter.isEmpty()) {
                if (builder.getJoins().isEmpty()) {
                    filter.appendSimpleDmlConditional(sb.append(" WHERE "));
                } else {
                    filter.appendJoinedDmlConditional(sb.append(" WHERE "));
                }
            }
            ArrayList<T> result = new ArrayList<>();
            String sql = sb.toString();
            LOG.fine(() -> String.format("Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                if (null != filter && !filter.isEmpty()) {
                    filter.applyWhereParameters(ps, 1);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs) {
                        while (rs.next()) {
                            T item = fromResultSet(rs);
                            result.add(item);
                        }
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            return result;
        }

        /**
         * Asynchronously loads {@link DataAccessObject} objects from the database.
         *
         * @param waitBorderPane The {@link WaitBorderPane} on which to show the busy indicator.
         * @param filter The {@link DaoFilter} that is used to build the WHERE clause of the SQL query.
         * @param onSuccess The {@link Consumer} to invoke if successful.
         * @param onFail The {@link Consumer} to invoke if an exception is thrown.
         * @return The {@link Task} that has been started.
         */
        public final Task<List<T>> loadAsync(WaitBorderPane waitBorderPane, DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
            LoadTask<T> task = new LoadTask<>(this, filter, onSuccess, onFail);
            waitBorderPane.startNow(task);
            return task;
        }

        public final Task<List<T>> loadAsync(DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
            LoadTask<T> task = new LoadTask<>(this, filter, onSuccess, onFail);
            MainController.startBusyTaskNow(task);
            return task;
        }

        public final T createClone(T source, boolean asNew) {
            T result = createNew();
            if (asNew) {
                onCloneProperties(source, result);
            } else {
                cloneProperties(source, result);
            }
            return result;
        }

        /**
         * Synchronizes the properties of 2 data access objects.
         *
         * @param source The source data access object.
         * @param target The data access object to be updated.
         * @throws IllegalStateException if the two objects represent different records.
         */
        public final void synchronize(T source, T target) {
            if (target.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW || target.getPrimaryKey() != source.getPrimaryKey())) {
                throw new IllegalStateException();
            }
            cloneProperties(source, target);
        }

        @SuppressWarnings("try")
        protected final void cloneProperties(T fromDAO, T toDAO) {
            if (Objects.equals(fromDAO, toDAO)) {
                return;
            }
            DataAccessObject d1 = (DataAccessObject) fromDAO;
            DataAccessObject d2 = (DataAccessObject) toDAO;
            try (ChangeEventDeferral eventDeferral = d2.deferChangeEvents()) {
                Timestamp oldCreateDate = d2.createDate;
                String oldCreatedBy = d2.createdBy;
                Timestamp oldLastModifiedDate = d2.lastModifiedDate;
                String oldLastModifiedBy = d2.lastModifiedBy;
                int oldPrimaryKey = d2.primaryKey;
                DataRowState oldRowState = d2.rowState;
                d2.createDate = d1.createDate;
                d2.createdBy = d1.createdBy;
                d2.lastModifiedDate = d1.lastModifiedDate;
                d2.lastModifiedBy = d1.lastModifiedBy;
                d2.primaryKey = d1.primaryKey;
                d2.originalValues.createDate = d1.originalValues.createDate;
                d2.originalValues.createdBy = d1.originalValues.createdBy;
                d2.originalValues.lastModifiedDate = d1.originalValues.lastModifiedDate;
                d2.originalValues.lastModifiedBy = d1.originalValues.lastModifiedBy;
                d2.primaryKey = d1.primaryKey;
                d2.rowState = d1.rowState;
                onCloneProperties(fromDAO, toDAO);
                d2.firePropertyChange(PROP_CREATEDATE, oldCreateDate, d2.createDate);
                d2.firePropertyChange(PROP_CREATEDBY, oldCreatedBy, d2.createdBy);
                d2.firePropertyChange(PROP_LASTMODIFIEDDATE, oldLastModifiedDate, d2.lastModifiedDate);
                d2.firePropertyChange(PROP_LASTMODIFIEDBY, oldLastModifiedBy, d2.lastModifiedBy);
                d2.firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, d2.primaryKey);
                d2.firePropertyChange(PROP_ROWSTATE, oldRowState, d2.rowState);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Unxpected exception in change deferral", ex);
            }
        }

        protected abstract void onCloneProperties(T fromDAO, T toDAO);

        /**
         * Creates a new data access object from database query results.
         *
         * @param rs The {@link ResultSet} containing values from the database.
         * @return The newly-initialized data access object.
         * @throws SQLException if unable to read values from the {@link ResultSet}.
         */
        @SuppressWarnings("try")
        protected final T fromResultSet(ResultSet rs) throws SQLException {
            int key = rs.getInt(getPrimaryKeyColumn().toString());
            T dao = dataObjectCache.get(key, () -> {
                T t = createNew();
                ((DataAccessObject) t).primaryKey = key;
                return t;
            });
            DataAccessObject obj = (DataAccessObject) dao;

            DataRowState oldRowState = obj.rowState;
            DataAccessObject dataAccessObject = (DataAccessObject) dao;
            try (ChangeEventDeferral eventDeferral = dataAccessObject.deferChangeEvents()) {
                Consumer<PropertyChangeSupport> consumer;
                synchronized (dao) {
                    obj.createDate = rs.getTimestamp(DbName.CREATE_DATE.toString());
                    obj.createdBy = rs.getString(DbName.CREATED_BY.toString());
                    obj.lastModifiedDate = rs.getTimestamp(DbName.LAST_UPDATE.toString());
                    obj.lastModifiedBy = rs.getString(DbName.LAST_UPDATE_BY.toString());
                    obj.rowState = DataRowState.UNMODIFIED;
                    consumer = onInitializeFromResultSet(dao, rs);
                }
                if (null != consumer) {
                    consumer.accept(obj.getPropertyChangeSupport());
                }
                dataAccessObject.acceptChanges();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Unxpected exception in change deferral", ex);
            } finally {
                dao.firePropertyChange(PROP_ROWSTATE, oldRowState, obj.rowState);
            }
            return dao;
        }

        /**
         * Creates a new {@link DataAccessObject} object.
         *
         * @return A newly constructed {@link DataAccessObject} object.
         */
        public abstract T createNew();

        /**
         * Gets a {@link DaoFilter} for returning all items.
         *
         * @return A {@link DaoFilter} for returning all items.
         */
        public abstract DaoFilter<T> getAllItemsFilter();

        /**
         * Completes property initialization for a {@link DataAccessObject}.
         *
         * @param dao The {@link DataAccessObject} to be initialized.
         * @param rs The {@link ResultSet} to read from.
         * @return A {@link Consumer} that gets invoked after the data access object is no longer in a synchronized state. This will allow
         * implementing classes to put fields directly while property change events are deferred. This value can be {@code null} if it is not
         * applicable.
         * @throws SQLException if unable to read from the {@link ResultSet}.
         */
        protected abstract Consumer<PropertyChangeSupport> onInitializeFromResultSet(T dao, ResultSet rs) throws SQLException;

        /**
         * Gets the {@link DbTable} for the supported {@link DataAccessObject}.
         *
         * @return The {@link DbTable} for the supported {@link DataAccessObject}.
         */
        public final DbTable getDbTable() {
            return AnnotationHelper.getDbTable(getDaoClass());
        }

        /**
         * Gets the primary key {@link DbColumn} for the supported {@link DataAccessObject}.
         *
         * @return The primary key {@link DbColumn} for the supported {@link DataAccessObject}.
         */
        public final DbColumn getPrimaryKeyColumn() {
            return SchemaHelper.getPrimaryKey(AnnotationHelper.getDbTable(getDaoClass()));
        }

        /**
         * Gets the {@link Class} for the target {@link DataAccessObject} type.
         *
         * @return The {@link Class} for the target {@link DataAccessObject} type.
         */
        public abstract Class<? extends T> getDaoClass();

        /**
         * Creates a {@link DmlSelectQueryBuilder} for a SQL SELECT statement.
         *
         * @return A {@link DmlSelectQueryBuilder} for building a SQL SELECT statement with all of the necessary joins.
         */
        public abstract DmlSelectQueryBuilder createDmlSelectQueryBuilder();

        /**
         * Sets the parameter value at the specified index from the value associated with the given {@link DbColumn}.
         *
         * @param dao The {@link DataAccessObject} to retrieve the value from.
         * @param dbColumn The {@link DbColumn} related to the value to apply.
         * @param ps The {@link PreparedStatement} to apply the value to.
         * @param index The index at which to apply the value.
         * @throws java.sql.SQLException if not able to put the parameter value.
         */
        protected abstract void applyColumnValue(T dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException;

        /**
         * Indicates whether {@link #createDmlSelectQueryBuilder()} returns a {@link DmlSelectQueryBuilder} with joined tables.
         *
         * @return {@code true} if {@link #createDmlSelectQueryBuilder()} returns a {@link DmlSelectQueryBuilder} with joined tables; otherwise,
         * {@code false}.
         */
        public abstract boolean isCompoundSelect();

        /**
         * Retrieves the {@link DataAccessObject} from the database which matches the given primary key.
         *
         * @param connection The database connection to use.
         * @param pk The value of the primary key for the {@link DataAccessObject}.
         * @return An {@link Optional} {@link DataAccessObject} which will be empty if no match was found.
         * @throws SQLException If unable to perform the database operation.
         */
        public final Optional<T> loadByPrimaryKey(Connection connection, int pk) throws SQLException {
            Objects.requireNonNull(connection, "Connection cannot be null");
            DmlSelectQueryBuilder builder = createDmlSelectQueryBuilder();
            StringBuffer sb = builder.build().append(" WHERE ");
            DbColumn dbColumn = getPrimaryKeyColumn();
            String n = dbColumn.getDbName().toString();
            if (builder.getJoins().isEmpty()) {
                if (!n.equals(dbColumn.toString())) {
                    sb.append(dbColumn.getTable().getDbName()).append(".");
                }
            } else {
                sb.append(dbColumn.getTable()).append(".");
            }
            sb.append(n).append("=?");
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, pk);
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Optional<T> result = Optional.of(fromResultSet(rs));
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            return Optional.empty();
        }

        protected abstract E createDbOperationEvent(E sourceEvent, DbOperationType operation);

        /**
         * Helper method that can be used to determine if a {@link DataAccessObject} object is supported by the current factory class.
         *
         * @param dao The {@link DataAccessObject} to test.
         * @return {@code true} if the current factory supports the {@link DataAccessObject} type; otherwise, {@code false}.
         */
        public final boolean isAssignableFrom(DataAccessObject dao) {
            return null != dao && getDaoClass().isAssignableFrom(dao.getClass());
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            return tail.append(eventHandlerManager);
        }

        /**
         * Registers a {@link DbOperationEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void addEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.addEventHandler(type, eventHandler);
        }

        /**
         * Registers a {@link DbOperationEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void addEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.addEventFilter(type, eventHandler);
        }

        /**
         * Unregisters a {@link DbOperationEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void removeEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.removeEventHandler(type, eventHandler);
        }

        /**
         * Unregisters a {@link DbOperationEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void removeEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.removeEventFilter(type, eventHandler);
        }

    }

    public static abstract class DaoTask<D extends DataAccessObject, M extends FxRecordModel<D>, E extends DbOperationEvent<M, D>> extends Task<E> {

        private final D dataAccessObject;
        private final DaoFactory<D, E> daoFactory;
        private final E validationEvent;
        private E completionEvent;
        private Connection connection;

        @SuppressWarnings("unchecked")
        protected DaoTask(E validationEvent) {
            dataAccessObject = (this.validationEvent = validationEvent).getDataAccessObject();
            daoFactory = (DaoFactory<D, E>) validationEvent.getModelFactory().getDaoFactory();
            switch (validationEvent.getOperation()) {
                case INSERT_VALIDATION:
                    if (dataAccessObject.getRowState() != DataRowState.NEW) {
                        throw new IllegalArgumentException(String.format("Operation type %s not supported for %s row state",
                                validationEvent.getOperation(), dataAccessObject.getRowState()));
                    }
                    break;
                case UPDATE_VALIDATION:
                    switch (dataAccessObject.getRowState()) {
                        case MODIFIED:
                        case UNMODIFIED:
                            break;
                        default:
                            throw new IllegalArgumentException(String.format("Operation type %s not supported for %s row state",
                                    validationEvent.getOperation(), dataAccessObject.getRowState()));
                    }
                    break;
                case DELETE_VALIDATION:
                    switch (dataAccessObject.getRowState()) {
                        case MODIFIED:
                        case UNMODIFIED:
                            break;
                        default:
                            throw new IllegalArgumentException(String.format("Operation type %s not supported for %s row state",
                                    validationEvent.getOperation(), dataAccessObject.getRowState()));
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Operation type %s not supported for %s row state",
                            validationEvent.getOperation(), dataAccessObject.getRowState()));
            }
        }

        @Override
        public void updateMessage(String message) {
            super.updateMessage(message);
        }

        @Override
        public void updateTitle(String title) {
            super.updateTitle(title);
        }

        public E getValidationEvent() {
            return validationEvent;
        }

        public E getCurrentEvent() {
            return (null == completionEvent) ? validationEvent : completionEvent;
        }

        public E getCompletionEvent() {
            return completionEvent;
        }

        public D getDataAccessObject() {
            return dataAccessObject;
        }

        public Connection getConnection() {
            return connection;
        }

        public DaoFactory<D, E> getDaoFactory() {
            return daoFactory;
        }

        private synchronized E ensureCompletedEvent() {
            if (null == completionEvent) {
                switch (validationEvent.getOperation()) {
                    case DELETE_VALIDATION:
                        completionEvent = daoFactory.createDbOperationEvent(validationEvent, DbOperationType.DB_DELETE);
                        break;
                    case INSERT_VALIDATION:
                        completionEvent = daoFactory.createDbOperationEvent(validationEvent, DbOperationType.DB_INSERT);
                        break;
                    case UPDATE_VALIDATION:
                        completionEvent = daoFactory.createDbOperationEvent(validationEvent, DbOperationType.DB_UPDATE);
                        break;
                    default:
                        return validationEvent;
                }
            }
            return completionEvent;
        }

        @Override
        protected void succeeded() {
            if (null == completionEvent) {
                ensureCompletedEvent();
                if (null != completionEvent) {
                    switch (validationEvent.getStatus()) {
                        case EVALUATING:
                            validationEvent.setSucceeded();
                            completionEvent.setCanceled();
                            break;
                        case FAULTED:
                            completionEvent.setFaulted(validationEvent.getSummaryTitle(), validationEvent.getDetailMessage(), validationEvent.getFault());
                            break;
                        case INVALID:
                            completionEvent.setInvalid(validationEvent.getSummaryTitle(), validationEvent.getDetailMessage());
                            break;
                        default:
                            completionEvent.setCanceled();
                            break;
                    }
                    LOG.fine(() -> String.format("Firing %s", completionEvent));
                    Event.fireEvent(getDataAccessObject(), completionEvent);
                } else if (validationEvent.getStatus() == EventEvaluationStatus.EVALUATING) {
                    validationEvent.setCanceled();
                }
            } else {
                if (completionEvent.getStatus() == EventEvaluationStatus.EVALUATING) {
                    completionEvent.setSucceeded();
                }
                LOG.fine(() -> String.format("Firing %s", completionEvent));
                Event.fireEvent(getDataAccessObject(), completionEvent);
            }
            super.succeeded();
        }

        @Override
        protected void cancelled() {
            LOG.warning(() -> String.format("Task canceled: %s", getCurrentEvent()));
            if (null == completionEvent) {
                if (validationEvent.getStatus() == EventEvaluationStatus.EVALUATING) {
                    validationEvent.setCanceled();
                }
            }
            E event = ensureCompletedEvent();
            if (event.getStatus() == EventEvaluationStatus.EVALUATING) {
                event.setCanceled();
            }
            if (null != completionEvent) {
                LOG.fine(() -> String.format("Firing %s", completionEvent));
                Event.fireEvent(getDataAccessObject(), completionEvent);
            }
            super.cancelled();
        }

        @Override
        protected void failed() {
            Throwable fault = getException();
            LOG.log(Level.SEVERE, String.format("Task failed: %s", getCurrentEvent()), fault);
            if (null == completionEvent) {
                if (validationEvent.getStatus() == EventEvaluationStatus.EVALUATING) {
                    validationEvent.setFaulted("Task failed", fault.getMessage(), fault);
                }
            }
            E event = ensureCompletedEvent();
            if (event.getStatus() == EventEvaluationStatus.EVALUATING) {
                event.setFaulted("Task failed", fault.getMessage(), fault);
            }
            if (null != completionEvent) {
                LOG.fine(() -> String.format("Firing %s", completionEvent));
                Event.fireEvent(getDataAccessObject(), completionEvent);
            }
            super.failed();
        }

        protected abstract void onValidate() throws Exception;

        protected abstract void onExecute() throws Exception;

        @Override
        protected E call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                connection = dbConnector.getConnection();
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                DataAccessObject dao = dataAccessObject;
                DataRowState oldRowState = dao.rowState;
                try (ChangeEventDeferral eventDeferral = dataAccessObject.deferChangeEvents()) {
                    synchronized (dataAccessObject) {
                        if (validationEvent.getOperation() == DbOperationType.INSERT_VALIDATION) {
                            if (dao.rowState != DataRowState.NEW) {
                                throw new IllegalStateException("Invalid row state");
                            }
                        } else {
                            switch (dao.rowState) {
                                case MODIFIED:
                                case UNMODIFIED:
                                    break;
                                default:
                                    throw new IllegalStateException("Invalid row state");
                            }
                        }
                    }
                    if (validationEvent.getStatus() == EventEvaluationStatus.EVALUATING) {
                        onValidate();
                    }
                    switch (validationEvent.getStatus()) {
                        case EVALUATING:
                            validationEvent.setSucceeded();
                            break;
                        case SUCCEEDED:
                            break;
                        default:
                            return validationEvent;
                    }
                    ensureCompletedEvent();
                    if (null != completionEvent) {
                        onExecute();
                    }
                }
                if (null != completionEvent && completionEvent.getStatus() == EventEvaluationStatus.SUCCEEDED) {
                    dao.acceptChanges();
                    dao.rowState = DataRowState.UNMODIFIED;
                    dao.firePropertyChange(PROP_ROWSTATE, oldRowState, dao.rowState);
                }
                return getCurrentEvent();
            }
        }

    }

    public static class SaveTask<D extends DataAccessObject, M extends FxRecordModel<D>, E extends DbOperationEvent<M, D>> extends DaoTask<D, M, E> {

        private static final Logger LOG = Logger.getLogger(SaveTask.class.getName());
        private DataRowState oldRowState;

        public SaveTask(E event) {
            super(event);
            switch (event.getOperation()) {
                case INSERT_VALIDATION:
                case UPDATE_VALIDATION:
                    oldRowState = event.getDataAccessObject().getRowState();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_SAVINGCHANGES));
        }

        @Override
        protected void succeeded() {
            E resultEvent = getValue();
            if (resultEvent.getStatus() == EventEvaluationStatus.SUCCEEDED) {
                DataAccessObject dataObj = (DataAccessObject) resultEvent.getDataAccessObject();
                dataObj.acceptChanges();
                dataObj.rowState = DataRowState.UNMODIFIED;
                dataObj.firePropertyChange(PROP_ROWSTATE, oldRowState, dataObj.rowState);
                LOG.fine(() -> String.format("Firing event %s on %s", resultEvent.getEventType().getName(), resultEvent.getDataAccessObject().getClass().getName()));
                Event.fireEvent(resultEvent.getDataAccessObject(), resultEvent);
            }
            super.succeeded();
        }

        @Override
        protected void onValidate() throws Exception {
            getDaoFactory().validateSave(this);
        }

        @Override
        protected void onExecute() throws Exception {
            getDaoFactory().saveChanges(this);
        }

    }

    /**
     * {@link Task} for deleting a {@link DataAccessObject} from the database. This will fire a {@link DbOperationEvent} on the target
     * {@link DataAccessObject} after successful deletion. The {@link DbOperationEvent#operation operation} property will be set to
     * {@link DbOperationType#DB_DELETE} and it will be fired in the FX application thread.
     *
     * @param <D> The type of {@link DataAccessObject} to be deleted.
     * @param <M> The type of {@link FxRecordModel} that corresponds to the {@link DataAccessObject} type.
     * @param <E> The type of {@link DbOperationEvent} that contains the {@link DataAccessObject} to be deleted.
     */
    public static class DeleteTask<D extends DataAccessObject, M extends FxRecordModel<D>, E extends DbOperationEvent<M, D>> extends DaoTask<D, M, E> {

        /**
         * Creates a new {@code DeleteTask} object.
         *
         * @param event A {@link DbOperationEvent} that refers to the {@link DataAccessObject} to be deleted.
         * @throws IllegalArgumentException if {@link DbOperationEvent#operation} is not set to {@link DbOperationType#DELETE_VALIDATION}.
         */
        public DeleteTask(E event) {
            super(event);
            if (event.getOperation() != DbOperationType.DELETE_VALIDATION) {
                throw new IllegalArgumentException();
            }
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
        }

        @Override
        protected void onValidate() throws Exception {
            getDaoFactory().validateDelete(this);
        }

        @Override
        protected void onExecute() throws Exception {
            getDaoFactory().deleteRecord(this);
        }

    }

    private class OriginalValues {

        private Timestamp createDate;
        private String createdBy;
        private Timestamp lastModifiedDate;
        private String lastModifiedBy;

        private OriginalValues() {
            this.createDate = DataAccessObject.this.createDate;
            this.createdBy = DataAccessObject.this.createdBy;
            this.lastModifiedDate = DataAccessObject.this.lastModifiedDate;
            this.lastModifiedBy = DataAccessObject.this.lastModifiedBy;
        }
    }
}
