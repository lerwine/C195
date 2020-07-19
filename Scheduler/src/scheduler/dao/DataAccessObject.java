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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
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
import scheduler.events.ModelFailedEvent;
import scheduler.model.DataEntity;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialEntityModel;
import scheduler.model.fx.PartialModel;
import scheduler.util.AnnotationHelper;
import scheduler.util.DateTimeUtil;
import scheduler.util.DbConnector;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.view.MainController;
import scheduler.view.task.WaitBorderPane;

/**
 * Data access object that represents all columns from a data row.
 * <p>
 * Classes that inherit from this must use the {@link scheduler.dao.schema.DatabaseTable} annotation to indicate which data table they represent. Each class must also have an
 * associated factory singleton instance that inherits from {@link DaoFactory} that can be retrieved using a static {@code getFactory()} method.</p>
 * <p>
 * The current {@link MainController} (if initialized) will be included in the event dispatch chain for events fired on this object.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class DataAccessObject extends PropertyBindable implements PartialDataAccessObject, DataEntity<Timestamp>, EventTarget {

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
        lastModifiedDate = createDate = DateTimeUtil.toUtcTimestamp(LocalDateTime.now());
        lastModifiedBy = createdBy = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
        rowState = DataRowState.NEW;
        originalValues = new OriginalValues();
    }

    public abstract EntityModel<? extends DataAccessObject> cachedModel(boolean create);

    /**
     * This gets called after the associated record in the database as been successfully inserted, updated or deleted. {@link PropertyChangeEvent}s will be deferred while this is
     * invoked.
     */
    protected abstract void onAcceptChanges();

    final void acceptChanges() {
        onAcceptChanges();
        originalValues.createDate = createDate;
        originalValues.createdBy = createdBy;
        originalValues.lastModifiedDate = lastModifiedDate;
        originalValues.lastModifiedBy = lastModifiedBy;
    }

    /**
     * This gets called when property changes are rejected and are to be restored to their original values. {@link PropertyChangeEvent}s will be deferred while this is invoked.
     */
    protected abstract void onRejectChanges();

    /**
     * Rejects property value changes changes and reverts them back to their original values.
     */
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
     * @return {@code true} if the property change should change a {@link #rowState} of {@link DataRowState#UNMODIFIED} to {@link DataRowState#MODIFIED}; otherwise, {@code false}
     * to leave {@link #rowState} unchanged.
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
            lastModifiedDate = DateTimeUtil.toUtcTimestamp(LocalDateTime.now());
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
    public <E extends ModelEvent<? extends DataAccessObject, ? extends EntityModel<? extends DataAccessObject>>>
            void addEventHandler(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    /**
     * Registers a {@link ModelEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public <E extends ModelEvent<? extends DataAccessObject, ? extends EntityModel<? extends DataAccessObject>>>
            void addEventFilter(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    /**
     * Unregisters a {@link ModelEvent} handler in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public <E extends ModelEvent<? extends DataAccessObject, ? extends EntityModel<? extends DataAccessObject>>>
            void removeEventHandler(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    /**
     * Unregisters a {@link ModelEvent} filter in the {@code EventHandlerManager} for the current {@link DataAccessObject}.
     *
     * @param <E> The type of {@link ModelEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public <E extends ModelEvent<? extends DataAccessObject, ? extends EntityModel<? extends DataAccessObject>>>
            void removeEventFilter(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        return tail.append(eventHandlerManager);
    }

    /**
     * Resets {@link #rowState} to {@link DataRowState#NEW} if the current state is {@link DataRowState#DELETED}; otherwise, this has no effect.
     */
    public synchronized void resetRowState() {
        if (rowState == DataRowState.DELETED) {
            primaryKey = Integer.MIN_VALUE;
            lastModifiedDate = createDate = DateTimeUtil.toUtcTimestamp(LocalDateTime.now());
            lastModifiedBy = createdBy = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
            rowState = DataRowState.NEW;
        }
    }

    private static class LoadTask<T extends DataAccessObject> extends Task<List<T>> {

        private final DaoFactory<T, ? extends ModelEvent<T, ? extends EntityModel<T>>> factory;
        private final DaoFilter<T> filter;
        private final Consumer<List<T>> onSuccess;
        private final Consumer<Throwable> onFail;

        LoadTask(DaoFactory<T, ? extends ModelEvent<T, ? extends EntityModel<T>>> factory, DaoFilter<T> filter, Consumer<List<T>> onSuccess,
                Consumer<Throwable> onFail) {
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

    /**
     * Used by {@link DaoFactory} to keep a weakly-referenced cache of {@link DataAccessObject}s. The intent is to only have one instance of a {@link DataAccessObject} for each
     * database record. This allows all related data references to be automatically updated whenever the data record itself is updated.
     * <p>
     * For instance, the {@link scheduler.model.fx.AppointmentModel} class has an {@link scheduler.model.fx.AppointmentModel#effectiveLocation effectiveLocation} property, which is
     * a calculated property where the calculation algorithm changes based upon the value of the {@link scheduler.model.fx.AppointmentModel#type type} property. Sometimes, the
     * algorithm uses properties from the {@link scheduler.model.fx.PartialCustomerModel} referenced by the {@link scheduler.model.fx.AppointmentModel#customer customer} property.
     * It is possible to have a navigation path in which the {@link scheduler.model.fx.CustomerModel#address} property is modified, and the application user will be eventually
     * returned to a scene that has a listing of appointments where one of the columns displays the {@link scheduler.model.fx.AppointmentModel#effectiveLocation effectiveLocation},
     * with one or more of them displaying the customer's address. By using cached objects, it is much easier to ensure that the customer address in the columns of the parent
     * listing is automatically updated after any change.</p>
     * This class was created (versus just using a {@link java.util.WeakHashMap} for the following reasons:
     * <ol>
     * <li>The mapping is keyed by the value of the {@link DataAccessObject#primaryKey primaryKey}. Items which have not yet been added to the database will not have a primary key,
     * so I wanted to prevent items with a {@link DataRowState#NEW} {@link DataAccessObject#rowState rowState} from being added.</li>
     * <li>I want to be sure that it's not possible to add a {@link DataAccessObject} to the hash where they key is not the same as the value of the
     * {@link DataAccessObject#primaryKey primaryKey} property.</li>
     * <li>The key values of a {@link java.util.WeakHashMap} are weakly referenced as well, and integers are not reference types.</li>
     * <li>Only the {@link DaoFactory} that created the instance should be able to add items to it.</li>
     * </ol>
     *
     * @param <T> The type of {@link DataAccessObject} being cached.
     */
    public static class DaoCache<T extends DataAccessObject> {

        private final HashMap<Integer, WeakReference<T>> backingMap = new HashMap<>();

        private DaoCache() {
        }

        /**
         * Gets the {@link DataAccessObject} whose {@link DataAccessObject#primaryKey} matches a specified value.
         *
         * @param key The value of the {@link DataAccessObject#primaryKey} to look up.
         * @return The {@link DataAccessObject} whose {@link DataAccessObject#primaryKey} matches the specified {@code key} or {@code null} if no such object exists in the
         * {@code DaoCache}.
         */
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

        /**
         * Gets the {@link DataAccessObject} whose {@link DataAccessObject#primaryKey} matches a specified value, adding a new object if needed.
         *
         * @param key The value of the {@link DataAccessObject#primaryKey} to look up.
         * @param orElse This gets invoked if no matching {@link DataAccessObject} is found, whereby the result object will be added to the cache.
         * @return The {@link DataAccessObject} whose {@link DataAccessObject#primaryKey} matches the specified {@code key} or the return value from the {@code orElse} callback no
         * such object existed in the {@code DaoCache}.
         */
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

        public synchronized Stream<T> findAll(Predicate<T> filter) {
            return backingMap.keySet().stream().map((t) -> {
                if (backingMap.containsKey(t)) {
                    T dao = backingMap.get(t).get();
                    if (null != dao) {
                        return dao;
                    }
                    backingMap.remove(t);
                }

                return null;
            }).filter((t) -> null != t && filter.test(t));
        }

        public synchronized <U> Stream<U> map(Function<T, U> mapper) {
            return backingMap.keySet().stream().map((t) -> {
                if (backingMap.containsKey(t)) {
                    T dao = backingMap.get(t).get();
                    if (null != dao) {
                        return dao;
                    }
                    backingMap.remove(t);
                }
                return null;
            }).filter((t) -> null != t).map(mapper);
        }

        public synchronized Optional<T> findFirst(Predicate<T> filter) {
            Iterator<Integer> iterator = backingMap.keySet().stream().iterator();
            while (iterator.hasNext()) {
                int key = iterator.next();
                if (backingMap.containsKey(key)) {
                    T dao = backingMap.get(key).get();
                    if (null != dao) {
                        return Optional.of(dao);
                    }
                    backingMap.remove(key);
                }
            }
            return Optional.empty();
        }

        private synchronized void put(T item) {
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
     * Base factory class for CRUD operations on {@link DataAccessObject} objects. This maintains a {@link WeakReference} cache of loaded {@link DataAccessObject}s so that there
     * will only ever be one instance of a {@link DataAccessObject} for each record in the database.
     *
     * @param <D> The type of {@link DataAccessObject} object supported.
     * @param <E> The {@link ModelEvent} type.
     */
    public static abstract class DaoFactory<D extends DataAccessObject, E extends ModelEvent<D, ? extends EntityModel<D>>>
            implements EventTarget {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DaoFactory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DaoFactory.class.getName());

        private final EventHandlerManager eventHandlerManager;
        private final DaoCache<D> cache = new DaoCache<>();

        protected DaoFactory() {
            eventHandlerManager = new EventHandlerManager(this);
        }

        public DaoCache<D> getCache() {
            return cache;
        }

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
        public final Task<List<D>> loadAsync(WaitBorderPane waitBorderPane, DaoFilter<D> filter, Consumer<List<D>> onSuccess,
                Consumer<Throwable> onFail) {
            LoadTask<D> task = new LoadTask<>(this, filter, onSuccess, onFail);
            waitBorderPane.startNow(task);
            return task;
        }

        public final Task<List<D>> loadAsync(DaoFilter<D> filter, Consumer<List<D>> onSuccess, Consumer<Throwable> onFail) {
            LoadTask<D> task = new LoadTask<>(this, filter, onSuccess, onFail);
            MainController.startBusyTaskNow(task);
            return task;
        }

        /**
         * This gets called when the properties of one {@link DataAccessObject} are being copied to another. {@link PropertyChangeEvent}s will be deferred while this is invoked.
         *
         * @param fromDAO The source {@link DataAccessObject} to be copied from.
         * @param toDAO The target {@link DataAccessObject} to be copied to.
         */
        protected abstract void onCloneProperties(D fromDAO, D toDAO);

        final Stream<D> findAllCached(Predicate<D> filter) {
            return cache.findAll(filter);
        }

        final <T> Stream<T> mapCached(Function<D, T> mapper) {
            return cache.map(mapper);
        }

        final Optional<D> findFirstCached(Predicate<D> filter) {
            return cache.findFirst(filter);
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
            D dao = cache.get(key, () -> {
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
        public abstract D createNew();

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
         * @return A {@link Consumer} that gets invoked after the data access object is no longer in a synchronized state. This will allow implementing classes to put fields
         * directly while property change events are deferred. This value can be {@code null} if it is not applicable.
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
         * @return {@code true} if {@link #createDmlSelectQueryBuilder()} returns a {@link DmlSelectQueryBuilder} with joined tables; otherwise, {@code false}.
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

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
            return tail.append(eventHandlerManager);
        }

        /**
         * Registers a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <T extends E> void addEventHandler(EventType<T> type, EventHandler<? super T> eventHandler) {
            eventHandlerManager.addEventHandler(type, eventHandler);
        }

        /**
         * Registers a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventFilter The event filter.
         */
        public final <T extends E> void addEventFilter(EventType<T> type, EventHandler<? super T> eventFilter) {
            eventHandlerManager.addEventFilter(type, eventFilter);
        }

        /**
         * Unregisters a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <T extends E> void removeEventHandler(EventType<T> type, EventHandler<? super T> eventHandler) {
            eventHandlerManager.removeEventHandler(type, eventHandler);
        }

        /**
         * Unregisters a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventFilter The event filter.
         */
        public final <T extends E> void removeEventFilter(EventType<T> type, EventHandler<? super T> eventFilter) {
            eventHandlerManager.removeEventFilter(type, eventFilter);
        }

    }

    /**
     * Background task which provides an opened database {@link Connection} and defers the firing of {@link java.beans.PropertyChangeEvent}s on the backing
     * {@link EntityModel#dataObject}. When completed, the {@link #finalEvent} is fired on the backing {@link DataAccessObject}.
     *
     * @param <D> The target {@link DataAccessObject} type.
     * @param <M> The associated {@link EntityModel} type.
     * @param <E> The result {@link ModelEvent} type.
     */
    public static abstract class DaoTask<D extends DataAccessObject, M extends EntityModel<D>, E extends ModelEvent<D, M>> extends Task<E> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DaoTask.class.getName());

        private final ReadOnlyObjectWrapper<D> dataAccessObject;
        private final ReadOnlyObjectWrapper<M> fxRecordModel;
//        private final ReadOnlyObjectWrapper<E> finalEvent;
        private final DataRowState originalRowState;

        /**
         * Creates a new {@code DaoTask} for the {@link EntityModel#dataObject DataAccessObject} of a {@link EntityModel}.
         *
         * @param target The {@link EntityModel} that contains the target {@link DataAccessObject}.
         */
        protected DaoTask(M target) {
            dataAccessObject = new ReadOnlyObjectWrapper<>(this, "dataAccessObject", target.dataObject());
            fxRecordModel = new ReadOnlyObjectWrapper<>(this, "fxRecordModel", target);
            originalRowState = dataAccessObject.get().getRowState();
//            finalEvent = new ReadOnlyObjectWrapper<>(this, "finalEvent", null);
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
         * Gets the {@link EntityModel} that wraps the target {@link DataAccessObject}.
         *
         * @return The {@link EntityModel} that wraps the target {@link DataAccessObject} or {@code null} if only the target {@link DataAccessObject} was provided to this task.
         */
        public M getEntityModel() {
            return fxRecordModel.get();
        }

        public ReadOnlyObjectProperty<M> fxRecordModelProperty() {
            return fxRecordModel.getReadOnlyProperty();
        }

        /**
         * Gets the original {@link DataAccessObject#rowState rowState} of the {@link DataAccessObject} when the {@code DaoTask} was created.
         *
         * @return The original {@link DataAccessObject#rowState rowState} of the {@link DataAccessObject} when the {@code DaoTask} was created.
         */
        protected final DataRowState getOriginalRowState() {
            return originalRowState;
        }

//        /**
//         * Gets the final {@link ModelEvent} after the {@code DaoTask} is finished.
//         *
//         * @return If successful, this will be the value from {@link #getValue()}; If failed, this will be set to the value obtained from {@link #createFaultedEvent()}; otherwise,
//         * this will be set to the value obtained from {@link #createCanceledEvent()}.
//         */
//        public E getFinalEvent() {
//            return finalEvent.get();
//        }
//
//        public ReadOnlyObjectProperty<E> finalEventProperty() {
//            return finalEvent.getReadOnlyProperty();
//        }

        /**
         * Invoked when the database {@link Connection} is opened and {@link java.beans.PropertyChangeEvent} firing is being deferred.
         *
         * @param connection The database {@link Connection}.
         * @return The {@link ModelEvent} for a successful task completion. This should never return a {@code null} value.
         * @throws Exception if an un-handled exception occurred during the task operation.
         */
        protected abstract E call(Connection connection) throws Exception;

        @Override
        @SuppressWarnings("try")
        protected E call() throws Exception {
            if (originalRowState != ((DataAccessObject) getDataAccessObject()).rowState) {
                throw new IllegalStateException("Row state has changed");
            }
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
                            throw new NullPointerException("No result event was produced");
                        }
                        return event;
                    }
                }
            }
        }

        /**
         * This gets called to create the {@link #finalEvent} when the task is canceled.
         *
         * @return A {@link ModelEvent} representing a cancellation or {@code null} if no event will be produced for the cancellation.
         */
        protected abstract E createCanceledEvent();

        /**
         * This gets called to create the {@link #finalEvent} when the task has failed.
         *
         * @return A {@link ModelEvent} representing a failure or {@code null} if no event will be produced for that failure.
         */
        protected abstract E createFaultedEvent();

        @Override
        protected void cancelled() {
            super.cancelled();
            E event = createCanceledEvent();
            if (null != event) {
//                try {
//                    finalEvent.set(event);
//                } finally {
                    LOG.fine(() -> String.format("Firing %s%n\ton %s", event, getDataAccessObject()));
                    Event.fireEvent(getDataAccessObject(), event);
//                }
            }
        }

        @Override
        protected void failed() {
            super.failed();
            E event = createFaultedEvent();
            if (null != event) {
//                try {
//                    finalEvent.set(event);
//                } finally {
                    LOG.fine(() -> String.format("Firing %s%n\ton %s", event, getDataAccessObject()));
                    Event.fireEvent(getDataAccessObject(), event);
//                }
            }
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            E event = getValue();
            if (null != event) {
                LOG.fine(() -> String.format("Succeeded with %s", event));
//                try {
//                    finalEvent.set(event);
//                } finally {
                    LOG.fine(() -> String.format("Firing %s%n\ton %s", event, getDataAccessObject()));
                    Event.fireEvent(getDataAccessObject(), event);
//                }
            }
        }

    }

    /**
     * A {@link DaoTask} that allows for validation before the actual operation is performed. This provides an opened database {@link Connection} and defers the firing of
     * {@link java.beans.PropertyChangeEvent}s on the target {@link DataAccessObject}. When completed, the {@link #finalEvent} is fired on the {@link ValidatingDaoTask} and the
     * target {@link DataAccessObject}. The {@link ModelEvent} produced by this task will be for successful completions as well as validation errors. {@link ModelEvent}s are also
     * produced for task failures and cancellations.
     *
     * @param <D> The type of the target {@link DataAccessObject}.
     * @param <M> The type of associated {@link EntityModel}, if applicable.
     * @param <E> The type of result {@link ModelEvent} produced by this task.
     */
    public static abstract class ValidatingDaoTask<D extends DataAccessObject, M extends EntityModel<D>, E extends ModelEvent<D, M>>
            extends DaoTask<D, M, E> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ValidatingDaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(ValidatingDaoTask.class.getName());

        private final ReadOnlyObjectWrapper<DaoFactory<D, E>> daoFactory;
        private final ReadOnlyObjectWrapper<EntityModel.EntityModelFactory<D, M, E, ? extends E>> modelFactory;
        private boolean validationSuccessful;
        private final ReadOnlyBooleanWrapper validationFailed;

        /**
         * Creates a new {@code ValidatingDaoTask} for the {@link EntityModel#dataObject DataAccessObject} of a {@link EntityModel}.
         *
         * @param target The {@link EntityModel} that contains the target {@link DataAccessObject}.
         * @param modelFactory The {@link EntityModelImpl.EntityModelFactory} associated with the source {@link EntityModel} type.
         * @param skipValidation {@code true} to skip validation for the target {@link DataAccessObject}; otherwise, {@code false} to invoke {@link #validate(Connection)} to
         * perform validation.
         */
        protected ValidatingDaoTask(M target, EntityModel.EntityModelFactory<D, M, E, ? extends E> modelFactory, boolean skipValidation) {
            super(target);
            daoFactory = new ReadOnlyObjectWrapper<>(modelFactory.getDaoFactory());
            this.modelFactory = new ReadOnlyObjectWrapper<>(modelFactory);
            validationSuccessful = skipValidation;
            validationFailed = new ReadOnlyBooleanWrapper(!skipValidation);
        }

        /**
         * Gets a value indicating whether validation has failed.
         *
         * @return {@code true} if validation has failed; otherwise, {@code false}.
         */
        public boolean isValidationFailed() {
            return validationFailed.get();
        }

        public ReadOnlyBooleanProperty validationFailedProperty() {
            return validationFailed.getReadOnlyProperty();
        }

        /**
         * Gets the {@link DaoFactory} associated with the target {@link DataAccessObject} type.
         *
         * @return The {@link DaoFactory} associated with the target {@link DataAccessObject} type.
         */
        public DaoFactory<D, E> getDaoFactory() {
            return daoFactory.get();
        }

        public ReadOnlyObjectProperty<DaoFactory<D, E>> daoFactoryProperty() {
            return daoFactory.getReadOnlyProperty();
        }

        /**
         * Gets the {@link EntityModelImpl.EntityModelFactory} associated with the source {@link EntityModel} type.
         *
         * @return The {@link EntityModelImpl.EntityModelFactory} associated with the source {@link EntityModel} type or {@code null} if a {@link EntityModel} was not specified in
         * the constructor.
         */
        public EntityModel.EntityModelFactory<D, M, E, ? extends E> getModelFactory() {
            return modelFactory.get();
        }

        public ReadOnlyObjectProperty<EntityModel.EntityModelFactory<D, M, E, ? extends E>> modelFactoryProperty() {
            return modelFactory.getReadOnlyProperty();
        }

        @Override
        protected final E call(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "call", connection);
            if (!validationSuccessful) {
                LOG.fine("Validating");
                E event = validate(connection);
                if (null != event && event instanceof ModelFailedEvent) {
                    LOG.fine(() -> String.format("Validation failed: %s", event));
                    return event;
                }
                validationSuccessful = true;
            }
            LOG.fine("Validated");
            return (isCancelled()) ? null : onValidated(connection);
        }

        /**
         * This gets called to validateForSave the current {@link DataAccessObject}.
         *
         * @param connection The opened database {@link Connection}.
         * @return The validation event, which can be {@code null} if the target {@link DataAccessObject} is valid.
         * @throws Exception if unable to perform validation.
         */
        protected abstract E validate(Connection connection) throws Exception;

        /**
         * This gets called after validation is successful.
         *
         * @param connection The opened database {@link Connection}.
         * @return A {@link ModelEvent} which will become the {@link DaoTask#finalEvent}, indicating successful completion. This should never return a {@code null} value.
         * @throws Exception if unable to complete the operation.
         */
        protected abstract E onValidated(Connection connection) throws Exception;

    }

    /**
     * A {@link ValidatingDaoTask} which saves the target {@link DataAccessObject} to the database. This provides an opened database {@link Connection} and defers the firing of
     * {@link java.beans.PropertyChangeEvent}s on the target {@link DataAccessObject}. When completed, the {@link #finalEvent} is fired on the {@link SaveDaoTask} and the target
     * {@link DataAccessObject}. The {@link ModelEvent} produced by this task will be for successful completions as well as validation errors. {@link ModelEvent}s are also produced
     * for task failures and cancellations. If successful, the target {@link DataAccessObject#rowState} will be set to {@link DataRowState#UNMODIFIED}.
     *
     * @param <D> The type of the target {@link DataAccessObject} to be saved.
     * @param <M> The type of associated {@link EntityModel}, if applicable.
     * @param <E> The type of result {@link ModelEvent} produced by this task.
     */
    public static abstract class SaveDaoTask<D extends DataAccessObject, M extends EntityModel<D>, E extends ModelEvent<D, M>>
            extends ValidatingDaoTask<D, M, E> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(SaveDaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(SaveDaoTask.class.getName());

        /**
         * Creates a new {@code SaveDaoTask} for the {@link EntityModel#dataObject DataAccessObject} of a {@link EntityModel}.
         *
         * @param target The {@link EntityModel} that contains the target {@link DataAccessObject}.
         * @param modelFactory The {@link EntityModelImpl.EntityModelFactory} associated with the source {@link EntityModel} type.
         * @param skipValidation {@code true} to skip validation for the target {@link DataAccessObject}; otherwise, {@code false} to invoke {@link #validate(Connection)} to
         * perform validation.
         * @throws IllegalArgumentException if {@link DataAccessObject#rowState} for the {@code fxRecordModel} is {@link DataRowState#DELETED}.
         */
        protected SaveDaoTask(M target, EntityModel.EntityModelFactory<D, M, E, ? extends E> modelFactory, boolean skipValidation) {
            super(target, modelFactory, skipValidation);
            if (getOriginalRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Record was already deleted");
            }
        }

        @Override
        protected final E onValidated(Connection connection) throws Exception {
            D dao = getDataAccessObject();
            DaoFactory<D, E> factory = getDaoFactory();
            DataAccessObject dataObj = (DataAccessObject) dao;
            Timestamp timeStamp = DateTimeUtil.toUtcTimestamp(LocalDateTime.now());
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
                        dataObj.rowState = DataRowState.UNMODIFIED;
                        factory.cache.put(dao);
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
            E event = getValue();
            LOG.fine(() -> String.format("Task succeeded: %s", event));
            if (event instanceof ModelFailedEvent) {
                obj.rejectChanges();
            } else {
                obj.acceptChanges();
                obj.rowState = DataRowState.UNMODIFIED;
                obj.firePropertyChange(PROP_ROWSTATE, getOriginalRowState(), obj.rowState);
            }
            super.succeeded();
        }

        /**
         * This gets called to create the {@link ModelEvent} object that represents a successful insert or update.
         *
         * @return A {@link ModelEvent} object that represents a successful deletion. This should never return a {@code null} value.
         */
        protected abstract E createSuccessEvent();

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
     * A {@link ValidatingDaoTask} which deletes the target {@link DataAccessObject} from the database. This provides an opened database {@link Connection} and defers the firing of
     * {@link java.beans.PropertyChangeEvent}s on the target {@link DataAccessObject}. When completed, the {@link #finalEvent} is fired on the {@link DeleteDaoTask} and the target
     * {@link DataAccessObject}. The {@link ModelEvent} produced by this task will be for successful completions as well as validation errors. {@link ModelEvent}s are also produced
     * for task failures and cancellations. If successful, the target {@link DataAccessObject#rowState} will be set to {@link DataRowState#DELETED}.
     *
     * @param <D> The type of the target {@link DataAccessObject} to be saved.
     * @param <M> The type of associated {@link EntityModel}, if applicable.
     * @param <E> The type of result {@link ModelEvent} produced by this task.
     */
    public static abstract class DeleteDaoTask<D extends DataAccessObject, M extends EntityModel<D>, E extends ModelEvent<D, M>>
            extends ValidatingDaoTask<D, M, E> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DeleteDaoTask.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(DeleteDaoTask.class.getName());

        /**
         * Creates a new {@code DeleteDaoTask} for the {@link EntityModel#dataObject DataAccessObject} of a {@link EntityModel}.
         *
         * @param target The {@link EntityModel} that contains the target {@link DataAccessObject}.
         * @param modelFactory The {@link EntityModelImpl.EntityModelFactory} associated with the source {@link EntityModel} type.
         * @param skipValidation {@code true} to skip validation for the target {@link DataAccessObject}; otherwise, {@code false} to invoke {@link #validate(Connection)} to
         * perform validation.
         * @throws IllegalStateException if {@link DataAccessObject#rowState} for the {@code fxRecordModel} is {@link DataRowState#DELETED} or {@link DataRowState#NEW}.
         */
        @SuppressWarnings("incomplete-switch")
        protected DeleteDaoTask(M target, EntityModel.EntityModelFactory<D, M, E, ? extends E> modelFactory, boolean skipValidation) {
            super(target, modelFactory, skipValidation);
            switch (getOriginalRowState()) {
                case DELETED:
                    throw new IllegalArgumentException("Record was already deleted");
                case NEW:
                    throw new IllegalArgumentException("Record was never saved");
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
            return createSuccessEvent();
        }

        /**
         * This gets called to create the {@link ModelEvent} object that represents a successful deletion.
         *
         * @return A {@link ModelEvent} object that represents a successful deletion. This should never return a {@code null} value.
         */
        protected abstract E createSuccessEvent();

        @Override
        protected void succeeded() {
            DataAccessObject obj = (DataAccessObject) getDataAccessObject();
            E event = getValue();
            LOG.fine(() -> String.format("Task succeeded: %s", event));
            if (event instanceof ModelFailedEvent) {
                obj.rejectChanges();
            } else {
                obj.acceptChanges();
                obj.rowState = DataRowState.DELETED;
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
