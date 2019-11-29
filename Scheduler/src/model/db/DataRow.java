/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import com.mysql.jdbc.Connection;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import utils.InternalException;
import utils.InvalidOperationException;

/**
 * Base class for data rows from the database.
 * @author Leonard T. Erwine
 */
public abstract class DataRow {
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
     * The name of the property that contains the {@link User} who inserted the row into the database.
     */
    public static final String PROP_CREATEDBY = "createdBy";
    
    private String createdBy;
    
    /**
     * Gets the {@link User#userName} of the {@link User} who inserted the row into the database.
     * @return The {@link User#userName} of the {@link User} who inserted the row into the database.
     */
    public String getCreatedBy() { return createdBy; }
    
    public Optional<User> lookupCreatedBy(Connection connection) throws SQLException {
        return User.getByUserName(connection, createdBy, true);
    }
    
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
     * The name of the property that contains the {@link User#userName} of the {@link User} who last updated the row in the database.
     */
    public static final String PROP_LASTUPDATEBY = "lastUpdateBy";
    
    private String lastUpdateBy;
    
    /**
     * Gets the {@link User#userName} of the {@link User} who last updated the row in the database.
     * @return The {@link User#userName} of the {@link User} who last updated the row in the database.
     */
    public final String getLastUpdateBy() { return lastUpdateBy; }
    
    public Optional<User> lookupLastUpdateBy(Connection connection) throws SQLException {
        return User.getByUserName(connection, lastUpdateBy, true);
    }
    
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
        primaryKey = 0;
        createDate = lastUpdate = LocalDateTime.now();
        Optional<User> user = scheduler.Context.getCurrentUser();
        if (user.isPresent())
            createdBy = lastUpdateBy = user.get().getUserName();
        else
            createdBy = lastUpdateBy = "";
        rowState = ROWSTATE_NEW;
    }
    
    protected DataRow(DataRow row) throws InvalidOperationException {
        if (row == null || row.rowState != ROWSTATE_UNMODIFIED)
            throw new InvalidOperationException("Can only clone unmodified rows");
        primaryKey = row.primaryKey;
        createDate = row.createDate;
        createdBy = row.createdBy;
        lastUpdate = row.lastUpdate;
        lastUpdateBy = row.lastUpdateBy;
        rowState = row.rowState;
    }
    
    /**
     * Creates a new data row object from a database query result row.
     * @param rs The {@link ResultSet} object that will be used to initialize the object's properties.
     * @throws java.sql.SQLException
     */
    protected DataRow(ResultSet rs) throws SQLException {
        primaryKey = rs.getInt(getPrimaryKeyColName(getClass()));
        createDate = rs.getTimestamp(PROP_CREATEDATE).toLocalDateTime();
        createdBy = rs.getString(PROP_CREATEDBY);
        lastUpdate = rs.getTimestamp(PROP_LASTUPDATE).toLocalDateTime();
        lastUpdateBy = rs.getString(PROP_LASTUPDATEBY);
        rowState = ROWSTATE_UNMODIFIED;
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

    protected abstract String[] getColumnNames();
    
    protected abstract void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException;
    
    protected abstract void refreshFromDb(ResultSet rs) throws SQLException;
    
    public final void refreshFromDb(Connection connection) throws SQLException, InvalidOperationException {
        Class<? extends DataRow> rowClass = getClass();
        String tableName = getTableName(rowClass);
        // Refresh from database is not applicable to rows that have not been retrieved from the database.
        if (rowState == ROWSTATE_DELETED)
            throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                    tableName));
        if (rowState == ROWSTATE_NEW)
            throw new InvalidOperationException(String.format("{0} row has not been added the database",
                    tableName));
        // Get row from database matching the current primary key value.
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `" +
                getPrimaryKeyColName(rowClass) + "` = ?");
        ps.setInt(1, primaryKey);
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            LocalDateTime oldCreateDate = createDate;
            String oldCreatedBy = createdBy;
            LocalDateTime oldLastUpdate = lastUpdate;
            String oldLastUpdateBy = lastUpdateBy;
            createDate = rs.getTimestamp(PROP_CREATEDATE).toLocalDateTime();
            createdBy = rs.getString(PROP_CREATEDBY);
            lastUpdate = rs.getTimestamp(PROP_LASTUPDATE).toLocalDateTime();
            lastUpdateBy = rs.getString(PROP_LASTUPDATEBY);
            // Let extended class update its properites before firing property change events.
            try { refreshFromDb(rs); }
            finally {
                // Execute property change events in nested try/finally statements to ensure that all
                // events get fired, even if one of the property change listeners throws an exception.
                try { firePropertyChange(PROP_CREATEDATE, oldCreateDate, createDate); }
                finally {
                    try { firePropertyChange(PROP_CREATEDBY, oldCreatedBy, createdBy); }
                    finally {
                        try { firePropertyChange(PROP_LASTUPDATE, oldLastUpdate, lastUpdate); }
                        finally { firePropertyChange(PROP_LASTUPDATEBY, oldLastUpdateBy, lastUpdateBy); }
                    }
                }
            }
            
            // Row state only gets changed if no exeptions were thrown.
            byte oldRowState = rowState;
            rowState = ROWSTATE_UNMODIFIED;
            firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
        }
    }
    
    public void saveChanges(Connection connection) throws SQLException, InvalidOperationException {
        Class<? extends DataRow> rowClass = getClass();
        String tableName = getTableName(rowClass);
        // Saving changes to database is not applicable to rows that have been deleted from the database.
        if (rowState == ROWSTATE_DELETED)
            throw new InvalidOperationException(String.format("{0} row has been deleted from the database",
                    tableName));
        String[] fieldNames = getColumnNames();
        int index = fieldNames.length + 1;
        String userName = scheduler.Context.getCurrentUser().get().getUserName();
        PreparedStatement ps;
        if (rowState == ROWSTATE_NEW) {
            ps = connection.prepareStatement("INSERT INTO `" + tableName +
                    "` (" + Arrays.stream(fieldNames).map((s) -> "`" + s + "`").reduce((p, n) -> p + ", " + n).get() +
                    "`" + PROP_CREATEDBY + "`, `" + PROP_LASTUPDATEBY + "`, `" + PROP_CREATEDATE +
                    "`, `" + PROP_LASTUPDATE + "`) VALUES (" +
                    Arrays.stream(fieldNames).map((s) -> "?").reduce((p, n) -> p + ", " + n).get() +
                    ", ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            setColumnValues(ps, fieldNames);
            ps.setString(index++, userName);
            ps.setString(index, userName);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int oldPk = primaryKey;
            primaryKey = rs.getInt(1);
            rowState = ROWSTATE_UNMODIFIED;
            try {
                DataRow.this.refreshFromDb(connection);
            } catch (InvalidOperationException ex) {
                Logger.getLogger(DataRow.class.getName()).log(Level.SEVERE, "Error refreshing from database", ex);
                throw new RuntimeException("Error refreshing from database", ex);
            } finally {
                try { firePropertyChange(PROP_PRIMARYKEY, oldPk, primaryKey); }
                finally { firePropertyChange(PROP_ROWSTATE, ROWSTATE_NEW, rowState); }
            }
        } else {
            ps = connection.prepareStatement("UPDATE `" + tableName +
                    "` SET " + Arrays.stream(fieldNames).map((s) -> "`" + s + "` = ?").reduce((p, n) -> p + ", " + n).get() +
                    ", `" + PROP_LASTUPDATEBY + "` = ?, `" + PROP_LASTUPDATE + "` = CURRENT_TIMESTAMP WHERE `" +
                    getPrimaryKeyColName(rowClass) + "` = ?");
            setColumnValues(ps, fieldNames);
            ps.setString(index++, userName);
            ps.setInt(index, primaryKey);
            ps.executeUpdate();
            try {
                DataRow.this.refreshFromDb(connection);
            } catch (InvalidOperationException ex) {
                Logger.getLogger(DataRow.class.getName()).log(Level.SEVERE, "Error refreshing from database", ex);
                throw new RuntimeException("Error refreshing from database", ex);
            }
        }
    }
    
    public void delete(Connection connection) throws SQLException, InvalidOperationException {
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
        byte oldRowState = rowState;
        rowState = ROWSTATE_DELETED;
        firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
    }
    
    public static final <R extends DataRow> ObservableList<R> selectAllFromDb(Connection connection, Class<R> rowClass,
            Function<ResultSet, R> create) throws SQLException {
        ObservableList<R> result = FXCollections.observableArrayList();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + getTableName(rowClass) + "`");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            result.add(create.apply(rs));
        return result;
    }
    
    public static final <R extends DataRow> ObservableList<R> selectFromDb(Connection connection, Class<R> rowClass,
            Function<ResultSet, R> create, String whereClause, Consumer<PreparedStatement> setValues) throws SQLException {
        ObservableList<R> result = FXCollections.observableArrayList();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + getTableName(rowClass) + "` WHERE " + whereClause);
        if (setValues != null)
            setValues.accept(ps);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            result.add(create.apply(rs));
        return result;
    }
    
    public static final <R extends DataRow> Optional<R> selectFirstFromDb(Connection connection, Class<R> rowClass,
            Function<ResultSet, R> create, String whereClause, Consumer<PreparedStatement> setValues) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + getTableName(rowClass) + "` WHERE " + whereClause);
        if (setValues != null)
            setValues.accept(ps);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return Optional.of(create.apply(rs));
        return Optional.empty();
    }
    
    public static final <R extends DataRow> Optional<R> selectFromDbById(Connection connection, Class<R> rowClass,
            Function<ResultSet, R> create, int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + getTableName(rowClass) + "` WHERE `" + 
                getPrimaryKeyColName(rowClass) + "` = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return Optional.of(create.apply(rs));
        return Optional.empty();
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport">
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, short oldValue, short newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, LocalDateTime oldValue, LocalDateTime newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected final void firePropertyChange(String propertyName, String oldValue, String newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
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
