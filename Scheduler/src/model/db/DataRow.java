package model.db;

import com.mysql.jdbc.Connection;
import java.beans.PropertyChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;
import scheduler.InvalidOperationException;

/**
 * Base class for data rows from the database.
 * @author Leonard T. Erwine
 */
public abstract class DataRow implements model.Record {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    /**
     * Value of {@link #getRowState()} property when the row has not been added to the database.
     */
    public static final byte ROWSTATE_NEW = 0;
    
    /**
     * Value of {@link #getRowState()} property when the row has no changes to be saved the database.
     */
    public static final byte ROWSTATE_UNMODIFIED = 1;
    
    /**
     * Value of {@link #getRowState()} property when the row has changes that need to be saved the database.
     */
    public static final byte ROWSTATE_MODIFIED = 2;
    
    /**
     * Value of {@link #getRowState()} property when the row has been deleted from the database.
     */
    public static final byte ROWSTATE_DELETED = 3;
    
    //<editor-fold defaultstate="collapsed" desc="primaryKey">
    
    /**
     * The name of the property that contains the database primary key value.
     */
    public static final String PROP_PRIMARYKEY = "primaryKey";
    /*
    private int primaryKey;
    
    public final int getPrimaryKey() { return primaryKey; }
    */
    private final ReadOnlyIntegerWrapper primaryKey;

    /**
    * Gets the primary key value for the data row.
    * @return The primary key value for the data row. This value will be zero if the data row hasn't been inserted into the database.
    */
    @Override
    public int getPrimaryKey() { return primaryKey.get(); }

    public ReadOnlyIntegerProperty primaryKeyProperty() { return primaryKey.getReadOnlyProperty(); }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createDate">
    
    /**
     * The name of the property that contains the date and time when the row was inserted into the database.
     */
    public static final String PROP_CREATEDATE = "createDate";
    
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;

    /**
     * Gets the date and time when the row was inserted into the database.
     * @return The date and time when the row was inserted into the database.
     */
    public LocalDateTime getCreateDate() { return createDate.get(); }

    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() { return createDate.getReadOnlyProperty(); }
    
    /*
    private LocalDateTime createDate;
    
    public final LocalDateTime getCreateDate() { return createDate; }
    */
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createdBy">
    
    /**
     * The name of the property that contains the {@link UserRow} who inserted the row into the database.
     */
    public static final String PROP_CREATEDBY = "createdBy";
    
    private final ReadOnlyStringWrapper createdBy;

    /**
     * Gets the {@link UserRow#userName} of the {@link UserRow} who inserted the row into the database.
     * @return The {@link UserRow#userName} of the {@link UserRow} who inserted the row into the database.
     */
    public String getCreatedBy() { return createdBy.get(); }

    public ReadOnlyStringProperty createdByProperty() { return createdBy.getReadOnlyProperty(); }
    
    /*
    private String createdBy;
    
    public String getCreatedBy() { return createdBy; }
    */
    
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdate">
    
    /**
     * The name of the property that contains the date and time when the row was last updated.
     */
    public static final String PROP_LASTUPDATE = "lastUpdate";
    
    private final ReadOnlyObjectWrapper<LocalDateTime> lastUpdate;

    /**
     * Gets the date and time when the row was last updated.
     * @return The date and time when the row was last updated.
     */
    public LocalDateTime getLastUpdate() { return lastUpdate.get(); }

    public ReadOnlyObjectProperty<LocalDateTime> lastUpdateProperty() { return lastUpdate.getReadOnlyProperty(); }
    
    /*
    private LocalDateTime lastUpdate;
    
    public final LocalDateTime getLastUpdate() { return lastUpdate; }
    */
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdateBy">
    
    /**
     * The name of the property that contains the {@link UserRow#userName} of the {@link UserRow} who last updated the row in the database.
     */
    public static final String PROP_LASTUPDATEBY = "lastUpdateBy";
    
    private final ReadOnlyStringWrapper lastUpdateBy;

    /**
     * Gets the {@link UserRow#userName} of the {@link UserRow} who last updated the row in the database.
     * @return The {@link UserRow#userName} of the {@link UserRow} who last updated the row in the database.
     */
    public String getLastUpdateBy() { return lastUpdateBy.get(); }

    public ReadOnlyStringProperty lastUpdateByProperty() { return lastUpdateBy.getReadOnlyProperty(); }
    
    /*
    private String lastUpdateBy;
    
    public final String getLastUpdateBy() { return lastUpdateBy; }
    */
    
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastDbSync">
    
    /**
     * The name of the property that contains the date and time when the row was last updated.
     */
    public static final String PROP_LASTDBSYNC = "lastDbSync";
    private final ReadOnlyObjectWrapper<LocalDateTime> lastDbSync;

    /**
     * Gets the date and time when the row was last loaded from the database.
     * @return The date and time when the row was last loaded from the database.
     */
    public LocalDateTime getLastDbSync() {
        return lastDbSync.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> lastDbSyncProperty() { return lastDbSync.getReadOnlyProperty(); }
    
    /*
    private LocalDateTime lastDbSync;
    
    public final LocalDateTime getLastDbSync() { return lastDbSync; }
    */
    
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="rowState">
    
    /**
     * The name of the property that indicates the current row disposition.
     */
    public static final String PROP_ROWSTATE = "rowState";
    
    private final ReadOnlyIntegerWrapper rowState;

    public int getRowState() { return rowState.get(); }

    public ReadOnlyIntegerProperty rowStateProperty() { return rowState.getReadOnlyProperty(); }
    
    /**
     * Gets the database disposition for the current row.
     * @return The database disposition for the current row.
     */
    /*
    private byte rowState;
    
    public final byte getRowState() { return rowState; }
    */
    
    //</editor-fold>
    
    //</editor-fold>
    
    protected static class RowIdChangeListener<R extends model.Record> implements ChangeListener<Number> {
        private final ObjectProperty<R> recordProperty;
        private final ReadOnlyIntegerWrapper primaryKeyProperty;
        private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper();

        public boolean isValid() { return valid.get(); }

        public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }
        
        RowIdChangeListener(ObjectProperty<R> recordProperty, ReadOnlyIntegerWrapper primaryKeyProperty) {
            this.recordProperty = recordProperty;
            this.primaryKeyProperty = primaryKeyProperty;
            recordProperty.addListener((ObservableValue<? extends R> observable, R oldValue, R newValue) -> {
                recordChanged(oldValue, newValue);
            });
            recordChanged(null, recordProperty.get());
        }
        
        private void recordChanged(R oldValue, R newValue) {
            if (oldValue != null && oldValue instanceof DataRow)
                ((DataRow)oldValue).primaryKeyProperty().removeListener(this);
            if (newValue != null) {
                if (newValue instanceof DataRow) {
                    ((DataRow)newValue).primaryKeyProperty().addListener(this);
                    int rowState = ((DataRow)newValue).getRowState();
                    valid.set(rowState == ROWSTATE_UNMODIFIED || rowState == ROWSTATE_MODIFIED);
                } else
                    valid.set(true);
                primaryKeyProperty.set(newValue.getPrimaryKey());
            } else {
                valid.set(false);
                primaryKeyProperty.set(0);
            }
        }
        
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if (newValue != null && newValue instanceof Integer) {
                primaryKeyProperty.set((int)newValue);
                R c = recordProperty.get();
                if (c != null) {
                    if (c instanceof DataRow) {
                        int rowState = ((DataRow)c).getRowState();
                        valid.set(rowState == ROWSTATE_UNMODIFIED || rowState == ROWSTATE_MODIFIED);
                    } else
                        valid.set(true);
                    return;
                }
            } else
                primaryKeyProperty.set(0);
            valid.set(false);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    /**
     * Creates a new data row object for a row that hasn't been inserted into the database.
     */
    protected DataRow() {
        primaryKey = new ReadOnlyIntegerWrapper(0);
        createDate = new ReadOnlyObjectWrapper<>(LocalDateTime.now());
        lastUpdate = new ReadOnlyObjectWrapper<>(createDate.getValue());
        Optional<UserRow> user = scheduler.App.getCurrentUser();
        lastUpdateBy = new ReadOnlyStringWrapper((user.isPresent()) ? user.get().getUserName() : "");
        createdBy = new ReadOnlyStringWrapper(lastUpdateBy.getValue());
        rowState = new ReadOnlyIntegerWrapper(ROWSTATE_NEW);
        lastDbSync = new ReadOnlyObjectWrapper<>(LocalDateTime.MIN);
    }
    
    protected DataRow(DataRow row) throws InvalidOperationException {
        if (row == null || row.getRowState() != ROWSTATE_UNMODIFIED)
            throw new InvalidOperationException("Can only clone unmodified rows");
        primaryKey = new ReadOnlyIntegerWrapper(row.getPrimaryKey());
        createDate = new ReadOnlyObjectWrapper<>(row.getCreateDate());
        createdBy = new ReadOnlyStringWrapper(row.getCreatedBy());
        lastUpdate = new ReadOnlyObjectWrapper<>(row.getLastUpdate());
        lastUpdateBy = new ReadOnlyStringWrapper(row.getLastUpdateBy());
        rowState = new ReadOnlyIntegerWrapper(row.getRowState());
        lastDbSync = new ReadOnlyObjectWrapper<>(row.getLastDbSync());
    }
    
    /**
     * Creates a new data row object from a database query result row.
     * @param rs The {@link ResultSet} object that will be used to initialize the object's properties.
     * @throws java.sql.SQLException
     */
    protected DataRow(ResultSet rs) throws SQLException {
        primaryKey = new ReadOnlyIntegerWrapper(rs.getInt(getPrimaryKeyColName(getClass())));
        createDate = new ReadOnlyObjectWrapper<>(rs.getTimestamp(PROP_CREATEDATE).toLocalDateTime());
        createdBy = new ReadOnlyStringWrapper(rs.getString(PROP_CREATEDBY));
        lastUpdate = new ReadOnlyObjectWrapper<>(rs.getTimestamp(PROP_LASTUPDATE).toLocalDateTime());
        lastUpdateBy = new ReadOnlyStringWrapper(rs.getString(PROP_LASTUPDATEBY));
        rowState = new ReadOnlyIntegerWrapper(ROWSTATE_UNMODIFIED);
        lastDbSync = new ReadOnlyObjectWrapper<>(LocalDateTime.now());
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database Read/Write methods">
    
    public static final <R extends DataRow> String getTableName(Class<R> rowClass) {
        Class<TableName> tableNameClass = TableName.class;
        if (rowClass.isAnnotationPresent(tableNameClass)) {
            String n = rowClass.getAnnotation(tableNameClass).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        throw new InternalException("Table name not defined");
    }
    
    public static final <R extends DataRow> String getPrimaryKeyColName(Class<R> rowClass) {
        Class<PrimaryKey> pkClass = PrimaryKey.class;
        if (rowClass.isAnnotationPresent(pkClass)) {
            String n = rowClass.getAnnotation(pkClass).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        throw new InternalException("Primary key column name not defined");
    }

    protected abstract String getSelectQuery();
    
    protected abstract String[] getColumnNames();
    
    protected abstract void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException;
    
    protected abstract void refreshFromDb(ResultSet rs) throws SQLException;
    
    private void refreshFromDb(Connection connection, Class<? extends DataRow> rowClass) throws SQLException, InvalidOperationException {
        StringBuilder sql = new StringBuilder();
        sql.append(getSelectQuery()).append(" WHERE `").append(getPrimaryKeyColName(rowClass)).append("` = ?");
        // Get row from database matching the current primary key value.
        Logger.getLogger(DataRow.class.getName()).log(Level.SEVERE, "Executing query: %s", sql.toString());
        PreparedStatement ps = connection.prepareStatement(sql.toString());
        ps.setInt(1, getPrimaryKey());
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            if (getRowState() == ROWSTATE_NEW) {
                createDate.setValue(rs.getTimestamp(PROP_CREATEDATE).toLocalDateTime());
                createdBy.setValue(rs.getString(PROP_CREATEDBY));
            }
            lastUpdate.setValue(rs.getTimestamp(PROP_LASTUPDATE).toLocalDateTime());
            lastUpdateBy.setValue(rs.getString(PROP_LASTUPDATEBY));
            refreshFromDb(rs);
            lastDbSync.setValue(LocalDateTime.now());
            rowState.setValue(ROWSTATE_UNMODIFIED);
        }
    }
    
    private void insert(Connection connection, String tableName) throws SQLException {
        String[] fieldNames = getColumnNames();
        String sql = String.format("INSERT INTO `%s` (%s, `%s`, `%s`, `%s`, `%s`) VALUES (%s, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", tableName,
                Arrays.stream(fieldNames).map((s) -> "`" + s + "`").reduce((p, n) -> p + ", " + n).get(),
                PROP_CREATEDBY, PROP_LASTUPDATEBY, PROP_CREATEDATE, PROP_LASTUPDATE,
                Arrays.stream(fieldNames).map((s) -> "?").reduce((p, n) -> p + ", " + n).get());
        Logger.getLogger(DataRow.class.getName()).log(Level.INFO, "Executing query: %s", sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        setColumnValues(ps, fieldNames);
        String userName = scheduler.App.getCurrentUser().get().getUserName();
        int index = fieldNames.length + 1;
        ps.setString(index++, userName);
        ps.setString(index, userName);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        primaryKey.setValue(rs.getInt(1));
    }
    
    private void update(Connection connection, Class<? extends DataRow> rowClass, String tableName) throws SQLException {
        String[] fieldNames = getColumnNames();
        String sql = String.format("UPDATE `%s` SET %s, `%s` = ?, `%s` = CURRENT_TIMESTAMP WHERE `%s` = ?",
                tableName, Arrays.stream(fieldNames).map((s) -> "`" + s + "` = ?").reduce((p, n) -> p + ", " + n).get(),
                PROP_LASTUPDATEBY, PROP_LASTUPDATE, getPrimaryKeyColName(rowClass));
        Logger.getLogger(DataRow.class.getName()).log(Level.INFO, "Executing query: %s", sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        setColumnValues(ps, fieldNames);
        String userName = scheduler.App.getCurrentUser().get().getUserName();
        int index = fieldNames.length + 1;
        ps.setString(index++, userName);
        ps.setInt(index, getPrimaryKey());
        ps.executeUpdate();
    }
    
    public final void saveChanges(Connection connection) throws SQLException, InvalidOperationException {
        Class<? extends DataRow> rowClass = getClass();
        String tableName = getTableName(rowClass);
        // Saving changes to database is not applicable to rows that have been deleted from the database.
        if (getRowState() == ROWSTATE_DELETED)
            throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                    tableName));
        if (getRowState() == ROWSTATE_NEW)
            insert(connection, tableName);
        else
            update(connection, rowClass, tableName);
        refreshFromDb(connection, getClass());
    }
    
    public final void refreshFromDb(Connection connection) throws SQLException, InvalidOperationException {
        Class<? extends DataRow> rowClass = getClass();
        String tableName = getTableName(rowClass);
        // Refresh from database is not applicable to rows that have not been retrieved from the database.
        if (getRowState() == ROWSTATE_DELETED)
            throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                    tableName));
        if (getRowState() == ROWSTATE_NEW)
            throw new InvalidOperationException(String.format("{0} row has not been added the database",
                    tableName));
        refreshFromDb(connection, rowClass);
    }
    
    public final void delete(Connection connection) throws SQLException, InvalidOperationException {
        Class<? extends DataRow> rowClass = getClass();
        String tableName = getTableName(rowClass);
        // Saving changes to database is not applicable to rows that are new or have been deleted from the database.
        if (getRowState() == ROWSTATE_DELETED)
            throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                    tableName));
        if (getRowState() == ROWSTATE_NEW)
            throw new InvalidOperationException(String.format("{0} row has not been added the database",
                    tableName));
        PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + tableName + "`  WHERE `" +
                getPrimaryKeyColName(rowClass) + "` = ?");
        ps.setInt(1, getPrimaryKey());
        ps.executeUpdate();
        rowState.setValue(ROWSTATE_DELETED);
    }
    
    protected static final <R extends DataRow> ObservableList<R> selectFromDb(Connection connection, String sql,
            Function<ResultSet, R> create) throws SQLException {
        ObservableList<R> result = FXCollections.observableArrayList();
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            result.add(create.apply(rs));
        return result;
    }
    
    protected static final <R extends DataRow> ObservableList<R> selectFromDb(Connection connection, String sql,
            Function<ResultSet, R> create, Consumer<PreparedStatement> setValues) throws SQLException {
        ObservableList<R> result = FXCollections.observableArrayList();
        PreparedStatement ps = connection.prepareStatement(sql);
        setValues.accept(ps);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            result.add(create.apply(rs));
        return result;
    }
    
    public static final <R extends DataRow> Optional<R> selectFirstFromDb(Connection connection, String sql,
            Function<ResultSet, R> create, Consumer<PreparedStatement> setValues) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        setValues.accept(ps);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return Optional.of(create.apply(rs));
        return Optional.empty();
    }
    
    public static final <R extends DataRow> Optional<R> selectFirstFromDb(Connection connection, String sql,
            Function<ResultSet, R> create) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return Optional.of(create.apply(rs));
        return Optional.empty();
    }
    
    //</editor-fold>
}
