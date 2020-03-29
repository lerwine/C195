package scheduler.dao;

import scheduler.dao.schema.DbTable;
import scheduler.util.PropertyBindable;
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
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
import scheduler.Scheduler;
import scheduler.dao.dml.deprecated.ColumnReference;
import scheduler.dao.dml.deprecated.SelectColumnList;
import scheduler.dao.dml.deprecated.TableColumnList;
import scheduler.dao.dml.deprecated.WhereStatement;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.dao.schema.SchemaHelper;
import scheduler.util.DB;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.ItemModel;

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

        public abstract DataObjectImpl.Factory_obsolete<T, ? extends ItemModel<T>> getDaoFactory();

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

    public static abstract class DaoFactory<T extends DataObjectImpl> {
        private static final Logger LOG = Logger.getLogger(DaoFactory.class.getName());

        public List<T> get(Connection connection, WhereStatement<T, ? extends ItemModel<T>> filter) {
            // TODO: Implement this.
            throw new UnsupportedOperationException();
        }
        
        /**
         * Gets the object responsible for generating base SQL select statements.
         * 
         * @return The {@link SelectColumnList} object that will be used to generate SELECT statements that correspond
         * to the target {@link DataObjectImpl}.
         */
        public abstract SelectColumnList getSelectColumns();

        /**
         * Gets the {@link Class} for the target {@link DataObjectImpl} type.
         * 
         * @return The {@link Class} for the target {@link DataObjectImpl} type.
         */
        public abstract Class<? extends T> getDaoClass();

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

    }
    
    /**
     * Base class for CRUD operations on {@link scheduler.dao.DataObjectImpl} objects.
     * 
     * @param <T> The type of {@link DataObjectImpl} supported.
     * @param <M> The type of {@link ItemModel} object that corresponds to the target {@link DataObjectImpl}.
     * @deprecated Use {@link DaoFactory}
     */
    @Deprecated
    public static abstract class Factory_obsolete<T extends DataObjectImpl, M extends ItemModel<T>> {

        private static final Logger LOG = Logger.getLogger(Factory_obsolete.class.getName());

        protected abstract T fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException;

        /**
         * Gets the object responsible for generating base SQL select statements.
         * 
         * @return The {@link SelectColumnList} object that will be used to generate SELECT statements that correspond
         * to the target {@link DataObjectImpl}.
         */
        public abstract SelectColumnList getSelectColumns();

        /**
         * Gets the {@link Class} for the target {@link DataObjectImpl} type.
         * 
         * @return The {@link Class} for the target {@link DataObjectImpl} type.
         */
        public abstract Class<? extends T> getDaoClass();

        public abstract DbTable getDbTable();

        /**
         * Finishes updating a data access object from a {@link ResultSet}. Do not call this directly. User
         * {@link #initializeDao(DataObjectImpl, ResultSet, TableColumnList) }, instead.
         *
         * @param target The {@link DataObjectImpl} to be initialized.
         * @param resultSet The data retrieved from the database.
         * @param columns The {@link TableColumnList} that was used to create query string.
         * @throws SQLException if not able to read data from the {@link ResultSet}.
         */
        protected abstract void onInitializeDao(T target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException;
        
        /**
         * Initializes a data access object from a {@link ResultSet}.
         *
         * @param target The {@link DataObjectImpl} to be initialized.
         * @param resultSet The data retrieved from the database.
         * @param columns The {@link TableColumnList} that was used to create query string.
         * @throws SQLException if not able to read data from the {@link ResultSet}.
         */
        protected final void initializeDao(T target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            DataObjectImpl dao = (DataObjectImpl)target;
            int oldPrimaryKey = dao.primaryKey;
            String oldCreatedBy = dao.createdBy;
            Timestamp oldCreateDate = dao.createDate;
            String oldLastModifiedBy = dao.lastModifiedBy;
            Timestamp oldLastModifiedDate = dao.lastModifiedDate;
            dao.primaryKey = columns.getInt(resultSet, SchemaHelper.getPrimaryKey(getDbTable()));
            dao.lastModifiedBy = columns.getString(resultSet, DbName.LAST_UPDATE_BY);
            dao.lastModifiedDate = columns.getTimestamp(resultSet, DbName.LAST_UPDATE);
            dao.createdBy = columns.getString(resultSet, DbName.CREATED_BY);
            dao.createDate = columns.getTimestamp(resultSet, DbName.CREATE_DATE);
            onInitializeDao(target, resultSet, columns);
            DataRowState oldRowState = dao.rowState;
            dao.rowState = DataRowState.UNMODIFIED;
            dao.getPropertyChangeSupport().firePropertyChange(PROP_ROWSTATE, oldRowState, dao.rowState);
            dao.getPropertyChangeSupport().firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, dao.primaryKey);
            dao.getPropertyChangeSupport().firePropertyChange(PROP_CREATEDBY, oldCreatedBy, dao.createdBy);
            dao.getPropertyChangeSupport().firePropertyChange(PROP_CREATEDATE, oldCreateDate, dao.createDate);
            dao.getPropertyChangeSupport().firePropertyChange(PROP_LASTMODIFIEDBY, oldLastModifiedBy, dao.lastModifiedBy);
            dao.getPropertyChangeSupport().firePropertyChange(PROP_LASTMODIFIEDDATE, oldLastModifiedDate, dao.lastModifiedDate);
        }
        
        /**
         * Saves a {@link DataObjectImpl} to the database.
         * 
         * @param dao The {@link DataObjectImpl} to be inserted or updated.
         * @param connection The database connection to use.
         * @throws SQLException If unable to perform the database operation.
         */
        public void save(T dao, Connection connection) throws SQLException {
            Objects.requireNonNull(dao, "Data access object cannot be null");
            Objects.requireNonNull(connection, "Connection cannot be null");
            synchronized (dao) {
                assert dao.getRowState() != DataRowState.DELETED : String.format("%s has been deleted", getClass().getName());
                StringBuilder sql = new StringBuilder();
                //HashMap<String, Integer> indexes = new HashMap<>();

                DbColumn[] columns = SchemaHelper.getTableColumns(getDbTable(), (t) -> {
                    switch (t.getDbName()) {
                        case CREATED_BY:
                        case CREATE_DATE:
                        case LAST_UPDATE:
                        case LAST_UPDATE_BY:
                            return false;
                    }
                    return true;
                }).toArray(DbColumn[]::new);

                if (dao.getRowState() == DataRowState.NEW) {
                    sql.append("INSERT INTO `").append(getDbTable().getDbName()).append("` (`")
                            .append(DbName.LAST_UPDATE).append("`, `")
                            .append(DbName.LAST_UPDATE_BY).append("`, `")
                            .append(DbName.CREATE_DATE).append("`, `")
                            .append(DbName.CREATED_BY);
                    for (DbColumn col : columns) {
                        sql.append("`, `").append(col.getDbName());
                    }
                    sql.append("`) VALUES (?");
                    int e = columns.length + 4;
                    for (int i = 1; i < e; i++) {
                        sql.append(", ?");
                    }
                    sql.append(")");
                } else {
                    sql.append("UPDATE `").append(getDbTable().getDbName()).append("` SET `")
                            .append(DbName.LAST_UPDATE).append("` = ?, `")
                            .append(DbName.LAST_UPDATE_BY);
                    for (DbColumn col : columns) {
                        sql.append("` = ?, `").append(col.getDbName());
                    }

                    sql.append("` = ? WHERE `").append(getDbTable().getPkColName()).append(" = ?");
                }
                LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql.toString()));
                int pk;
                try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                    int index;
                    if (dao.getRowState() == DataRowState.NEW) {
                        ps.setTimestamp(1, dao.getCreateDate());
                        ps.setString(2, dao.getCreatedBy());
                        ps.setTimestamp(3, dao.getCreateDate());
                        ps.setString(4, dao.getCreatedBy());
                        index = 4;
                    } else {
                        ps.setTimestamp(1, dao.getLastModifiedDate());
                        ps.setString(2, dao.getLastModifiedBy());
                        index = 2;
                    }
                    for (DbColumn col : columns) {
                        setSqlParameter(dao, col, ps, ++index);
                    }
                    if (dao.getRowState() != DataRowState.NEW) {
                        ps.setInt(index, dao.getPrimaryKey());
                    }
                    ps.executeUpdate();
                    if (dao.getRowState() == DataRowState.NEW) {
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            pk = rs.getInt(1);
                        }
                    } else {
                        pk = dao.getPrimaryKey();
                    }
                }
                SelectColumnList dml = getSelectColumns();
                sql = dml.getSelectQuery();
                sql.append(" WHERE `").append(getDbTable().getPkColName()).append("`=%");
                LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql.toString()));
                try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                    ps.setInt(1, pk);
                    try (ResultSet rs = ps.getResultSet()) {
                        assert rs.next() : "Updated record not found";
                        initializeDao(dao, rs, dml);
                    }
                }
            }
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
            Objects.requireNonNull(connection, "Connection cannot be null");
            SelectColumnList dml = getSelectColumns();
            String sql = dml.getSelectQuery().append(" WHERE p.`").append(getDbTable().getPkColName()).append("`=?").toString();
            LOG.log(Level.SEVERE, String.format("Finalizing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, pk);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(fromResultSet(rs, dml));
                    }
                }
            }
            return Optional.empty();
        }

        /**
         * Deletes the corresponding {@link DataObjectImpl} from the database.
         * 
         * @param dao The {@link DataObjectImpl} to delete.
         * @param connection The database connection to use.
         * @throws SQLException If unable to perform the database operation.
         */
        public void delete(T dao, Connection connection) throws SQLException {
            Objects.requireNonNull(dao, "Data access object cannot be null");
            Objects.requireNonNull(connection, "Connection cannot be null");
            synchronized (dao) {
                assert dao.getRowState() != DataRowState.DELETED : String.format("%s has already been deleted", getClass().getName());
                assert dao.getRowState() != DataRowState.NEW : String.format("%s has not been inserted into the database", getClass().getName());
                String sql = String.format("DELETE FROM `%s` WHERE `%s` = ?", getDbTable().getDbName(), getDbTable().getPkColName());
                LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql));
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, dao.getPrimaryKey());
                    assert ps.executeUpdate() > 0 : String.format("Failed to delete associated database row on %s where %s = %d",
                            getDbTable().getDbName(), getDbTable().getPkColName(), dao.getPrimaryKey());
                }
                dao.setDeleted();
            }
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

        /**
         * @deprecated Filters will be defined outside of the item factory.
         */
        // TODO: Remove this
        @Deprecated
        public abstract ModelListingFilter<T, M> getAllItemsFilter();

        /**
         * @deprecated Filters will be defined outside of the item factory.
         */
        // TODO: Remove this
        @Deprecated
        public abstract ModelListingFilter<T, M> getDefaultFilter();

        /**
         * Sets parameterized SQL query values.
         * 
         * @param dao The {@link DataObjectImpl} to be updated.
         * @param column The target database column column.
         * @param ps The {@link PreparedStatement} to apply the value to.
         * @param index The index to use when setting the value.
         * @throws SQLException if unable to set the value.
         */
        protected abstract void setSqlParameter(T dao, DbColumn column, PreparedStatement ps, int index) throws SQLException;
    }

}
