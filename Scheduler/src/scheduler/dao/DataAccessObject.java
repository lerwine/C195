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
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;
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
     * Registers a {@link ModelEvent} handler in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Switch to ModelEvent
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
            void addEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    /**
     * Registers a {@link ModelEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Switch to ModelEvent
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
            void addEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    /**
     * Unregisters a {@link ModelEvent} handler in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Switch to ModelEvent
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
            void removeEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    /**
     * Unregisters a {@link ModelEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    // FIXME: Switch to ModelEvent
    public <E extends ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>>
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

        private final DaoFactory<T, ? extends ModelEvent<T, ? extends FxRecordModel<T>>> factory;
        private final DaoFilter<T> filter;
        private final Consumer<List<T>> onSuccess;
        private final Consumer<Throwable> onFail;

        LoadTask(DaoFactory<T, ? extends ModelEvent<T, ? extends FxRecordModel<T>>> factory, DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
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

    // FIXME: Switch to ModelEvent
    /**
     * Base factory class for {@link DataAccessObject} objects.
     *
     * @param <T> The type of {@link DataAccessObject} object supported.
     * @param <E> The {@link ModelEvent} type.
     */
    public static abstract class DaoFactory<T extends DataAccessObject, E extends ModelEvent<T, ? extends FxRecordModel<T>>> implements EventTarget {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DaoFactory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DaoFactory.class.getName());
        private final EventHandlerManager eventHandlerManager;
        private final DataObjectCache<T> dataObjectCache = new DataObjectCache<>();

        protected DaoFactory() {
            eventHandlerManager = new EventHandlerManager(this);
        }

        /**
         * Loads items from the database. {@link #save(scheduler.events.ModelEvent, java.sql.Connection, boolean)}
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
         * Registers a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void addEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.addEventHandler(type, eventHandler);
        }

        /**
         * Registers a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void addEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.addEventFilter(type, eventHandler);
        }

        /**
         * Unregisters a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void removeEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.removeEventHandler(type, eventHandler);
        }

        /**
         * Unregisters a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final void removeEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
            eventHandlerManager.removeEventFilter(type, eventHandler);
        }

    }

    /**
     * Background task which provides an opened database {@link Connection} and defers the firing of {@link java.beans.PropertyChangeEvent}s on a
     * {@link DataAccessObject}.
     *
     * @param <D> The target {@link DataAccessObject} type.
     * @param <M> The associated {@link FxRecordModel} type.
     * @param <E> The result {@link ModelEvent} type.
     */
    public static abstract class DaoTask<D extends DataAccessObject, M extends FxRecordModel<D>, E extends ModelEvent<D, M>> extends Task<E> {

        private final ReadOnlyObjectWrapper<D> dataAccessObject;
        private final ReadOnlyObjectWrapper<M> fxRecordModel;

        /**
         *
         * @param fxRecordModel The {@link FxRecordModel} that wraps the target {@link DataAccessObject}.
         */
        protected DaoTask(M fxRecordModel) {
            dataAccessObject = new ReadOnlyObjectWrapper<>(this, "dataAccessObject", fxRecordModel.dataObject());
            this.fxRecordModel = new ReadOnlyObjectWrapper<>(this, "fxRecordModel", fxRecordModel);
        }

        /**
         *
         * @param dataAccessObject The target {@link DataAccessObject}.
         */
        protected DaoTask(D dataAccessObject) {
            this.dataAccessObject = new ReadOnlyObjectWrapper<>(this, "dataAccessObject", Objects.requireNonNull(dataAccessObject));
            fxRecordModel = new ReadOnlyObjectWrapper<>(this, "fxRecordModel", null);
        }

        /**
         * Gets the target {@link DataAccessObject}.
         *
         * @return The target {@link DataAccessObject}.
         */
        public D getDataAccessObject() {
            return dataAccessObject.get();
        }

        public ReadOnlyObjectProperty<D> dataAccessObjectProperty() {
            return dataAccessObject.getReadOnlyProperty();
        }

        /**
         * Gets the {@link FxRecordModel} that wraps the target {@link DataAccessObject}.
         *
         * @return The {@link FxRecordModel} that wraps the target {@link DataAccessObject} or {@code null} if only the target
         * {@link DataAccessObject} was provided to this task.
         */
        public M getFxRecordModel() {
            return fxRecordModel.get();
        }

        public ReadOnlyObjectProperty<M> fxRecordModelProperty() {
            return fxRecordModel.getReadOnlyProperty();
        }

        /**
         * Invoked when the database {@link Connection} is opened and {@link java.beans.PropertyChangeEvent} firing is being deferred.
         *
         * @param connection The database {@link Connection}.
         * @return The {@link ModelEvent} for a successful task completion.
         * @throws Exception if an un-handled exception occurred during the task operation.
         */
        protected abstract E call(Connection connection) throws Exception;

        @Override
        protected E call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                if (isCancelled()) {
                    return null;
                }
                try (ChangeEventDeferral eventDeferral = ((DataAccessObject) dataAccessObject.get()).deferChangeEvents()) {
                    synchronized (dataAccessObject) {
                        E event = call(dbConnector.getConnection());
                        if (null == event && !isCancelled()) {
                            throw new IllegalAccessException("No result event was produced");
                        }
                        return event;
                    }
                }
            }
        }

    }

    public static abstract class ValidatingDaoTask<D extends DataAccessObject, M extends FxRecordModel<D>, E extends ModelEvent<D, M>> extends DaoTask<D, M, E> {

        private final ReadOnlyObjectWrapper<DaoFactory<D, E>> daoFactory;
        private final ReadOnlyObjectWrapper<FxRecordModel.ModelFactory<D, M, E>> modelFactory;
        private final ReadOnlyObjectWrapper<E> finalEvent;
        private boolean validationSuccessful;
        private DataRowState originalRowState;

        // FIXME: ModelFactory needs to use same event type
        protected ValidatingDaoTask(M fxRecordModel, FxRecordModel.ModelFactory<D, M, E> modelFactory, boolean alreadyValidated) {
            super(fxRecordModel);
            daoFactory = new ReadOnlyObjectWrapper<>(modelFactory.getDaoFactory());
            this.modelFactory = new ReadOnlyObjectWrapper<>(modelFactory);
            validationSuccessful = alreadyValidated;
            validationFailed = new ReadOnlyBooleanWrapper(!alreadyValidated);
            originalRowState = getDataAccessObject().getRowState();
            finalEvent = new ReadOnlyObjectWrapper<>(null);
        }

        // FIXME: DaoFactory needs to use same event type
        protected ValidatingDaoTask(D dataAccessObject, DaoFactory<D, E> daoFactory, boolean alreadyValidated) {
            super(dataAccessObject);
            originalRowState = getDataAccessObject().getRowState();
            this.daoFactory = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(daoFactory));
            this.modelFactory = new ReadOnlyObjectWrapper<>(null);
            validationSuccessful = alreadyValidated;
            validationFailed = new ReadOnlyBooleanWrapper(!alreadyValidated);
            finalEvent = new ReadOnlyObjectWrapper<>(null);
        }
        private final ReadOnlyBooleanWrapper validationFailed;

        public boolean isValidationFailed() {
            return validationFailed.get();
        }

        public ReadOnlyBooleanProperty validationFailedProperty() {
            return validationFailed.getReadOnlyProperty();
        }

        protected final DataRowState getOriginalRowState() {
            return originalRowState;
        }

        public DaoFactory<D, E> getDaoFactory() {
            return daoFactory.get();
        }

        public ReadOnlyObjectProperty<DaoFactory<D, E>> daoFactoryProperty() {
            return daoFactory.getReadOnlyProperty();
        }

        public FxRecordModel.ModelFactory<D, M, E> getModelFactory() {
            return modelFactory.get();
        }

        public ReadOnlyObjectProperty<FxRecordModel.ModelFactory<D, M, E>> modelFactoryProperty() {
            return modelFactory.getReadOnlyProperty();
        }

        public E getFinalEvent() {
            return finalEvent.get();
        }

        public ReadOnlyObjectProperty<E> finalEventProperty() {
            return finalEvent.getReadOnlyProperty();
        }

        @Override
        protected final E call() throws Exception {
            if (originalRowState != ((DataAccessObject) getDataAccessObject()).rowState) {
                throw new IllegalStateException("Row state has changed");
            }
            return super.call();
        }

        @Override
        protected final E call(Connection connection) throws Exception {
            if (!validationSuccessful) {
                validate(connection);
                validationSuccessful = true;
            }
            return onValidated(connection);
        }

        /**
         * Validates the current {@link DataAccessObject}.
         *
         * @param connection
         * @throws Exception if unable to perform validation.
         */
        protected abstract void validate(Connection connection) throws Exception;

        protected abstract E onValidated(Connection connection) throws Exception;

        protected abstract E createUnhandledExceptionEvent(Throwable fault);

        protected abstract E createCancelledEvent();

        protected abstract E createValidationFailureEvent(ValidationFailureException ex);

        @Override
        protected void cancelled() {
            super.cancelled();
            E event = createCancelledEvent();
            finalEvent.set(event);
            onFinished(event);
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable ex = getException();
            E event;
            if (validationFailed.get() && ex instanceof ValidationFailureException) {
                event = createValidationFailureEvent((ValidationFailureException) ex);
            } else {
                event = createUnhandledExceptionEvent(ex);
            }
            finalEvent.set(event);
            onFinished(event);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            E event = getValue();
            finalEvent.set(event);
            onFinished(event);
        }

        protected void onFinished(E event) {

        }
    }

    public static abstract class SaveDaoTask<D extends DataAccessObject, M extends FxRecordModel<D>, E extends ModelEvent<D, M>> extends ValidatingDaoTask<D, M, E> {

        protected SaveDaoTask(M fxRecordModel, FxRecordModel.ModelFactory<D, M, E> modelFactory, boolean alreadyValidated) {
            super(fxRecordModel, modelFactory, alreadyValidated);
            if (getOriginalRowState() == DataRowState.DELETED) {
                throw new IllegalStateException("Record was already deleted");
            }
        }

        protected SaveDaoTask(D dataAccessObject, DaoFactory<D, E> daoFactory, boolean alreadyValidated) {
            super(dataAccessObject, daoFactory, alreadyValidated);
            if (getOriginalRowState() == DataRowState.DELETED) {
                throw new IllegalStateException("Record was already deleted");
            }
        }

        protected abstract E createSuccessEvent();

        @Override
        protected final E onValidated(Connection connection) throws Exception {
            D dao = getDataAccessObject();
            DaoFactory<D, E> factory = getDaoFactory();
            DataAccessObject dataObj = (DataAccessObject) dao;
            Timestamp timeStamp = DB.toUtcTimestamp(LocalDateTime.now());
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
                        ps.setInt(index, dataObj.primaryKey);
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
                        dataObj.primaryKey = rs.getInt(1);
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
            return createSuccessEvent();
        }

        @Override
        protected void succeeded() {
            DataAccessObject obj = (DataAccessObject) getDataAccessObject();
            obj.acceptChanges();
            obj.rowState = DataRowState.UNMODIFIED;
            obj.firePropertyChange(PROP_ROWSTATE, getOriginalRowState(), obj.rowState);
            super.succeeded();
        }

    }

    public static abstract class DeleteDaoTask<D extends DataAccessObject, M extends FxRecordModel<D>, E extends ModelEvent<D, M>> extends ValidatingDaoTask<D, M, E> {

        protected DeleteDaoTask(M fxRecordModel, FxRecordModel.ModelFactory<D, M, E> modelFactory, boolean alreadyValidated) {
            super(fxRecordModel, modelFactory, alreadyValidated);
            switch (getOriginalRowState()) {
                case DELETED:
                    throw new IllegalStateException("Record was already deleted");
                case NEW:
                    throw new IllegalStateException("Record was never saved");
            }
        }

        protected DeleteDaoTask(D dataAccessObject, DaoFactory<D, E> daoFactory, boolean alreadyValidated) {
            super(dataAccessObject, daoFactory, alreadyValidated);
            switch (getOriginalRowState()) {
                case DELETED:
                    throw new IllegalStateException("Record was already deleted");
                case NEW:
                    throw new IllegalStateException("Record was never saved");
            }
        }

        @Override
        protected E onValidated(Connection connection) throws Exception {
            D dao = getDataAccessObject();
            DaoFactory<D, E> factory = getDaoFactory();
            StringBuilder sb = new StringBuilder("DELETE FROM ");
            sb.append(factory.getDbTable().getDbName()).append(" WHERE ").append(factory.getPrimaryKeyColumn().getDbName()).append("=?");
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, ((DataAccessObject) dao).primaryKey);
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                if (ps.executeUpdate() < 1) {
                    LogHelper.logWarnings(connection, LOG);
                    throw new Exception("No database changes as result of query");
                }
                LogHelper.logWarnings(connection, LOG);
            }
            DataAccessObject obj = (DataAccessObject) dao;
            obj.acceptChanges();
            obj.rowState = DataRowState.DELETED;
            dao.firePropertyChange(PROP_ROWSTATE, getOriginalRowState(), obj.rowState);
            return createSuccessEvent();
        }

        protected abstract E createSuccessEvent();

        @Override
        protected void succeeded() {
            DataAccessObject obj = (DataAccessObject) getDataAccessObject();
            obj.acceptChanges();
            obj.rowState = DataRowState.DELETED;
            obj.firePropertyChange(PROP_ROWSTATE, getOriginalRowState(), obj.rowState);
            super.succeeded();
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
