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
import java.util.Arrays;
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
import scheduler.App;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.ItemModel;

public class DataObjectImpl extends PropertyBindable implements DataObject, TableNames, DataObjectColumns {

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
    public static final String PROP_ROWSTATE = "rowState";

    private int primaryKey;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastModifiedDate;
    private String lastModifiedBy;
    private DataRowState rowState;
    
    /**
     * Initializes a {@link Values#ROWSTATE_NEW} data access object.
     */
    protected DataObjectImpl() {
        primaryKey = 0;
        lastModifiedDate = createDate = DB.toUtcTimestamp(LocalDateTime.now());
        lastModifiedBy = createdBy = (App.getCurrentUser() == null) ? "" : App.getCurrentUser().getUserName();
        rowState = DataRowState.NEW;
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey;
    }

    private void setPrimaryKey(int primaryKey) {
        int oldPrimaryKey = this.primaryKey;
        this.primaryKey = primaryKey;
        getPropertyChangeSupport().firePropertyChange(PROP_PRIMARYKEY, oldPrimaryKey, primaryKey);
    }

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
                        UserImpl currentUser = App.getCurrentUser();
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

        public abstract DataObjectImpl.Factory<T, ? extends ItemModel<T>> getDaoFactory();

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

    public static abstract class Factory<T extends DataObjectImpl, M extends ItemModel<T>> {

        private static final Logger LOG = Logger.getLogger(Factory.class.getName());

        protected abstract T fromResultSet(ResultSet resultSet) throws SQLException;

        @Deprecated
        public abstract String getBaseSelectQuery();

        public abstract Class<? extends T> getDaoClass();

        @Deprecated
        public abstract String getTableName_old();
        
        public abstract DbTable getTableName();

//        public abstract String getTableAlias();

        @Deprecated
        public abstract String getPrimaryKeyColName();

//        protected abstract Stream<ColNameSupplier> getExtendedColumns(DmlType type);
        
        @Deprecated
        protected abstract List<String> getExtendedColNames();

        protected abstract void setSaveStatementValues(T dao, PreparedStatement ps) throws SQLException;

        private int assertBaseResultSetValid(DataObjectImpl target, ResultSet resultSet) throws SQLException {
            Objects.requireNonNull(resultSet, "Result set cannot be null");
            assert !resultSet.isClosed() : "Result set is closed.";
            assert !(resultSet.isBeforeFirst() || resultSet.isAfterLast()) : "Result set is not positioned on a result row";
            String pkColName = getTableName().getPkColName();
            int pk = resultSet.getInt(pkColName);
            assert !resultSet.wasNull() : String.format("%s was null", pkColName);
            String tn = resultSet.getMetaData().getTableName(resultSet.findColumn(pkColName));
            assert tn.equals(getTableName().getAlias()) || tn.equals(getTableName().getDbName()) : "Table name mismatch";
            if (target.rowState != DataRowState.NEW) {
                assert pk == target.getPrimaryKey() : "Primary key does not match";
            }
            return pk;
        }

        /**
         * Finishes updating a data access object from a {@link ResultSet}. Do not call this directly. User
         * {@link #initializeDao(scheduler.dao.DataObjectImpl, java.sql.ResultSet)}, instead.
         *
         * @param target The {@link DataObjectImpl} to be initialized.
         * @param resultSet The data retrieved from the database.
         * @throws SQLException if not able to read data from the {@link ResultSet}.
         */
        protected abstract void onInitializeDao(T target, ResultSet resultSet) throws SQLException;

        /**
         * Initializes a data access object from a {@link ResultSet}.
         *
         * @param target The {@link DataObjectImpl} to be initialized.
         * @param resultSet The data retrieved from the database.
         * @throws SQLException if not able to read data from the {@link ResultSet}.
         */
        protected void initializeDao(T target, ResultSet resultSet) throws SQLException {
            DataObjectImpl dao = (DataObjectImpl) target;
            dao.setPrimaryKey(assertBaseResultSetValid(target, resultSet));
            dao.setCreateDate(resultSet.getTimestamp(COLNAME_CREATEDATE));
            assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDATE);
            dao.setCreatedBy(resultSet.getString(COLNAME_CREATEDBY));
            assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDBY);
            dao.setLastModifiedDate(resultSet.getTimestamp(COLNAME_LASTUPDATE));
            assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATE);
            dao.setLastModifiedBy(resultSet.getString(COLNAME_LASTUPDATEBY));
            assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATEBY);
            onInitializeDao(target, resultSet);
            DataRowState oldRowState = dao.rowState;
            dao.rowState = DataRowState.MODIFIED;
            dao.getPropertyChangeSupport().firePropertyChange(PROP_ROWSTATE, oldRowState, dao.rowState);
        }

        public void save(T dao, Connection connection) throws SQLException {
            Objects.requireNonNull(dao, "Data access object cannot be null");
            Objects.requireNonNull(connection, "Connection cannot be null");
            synchronized (dao) {
                assert dao.getRowState() != DataRowState.DELETED : String.format("%s has been deleted", getClass().getName());
                StringBuilder sql = new StringBuilder();
                //HashMap<String, Integer> indexes = new HashMap<>();
                List<String> extendedFields = getExtendedColNames();
                if (dao.getRowState() == DataRowState.NEW) {
                    sql.append("INSERT INTO `").append(getTableName_old()).append("` (`").append(String.join("`, `", extendedFields))
                            .append("`, `").append(String.join("`, `", Arrays.asList(COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY,
                            COLNAME_CREATEDATE, COLNAME_CREATEDBY))).append("`) VALUES (?");
                    int e = extendedFields.size() + 4;
                    for (int i = 1; i < e; i++) {
                        sql.append(", ?");
                    }
                    sql.append(")");
                } else {
                    sql.append("UPDATE `").append(getTableName_old()).append("` SET `").append(String.join("` = ?, `", extendedFields))
                            .append("` = ?, `").append(COLNAME_LASTUPDATE).append("` = ?, `").append(COLNAME_LASTUPDATEBY).append("` = ? WHERE `")
                            .append(getPrimaryKeyColName()).append(" = ?");
                }
                LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql.toString()));
                int pk;
                try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                    setSaveStatementValues(dao, ps);
                    int index = extendedFields.size();
                    ps.setTimestamp(index++, dao.getLastModifiedDate());
                    ps.setString(index++, dao.getLastModifiedBy());
                    if (dao.getRowState() == DataRowState.NEW) {
                        ps.setTimestamp(index++, dao.getCreateDate());
                        ps.setString(index, dao.getCreatedBy());
                    } else {
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
                sql = new StringBuilder(getBaseSelectQuery());
                sql.append(" WHERE `").append(getPrimaryKeyColName()).append("`=%");
                LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql.toString()));
                try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                    ps.setInt(1, pk);
                    try (ResultSet rs = ps.getResultSet()) {
                        assert rs.next() : "Updated record not found";
                        initializeDao(dao, rs);
                    }
                }
            }
        }

        public Optional<T> loadByPrimaryKey(Connection connection, int pk) throws SQLException {
            Objects.requireNonNull(connection, "Connection cannot be null");
            String sql = String.format("%s WHERE p.`%s`=?", getBaseSelectQuery(), getPrimaryKeyColName());
            LOG.log(Level.SEVERE, String.format("Finalizing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, pk);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(fromResultSet(rs));
                    }
                }
            }
            return Optional.empty();
        }

        public void delete(T dao, Connection connection) throws SQLException {
            Objects.requireNonNull(dao, "Data access object cannot be null");
            Objects.requireNonNull(connection, "Connection cannot be null");
            synchronized (dao) {
                assert dao.getRowState() != DataRowState.DELETED : String.format("%s has already been deleted", getClass().getName());
                assert dao.getRowState() != DataRowState.NEW : String.format("%s has not been inserted into the database", getClass().getName());
                String sql = String.format("DELETE FROM `%s` WHERE `%s` = ?", getTableName_old(), getPrimaryKeyColName());
                LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql));
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, dao.getPrimaryKey());
                    assert ps.executeUpdate() > 0 : String.format("Failed to delete associated database row on %s where %s = %d",
                            getTableName_old(), getPrimaryKeyColName(), dao.getPrimaryKey());
                }
                dao.setDeleted();
            }
        }

        public abstract String getDeleteDependencyMessage(T dao, Connection connection) throws SQLException;

        public abstract String getSaveConflictMessage(T dao, Connection connection) throws SQLException;

        public abstract ModelFilter<T, M> getAllItemsFilter();

        public abstract ModelFilter<T, M> getDefaultFilter();
    }

}
