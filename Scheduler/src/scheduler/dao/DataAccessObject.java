package scheduler.dao;

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
import javafx.event.EventHandler;
import javafx.event.EventType;
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
import scheduler.events.ModelEvent;
import scheduler.model.DataObject;
import static scheduler.model.DataObject.PROP_ROWSTATE;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.AnnotationHelper;
import scheduler.util.DB;
import scheduler.util.DbConnector;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.Values;
import static scheduler.util.Values.asNonNullAndTrimmed;
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
public abstract class DataAccessObject extends PropertyBindable implements DbRecord {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DataAccessObject.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(DataAccessObject.class.getName());

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
        primaryKey = Integer.MIN_VALUE;
        lastModifiedDate = createDate = DB.toUtcTimestamp(LocalDateTime.now());
        lastModifiedBy = createdBy = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
        rowState = DataRowState.NEW;
        originalValues = new OriginalValues();
    }

    /**
     * This gets called after the associated record in the database as been successfully inserted, updated or deleted. {@link PropertyChangeEvent}s
     * will be deferred while this is invoked.
     */
    protected abstract void onAcceptChanges();

    private void acceptChanges() {
        onAcceptChanges();
        originalValues.createDate = getCreateDate();
        originalValues.createdBy = getCreatedBy();
        originalValues.lastModifiedDate = getLastModifiedDate();
        originalValues.lastModifiedBy = getLastModifiedBy();
    }

    /**
     * This gets called when property changes are rejected and are to be restored to their original values. {@link PropertyChangeEvent}s will be
     * deferred while this is invoked.
     */
    protected abstract void onRejectChanges();

    /**
     * Rejects property value changes changes and reverts them back to their original values.
     */
    @SuppressWarnings("try")
    public void rejectChanges() {
        try (ChangeEventDeferral eventDeferral = deferChangeEvents()) {
            DataRowState oldRowState = rowState;
            onRejectChanges();
            setCreateDate(originalValues.createDate);
            setCreatedBy(originalValues.createdBy);
            setLastModifiedDate(originalValues.lastModifiedDate);
            setLastModifiedBy(originalValues.lastModifiedBy);
            if (rowState == DataRowState.MODIFIED) {
                rowState = DataRowState.UNMODIFIED;
                firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
            }
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

    private void setCreateDate(Timestamp value) {
        Timestamp oldValue = createDate;
        createDate = Objects.requireNonNull(value);
        firePropertyChange(PROP_CREATEDATE, oldValue, createDate);
    }

    @Override
    public final String getCreatedBy() {
        return createdBy;
    }

    private void setCreatedBy(String value) {
        String oldValue = createdBy;
        createdBy = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_CREATEDBY, oldValue, createdBy);
    }

    @Override
    public final Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }

    private void setLastModifiedDate(Timestamp value) {
        Timestamp oldValue = lastModifiedDate;
        lastModifiedDate = Objects.requireNonNull(value);
        firePropertyChange(PROP_LASTMODIFIEDDATE, oldValue, lastModifiedDate);
    }

    @Override
    public final String getLastModifiedBy() {
        return lastModifiedBy;
    }

    private void setLastModifiedBy(String value) {
        String oldValue = lastModifiedBy;
        lastModifiedBy = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_LASTMODIFIEDBY, oldValue, lastModifiedBy);
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
            DataRowState oldRowState = rowState;
            setLastModifiedBy((null == currentUser) ? "admin" : currentUser.getUserName());
            setLastModifiedDate(DB.toUtcTimestamp(LocalDateTime.now()));
            switch (rowState) {
                case DELETED:
                case MODIFIED:
                    break;
                case UNMODIFIED:
                    rowState = DataRowState.MODIFIED;
                    break;
                default:
                    setCreatedBy(getLastModifiedBy());
                    setCreateDate(getLastModifiedDate());
                    rowState = DataRowState.NEW;
                    break;
            }
            firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
        }
    }

    /**
     * Registers a {@link ModelEvent} handler in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Remove this method
    @Deprecated
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
            void addEventHandler(EventType<E> type, EventHandler<E> eventHandler) {
    }

    /**
     * Registers a {@link ModelEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Remove this method
    @Deprecated
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
            void addEventFilter(EventType<E> type, EventHandler<E> eventHandler) {
    }

    /**
     * Unregisters a {@link ModelEvent} handler in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Remove this method
    @Deprecated
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
            void removeEventHandler(EventType<E> type, EventHandler<E> eventHandler) {
    }

    /**
     * Unregisters a {@link ModelEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Remove this method
    @Deprecated
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
            void removeEventFilter(EventType<E> type, EventHandler<E> eventHandler) {
    }

    /**
     * Resets {@link #rowState} to {@link DataRowState#NEW} if the current state is {@link DataRowState#DELETED}; otherwise, this has no effect.
     */
    public synchronized void resetRowState() {
        if (rowState == DataRowState.DELETED) {
            int oldPrimaryKey = getPrimaryKey();
            primaryKey = Integer.MIN_VALUE;
            Timestamp ts = DB.toUtcTimestamp(LocalDateTime.now());
            String s = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
            setLastModifiedDate(ts);
            setCreateDate(ts);
            setLastModifiedBy(s);
            setCreatedBy(s);
            rowState = DataRowState.NEW;
            firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, primaryKey);
            firePropertyChange(PROP_ROWSTATE, DataRowState.DELETED, rowState);
        }
    }

    private static class LoadTask<T extends DataAccessObject> extends Task<List<T>> {

        private final DaoFactory<T> factory;
        private final DaoFilter<T> filter;
        private final Consumer<List<T>> onSuccess;
        private final Consumer<Throwable> onFail;

        LoadTask(DaoFactory<T> factory, DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
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

    private static class DataObjectCacheIterator<T extends DataObject> implements Iterator<T> {

        private T next = null;
        private final DataObjectCache<T> target;
        private final Iterator<Integer> backingIterator;

        private DataObjectCacheIterator(DataObjectCache<T> target) {
            backingIterator = (this.target = target).backingMap.keySet().iterator();
        }

        @Override
        public synchronized boolean hasNext() {
            if (null != next) {
                return true;
            }
            while (backingIterator.hasNext()) {
                if (null != (next = target.get(backingIterator.next()))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public synchronized T next() {
            T result = next;
            next = null;
            while (null == result) {
                result = target.get(backingIterator.next());
            }
            return result;
        }

    }

    private static class DataObjectCache<T extends DataObject> {

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
     * Base factory class for CRUD operations on {@link DataAccessObject} objects. This maintains a {@link WeakReference} cache of loaded
     * {@link DataAccessObject}s so that there will only ever be one instance of a {@link DataAccessObject} for each record in the database.
     *
     * @param <D> The type of {@link DataAccessObject} object supported.
     */
    public static abstract class DaoFactory<D extends DataAccessObject> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DaoFactory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DaoFactory.class.getName());

        private final DataObjectCache<D> dataObjectCache = new DataObjectCache<>();

        /**
         * Loads items from the database.
         *
         * @param connection An opened database connection.
         * @param filter The {@link DaoFilter} that is used to build the WHERE clause of the SQL query.
         * @return A list of items loaded.
         * @throws SQLException if unable to read data from the database.
         */
        public final List<D> load(Connection connection, DaoFilter<D> filter) throws SQLException {
            DmlSelectQueryBuilder builder = createDmlSelectQueryBuilder();
            StringBuffer sb = builder.build();
            if (null != filter && !filter.isEmpty()) {
                if (builder.getJoins().isEmpty()) {
                    filter.appendSimpleDmlConditional(sb.append(" WHERE "));
                } else {
                    filter.appendJoinedDmlConditional(sb.append(" WHERE "));
                }
            }
            ArrayList<D> result = new ArrayList<>();
            String sql = sb.toString();
            LOG.fine(() -> String.format("Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                if (null != filter && !filter.isEmpty()) {
                    filter.applyWhereParameters(ps, 1);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs) {
                        while (rs.next()) {
                            D item = fromResultSet(rs);
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
        public final Task<List<D>> loadAsync(WaitBorderPane waitBorderPane, DaoFilter<D> filter, Consumer<List<D>> onSuccess, Consumer<Throwable> onFail) {
            LoadTask<D> task = new LoadTask<>(this, filter, onSuccess, onFail);
            waitBorderPane.startNow(task);
            return task;
        }

        public final Task<List<D>> loadAsync(DaoFilter<D> filter, Consumer<List<D>> onSuccess, Consumer<Throwable> onFail) {
            LoadTask<D> task = new LoadTask<>(this, filter, onSuccess, onFail);
            MainController.startBusyTaskNow(task);
            return task;
        }

        public final D createClone(D source, boolean asNew) {
            D result = createNew();
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
        public final void synchronize(D source, D target) {
            if (target.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW || target.getPrimaryKey() != source.getPrimaryKey())) {
                throw new IllegalStateException();
            }
            cloneProperties(source, target);
        }

        /**
         * Clones the properties from one {@link DataAccessObject} to another.
         *
         * @param fromDAO The source {@link DataAccessObject} to be copied from.
         * @param toDAO The target {@link DataAccessObject} to be copied to.
         */
        @SuppressWarnings("try")
        protected final void cloneProperties(D fromDAO, D toDAO) {
            if (Objects.equals(fromDAO, toDAO)) {
                return;
            }
            DataAccessObject d1 = (DataAccessObject) fromDAO;
            DataAccessObject d2 = (DataAccessObject) toDAO;
            try (ChangeEventDeferral eventDeferral = d2.deferChangeEvents()) {
                d2.setCreateDate(d1.getCreateDate());
                d2.setCreatedBy(d1.getCreatedBy());
                d2.setLastModifiedDate(d1.getLastModifiedDate());
                d2.setLastModifiedBy(d1.getLastModifiedBy());
                int oldPrimaryKey = d2.getPrimaryKey();
                DataRowState oldRowState = d2.rowState;
                d2.primaryKey = d1.getPrimaryKey();
                d2.rowState = d1.rowState;
                onCloneProperties(fromDAO, toDAO);
                d2.firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, d2.getPrimaryKey());
                d2.firePropertyChange(PROP_ROWSTATE, oldRowState, d2.rowState);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Unxpected exception in change deferral", ex);
            }
        }

        /**
         * This gets called when the properties of one {@link DataAccessObject} are being copied to another. {@link PropertyChangeEvent}s will be
         * deferred while this is invoked.
         *
         * @param fromDAO The source {@link DataAccessObject} to be copied from.
         * @param toDAO The target {@link DataAccessObject} to be copied to.
         */
        protected abstract void onCloneProperties(D fromDAO, D toDAO);

        protected final Iterator<D> cacheIterator() {
            return new DataObjectCacheIterator<>(dataObjectCache);
        }

        /**
         * Creates a new data access object from database query results.
         *
         * @param rs The {@link ResultSet} containing values from the database.
         * @return The newly-initialized data access object.
         * @throws SQLException if unable to read values from the {@link ResultSet}.
         */
        @SuppressWarnings("try")
        protected final D fromResultSet(ResultSet rs) throws SQLException {
            int key = rs.getInt(getPrimaryKeyColumn().toString());
            D dao = dataObjectCache.get(key, () -> {
                D t = createNew();
                ((DataAccessObject) t).primaryKey = key;
                return t;
            });
            DataAccessObject obj = (DataAccessObject) dao;

            DataRowState oldRowState = obj.rowState;
            DataAccessObject dataAccessObject = (DataAccessObject) dao;
            try (ChangeEventDeferral eventDeferral = dataAccessObject.deferChangeEvents()) {
                Consumer<PropertyChangeSupport> consumer;
                synchronized (dao) {
                    obj.setCreateDate(rs.getTimestamp(DbName.CREATE_DATE.toString()));
                    obj.setCreatedBy(rs.getString(DbName.CREATED_BY.toString()));
                    obj.setLastModifiedDate(rs.getTimestamp(DbName.LAST_UPDATE.toString()));
                    obj.setLastModifiedBy(rs.getString(DbName.LAST_UPDATE_BY.toString()));
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
        public abstract D createNew();

//        /**
//         * Creates a new {@link SaveDaoTask} for saving changes to a {@link DataAccessObject} to the database.
//         *
//         * @param target The {@link RecordModelContext} that contains the {@link DataAccessObject} to be saved the database.
//         * @return A {@link SaveDaoTask} for saving changes to a {@link DataAccessObject} to the database.
//         */
//        public abstract SaveDaoTask<D, ? extends FxRecordModel<D>, E> createSaveTask(RecordModelContext<D, ? extends FxRecordModel<D>> target);
//
//        /**
//         * Creates a new {@link SaveDaoTask} for deleting a {@link DataAccessObject} from the database.
//         *
//         * @param target The {@link RecordModelContext} that contains the {@link DataAccessObject} to be saved the database.
//         * @return A {@link SaveDaoTask} for deleting a {@link DataAccessObject} from the database.
//         */
//        public abstract DeleteDaoTask<D, ? extends FxRecordModel<D>, E> createDeleteTask(RecordModelContext<D, ? extends FxRecordModel<D>> target);
        /**
         * Gets a {@link DaoFilter} for returning all items.
         *
         * @return A {@link DaoFilter} for returning all items.
         */
        public abstract DaoFilter<D> getAllItemsFilter();

        /**
         * Completes property initialization for a {@link DataAccessObject}. {@link PropertyChangeEvent}s will be deferred while this is invoked.
         *
         * @param dao The {@link DataAccessObject} to be initialized.
         * @param rs The {@link ResultSet} to read from.
         * @return A {@link Consumer} that gets invoked after the data access object is no longer in a synchronized state. This will allow
         * implementing classes to put fields directly while property change events are deferred. This value can be {@code null} if it is not
         * applicable.
         * @throws SQLException if unable to read from the {@link ResultSet}.
         */
        protected abstract Consumer<PropertyChangeSupport> onInitializeFromResultSet(D dao, ResultSet rs) throws SQLException;

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
        public abstract Class<? extends D> getDaoClass();

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
        protected abstract void applyColumnValue(D dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException;

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
        public final Optional<D> loadByPrimaryKey(Connection connection, int pk) throws SQLException {
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
                        Optional<D> result = Optional.of(fromResultSet(rs));
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            return Optional.empty();
        }

        /**
         * Helper method that can be used to determine if a {@link DataAccessObject} object is supported by the current factory class.
         *
         * @param dao The {@link DataAccessObject} to test.
         * @return {@code true} if the current factory supports the {@link DataAccessObject} type; otherwise, {@code false}.
         */
        public final boolean isAssignableFrom(DataAccessObject dao) {
            return null != dao && getDaoClass().isAssignableFrom(dao.getClass());
        }

    }

    /**
     * Background task which provides an opened database {@link Connection} and defers the firing of {@link java.beans.PropertyChangeEvent}s on a
     * {@link DataAccessObject}.
     *
     * @param <D> The target {@link DataAccessObject} type.
     * @param <M> The associated {@link FxRecordModel} type.
     * @param <R> The result type.
     */
    public static abstract class DaoTask<D extends DataAccessObject, M extends FxRecordModel<D>, R> extends Task<Optional<R>> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DaoTask.class.getName());

        private final DataRowState originalRowState;
        private final M fxRecordModel;
        private final D dataAccessObject;

        /**
         * Creates a new {@code DaoTask} for the {@link FxRecordModel#dataObject DataAccessObject} of a {@link FxRecordModel}.
         *
         * @param fxRecordModel The {@link FxRecordModel} that contains the target {@link DataAccessObject}.
         */
        protected DaoTask(M fxRecordModel) {
            dataAccessObject = (this.fxRecordModel = fxRecordModel).dataObject();
            originalRowState = dataAccessObject.getRowState();
        }

        /**
         * Gets the target {@link DataAccessObject}.
         *
         * @return The target {@link DataAccessObject}.
         */
        public D getDataAccessObject() {
            return dataAccessObject;
        }

        /**
         * Gets the {@link FxRecordModel} that wraps the target {@link DataAccessObject}.
         *
         * @return The {@link FxRecordModel} that wraps the target {@link DataAccessObject} or {@code null} if only the target
         * {@link DataAccessObject} was provided to this task.
         */
        public M getFxRecordModel() {
            return fxRecordModel;
        }

        /**
         * Gets the original {@link DataAccessObject#rowState rowState} of the {@link DataAccessObject} when the {@code DaoTask} was created.
         *
         * @return The original {@link DataAccessObject#rowState rowState} of the {@link DataAccessObject} when the {@code DaoTask} was created.
         */
        protected final DataRowState getOriginalRowState() {
            return originalRowState;
        }

        /**
         * Invoked when the database {@link Connection} is opened and {@link java.beans.PropertyChangeEvent} firing is being deferred.
         *
         * @param connection The database {@link Connection}.
         * @return The {@link ModelEvent} for a successful task completion. This should never return a {@code null} value.
         * @throws Exception if an un-handled exception occurred during the task operation.
         */
        protected abstract Optional<R> call(Connection connection) throws Exception;

        @Override
        @SuppressWarnings("try")
        protected final Optional<R> call() throws Exception {
            synchronized (getFxRecordModel()) {
                if (originalRowState != ((DataAccessObject) getDataAccessObject()).rowState) {
                    throw new IllegalStateException("Row state has changed");
                }
                Optional<R> result = onBeforeDbConnect();
                if (result.isPresent()) {
                    return result;
                }
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
                try (DbConnector dbConnector = new DbConnector()) {
                    updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                    if (!isCancelled()) {
                        try (ChangeEventDeferral eventDeferral = ((DataAccessObject) dataAccessObject).deferChangeEvents()) {
                            synchronized (dataAccessObject) {
                                result = call(dbConnector.getConnection());
                                if (null != result && !isCancelled()) {
                                    return result;
                                }
                            }
                        }
                    }
                }
            }
            return Optional.empty();
        }

        protected abstract Optional<R> onBeforeDbConnect();
    }

    /**
     * A {@link DaoTask} that allows for validation before the actual operation is performed. This provides an opened database {@link Connection} and
     * defers the firing of {@link java.beans.PropertyChangeEvent}s on the target {@link DataAccessObject}. When completed, {@link #getValue()} will
     * return the validation message if validation has failed. If this is null, empty or contains only whitespace, then that indicates that validation
     * has succeeded and that the {@link #onValidated(Connection)} has been invoked, unless the task itself has failed or has been canceled.
     *
     * @param <D> The type of the target {@link DataAccessObject}.
     * @param <M> The type of associated {@link FxRecordModel}, if applicable.
     */
    public static abstract class ValidatingDaoTask<D extends DataAccessObject, M extends FxRecordModel<D>> extends DaoTask<D, M, String> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ValidatingDaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(ValidatingDaoTask.class.getName());

        private final DaoFactory<D> daoFactory;
        private final FxRecordModel.FxModelFactory<D, M> modelFactory;
        private boolean validationSuccessful;

        /**
         * Creates a new {@code ValidatingDaoTask} for the {@link FxRecordModel#dataObject DataAccessObject} of a {@link FxRecordModel}.
         *
         * @param fxRecordModel The {@link FxRecordModel} that contains the target {@link DataAccessObject}.
         * @param modelFactory The {@link FxRecordModel.FxModelFactory} associated with the source {@link FxRecordModel} type.
         * {@link #validate(Connection)} to perform validation.
         */
        protected ValidatingDaoTask(M fxRecordModel, FxRecordModel.FxModelFactory<D, M> modelFactory) {
            super(fxRecordModel);
            daoFactory = (this.modelFactory = modelFactory).getDaoFactory();
            validationSuccessful = false;
        }

        /**
         * Gets the {@link DaoFactory} associated with the target {@link DataAccessObject} type.
         *
         * @return The {@link DaoFactory} associated with the target {@link DataAccessObject} type.
         */
        public DaoFactory<D> getDaoFactory() {
            return daoFactory;
        }

        /**
         * Gets the {@link FxRecordModel.FxModelFactory} associated with the source {@link FxRecordModel} type.
         *
         * @return The {@link FxRecordModel.FxModelFactory} associated with the source {@link FxRecordModel} type or {@code null} if a
         * {@link FxRecordModel} was not specified in the constructor.
         */
        public FxRecordModel.FxModelFactory<D, M> getModelFactory() {
            return modelFactory;
        }

        @Override
        protected final Optional<String> call(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "call", connection);
            LOG.fine("Validating");
            String message = validate(connection);
            if (!isCancelled()) {
                validationSuccessful = Values.isNullWhiteSpaceOrEmpty(message);
                LOG.fine("Validated");
                if (validationSuccessful) {
                    onValidated(connection);
                } else {
                    return Optional.of(message);
                }
            }
            return Optional.empty();
        }

        /**
         * This gets called to validateProperties the current {@link DataAccessObject}.
         *
         * @param connection The opened database {@link Connection}.
         * @return The validation event, which can be {@code null} if the target {@link DataAccessObject} is valid.
         * @throws Exception if unable to perform validation.
         */
        protected abstract String validate(Connection connection) throws Exception;

        /**
         * This gets called after validation is successful.
         *
         * @param connection The opened database {@link Connection}.
         * @throws Exception if unable to complete the operation.
         */
        protected abstract void onValidated(Connection connection) throws Exception;

    }

    /**
     * A {@link ValidatingDaoTask} which saves the target {@link DataAccessObject} to the database. This provides an opened database
     * {@link Connection} and defers the firing of {@link java.beans.PropertyChangeEvent}s on the target {@link DataAccessObject}. When completed,
     * {@link #getValue()} will return the validation message if validation has failed. If this is null, empty or contains only whitespace, then that
     * indicates that validation has succeeded and that changes have been saved to the database, unless the task itself has failed or has been
     * canceled. If successful, the target {@link FxRecordModel#rowState} will be set to {@link DataRowState#UNMODIFIED}.
     *
     * @param <D> The type of the target {@link DataAccessObject} to be saved.
     * @param <M> The type of associated {@link FxRecordModel}, if applicable.
     */
    public static abstract class SaveDaoTask<D extends DataAccessObject, M extends FxRecordModel<D>> extends ValidatingDaoTask<D, M> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(SaveDaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(SaveDaoTask.class.getName());
        private boolean force;
        private int originalPk;

        /**
         * Creates a new {@code SaveDaoTask} for the {@link FxRecordModel#dataObject DataAccessObject} of a {@link FxRecordModel}.
         *
         * @param fxRecordModel The {@link FxRecordModel} that contains the target {@link DataAccessObject}.
         * @param modelFactory The {@link FxRecordModel.FxModelFactory} associated with the source {@link FxRecordModel} type.
         * @param force {@code true} to force the save operation even if {@link FxRecordModel#changed} is {@code false}. {@link #validate(Connection)}
         * to perform validation.
         * @throws IllegalArgumentException if {@link DataAccessObject#rowState} for the {@code fxRecordModel} is {@link DataRowState#DELETED}.
         */
        protected SaveDaoTask(M fxRecordModel, FxRecordModel.FxModelFactory<D, M> modelFactory, boolean force) {
            super(fxRecordModel, modelFactory);
            if (getOriginalRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Record was already deleted");
            }
            originalPk = fxRecordModel.getPrimaryKey();
            this.force = force;
        }

        @Override
        protected Optional<String> onBeforeDbConnect() {
            if (force || getFxRecordModel().isChanged()) {
                return Optional.empty();
            }
            return Optional.of("");
        }

        /**
         * This gets called to update the {@link FxRecordModel#dataObject} with the changes to the {@link FxRecordModel}.
         *
         * @param model The {@link FxRecordModel} that wraps the {@link DataAccessObject} to be updated.
         */
        protected abstract void updateDataAccessObject(M model);

        @Override
        protected final void onValidated(Connection connection) throws Exception {
            D dao = getDataAccessObject();
            DataAccessObject dataObj = (DataAccessObject) dao;
            DaoFactory<D> factory = getDaoFactory();
            M model = getFxRecordModel();
            String n = Scheduler.getCurrentUser().getUserName();
            Timestamp ts = DB.toUtcTimestamp(LocalDateTime.now());
            if (model.getRowState() == DataRowState.NEW) {
                dataObj.setCreatedBy(n);
                dataObj.setCreateDate(ts);
            }
            dataObj.setLastModifiedBy(n);
            dataObj.setLastModifiedDate(ts);
            updateDataAccessObject(model);

            StringBuilder sb = new StringBuilder();
            DbColumn[] columns;
            Iterator<DbColumn> iterator;
            DbName dbName;
            int colNum = 0;
            if (getOriginalRowState() == DataRowState.NEW) {
                columns = SchemaHelper.getTableColumns(factory.getDbTable(),
                        (t) -> t.getUsageCategory() != ColumnCategory.PRIMARY_KEY).toArray(DbColumn[]::new);
                iterator = Arrays.stream(columns).iterator();
                sb.append("INSERT INTO ").append(factory.getDbTable().getDbName()).append(" (")
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
                columns = SchemaHelper.getTableColumns(factory.getDbTable(), (t) -> SchemaHelper.isUpdatable(t)).toArray(DbColumn[]::new);
                iterator = Arrays.stream(columns).iterator();
                dbName = iterator.next().getDbName();
                sb.append("UPDATE ").append(factory.getDbTable().getDbName()).append(" SET ");
                LOG.fine(String.format("Appending column SQL for column %s at index %d", dbName, ++colNum));
                sb.append(dbName).append("=?");
                while (iterator.hasNext()) {
                    dbName = iterator.next().getDbName();
                    LOG.fine(String.format("Appending column SQL for %s at index %d", dbName, ++colNum));
                    sb.append(", ").append(dbName).append("=?");
                }
                dbName = factory.getPrimaryKeyColumn().getDbName();
                LOG.fine(String.format("Appending column SQL for %s at index %d", dbName, ++colNum));
                sb.append(" WHERE ").append(dbName).append("=?");
            }
            String sql = sb.toString();
            try (PreparedStatement ps = (getOriginalRowState() == DataRowState.NEW)
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
                                    ps.setTimestamp(index++, dataObj.getCreateDate());
                                    break;
                                case CREATED_BY:
                                    ps.setString(index++, dataObj.getCreatedBy());
                                    break;
                                case LAST_UPDATE:
                                    ps.setTimestamp(index++, dataObj.getLastModifiedDate());
                                    break;
                                case LAST_UPDATE_BY:
                                    ps.setString(index++, dataObj.getLastModifiedBy());
                                    break;
                                default:
                                    LogHelper.logWarnings(connection, LOG);
                                    throw new InternalException(String.format("Unexpected AUDIT column name %s", column.getDbName()));
                            }
                        } else {
                            factory.applyColumnValue(dao, column, ps, index++);
                        }
                    } catch (SQLException ex) {
                        LogHelper.logWarnings(connection, LOG);
                        throw new Exception(String.format("Error setting value for column %s", column.getDbName()), ex);
                    }
                } while (iterator.hasNext());
                if (getOriginalRowState() != DataRowState.NEW) {
                    try {
                        LOG.fine(String.format("Setting value primary key at index %d", index));
                        ps.setInt(index, dataObj.getPrimaryKey());
                    } catch (SQLException ex) {
                        LogHelper.logWarnings(connection, LOG);
                        throw new Exception("Error setting value for primary key column", ex);
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                if (ps.executeUpdate() < 1) {
                    LogHelper.logWarnings(connection, LOG);
                    throw new Exception("No database changes as result of query");
                }
                LogHelper.logWarnings(connection, LOG);
                if (getOriginalRowState() == DataRowState.NEW) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No primary key returned");
                        }
                        // FIXME: Make sure change events get fired
                        dataObj.primaryKey = rs.getInt(1);
                        dataObj.rowState = DataRowState.UNMODIFIED;
                        factory.dataObjectCache.put(dao);
                    } catch (SQLException ex) {
                        LogHelper.logWarnings(connection, LOG);
                        throw new Exception("Error getting new primary key value", ex);
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            } catch (SQLException ex) {
                LogHelper.logWarnings(connection, LOG);
                LOG.log(Level.SEVERE, String.format("Error executing DML statement: %s", sql), ex);
                throw new Exception("Error executing DML statement", ex);
            }
        }

        @Override
        protected void succeeded() {
            DataAccessObject obj = (DataAccessObject) getDataAccessObject();
            String result = getValue().orElse(null);
            if (null != result) {
                obj.rejectChanges();
                LOG.fine(() -> String.format("Task succeeded, but invalid: %s", result));
            } else {
                LOG.fine("Task succeeded");
                obj.acceptChanges();
                obj.rowState = DataRowState.UNMODIFIED;
                obj.firePropertyChange(PROP_PRIMARYKEY, originalPk, obj.primaryKey);
                obj.firePropertyChange(PROP_ROWSTATE, getOriginalRowState(), obj.rowState);
            }
            super.succeeded();
        }

        @Override
        protected void cancelled() {
            ((DataAccessObject) getDataAccessObject()).rejectChanges();
            super.cancelled();
        }

        @Override
        protected void failed() {
            ((DataAccessObject) getDataAccessObject()).rejectChanges();
            super.failed();
        }

    }

    /**
     * A {@link ValidatingDaoTask} which deletes the target {@link DataAccessObject} from the database. This provides an opened database
     * {@link Connection} and defers the firing of {@link java.beans.PropertyChangeEvent}s on the target {@link DataAccessObject}. When completed, the
     * {@link #finalEvent} is fired on the {@link DeleteDaoTask} and the target {@link DataAccessObject}. The {@link ModelEvent} produced by this task
     * will be for successful completions as well as validation errors. {@link ModelEvent}s are also produced for task failures and cancellations. If
     * successful, the target {@link DataAccessObject#rowState} will be set to {@link DataRowState#DELETED}.
     *
     * @param <D> The type of the target {@link DataAccessObject} to be saved.
     * @param <M> The type of associated {@link FxRecordModel}, if applicable.
     */
    public static abstract class DeleteDaoTask<D extends DataAccessObject, M extends FxRecordModel<D>> extends ValidatingDaoTask<D, M> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DeleteDaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DeleteDaoTask.class.getName());

        /**
         * Creates a new {@code DeleteDaoTask} for the {@link FxRecordModel#dataObject DataAccessObject} of a {@link FxRecordModel}.
         *
         * @param fxRecordModel The {@link FxRecordModel} that contains the target {@link DataAccessObject}.
         * @param modelFactory The {@link FxRecordModel.FxModelFactory} associated with the source {@link FxRecordModel} type.
         * @throws IllegalStateException if {@link DataAccessObject#rowState} for the {@code fxRecordModel} is {@link DataRowState#DELETED} or
         * {@link DataRowState#NEW}.
         */
        @SuppressWarnings("incomplete-switch")
        protected DeleteDaoTask(M fxRecordModel, FxRecordModel.FxModelFactory<D, M> modelFactory) {
            super(fxRecordModel, modelFactory);
            switch (getOriginalRowState()) {
                case DELETED:
                    throw new IllegalArgumentException("Record was already deleted");
                case NEW:
                    throw new IllegalArgumentException("Record was never saved");
            }
        }

        @Override
        protected void onValidated(Connection connection) throws Exception {
            D dao = getDataAccessObject();
            DaoFactory<D> factory = getDaoFactory();
            StringBuilder sb = new StringBuilder("DELETE FROM ");
            sb.append(factory.getDbTable().getDbName()).append(" WHERE ").append(factory.getPrimaryKeyColumn().getDbName()).append("=?");
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, ((DataAccessObject) dao).getPrimaryKey());
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                if (ps.executeUpdate() < 1) {
                    LogHelper.logWarnings(connection, LOG);
                    throw new Exception("No database changes as result of query");
                }
                LogHelper.logWarnings(connection, LOG);
            }
            DataAccessObject obj = (DataAccessObject) dao;
            obj.acceptChanges();
        }

        @Override
        protected void succeeded() {
            DataAccessObject obj = (DataAccessObject) getDataAccessObject();
            String result = getValue().orElse(null);
            if (null == result) {
                LOG.fine("Task succeeded");
                obj.acceptChanges();
                obj.rowState = DataRowState.DELETED;
                obj.firePropertyChange(PROP_ROWSTATE, getOriginalRowState(), obj.rowState);
            } else {
                LOG.fine(() -> String.format("Task succeeded, but invalid: %s", result));
                obj.rejectChanges();
            }
            super.succeeded();
        }

        @Override
        protected void cancelled() {
            ((DataAccessObject) getDataAccessObject()).rejectChanges();
            super.cancelled();
        }

        @Override
        protected void failed() {
            ((DataAccessObject) getDataAccessObject()).rejectChanges();
            super.failed();
        }

        @Override
        protected Optional<String> onBeforeDbConnect() {
            return Optional.empty();
        }

    }

    private class OriginalValues {

        private Timestamp createDate;
        private String createdBy;
        private Timestamp lastModifiedDate;
        private String lastModifiedBy;

        private OriginalValues() {
            this.createDate = DataAccessObject.this.getCreateDate();
            this.createdBy = DataAccessObject.this.getCreatedBy();
            this.lastModifiedDate = DataAccessObject.this.getLastModifiedDate();
            this.lastModifiedBy = DataAccessObject.this.getLastModifiedBy();
        }
    }
}
