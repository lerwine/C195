package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import scheduler.App;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.ItemModel;

/**
 *
 * @author erwinel
 * @param <D> The Data Access Object type.
 * @param <M> The Java FX model type.
 */
public abstract class DataObjectFactory<D extends DataObjectFactory.DataObjectImpl, M extends ItemModel<D>> {

    private static final Logger LOG = Logger.getLogger(DataObjectFactory.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="Database table names">

    /**
     * The name of the {@link User} database table.
     */
    public static final String TABLENAME_USER = "user";

    /**
     * The name of the {@link Country} database table.
     */
    public static final String TABLENAME_COUNTRY = "country";

    /**
     * The name of the {@link City} database table.
     */
    public static final String TABLENAME_CITY = "city";

    /**
     * The name of the {@link Address} database table.
     */
    public static final String TABLENAME_ADDRESS = "address";

    /**
     * The name of the {@link Customer} database table.
     */
    public static final String TABLENAME_CUSTOMER = "customer";
    
    /**
     * The name of the {@link Appointment} database table.
     */
    public static final String TABLENAME_APPOINTMENT = "appointment";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    /**
     * The name of the 'createDate' column.
     */
    public static final String COLNAME_CREATEDATE = "createDate";
    
    /**
     * The name of the 'createdBy' column.
     */
    public static final String COLNAME_CREATEDBY = "createdBy";
    
    /**
     * The name of the 'lastUpdate' column.
     */
    public static final String COLNAME_LASTUPDATE = "lastUpdate";
    
    /**
     * The name of the 'lastUpdateBy' column.
     */
    public static final String COLNAME_LASTUPDATEBY = "lastUpdateBy";
    
    //</editor-fold>
    
    protected static Stream<String> getBaseFieldNames() {
        return Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY);
    }
    
    protected abstract D fromResultSet(ResultSet resultSet) throws SQLException;
    
    public abstract M fromDataAccessObject(D dao);
    
    public abstract String getBaseQuery();
    
    public abstract Class<? extends D> getDaoClass();
    
//    public final String getTableName() { return getTableName(getDaoClass()); }
//    
//    public final String getPrimaryKeyColName() { return getPrimaryKeyColName(getDaoClass()); }
    
    public abstract String getTableName();
    
    public abstract String getPrimaryKeyColName();
    
    public void delete(D dao, Connection connection) throws Exception {
        Objects.requireNonNull(dao, "Data access object cannot be null");
        Objects.requireNonNull(connection, "Connection cannot be null");
        synchronized (dao) {
            assert dao.getRowState() != Values.ROWSTATE_DELETED : String.format("%s has already been deleted", getClass().getName());
            assert dao.getRowState() != Values.ROWSTATE_NEW : String.format("%s has not been inserted into the database", getClass().getName());
            String sql = String.format("DELETE FROM `%s` WHERE `%s` = ?", getTableName(), getPrimaryKeyColName());
            LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, dao.getPrimaryKey());
                assert ps.executeUpdate() > 0 : String.format("Failed to delete associated database row on %s where %s = %d",
                        getTableName(), getPrimaryKeyColName(), dao.getPrimaryKey());
            }
            dao.setDeleted();
        }
    }
    
    protected abstract Stream<String> getExtendedColNames();
    
    protected abstract void setStatementValues(D dao, PreparedStatement ps) throws SQLException;
    
    public void save(D dao, Connection connection) throws Exception {
        Objects.requireNonNull(dao, "Data access object cannot be null");
        Objects.requireNonNull(connection, "Connection cannot be null");
        synchronized (dao) {
            assert dao.getRowState() != Values.ROWSTATE_DELETED : String.format("%s has been deleted", getClass().getName());
            StringBuilder sql = new StringBuilder();
            String[] colNames;
            dao.setAsModified();
            if (dao.getRowState() == Values.ROWSTATE_NEW) {
                colNames = Stream.concat(getExtendedColNames(), Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY))
                        .toArray(String[]::new);
                sql.append("INSERT INTO `").append(getTableName()).append("` (`").append(String.join("`, `", colNames)).append("`) VALUES (?");
                for (int i = 1; i < colNames.length; i++)
                    sql.append(", ?");
                sql.append(")");
            } else {
                colNames = Stream.concat(getExtendedColNames(), Stream.of(COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY)).toArray(String[]::new);
                sql.append("UPDATE `").append(getTableName()).append("` SET `").append(String.join("` = ?, `", colNames)).append("` = ? WHERE `")
                        .append(getPrimaryKeyColName()).append(" = ?");
            }
            LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql.toString()));
            int pk;
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                setStatementValues(dao, ps);
                int index;
                if (dao.getRowState() == Values.ROWSTATE_NEW) {
                    index = colNames.length - 4;
                    ps.setTimestamp(++index, dao.getCreateDate());
                    ps.setString(++index, dao.getCreatedBy());
                    ps.setTimestamp(++index, dao.getCreateDate());
                    ps.setString(++index, dao.getCreatedBy());
                } else {
                    index = colNames.length - 2;
                    ps.setTimestamp(++index, dao.getLastModifiedDate());
                    ps.setString(++index, dao.getLastModifiedBy());
                    ps.setInt(++index, dao.getPrimaryKey());
                }
                ps.executeUpdate();
                if (dao.getRowState() == Values.ROWSTATE_NEW)
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        pk = rs.getInt(1);
                    }
                else
                    pk = dao.getPrimaryKey();
            }
            sql = new StringBuilder(getBaseQuery());
            sql.append(" WHERE `").append(getPrimaryKeyColName()).append("`=%");
            LOG.log(Level.SEVERE, String.format("Executing query \"%s\"", sql.toString()));
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                ps.setInt(1, pk);
                try (ResultSet rs = ps.getResultSet()) {
                    assert rs.next() : "Updated record not found";
                    refresh(dao, rs);
                }
            }
        }
    }
    
    public Optional<D> loadByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s`=?", getBaseQuery(), getPrimaryKeyColName());
        LOG.log(Level.SEVERE, String.format("Finalizing query \"%s\"", sql));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(fromResultSet(rs));
            }
        }
        return Optional.empty();
    }

    private int assertBaseResultSetValid(DataObjectImpl target, ResultSet resultSet) throws SQLException {
        Objects.requireNonNull(resultSet, "Result set cannot be null");
        assert !resultSet.isClosed() : "Result set is closed.";
        assert !(resultSet.isBeforeFirst() || resultSet.isAfterLast()) : "Result set is not positioned on a result row";
        String pkColName = getPrimaryKeyColName();
        int pk = resultSet.getInt(pkColName);
        assert !resultSet.wasNull() : String.format("%s was null", pkColName);
        assert resultSet.getMetaData().getTableName(resultSet.findColumn(pkColName)).equals(getTableName()) : "Table name mismatch";
        if (target.rowState != Values.ROWSTATE_NEW)
            assert pk == target.getPrimaryKey() : "Primary key does not match";
        return pk;
    }

    protected synchronized void refresh(DataObjectImpl target, ResultSet resultSet) throws SQLException {
        assert target.rowState != Values.ROWSTATE_DELETED : "Associated row was already deleted";
        int pk = assertBaseResultSetValid(target, resultSet);
        if (target.rowState != Values.ROWSTATE_NEW)
            assert pk == target.getPrimaryKey() : "Primary key does not match";
        Timestamp cd = resultSet.getTimestamp(COLNAME_CREATEDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDATE);
        String cb = resultSet.getString(COLNAME_CREATEDBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDBY);
        Timestamp ud = resultSet.getTimestamp(COLNAME_LASTUPDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATE);
        String ub = resultSet.getString(COLNAME_LASTUPDATEBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATEBY);
        target.primaryKey = pk;
        target.createDate = cd;
        target.createdBy = cb;
        target.lastModifiedDate = ud;
        target.lastModifiedBy = ub;
        target.rowState = Values.ROWSTATE_UNMODIFIED;
    }
    
    /**
     * Initializes a data access object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    protected void initializeDao(D target, ResultSet resultSet) throws SQLException {
        ((DataObjectImpl)target).primaryKey = assertBaseResultSetValid(target, resultSet);
        ((DataObjectImpl)target).createDate = resultSet.getTimestamp(COLNAME_CREATEDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDATE);
        ((DataObjectImpl)target).createdBy = resultSet.getString(COLNAME_CREATEDBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDBY);
        ((DataObjectImpl)target).lastModifiedDate = resultSet.getTimestamp(COLNAME_LASTUPDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATE);
        ((DataObjectImpl)target).lastModifiedBy = resultSet.getString(COLNAME_LASTUPDATEBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATEBY);
        ((DataObjectImpl)target).rowState = Values.ROWSTATE_UNMODIFIED;
    }

    /**
    * The base DAO which contains properties and methods common to all DAO objects.
    * @author erwinel
    */
   public static abstract class DataObjectImpl implements DataObject {
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

       final void setDeleted() { rowState = Values.ROWSTATE_DELETED; }

       public final boolean isModified() { return rowState != Values.ROWSTATE_UNMODIFIED; }

       synchronized final void setAsModified() {
           assert rowState != Values.ROWSTATE_DELETED : "Row has been deleted.";
           UserFactory.UserImpl currentUser = App.getCurrentUser();
           lastModifiedBy = currentUser.getUserName();
           lastModifiedDate = DB.toUtcTimestamp(LocalDateTime.now());
           if (rowState != Values.ROWSTATE_NEW) {
               rowState = Values.ROWSTATE_MODIFIED;
               return;
           }
           createdBy = lastModifiedBy;
           createDate = lastModifiedDate;
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
           rowState = Values.ROWSTATE_NEW;
       }

       protected DataObjectImpl(int primaryKey, Timestamp createDate, String createdBy, Timestamp lastModifiedDate, String lastModifiedBy, int rowState) {
           this.primaryKey = primaryKey;
           this.createDate = createDate;
           this.createdBy = createdBy;
           this.lastModifiedDate = lastModifiedDate;
           this.lastModifiedBy = lastModifiedBy;
           this.rowState = Values.asValidRowState(rowState);
       }

       public abstract void saveChanges(Connection connection) throws Exception;

       public abstract void delete(Connection connection) throws Exception;

   }
}
