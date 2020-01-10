/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import expressions.NonNullableStringProperty;
import expressions.NonNullableTimestampProperty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.App;

/**
 *
 * @author erwinel
 */
public abstract class DataObjectImpl implements DataObject {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    public static final String[] SQL_BASE_FIELDNAMES;
    
    //<editor-fold defaultstate="collapsed" desc="primaryKey property">
    
    private final ReadOnlyIntegerWrapper primaryKey;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getPrimaryKey() { return primaryKey.get(); }
    
    public final ReadOnlyIntegerProperty primaryKeyProperty() { return primaryKey.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="createDate property">
    
    public static final String COLNAME_CREATEDATE = "createDate";
    
    private final NonNullableTimestampProperty createDate;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getCreateDate() { return createDate.get(); }
    
    public ReadOnlyObjectProperty<Timestamp> createDateProperty() { return createDate.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="createdBy property">
    
    public static final String COLNAME_CREATEDBY = "createdBy";
    
    private final NonNullableStringProperty createdBy;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreatedBy() { return createdBy.get(); }
    
    public ReadOnlyStringProperty createdByProperty() { return createdBy.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="lastModifiedDate property">
    
    public static final String COLNAME_LASTUPDATE = "lastUpdate";
    
    private final NonNullableTimestampProperty lastModifiedDate;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getLastModifiedDate() { return lastModifiedDate.get(); }
    
    public ReadOnlyObjectProperty<Timestamp> lastModifiedDateProperty() { return lastModifiedDate.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="lastModifiedBy property">
    
    public static final String COLNAME_LASTUPDATEBY = "lastUpdateBy";
    
    private final NonNullableStringProperty lastModifiedBy;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastModifiedBy() { return lastModifiedBy.get(); }
    
    public ReadOnlyStringProperty lastModifiedByProperty() { return lastModifiedBy.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="rowState property">
    
    private final ReadOnlyIntegerWrapper rowState;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowState() { return rowState.get(); }
    
    public ReadOnlyIntegerProperty rowStateProperty() { return rowState.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //</editor-fold>
    
    static {
        SQL_BASE_FIELDNAMES = new String[] { COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY };
    }
    
    public DataObjectImpl() {
        primaryKey = new ReadOnlyIntegerWrapper();
        createDate = new NonNullableTimestampProperty();
        createdBy = new NonNullableStringProperty();
        lastModifiedDate = new NonNullableTimestampProperty();
        lastModifiedBy = new NonNullableStringProperty();
        rowState = new ReadOnlyIntegerWrapper();
    }

    public static final <R extends DataObjectImpl> String getTableName(Class<R> rowClass) {
        Class<TableName> tableNameClass = TableName.class;
        if (rowClass.isAnnotationPresent(tableNameClass)) {
            String n = rowClass.getAnnotation(tableNameClass).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        throw new IllegalArgumentException("Table name not defined");
    }
    
    public static final <R extends DataObjectImpl> String getPrimaryKeyColName(Class<R> rowClass) {
        Class<PrimaryKey> pkClass = PrimaryKey.class;
        if (rowClass.isAnnotationPresent(pkClass)) {
            String n = rowClass.getAnnotation(pkClass).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        throw new IllegalArgumentException("Primary key column name not defined");
    }

    public class SaveQueryBuilder {
        private final boolean insert;
        private final ArrayList<String> names;
        private final ArrayList<Integer> types;
        private final ArrayList<Object> values;
        private boolean finalized;
        
        public final boolean isInsert() { return insert; }
        
        private synchronized int setColumn(String name, int t, Object value) {
            Objects.requireNonNull(name);
            assert !name.trim().isEmpty() : "Name cannot be empty";
            assert !finalized : "Query builder is finalized";
            int index = names.indexOf(name);
            if (index < 0) {
                index = names.size();
                names.add(name);
                types.add(t);
                values.add(value);
            } else {
                assert types.get(index) == t : "Cannot change column type";
                values.set(index, value);
            }
            return index;
            
        }
        
        public int setIntColumn(String name, int value) { return setColumn(name, Types.INTEGER, value); }
        
        public int getIntColumn(String name) { return (int)values.get(names.indexOf(name)); }
        
        public int setStringColumn(String name, String value) { return setColumn(name, Types.NVARCHAR, value); }
        
        public String getStringColumn(String name) { return (String)values.get(names.indexOf(name)); }
        
        public int setBooleanColumn(String name, boolean value) { return setColumn(name, Types.BIT, value); }
        
        public boolean getBooleanColumn(String name) { return (boolean)values.get(names.indexOf(name)); }
        
        public int setTimestampColumn(String name, Timestamp value) { return setColumn(name, Types.TIMESTAMP, value); }
        
        public Timestamp getTimestampColumn(String name) { return (Timestamp)values.get(names.indexOf(name)); }
        
        private SaveQueryBuilder() {
            insert = getRowState() == ROWSTATE_NEW;
            names = new ArrayList<>();
            types = new ArrayList<>();
            values = new ArrayList<>();
            finalized = false;
        }
        
        public synchronized PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            Class<? extends DataObjectImpl> itemClass = DataObjectImpl.this.getClass();
            final PreparedStatement ps;
            if (insert)
                ps = connection.prepareStatement(String.format("INSERT INTO %s (`%s`) VALUES (%s)", getTableName(itemClass), names.stream().reduce((t, u) -> t + "`, `" + u),
                        names.stream().skip(1).reduce("%", ((t, u) -> t + ", %"))));
            else {
                String pk = getPrimaryKeyColName(itemClass);
                if (!finalized) {
                    names.add(pk);
                    types.add(Types.INTEGER);
                    values.add(getPrimaryKey());
                }
                ps = connection.prepareStatement(String.format("UPDATE %s SET `%s` WHERE `%s` = %%", getTableName(itemClass), names.stream().reduce((t, u) -> t + "`, `" + u), pk));
            }
            
            finalized = true;
            types.forEach(new Consumer<Integer>(){
                private int index = 0;
                @Override
                public void accept(Integer t) {
                    try {
                        Object v = values.get(index++);
                        if (null == v)
                            ps.setNull(index, t);
                        else
                            switch (t) {
                                case Types.INTEGER:
                                    ps.setInt(index, (int)v);
                                    break;
                                case Types.BIT:
                                    ps.setBoolean(index, (boolean)v);
                                    break;
                                case Types.TIMESTAMP:
                                    ps.setTimestamp(index, (Timestamp)v);
                                    break;
                                default:
                                    break;
                            }
                    } catch (SQLException ex) {
                        Logger.getLogger(DataObjectImpl.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException("Error setting column value", ex);
                    }
                }
            });
            return ps;
        }
    }
    
    public static ZonedDateTime toZonedUtc(LocalDateTime dateTime) { return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")); }
    
    public static Timestamp toUtcTimestamp(LocalDateTime dateTime) { return Timestamp.valueOf(toZonedUtc(dateTime).toLocalDateTime()); }
    
    /**
     * Base class for object intended for modifying property values of owning class.
     * 
     * The purpose is to ensure that properties of the owning class are only modified from the Fx Application thread.
     */
    public abstract class EditableBase implements DataObject {
        //<editor-fold defaultstate="collapsed" desc="Properties">
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final int getPrimaryKey() { return primaryKey.get(); }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Timestamp getCreateDate() { return createDate.get(); }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final String getCreatedBy() { return createdBy.get(); }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final Timestamp getLastModifiedDate() { return lastModifiedDate.get(); }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final String getLastModifiedBy() { return lastModifiedBy.get(); }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final int getRowState() { return rowState.get(); }

        //</editor-fold>
        
        public abstract BooleanBinding isValid();
        
        protected EditableBase() {
            if (getRowState() == ROWSTATE_DELETED)
                throw new IllegalStateException("Cannot edit a deleted item");
        }
        
        /**
         * Updates the properties of the owning class with the modified values.
         */
        public abstract void applyChanges();
        
        /**
         * Discards any modified property values.
         */
        public abstract void undoChanges();
        
        /**
         * This gets invoked before an insert or update database operation.
         * 
         * @param connection The database connection that will be used for saving changes.
         * @param queryBuilder The {@link SaveQueryBuilder} object used for specifying columns to be included in the insert or update query.
         */
        protected void beforeSaveChanges(Connection connection, SaveQueryBuilder queryBuilder) {
            Timestamp modifiedDate = toUtcTimestamp(LocalDateTime.now());
            String modifiedBy = App.CURRENT.get().getCurrentUser().getUserName();
            if (getRowState() == ROWSTATE_NEW) {
                queryBuilder.setTimestampColumn(COLNAME_CREATEDATE, modifiedDate);
                queryBuilder.setStringColumn(COLNAME_CREATEDBY, modifiedBy);
            }
            queryBuilder.setTimestampColumn(COLNAME_LASTUPDATE, modifiedDate);
            queryBuilder.setStringColumn(COLNAME_LASTUPDATEBY, modifiedBy);
        }
    
        /**
         * Saves the current object to the database.
         * 
         * @param connection The SQL database connection to use.
         * 
         * @throws SQLException if unable to complete the insert or update operation.
         */
        public synchronized final void saveChanges(Connection connection) throws SQLException {
            if (getRowState() == ROWSTATE_DELETED)
                throw new IllegalStateException("Cannot save a deleted item");
            final SaveQueryBuilder queryBuilder = new SaveQueryBuilder();
            beforeSaveChanges(connection, queryBuilder);
            final int pk;
            try (PreparedStatement ps = queryBuilder.createPreparedStatement(connection)) {
                ps.executeUpdate();
                if (getRowState() == ROWSTATE_NEW) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        pk = rs.getInt(1);
                    }
                } else
                    pk = getPrimaryKey();
            }
            if (Platform.isFxApplicationThread())
                afterSave(queryBuilder, pk);
            else
                Platform.runLater(() -> afterSave(queryBuilder, pk));
        }

        private void afterSave(SaveQueryBuilder queryBuilder, int pk) {
            lastModifiedDate.set(queryBuilder.getTimestampColumn(COLNAME_LASTUPDATE));
            lastModifiedBy.set(queryBuilder.getStringColumn(COLNAME_LASTUPDATEBY));
            if (getRowState() == ROWSTATE_NEW) {
                createDate.set(queryBuilder.getTimestampColumn(COLNAME_CREATEDATE));
                createdBy.set(queryBuilder.getStringColumn(COLNAME_CREATEDBY));
                primaryKey.set(pk);
            }
            rowState.set(ROWSTATE_UNMODIFIED);
            applyChanges();
        }

        /**
         * Deletes the current object from the database.
         * 
         * @param connection The database connection to use.
         * @throws java.sql.SQLException
         */
        public synchronized final void delete(Connection connection) throws SQLException {
            if (getRowState() == ROWSTATE_DELETED)
                throw new IllegalStateException("Item was already deleted");
            if (getRowState() == ROWSTATE_NEW)
                throw new IllegalStateException("Items that have never been saved cannot be deleted");
            onBeforeDelete(connection);
            Class<? extends DataObjectImpl> itemClass = DataObjectImpl.this.getClass();
            final PreparedStatement ps = connection.prepareStatement(String.format("DELETE FROM `%s` WHERE `%s` = %%", getTableName(itemClass), getPrimaryKeyColName(itemClass)));
            if (Platform.isFxApplicationThread()) {
                rowState.set(ROWSTATE_DELETED);
                applyChanges();
            } else
                Platform.runLater(() -> {
                    rowState.set(ROWSTATE_DELETED);
                    applyChanges();
                });
            
        }

        /**
         * This gets called before a delete operation.
         * Implementing classes can throw an {@link IllegalStateException} if it is not to be deleted.
         * 
         * @param connection The database connection to use.
         */
        protected void onBeforeDelete(Connection connection) throws IllegalStateException { }
    }
}
