package scheduler.dao;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
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
import scheduler.util.AnnotationHelper;
import scheduler.util.DB;
import scheduler.util.DbConnector;
import scheduler.util.InternalException;
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

    /**
     * The name of the 'primaryKey' property.
     */
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
    protected DataAccessObject() {
        primaryKey = Integer.MIN_VALUE;
        lastModifiedDate = createDate = DB.toUtcTimestamp(LocalDateTime.now());
        lastModifiedBy = createdBy = (Scheduler.getCurrentUser() == null) ? "" : Scheduler.getCurrentUser().getUserName();
        rowState = DataRowState.NEW;
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

    private PropertyStateInfo getPropertyStateInfo() {
        return new PropertyStateInfo();
    }

    private synchronized PropertyStateInfo setChanged() {
        PropertyStateInfo result = new PropertyStateInfo();
        UserDAO currentUser = Scheduler.getCurrentUser();
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
                createdBy = lastModifiedBy;
                createDate = lastModifiedDate;
                rowState = DataRowState.NEW;
                break;
        }

        return result;
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
                        setChanged().firePropertyChanges();
                    }
                    break;
            }
        }
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return Scheduler.buildMainControllerEventDispatchChain(tail);
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

    /**
     * Base factory class for {@link DataAccessObject} objects.
     *
     * @param <T> The type of {@link DataAccessObject} object supported.
     */
    public static abstract class DaoFactory<T extends DataAccessObject> {

        private static final Logger LOG = Logger.getLogger(DaoFactory.class.getName());

        /**
         * Loads items from the database.
         *
         * @param connection An opened database connection.
         * @param filter The {@link DaoFilter} that is used to build the WHERE clause of the SQL query.
         * @return A list of items loaded.
         * @throws SQLException if unable to read data from the database.
         */
        public List<T> load(Connection connection, DaoFilter<T> filter) throws SQLException {
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
            LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
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
        public Task<List<T>> loadAsync(WaitBorderPane waitBorderPane, DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
            LoadTask<T> task = new LoadTask<>(this, filter, onSuccess, onFail);
            waitBorderPane.startNow(task);
            return task;
        }

        public Task<List<T>> loadAsync(DaoFilter<T> filter, Consumer<List<T>> onSuccess, Consumer<Throwable> onFail) {
            LoadTask<T> task = new LoadTask<>(this, filter, onSuccess, onFail);
            MainController.startBusyTaskNow(task);
            return task;
        }

        /**
         * Creates a new data access object from database query results.
         *
         * @param rs The {@link ResultSet} containing values from the database.
         * @return The newly-initialized data access object.
         * @throws SQLException if unable to read values from the {@link ResultSet}.
         */
        protected T fromResultSet(ResultSet rs) throws SQLException {
            T item = createNew();
            initializeFromResultSet(item, rs);
            return item;
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
         * implementing classes to set fields directly and defer property change events. This value can be {@code null} if it is not applicable.
         * @throws SQLException if unable to read from the {@link ResultSet}.
         */
        protected abstract Consumer<PropertyChangeSupport> onInitializeFromResultSet(T dao, ResultSet rs) throws SQLException;

        /**
         * Gets the {@link DbTable} for the supported {@link DataAccessObject}.
         *
         * @return The {@link DbTable} for the supported {@link DataAccessObject}.
         */
        public DbTable getDbTable() {
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
         * Initializes the properties of a {@link DataAccessObject} from a {@link ResultSet}. This method will call
         * {@link #onInitializeFromResultSet(DataAccessObject, ResultSet)} for implementing classes to finish property initialization.
         *
         * @param dao The {@link DataAccessObject} to be initialized.
         * @param rs The {@link ResultSet} to read from.
         * @throws SQLException if unable to read from the {@link ResultSet}.
         */
        protected final void initializeFromResultSet(T dao, ResultSet rs) throws SQLException {
            DataAccessObject obj = (DataAccessObject) dao;

            PropertyStateInfo propertyChangeInfo = null;
            Consumer<PropertyChangeSupport> consumer = null;
            try {
                synchronized (dao) {
                    propertyChangeInfo = obj.getPropertyStateInfo();
                    obj.primaryKey = rs.getInt(getPrimaryKeyColumn().toString());
                    obj.createDate = rs.getTimestamp(DbName.CREATE_DATE.toString());
                    obj.createdBy = rs.getString(DbName.CREATED_BY.toString());
                    obj.lastModifiedDate = rs.getTimestamp(DbName.LAST_UPDATE.toString());
                    obj.lastModifiedBy = rs.getString(DbName.LAST_UPDATE_BY.toString());
                    obj.rowState = DataRowState.UNMODIFIED;
                    consumer = onInitializeFromResultSet(dao, rs);
                }
            } finally {
                try {
                    if (null != propertyChangeInfo) {
                        propertyChangeInfo.firePropertyChanges();
                    }
                } finally {
                    if (null != consumer) {
                        consumer.accept(obj.getPropertyChangeSupport());
                    }
                }
            }
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
         * Saves a {@link DataAccessObject} to the database if there are changes.
         * <p>
         * {@link #getSaveDbConflictMessage(DataAccessObject, Connection)} should be called before this method is invoked in order to check for
         * database conflict errors ahead of time and to get a descriptive message.</p>
         *
         * @param dao The {@link DataAccessObject} to be inserted or updated.
         * @param connection The database connection to use.
         * @throws SQLException If unable to perform the database operation.
         */
        public final void save(T dao, Connection connection) throws SQLException {
            save(dao, connection, false);
        }

        /**
         * Saves a {@link DataAccessObject} to the database.
         * <p>
         * {@link #getSaveDbConflictMessage(DataAccessObject, Connection)} should be called before this method is invoked in order to check for
         * database conflict errors ahead of time and to get a descriptive message.</p>
         *
         * @param dao The {@link DataAccessObject} to be inserted or updated.
         * @param connection The database connection to use.
         * @param force A {@code true} value will save changes to the database, even if {@link #rowState} is {@link DataRowState#UNMODIFIED}.
         * @throws SQLException If unable to perform the database operation.
         */
        public void save(T dao, Connection connection, boolean force) throws SQLException {
            DataAccessObject dataObj = (DataAccessObject) dao;
            PropertyStateInfo propertyChangeInfo = null;
            try {
                synchronized (dao) {
                    Iterator<DbColumn> iterator;
                    StringBuilder sb;
                    String sql;
                    int index;
                    propertyChangeInfo = dataObj.getPropertyStateInfo();
                    Timestamp timeStamp = DB.toUtcTimestamp(LocalDateTime.now());
                    DbColumn[] columns;
                    switch (((DataAccessObject) dao).rowState) {
                        case NEW:
                            sb = new StringBuilder();
                            sb.append("INSERT INTO ").append(getDbTable().getDbName()).append(" (");
                            columns = SchemaHelper.getTableColumns(getDbTable(),
                                    (t) -> t.getUsageCategory() != ColumnCategory.PRIMARY_KEY).toArray(DbColumn[]::new);
                            iterator = Arrays.stream(columns).iterator();
                            sb.append(iterator.next().getDbName());
                            index = 1;
                            while (iterator.hasNext()) {
                                index++;
                                sb.append(", ").append(iterator.next().getDbName());
                            }
                            sb.append(") VALUES (?");
                            for (int i = 1; i < index; i++) {
                                sb.append(", ?");
                            }
                            sb.append(")");
                            sql = sb.toString();
                            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                                index = 1;
                                iterator = Arrays.stream(columns).iterator();
                                do {
                                    DbColumn column = iterator.next();
                                    LOG.log(Level.INFO, String.format("Setting value for %s at index %d", column.getDbName(), index));
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
                                                throw new InternalException(String.format("Unexpected AUDIT column name %s", column.getDbName()));
                                        }
                                    } else {
                                        applyColumnValue(dao, column, ps, index++);
                                    }
                                } while (iterator.hasNext());
                                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                                if (ps.executeUpdate() < 1) {
                                    throw new SQLException("executeUpdate unexpectedly resulted in no database changes");
                                }
                                try (ResultSet rs = ps.getGeneratedKeys()) {
                                    if (!rs.next()) {
                                        throw new SQLException("No primary key returned");
                                    }
                                    dataObj.primaryKey = rs.getInt(1);
                                }
                            }
                            break;
                        case UNMODIFIED:
                            if (!force) {
                                return;
                            }
                        // fall through to the next case on purpose
                        case MODIFIED:
                            sb = new StringBuilder();
                            sb.append("UPDATE ").append(getDbTable().getDbName()).append(" SET ");
                            columns = SchemaHelper.getTableColumns(getDbTable(), (t) -> SchemaHelper.isUpdatable(t)).toArray(DbColumn[]::new);
                            iterator = Arrays.stream(columns).iterator();
                            DbName dbName = iterator.next().getDbName();
                            int colNum = 0;
                            LOG.info(String.format("Appending column SQL for column %s at index %d", dbName, ++colNum));
                            sb.append(dbName).append("=?");
                            while (iterator.hasNext()) {
                                dbName = iterator.next().getDbName();
                                LOG.info(String.format("Appending column SQL for %s at index %d", dbName, ++colNum));
                                sb.append(", ").append(dbName).append("=?");
                            }
                            dbName = getPrimaryKeyColumn().getDbName();
                            LOG.info(String.format("Appending column SQL for %s at index %d", dbName, ++colNum));
                            sb.append(" WHERE ").append(dbName).append("=?");
                            sql = sb.toString();
                            try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
                                iterator = Arrays.stream(columns).iterator();
                                index = 1;
                                do {
                                    DbColumn column = iterator.next();
                                    LOG.info(String.format("Setting value SQL for column %s at index %d", column, index));
                                    if (column.getUsageCategory() == ColumnCategory.AUDIT) {
                                        switch (column.getDbName()) {
                                            case LAST_UPDATE:
                                                dataObj.lastModifiedDate = timeStamp;
                                                ps.setTimestamp(index++, dataObj.lastModifiedDate);
                                                break;
                                            case LAST_UPDATE_BY:
                                                dataObj.lastModifiedBy = Scheduler.getCurrentUser().getUserName();
                                                ps.setString(index++, dataObj.lastModifiedBy);
                                                break;
                                            default:
                                                throw new InternalException(String.format("Unexpected AUDIT column name %s", column.getDbName()));
                                        }
                                    } else {
                                        applyColumnValue(dao, column, ps, index++);
                                    }
                                } while (iterator.hasNext());
                                LOG.info(String.format("Setting value primary key at index %d", index));
                                ps.setInt(index, dataObj.primaryKey);
                                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                                if (ps.executeUpdate() < 1) {
                                    throw new SQLException("executeUpdate unexpectedly resulted in no database changes");
                                }
                            }
                            break;
                        default:
                            throw new IllegalStateException(String.format("%s item has already been deleted.", getDbTable()));
                    }
                    dataObj.rowState = DataRowState.UNMODIFIED;
                }
            } finally {
                if (null != propertyChangeInfo) {
                    propertyChangeInfo.firePropertyChanges();
                }
            }
        }

        /**
         * Sets the parameter value at the specified index from the value associated with the given {@link DbColumn}.
         *
         * @param dao The {@link DataAccessObject} to retrieve the value from.
         * @param dbColumn The {@link DbColumn} related to the value to apply.
         * @param ps The {@link PreparedStatement} to apply the value to.
         * @param index The index at which to apply the value.
         * @throws java.sql.SQLException if not able to set the parameter value.
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
        public Optional<T> loadByPrimaryKey(Connection connection, int pk) throws SQLException {
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
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(fromResultSet(rs));
                    }
                }
            }
            return Optional.empty();
        }

        /**
         * Deletes the corresponding {@link DataAccessObject} from the database.
         * <p>
         * {@link #getDeleteDependencyMessage(DataAccessObject, Connection)} should be called before this method is invoked in order to check for
         * dependency errors ahead of time and to get a descriptive error message.</p>
         *
         * @param dao The {@link DataAccessObject} to delete.
         * @param connection The database connection to use.
         * @throws SQLException If unable to perform the database operation.
         */
        public void delete(T dao, Connection connection) throws SQLException {
            Objects.requireNonNull(dao, "Data access object cannot be null");
            Objects.requireNonNull(connection, "Connection cannot be null");
            DataAccessObject dataObj = (DataAccessObject) dao;
            PropertyStateInfo propertyChangeInfo = null;
            try {
                synchronized (dao) {
                    if (dao.getRowState() == DataRowState.DELETED) {
                        throw new IllegalArgumentException(String.format("%s has been deleted", dao.getClass().getName()));
                    }
                    if (dao.getRowState() == DataRowState.NEW) {
                        throw new IllegalArgumentException(String.format("%s has not been inserted into the database", dao.getClass().getName()));
                    }
                    StringBuilder sb = new StringBuilder("DELETE FROM ");
                    sb.append(getDbTable().getDbName()).append(" WHERE ").append(getPrimaryKeyColumn().getDbName()).append("=?");
                    String sql = sb.toString();
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setInt(1, dao.getPrimaryKey());
                        LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                        if (ps.executeUpdate() < 1) {
                            throw new SQLException("executeUpdate unexpectedly resulted in no database changes");
                        }
                    }
                    propertyChangeInfo = dataObj.setChanged();
                    dataObj.rowState = DataRowState.DELETED;
                }
            } finally {
                if (null != propertyChangeInfo) {
                    propertyChangeInfo.firePropertyChanges();
                }
            }
        }

        /**
         * Checks to see if the current {@link DataAccessObject} can safely be deleted from the database.
         *
         * @param dao The {@link DataAccessObject} intended for deletion.
         * @param connection The database connection to use.
         * @return A user-friendly description of the reason that the {@link DataAccessObject} cannot be deleted or a null or empty string if it can
         * be safely deleted.
         * @throws SQLException If unable to perform the database operation.
         */
        public abstract String getDeleteDependencyMessage(T dao, Connection connection) throws SQLException;

        /**
         * Checks to see if any impending changes cause any database conflicts.
         *
         * @param dao The target {@link DataAccessObject}.
         * @param connection The database connection to use.
         * @return A user-friendly description of the reason that the changes to the {@link DataAccessObject} cannot be saved or a null or empty
         * string if it can be safely deleted.
         * @throws SQLException If unable to perform the database operation.
         */
        public abstract String getSaveDbConflictMessage(T dao, Connection connection) throws SQLException;

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

    private class PropertyStateInfo {

        private int oldPrimaryKey = primaryKey;
        private Timestamp oldCreateDate = createDate;
        private String oldCreatedBy = createdBy;
        private Timestamp oldLastModifiedDate = lastModifiedDate;
        private String oldLastModifiedBy = lastModifiedBy;
        private DataRowState oldRowState = rowState;

        protected void firePropertyChanges() {
            if (oldPrimaryKey != primaryKey) {
                firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, primaryKey);
            }
            oldPrimaryKey = primaryKey;
            if (!createDate.equals(oldCreateDate)) {
                firePropertyChange(PROP_LASTMODIFIEDDATE, oldCreateDate, createDate);
            }
            oldCreateDate = createDate;
            if (!createdBy.equals(oldCreatedBy)) {
                firePropertyChange(PROP_LASTMODIFIEDBY, oldCreatedBy, createdBy);
            }
            oldCreatedBy = createdBy;
            if (!lastModifiedDate.equals(oldLastModifiedDate)) {
                firePropertyChange(PROP_LASTMODIFIEDDATE, oldLastModifiedDate, lastModifiedDate);
            }
            oldLastModifiedDate = lastModifiedDate;
            if (!lastModifiedBy.equals(oldLastModifiedBy)) {
                firePropertyChange(PROP_LASTMODIFIEDBY, oldLastModifiedBy, lastModifiedBy);
            }
            oldLastModifiedBy = lastModifiedBy;
            if (oldRowState != rowState) {
                firePropertyChange(PROP_CREATEDBY, oldRowState, rowState);
            }
            oldRowState = rowState;
        }
    }

}
