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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.annotations.DbColumn;
import model.meta.ColumnInfo;
import model.meta.SqlImportTypes;
import model.meta.TableInfo;
import utils.InvalidArgumentException;

/**
 * Base class for data rows from the database.
 * @author Leonard T. Erwine
 */
public class DataRow {
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
    
    @DbColumn
    private int primaryKey;
    
    /**
     * Gets the primary key value for the data row.
     * @return The primary key value for the data row. This value will be zero if the data row hasn't been inserted into the database.
     */
    public int getPrimaryKey() { return primaryKey; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createDate">
    
    /**
     * The name of the property that contains the date and time when the row was inserted into the database.
     */
    public static final String PROP_CREATEDATE = "createDate";
    
    @DbColumn
    private LocalDateTime createDate;
    
    /**
     * Gets the date and time when the row was inserted into the database.
     * @return The date and time when the row was inserted into the database.
     */
    public LocalDateTime getCreateDate() { return createDate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createdBy">
    
    /**
     * The name of the property that contains the {@link User#userName} of the {@link User} who inserted the row into the database.
     */
    public static final String PROP_CREATEDBY = "createdBy";
    
    @DbColumn
    private String createdBy;
    
    /**
     * Gets the {@link User#userName} of the {@link User} who inserted the row into the database.
     * @return The {@link User#userName} of the {@link User} who inserted the row into the database.
     */
    public String getCreatedBy() { return createdBy; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdate">
    
    /**
     * The name of the property that contains the date and time when the row was last updated.
     */
    public static final String PROP_LASTUPDATE = "lastUpdate";
    
    @DbColumn
    private LocalDateTime lastUpdate;
    
    /**
     * Gets the date and time when the row was last updated.
     * @return The date and time when the row was last updated.
     */
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdateBy">
    
    /**
     * The name of the property that contains the {@link User#userName} of the {@link User} who last updated the row in the database.
     */
    public static final String PROP_LASTUPDATEBY = "lastUpdateBy";
    
    @DbColumn
    private String lastUpdateBy;
    
    /**
     * Gets the {@link User#userName} of the {@link User} who last updated the row in the database.
     * @return The {@link User#userName} of the {@link User} who last updated the row in the database.
     */
    public String getLastUpdateBy() { return lastUpdateBy; }
    
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
    public byte getRowState() { return rowState; }
    
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
    
    /**
     * Creates a new data row object from a database query result row.
     * @param rb The {@link RowBuilder} object that will be used to initialize the object's properties.
     */
    protected DataRow(RowBuilder rb) {
        rb.tableInfo.stream().forEach(new Consumer<ColumnInfo>() {
            @Override
            public void accept(ColumnInfo c) {
                try {
                    c.apply(rb.resultSet, DataRow.this, propertyChangeSupport);
                }catch (SQLException | IllegalAccessException ex) {
                    String n = c.getColName();
                    throw new RuntimeException(String.format("Error applying value for {0} property", n), ex);
                }
            }
        });
        rowState = ROWSTATE_UNMODIFIED;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport">
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected void firePropertyChange(String propertyName, short oldValue, short newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected void firePropertyChange(String propertyName, LocalDateTime oldValue, LocalDateTime newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property update to listeners that have been registered to track updates of all properties or a property with the specified name.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      The old value of the property.
     * @param newValue      The new value of the property.
     */
    protected void firePropertyChange(String propertyName, String oldValue, String newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * Add a {@link PropertyChangeListener} to the listener list.
     * @param listener The {@link PropertyChangeListener} to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes a {@link PropertyChangeListener} from the listener list.
     * @param listener The {@link PropertyChangeListener} to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    //</editor-fold>

    /**
     * Pattern that matches a column value place holder.
     */
    static Pattern placeHolderPattern = Pattern.compile(":(:|[a-zA-Z][a-zA-Z\\d]+)");

    protected static <R extends DataRow> ArrayList<R> getFromDatabase(Class<R> rowClass, Connection connection, Function<RowBuilder<R>, R> newRow,
            String whereClause, Consumer<PreparedStatement> setValues) throws InvalidArgumentException, SQLException {
        TableInfo<R> tableInfo = new TableInfo(rowClass);
        String query = "SELECT * FROM `" + tableInfo.tableName() + "` WHERE " + whereClause;
        PreparedStatement ps = connection.prepareStatement(query);
        if (setValues != null)
            setValues.accept(ps);
        RowBuilder<R> rb = new RowBuilder(ps.getResultSet(), tableInfo);
        ArrayList<R> result = new ArrayList<>();
        while (rb.resultSet.next())
            result.add(newRow.apply(rb));
        return result;
    }
    
    protected static <R extends DataRow> Optional<R> getFirstFromDatabase(Class<R> rowClass, Connection connection, Function<RowBuilder<R>, R> newRow,
            String whereClause, BiConsumer<TableInfo<R>, PreparedStatement> setValues) throws InvalidArgumentException, SQLException {
        TableInfo<R> tableInfo = new TableInfo(rowClass);
        String query = "SELECT * FROM `" + tableInfo.tableName() + "` WHERE " + whereClause;
        PreparedStatement ps = connection.prepareStatement(query);
        if (setValues != null)
            setValues.accept(tableInfo, ps);
        RowBuilder<R> rb = new RowBuilder(ps.getResultSet(), tableInfo);
        if (rb.resultSet.next())
            Optional.of(newRow.apply(rb));
        return Optional.empty();
    }
    
    /**
     * Contains the {@link TableInfo} and {@link ResultSet} that will be used to initialize a data row.
     * @param <T> 
     */
    public static class RowBuilder<T extends DataRow> {
        private final TableInfo tableInfo;
        private final ResultSet resultSet;
        
        /**
         * Gets a {@link TableInfo} object that describes the data schema for the row.
         * @return A {@link TableInfo} object that describes the data schema for the row.
         */
        public TableInfo getTableInfo() { return tableInfo; }
        
        RowBuilder(ResultSet rs, TableInfo<T> t) {
            tableInfo = t;
            resultSet = rs;
        }
    }
}
