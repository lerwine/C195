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
    /**
     * Monitor object for synchronized operations.
     */
    protected final Object syncRoot;
    
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
    
    private int primaryKey;
    
    /**
     * Gets the primary key value for the data row.
     * @return The primary key value for the data row. This value will be zero if the data row hasn't been inserted into the database.
     */
    @Override
    public final int getPrimaryKey() { return primaryKey; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createDate">
    
    /**
     * The name of the property that contains the date and time when the row was inserted into the database.
     */
    public static final String PROP_CREATEDATE = "createDate";
    
    private LocalDateTime createDate;
    
    /**
     * Gets the date and time when the row was inserted into the database.
     * @return The date and time when the row was inserted into the database.
     */
    public final LocalDateTime getCreateDate() { return createDate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createdBy">
    
    /**
     * The name of the property that contains the {@link UserRow} who inserted the row into the database.
     */
    public static final String PROP_CREATEDBY = "createdBy";
    
    private String createdBy;
    
    /**
     * Gets the {@link UserRow#userName} of the {@link UserRow} who inserted the row into the database.
     * @return The {@link UserRow#userName} of the {@link UserRow} who inserted the row into the database.
     */
    public String getCreatedBy() { return createdBy; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdate">
    
    /**
     * The name of the property that contains the date and time when the row was last updated.
     */
    public static final String PROP_LASTUPDATE = "lastUpdate";
    
    private LocalDateTime lastUpdate;
    
    /**
     * Gets the date and time when the row was last updated.
     * @return The date and time when the row was last updated.
     */
    public final LocalDateTime getLastUpdate() { return lastUpdate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdateBy">
    
    /**
     * The name of the property that contains the {@link UserRow#userName} of the {@link UserRow} who last updated the row in the database.
     */
    public static final String PROP_LASTUPDATEBY = "lastUpdateBy";
    
    private String lastUpdateBy;
    
    /**
     * Gets the {@link UserRow#userName} of the {@link UserRow} who last updated the row in the database.
     * @return The {@link UserRow#userName} of the {@link UserRow} who last updated the row in the database.
     */
    public final String getLastUpdateBy() { return lastUpdateBy; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastDbSync">
    
    /**
     * The name of the property that contains the date and time when the row was last updated.
     */
    public static final String PROP_LASTDBSYNC = "lastDbSync";
    
    private LocalDateTime lastDbSync;
    
    /**
     * Gets the date and time when the row was last loaded from the database.
     * @return The date and time when the row was last loaded from the database.
     */
    public final LocalDateTime getLastDbSync() { return lastDbSync; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="rowState">
    
    private byte rowState;
    
    /**
     * The name of the property that indicates the current row disposition.
     */
    public static final String PROP_ROWSTATE = "rowState";
    
    /**
     * Gets the database disposition for the current row.
     * @return The database disposition for the current row.
     */
    public final byte getRowState() { return rowState; }
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    /**
     * Creates a new data row object for a row that hasn't been inserted into the database.
     */
    protected DataRow() {
        syncRoot = new Object();
        primaryKey = 0;
        createDate = lastUpdate = LocalDateTime.now();
        Optional<UserRow> user = scheduler.App.getCurrentUser();
        if (user.isPresent())
            createdBy = lastUpdateBy = user.get().getUserName();
        else
            createdBy = lastUpdateBy = "";
        rowState = ROWSTATE_NEW;
        lastDbSync = LocalDateTime.MIN;
    }
    
    protected DataRow(DataRow row) throws InvalidOperationException {
        if (row == null || row.rowState != ROWSTATE_UNMODIFIED)
            throw new InvalidOperationException("Can only clone unmodified rows");
        syncRoot = new Object();
        primaryKey = row.primaryKey;
        createDate = row.createDate;
        createdBy = row.createdBy;
        lastUpdate = row.lastUpdate;
        lastUpdateBy = row.lastUpdateBy;
        rowState = row.rowState;
        lastDbSync = row.lastDbSync;
    }
    
    /**
     * Creates a new data row object from a database query result row.
     * @param rs The {@link ResultSet} object that will be used to initialize the object's properties.
     * @throws java.sql.SQLException
     */
    protected DataRow(ResultSet rs) throws SQLException {
        syncRoot = new Object();
        primaryKey = rs.getInt(getPrimaryKeyColName(getClass()));
        createDate = rs.getTimestamp(PROP_CREATEDATE).toLocalDateTime();
        createdBy = rs.getString(PROP_CREATEDBY);
        lastUpdate = rs.getTimestamp(PROP_LASTUPDATE).toLocalDateTime();
        lastUpdateBy = rs.getString(PROP_LASTUPDATEBY);
        rowState = ROWSTATE_UNMODIFIED;
        lastDbSync = LocalDateTime.now();
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
        ps.setInt(1, primaryKey);
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            try {
                if (rowState == ROWSTATE_NEW) {
                    deferPropertyChangeEvent(PROP_PRIMARYKEY);
                    deferPropertyChangeEvent(PROP_CREATEDATE);
                    deferPropertyChangeEvent(PROP_CREATEDBY);
                }
                deferPropertyChangeEvent(PROP_LASTUPDATE);
                deferPropertyChangeEvent(PROP_LASTUPDATEBY);
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(DataRow.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (rowState == ROWSTATE_NEW) {
                createDate = rs.getTimestamp(PROP_CREATEDATE).toLocalDateTime();
                createdBy = rs.getString(PROP_CREATEDBY);
            }
            lastUpdate = rs.getTimestamp(PROP_LASTUPDATE).toLocalDateTime();
            lastUpdateBy = rs.getString(PROP_LASTUPDATEBY);
            try {
                refreshFromDb(rs);
                lastDbSync = LocalDateTime.now();
                deferPropertyChangeEvent(PROP_ROWSTATE);
                rowState = ROWSTATE_UNMODIFIED;
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(DataRow.class.getName()).log(Level.SEVERE, null, ex);
            } finally { fireDeferredPropertyChanges(); }
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

        try {
            deferPropertyChangeEvent(PROP_PRIMARYKEY);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(DataRow.class.getName()).log(Level.SEVERE, "Error refreshing from database", ex);
            throw new RuntimeException("Error refreshing from database", ex);
        }

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        primaryKey = rs.getInt(1);
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
        ps.setInt(index, primaryKey);
        ps.executeUpdate();
    }
    
    public final void saveChanges(Connection connection) throws SQLException, InvalidOperationException {
        synchronized (syncRoot) {
            Class<? extends DataRow> rowClass = getClass();
            String tableName = getTableName(rowClass);
            // Saving changes to database is not applicable to rows that have been deleted from the database.
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                        tableName));
            if (rowState == ROWSTATE_NEW)
                insert(connection, tableName);
            else
                update(connection, rowClass, tableName);
            refreshFromDb(connection, getClass());
        }
        fireDeferredPropertyChanges();
    }
    
    public final void refreshFromDb(Connection connection) throws SQLException, InvalidOperationException {
        synchronized (syncRoot) {
            Class<? extends DataRow> rowClass = getClass();
            String tableName = getTableName(rowClass);
            // Refresh from database is not applicable to rows that have not been retrieved from the database.
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                        tableName));
            if (rowState == ROWSTATE_NEW)
                throw new InvalidOperationException(String.format("{0} row has not been added the database",
                        tableName));
            refreshFromDb(connection, rowClass);
        }
        fireDeferredPropertyChanges();
    }
    
    public final void delete(Connection connection) throws SQLException, InvalidOperationException {
        synchronized (syncRoot) {
            Class<? extends DataRow> rowClass = getClass();
            String tableName = getTableName(rowClass);
            // Saving changes to database is not applicable to rows that are new or have been deleted from the database.
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                        tableName));
            if (rowState == ROWSTATE_NEW)
                throw new InvalidOperationException(String.format("{0} row has not been added the database",
                        tableName));
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + tableName + "`  WHERE `" +
                    getPrimaryKeyColName(rowClass) + "` = ?");
            ps.setInt(1, primaryKey);
            ps.executeUpdate();
            try {
                deferPropertyChangeEvent(PROP_ROWSTATE);
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(DataRow.class.getName()).log(Level.SEVERE, null, ex);
            }
            rowState = ROWSTATE_DELETED;
        }
        fireDeferredPropertyChanges();
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
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport">
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  The programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  The programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  The programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, short oldValue, short newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  The programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, LocalDateTime oldValue, LocalDateTime newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  The programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, String oldValue, String newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param <R>           The type of value that was changed.
     * @param propertyName  The programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final <R extends model.Record> void firePropertyChange(String propertyName, R oldValue, R newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    protected void deferPropertyChangeEvent(String propertyName, String fieldName) throws NoSuchFieldException {
        propertyChangeSupport.deferPropertyChangeEvent(propertyName, fieldName);
    }

    protected void deferPropertyChangeEvent(String propertyName) throws NoSuchFieldException {
        propertyChangeSupport.deferPropertyChangeEvent(propertyName);
    }

    protected void deferPropertyChangeEvent(String propertyName, Function<DataRow, Object> getValue) {
        propertyChangeSupport.deferPropertyChangeEvent(propertyName, getValue);
    }
    
    protected void fireDeferredPropertyChanges() { propertyChangeSupport.fireDeferredPropertyChanges(); }
    
    private transient final model.DeferrablePropertyChangeSupport<DataRow> propertyChangeSupport = new model.DeferrablePropertyChangeSupport<>(this);
    
    /**
     * Add a {@link PropertyChangeListener} to the listener list.
     * @param listener The {@link PropertyChangeListener} to be added.
     */
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes a {@link PropertyChangeListener} from the listener list.
     * @param listener The {@link PropertyChangeListener} to be removed.
     */
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    //</editor-fold>
}
