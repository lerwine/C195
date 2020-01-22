package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import scheduler.App;
import util.DB;

/**
 * The base DAO which contains properties and methods common to all DAO objects.
 * @author erwinel
 */
public abstract class DataObjectImpl implements DataObject {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="primaryKey property">
    
    private int primaryKey;

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getPrimaryKey() { return primaryKey; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="createDate property">
    
    /**
     * The name of the 'createDate' property.
     */
    public static final String PROP_CREATEDATE = "createDate";
    
    private Timestamp createDate;
    
    /**
     * Gets the timestamp when the data row associated with the current data object was inserted into the database.
     * @return The timestamp when the data row associated with the current data object was inserted into the database.
     */
    public final Timestamp getCreateDate() { return createDate; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="createdBy property">
    
    /**
     * The name of the 'createdBy' property.
     */
    public static final String PROP_CREATEDBY = "createdBy";
    
    private String createdBy;
    
    /**
     * Gets the user name of the person who inserted the data row associated with the current data object into the database.
     * @return The user name of the person who inserted the data row associated with the current data object into the database.
     */
    public final String getCreatedBy() { return createdBy; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="lastModifiedDate property">
    
    /**
     * The name of the 'lastModifiedDate' property.
     */
    public static final String PROP_LASTMODIFIEDDATE = "lastModifiedDate";
    
    private Timestamp lastModifiedDate;
    
    /**
     * Gets the timestamp when the data row associated with the current data object was last modified.
     * @return The timestamp when the data row associated with the current data object was last modified.
     */
    public final Timestamp getLastModifiedDate() { return lastModifiedDate; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="lastModifiedBy property">
    
    /**
     * The name of the 'lastModifiedBy' property.
     */
    public static final String PROP_LASTMODIFIEDBY = "lastModifiedBy";
    
    private String lastModifiedBy;
    
    /**
     * Gets the user name of the person who last modified the data row associated with the current data object in the database.
     * @return The user name of the person who last modified the data row associated with the current data object in the database.
     */
    public final String getLastModifiedBy() { return lastModifiedBy; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="rowState">
    
    private int rowState;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getRowState() { return rowState; }
    
    public final boolean isModified() { return rowState != DataObjectFactory.ROWSTATE_UNMODIFIED; }
    
    protected synchronized final void setAsModified() {
        if (rowState != DataObjectFactory.ROWSTATE_UNMODIFIED)
            return;
        rowState = DataObjectFactory.ROWSTATE_MODIFIED;
        UserImpl currentUser = App.getCurrentUser();
        if (currentUser == null)
            return;
        lastModifiedBy = currentUser.getUserName();
        lastModifiedDate = DB.toUtcTimestamp(LocalDateTime.now());
    }
    
    //</editor-fold>
    
    //</editor-fold>

    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} data access object.
     */
    protected DataObjectImpl() {
        primaryKey = 0;
        lastModifiedDate = createDate = DB.toUtcTimestamp(LocalDateTime.now());
        lastModifiedBy = createdBy = (App.getCurrentUser() == null) ? "" : App.getCurrentUser().getUserName();
        rowState = DataObjectFactory.ROWSTATE_NEW;
    }
    
    protected DataObjectImpl(int primaryKey, Timestamp createDate, String createdBy, Timestamp lastModifiedDate, String lastModifiedBy, int rowState) {
        this.primaryKey = primaryKey;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedBy = lastModifiedBy;
        this.rowState = DataObjectFactory.asValidRowState(rowState);
    }

    /**
     * Initializes a data access object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    protected DataObjectImpl(ResultSet resultSet) throws SQLException {
        primaryKey = assertBaseResultSetValid(resultSet);
        createDate = resultSet.getTimestamp(DataObjectFactory.COLNAME_CREATEDATE);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_CREATEDATE);
        createdBy = resultSet.getString(DataObjectFactory.COLNAME_CREATEDBY);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_CREATEDBY);
        lastModifiedDate = resultSet.getTimestamp(DataObjectFactory.COLNAME_LASTUPDATE);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_LASTUPDATE);
        lastModifiedBy = resultSet.getString(DataObjectFactory.COLNAME_LASTUPDATEBY);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_LASTUPDATEBY);
        rowState = DataObjectFactory.ROWSTATE_UNMODIFIED;
    }
    
    public synchronized void delete(Connection connection) throws Exception {
        Objects.requireNonNull(connection, "Connection cannot be null");
        assert rowState == DataObjectFactory.ROWSTATE_UNMODIFIED || rowState == DataObjectFactory.ROWSTATE_MODIFIED : "Associated row does not exist";
        String sql = String.format("DELETE FROM `%s` WHERE `%s` = %%", getTableName(), getPrimaryKeyColName());
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, primaryKey);
            assert ps.executeUpdate() > 0 : String.format("Failed to delete associated database row on %s where %s = %d",
                    getTableName(), getPrimaryKeyColName(), primaryKey);
        }
        rowState = DataObjectFactory.ROWSTATE_DELETED;
    }
    
    private int assertBaseResultSetValid(ResultSet resultSet) throws SQLException {
        Objects.requireNonNull(resultSet, "Result set cannot be null");
        assert !resultSet.isClosed() : "Result set is closed.";
        assert !(resultSet.isBeforeFirst() || resultSet.isAfterLast()) : "Result set is not positioned on a result row";
        String pkColName = getPrimaryKeyColName();
        int pk = resultSet.getInt(pkColName);
        assert !resultSet.wasNull() : String.format("%s was null", pkColName);
        return pk;
    }
    
    protected synchronized void refresh(ResultSet resultSet) throws SQLException {
        assert rowState != DataObjectFactory.ROWSTATE_DELETED : "Associated row was already deleted";
        int pk = assertBaseResultSetValid(resultSet);
        Timestamp cd = resultSet.getTimestamp(DataObjectFactory.COLNAME_CREATEDATE);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_CREATEDATE);
        String cb = resultSet.getString(DataObjectFactory.COLNAME_CREATEDBY);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_CREATEDBY);
        Timestamp ud = resultSet.getTimestamp(DataObjectFactory.COLNAME_LASTUPDATE);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_LASTUPDATE);
        String ub = resultSet.getString(DataObjectFactory.COLNAME_LASTUPDATEBY);
        assert !resultSet.wasNull() : String.format("%s was null", DataObjectFactory.COLNAME_LASTUPDATEBY);
        primaryKey = pk;
        createDate = cd;
        createdBy = cb;
        lastModifiedDate = ud;
        lastModifiedBy = ub;
        rowState = DataObjectFactory.ROWSTATE_UNMODIFIED;
    }
    
    public final String getTableName() { return DataObjectFactory.getTableName(getClass()); }
    
    public final String getPrimaryKeyColName() { return DataObjectFactory.getPrimaryKeyColName(getClass()); }
    
    public void saveChanges(Connection connection) throws SQLException {
        
    }
}
