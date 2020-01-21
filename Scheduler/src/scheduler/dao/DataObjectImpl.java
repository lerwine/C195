/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;
import scheduler.App;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.SqlStatementBuilder;
import util.DB;
import util.ResultSetFunction;
import util.ThrowableFunction;
import view.ItemModel;

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
    
    public final boolean isModified() { return rowState != DataObject.ROWSTATE_UNMODIFIED; }
    
    protected synchronized final void setAsModified() {
        if (rowState != DataObject.ROWSTATE_UNMODIFIED)
            return;
        rowState = DataObject.ROWSTATE_MODIFIED;
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
        rowState = DataObject.ROWSTATE_NEW;
    }
    
    protected DataObjectImpl(int primaryKey, Timestamp createDate, String createdBy, Timestamp lastModifiedDate, String lastModifiedBy, int rowState) {
        this.primaryKey = primaryKey;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedBy = lastModifiedBy;
        this.rowState = DataObject.asValidRowState(rowState);
    }

    /**
     * Initializes a data access object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    protected DataObjectImpl(ResultSet resultSet) throws SQLException {
        primaryKey = assertBaseResultSetValid(resultSet);
        createDate = resultSet.getTimestamp(COLNAME_CREATEDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDATE);
        createdBy = resultSet.getString(COLNAME_CREATEDBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDBY);
        lastModifiedDate = resultSet.getTimestamp(COLNAME_LASTUPDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATE);
        lastModifiedBy = resultSet.getString(COLNAME_LASTUPDATEBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATEBY);
        rowState = DataObject.ROWSTATE_UNMODIFIED;
    }
    
    public synchronized void delete(Connection connection) throws Exception {
        Objects.requireNonNull(connection, "Connection cannot be null");
        assert rowState == DataObject.ROWSTATE_UNMODIFIED || rowState == DataObject.ROWSTATE_MODIFIED : "Associated row does not exist";
        String sql = String.format("DELETE FROM `%s` WHERE `%s` = %%", getTableName(), getPrimaryKeyColName());
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, primaryKey);
            assert ps.executeUpdate() > 0 : String.format("Failed to delete associated database row on %s where %s = %d",
                    getTableName(), getPrimaryKeyColName(), primaryKey);
        }
        rowState = DataObject.ROWSTATE_DELETED;
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
        assert rowState != DataObject.ROWSTATE_DELETED : "Associated row was already deleted";
        int pk = assertBaseResultSetValid(resultSet);
        Timestamp cd = resultSet.getTimestamp(COLNAME_CREATEDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDATE);
        String cb = resultSet.getString(COLNAME_CREATEDBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_CREATEDBY);
        Timestamp ud = resultSet.getTimestamp(COLNAME_LASTUPDATE);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATE);
        String ub = resultSet.getString(COLNAME_LASTUPDATEBY);
        assert !resultSet.wasNull() : String.format("%s was null", COLNAME_LASTUPDATEBY);
        primaryKey = pk;
        createDate = cd;
        createdBy = cb;
        lastModifiedDate = ud;
        lastModifiedBy = ub;
        rowState = DataObject.ROWSTATE_UNMODIFIED;
    }
    
    protected static <T> ArrayList<T> toList(PreparedStatement ps, ResultSetFunction<T> factory) throws SQLException {
        ArrayList<T> result = new ArrayList<>();
        try (ResultSet rs = ps.getResultSet()) {
            while (rs.next())
                result.add(factory.apply(rs));
        }
        return result;
    }
    
    protected static <T> Optional<T> toOptional(PreparedStatement ps, ResultSetFunction<T> factory) throws SQLException {
        try (ResultSet rs = ps.getResultSet()) {
            if (rs.next())
                return Optional.of(factory.apply(rs));
        }
        return Optional.empty();
    }
    
    protected static Stream<String> getBaseFieldNames() {
        return Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY);
    }
    
    public interface TableAndName {
        String getName();
        String getTable();
    }
    
    public interface JoinTable {
        String getParentColName();
        String getChildColName();
        String getTable();
    }
    
    public static final class SelectQueryBuilder {
        private final String tableName;
        public String getTableName() { return tableName; }
        private final TreeMap<String, TableAndName> columns;
        private final TreeMap<String, JoinTable> joins;
        public SelectQueryBuilder(String tableName) {
            columns = new TreeMap<>();
            joins = new TreeMap<>();
            this.tableName = tableName;
        }

        public JoinTable join(String tableName, String tableAlias, String parentColName, String childColName) {
            JoinTable result = new JoinTable() {
                @Override
                public String getParentColName() { return parentColName; }
                @Override
                public String getChildColName() { return childColName; }
                @Override
                public String getTable() { return tableName; }
            };
            joins.put(tableAlias, result);
            return result;
        }
        
        public void addColumn(String tableAlias, String name, String columnAlias) {
            columns.put(columnAlias, new TableAndName() {
                @Override
                public String getName() { return name; }
                @Override
                public String getTable() { return SelectQueryBuilder.this.tableName; }
            });
        }
    }
    
    /**
     * Gets the name of the data table associated a DAO.
     * @param <R> The type of DAO.
     * @param rowClass The DAO class.
     * @return The name of the data table associated with the specified DAO.
     * @throws IllegalArgumentException if the table class name is not defined through the {@link TableName} annotation.
     */
    public static final <R extends DataObjectImpl> String getTableName(Class<R> rowClass) {
        Class<TableName> tableNameClass = TableName.class;
        if (rowClass.isAnnotationPresent(tableNameClass)) {
            String n = rowClass.getAnnotation(tableNameClass).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        throw new IllegalArgumentException("Table name not defined");
    }
    
    public final String getTableName() { return getTableName(getClass()); }
    
    /**
     * Gets the name of the primary key column associated a DAO.
     * @param <R> The type of DAO.
     * @param rowClass The DAO class.
     * @return The name of the primary key column associated with the specified DAO.
     * @throws IllegalArgumentException if the primary key column is not defined through the {@link PrimaryKeyColumn} annotation.
     */
    public static final <R extends DataObjectImpl> String getPrimaryKeyColName(Class<R> rowClass) {
        Class<PrimaryKeyColumn> pkClass = PrimaryKeyColumn.class;
        if (rowClass.isAnnotationPresent(pkClass)) {
            String n = rowClass.getAnnotation(pkClass).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        throw new IllegalArgumentException("Primary key column name not defined");
    }
    
    public final String getPrimaryKeyColName() { return getPrimaryKeyColName(getClass()); }
    
    public static <T extends DataObjectImpl> ArrayList<T> loadAll(Connection connection, String baseQuery,
            ThrowableFunction<ResultSet, T, SQLException> create) throws Exception {
        return loadAll(connection, baseQuery, null, create);
    }
    
    public static <T extends DataObjectImpl> ArrayList<T> loadAll(Connection connection, String baseQuery, Iterable<OrderBy> orderBy,
            ThrowableFunction<ResultSet, T, SQLException> create) throws Exception {
        ArrayList<T> result = new ArrayList<>();
        try (PreparedStatement ps = OrderBy.prepareStatement(connection, baseQuery, orderBy)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    result.add(create.apply(rs));
            }
        }
        return result;
    }
    
    public static <T extends DataObjectImpl> ArrayList<T> load(Connection connection, String baseQuery, ModelFilter<? extends ItemModel<T>> filter,
            ThrowableFunction<ResultSet, T, SQLException> create) throws Exception {
        return load(connection, baseQuery, filter, null, create);
    }
    
    public static <T extends DataObjectImpl> ArrayList<T> load(Connection connection, String baseQuery, ModelFilter<? extends ItemModel<T>> filter,
            Iterable<OrderBy> orderBy, ThrowableFunction<ResultSet, T, SQLException> create) throws Exception {
        ArrayList<T> result = new ArrayList<>();
        try (SqlStatementBuilder<PreparedStatement> builder = SqlStatementBuilder.fromConnection(connection)) {
            builder.appendSql(baseQuery);
            if (null != filter) {
                String s = filter.get();
                if (!s.isEmpty())
                    builder.appendSql(" WHERE ").appendSql(s);
                filter.setParameterValues(builder.finalizeSql());
            }
            if (null != orderBy) {
                String s = OrderBy.toSqlClause(orderBy);
                if (!s.isEmpty())
                    builder.appendSql(" ").appendSql(s);
            }
            try (ResultSet rs = builder.getResult().executeQuery()) {
                while (rs.next())
                    result.add(create.apply(rs));
            }
        }
        return result;
    }
    
    public void saveChanges(Connection connection) throws SQLException {
        
    }
}
