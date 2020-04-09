package scheduler.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import scheduler.App;
import scheduler.util.PropertyBindable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public abstract class DataRow extends PropertyBindable implements IDataRow {

    public static final int PRIMARY_KEY_NEW = Integer.MIN_VALUE;
    public static final String DEFAULT_ADMIN_LOGIN_NAME = "admin";
    
    private int primaryKey;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastModifiedDate;
    private String lastModifiedBy;
    private DataRowState rowState;

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

    protected DataRow() {
        primaryKey = PRIMARY_KEY_NEW;
        createDate = lastModifiedDate = Timestamp.valueOf(LocalDateTime.now());
        UserDataRow user = App.getCurrentUser();
        if (null == user) {
            createdBy = lastModifiedBy = DEFAULT_ADMIN_LOGIN_NAME;
        } else {
            createdBy = lastModifiedBy = user.getUserName();
        }
        rowState = DataRowState.NEW;
    }
    
    protected DataRow(Builder builder) {
        primaryKey = builder.primaryKey;
        createDate = builder.createDate;
        lastModifiedDate = builder.lastModifiedDate;
        createdBy = builder.createdBy;
        lastModifiedBy = builder.lastModifiedBy;
        rowState = builder.rowState;
    }
    
    @Override
    protected synchronized void onPropertyChange(PropertyChangeEvent event) {
        String propertyName = event.getPropertyName();
        if (null == propertyName) {
            super.onPropertyChange(event);
            return;
        }
        switch (propertyName) {
            case PROP_CREATEDATE:
            case PROP_CREATEDBY:
            case PROP_LASTMODIFIEDBY:
            case PROP_LASTMODIFIEDDATE:
            case PROP_PRIMARYKEY:
            case PROP_ROWSTATE:
                super.onPropertyChange(event);
                return;
        }
        DataRowState oldRowState = getRowState();
        Timestamp oldModifiedDate;
        String oldLastModifiedBy;
        switch (oldRowState) {
            case NEW:
                super.onPropertyChange(event);
                break;
            case UNMODIFIED:
                oldRowState = rowState;
                oldLastModifiedBy = lastModifiedBy;
                oldModifiedDate = lastModifiedDate;
                lastModifiedBy = App.getCurrentUserObsolete().getUserName();
                lastModifiedDate = Timestamp.valueOf(LocalDateTime.now());
                if (isRowStateChangeProperty(propertyName)) {
                    rowState = DataRowState.MODIFIED;
                    super.onPropertyChange(event);
                    getPropertyChangeSupport().firePropertyChange(PROP_ROWSTATE, oldRowState, rowState);
                } else {
                    super.onPropertyChange(event);
                }
                getPropertyChangeSupport().firePropertyChange(PROP_LASTMODIFIEDBY, oldLastModifiedBy, lastModifiedBy);
                getPropertyChangeSupport().firePropertyChange(PROP_LASTMODIFIEDDATE, oldModifiedDate, lastModifiedDate);
                break;
            default:
                oldLastModifiedBy = lastModifiedBy;
                oldModifiedDate = lastModifiedDate;
                lastModifiedBy = App.getCurrentUserObsolete().getUserName();
                lastModifiedDate = Timestamp.valueOf(LocalDateTime.now());
                super.onPropertyChange(event);
                getPropertyChangeSupport().firePropertyChange(PROP_LASTMODIFIEDBY, oldLastModifiedBy, lastModifiedBy);
                getPropertyChangeSupport().firePropertyChange(PROP_LASTMODIFIEDDATE, oldModifiedDate, lastModifiedDate);
                break;
        }
    }

    @Override
    public DataRowState getRowState() {
        return rowState;
    }

    /**
     * Tests whether a change to a property in an extending class should change an {@link DataRowState#UNMODIFIED} {@link rowState} to
     * {@link DataRowState#MODIFIED}.
     * This is only for testing the properties of extending classes and not this base class.
     * 
     * @param propertyName The name of the target property.
     * @return {@code true} if the a change to the {@code name}d property should change an {@link DataRowState#UNMODIFIED} {@link rowState} to
     * {@link DataRowState#MODIFIED} and update the {@link lastModifiedDate} and {@link lastModifiedBy} properties.
     */
    protected boolean isRowStateChangeProperty(String propertyName) {
        return null != propertyName;
    }

    public static abstract class Builder {
        private final int primaryKey;
        private final Timestamp createDate;
        private final String createdBy;
        private final Timestamp lastModifiedDate;
        private final String lastModifiedBy;
        private final DataRowState rowState;
        
        protected Builder(String primaryKeyColName, ResultSet rs) throws SQLException {
            primaryKey = rs.getInt(primaryKeyColName);
            if (rs.wasNull())
                throw new SQLException(String.format("Unexpected null primary key column (%s) value", primaryKeyColName));
            createDate = rs.getTimestamp(COLNAME_CREATEDATE);
            if (rs.wasNull())
                throw new SQLException(String.format("Unexpected null %s column value", COLNAME_CREATEDATE));
            createdBy = rs.getString(COLNAME_CREATEDBY);
            if (rs.wasNull())
                throw new SQLException(String.format("Unexpected null %s column value", COLNAME_CREATEDBY));
            lastModifiedDate = rs.getTimestamp(COLNAME_LASTUPDATE);
            if (rs.wasNull())
                throw new SQLException(String.format("Unexpected null %s column value", COLNAME_LASTUPDATE));
            lastModifiedBy = rs.getString(COLNAME_LASTUPDATEBY);
            if (rs.wasNull())
                throw new SQLException(String.format("Unexpected null %s column value", COLNAME_LASTUPDATEBY));
            rowState = DataRowState.UNMODIFIED;
        }
    }
    
    public static abstract class Factory<T extends DataRow> {
        /**
         * Gets the database column name for the primary key.
         * 
         * @return The database column name for the primary key.
         */
        public abstract String getPrimaryKeyColName();
    
        /**
         * Gets the database table name for the target data row type.
         * 
         * @return The database table name for the target data row type.
         */
        public abstract String getTableName();
    
        public final boolean deleteFromDatabase(T dataRow, Connection connection) throws SQLException {
            DataRow target = (DataRow)dataRow;
            switch (target.rowState) {
                case DELETED:
                    return false;
                case NEW:
                    DataRowState oldRowState = target.rowState;
                    target.rowState = DataRowState.DELETED;
                    target.getPropertyChangeSupport().firePropertyChange(PROP_ROWSTATE, oldRowState, target.rowState);
                    return true;
            }
            try (PreparedStatement ps = connection.prepareStatement(String.format("DELETE FROM `%s` WHERE `%s` = ?", getTableName(),
                    getPrimaryKeyColName()))) {
                ps.setInt(1, target.primaryKey);
                return ps.executeUpdate() > 0;
            }
        }
        
        protected Stream<String> getBaseSelectColNames() {
            return Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, getPrimaryKeyColName());
        }
        
        protected abstract Stream<SelectStatementColumn> getExtendedColumns();
        
        public abstract SelectStatementTable<T> getFullSelectStatement();
        
        public final SelectStatementTableImpl<T> getBasicSelectStatement() {
            return new SelectStatementTableImpl<>(getTableName(), Stream.concat(getExtendedColumns(),
                    getBaseSelectColNames().map((t) -> () -> t)).iterator());
        }
        
        public final SelectStatementTableImpl<T> getPartialSelectStatement() {
            return new SelectStatementTableImpl<>(getTableName(), getExtendedColumns().iterator());
        }
        
        public final ArrayList<T> select(Connection connection, SelectStatement<T> statement) {
            SelectStatementTable<T> statement = getFullSelectStatement();
        }
    }
}
