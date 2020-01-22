package scheduler.dao.factory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.PrimaryKeyColumn;
import scheduler.dao.TableName;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.SqlStatementBuilder;
import scheduler.filter.ValueAccessor;
import util.ResultSetFunction;
import util.ThrowableFunction;
import view.ItemModel;

/**
 *
 * @author erwinel
 * @param <D> The Data Access Object type.
 * @param <M> The Java FX model type.
 */
public abstract class DataObjectFactory<D extends DataObjectImpl, M extends ItemModel<D>> {
    
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
    
    //<editor-fold defaultstate="collapsed" desc="Row state values">
    
    /**
     * Value of {@link #getRowState()} when the current data object has been deleted from the database.
     */
    public static final int ROWSTATE_DELETED = -1;
    
    /**
     * Value of {@link #getRowState()} when the current data object has not yet been added to the database.
     */
    public static final int ROWSTATE_NEW = 0;
    
    /**
     * Value of {@link #getRowState()} when the properties of the current data object has not been modified since it was last synchronized with the database.
     */
    public static final int ROWSTATE_UNMODIFIED = 1;
    
    /**
     * Value of {@link #getRowState()} when the properties of the current data object differ from the data stored in the database.
     */
    public static final int ROWSTATE_MODIFIED = 2;
    
    public static int asValidRowState(int value) {
        if (value < ROWSTATE_DELETED)
            return ROWSTATE_DELETED;
        return (value > ROWSTATE_MODIFIED) ? ROWSTATE_MODIFIED : value;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="ValueAccessor definitions">
    
    private final ValueAccessor<M, LocalDateTime> createDateAccessor = new ValueAccessor<M, LocalDateTime>() {
        @Override
        public void accept(LocalDateTime t, ParameterConsumer u) throws SQLException { u.setDateTime(t); }
        @Override
        public LocalDateTime apply(M t) { return t.getCreateDate(); }
        @Override
        public String get() { return COLNAME_CREATEDATE; }
    };
    
    public final ValueAccessor<M, LocalDateTime> getCreateDateAccessor() { return createDateAccessor; }
    
    private final ValueAccessor<M, String> createdByAccessor = new ValueAccessor<M, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(M t) { return t.getCreatedBy(); }
        @Override
        public String get() { return COLNAME_CREATEDBY; }
    };
    
    public final ValueAccessor<M, String> getCreatedByAccessor() { return createdByAccessor; }
    
    private final ValueAccessor<M, LocalDateTime> lastModifiedDateAccessor = new ValueAccessor<M, LocalDateTime>() {
        @Override
        public void accept(LocalDateTime t, ParameterConsumer u) throws SQLException { u.setDateTime(t); }
        @Override
        public LocalDateTime apply(M t) { return t.getLastModifiedDate(); }
        @Override
        public String get() { return COLNAME_LASTUPDATE; }
    };
    
    public final ValueAccessor<M, LocalDateTime> getLastModifiedDateAccessor() { return lastModifiedDateAccessor; }
    
    private final ValueAccessor<M, String> lastModifiedByAccessor = new ValueAccessor<M, String>() {
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
        @Override
        public String apply(M t) { return t.getLastModifiedBy(); }
        @Override
        public String get() { return COLNAME_LASTUPDATEBY; }
    };
    
    public final ValueAccessor<M, String> getLastModifiedByAccessor() { return lastModifiedByAccessor; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ModelFilter definitions">
    
    public ModelFilter<M> whereCreateDateIsGreaterThan(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThan(getCreateDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereCreateDateIsGreaterThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThanOrEqualTo(getCreateDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereCreateDateIsLessThan(LocalDateTime value) {
        return ModelFilter.columnIsLessThan(getCreateDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereCreateDateIsLessThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsLessThanOrEqualTo(getCreateDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereCreatedByIs(String value) {
        return ModelFilter.columnIsEqualTo(getCreatedByAccessor(), ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereCreatedByIsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(getCreatedByAccessor(), ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereLastModifiedDateIsGreaterThan(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThan(getLastModifiedDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereLastModifiedDateIsGreaterThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThanOrEqualTo(getLastModifiedDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereLastModifiedDateIsLessThan(LocalDateTime value) {
        return ModelFilter.columnIsLessThan(getLastModifiedDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereLastModifiedDateIsLessThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsLessThanOrEqualTo(getLastModifiedDateAccessor(), ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereLastModifiedByIs(String value) {
        return ModelFilter.columnIsEqualTo(getLastModifiedByAccessor(), ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    public ModelFilter<M> whereLastModifiedByIsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(getLastModifiedByAccessor(), ModelFilter.COMPARATOR_STRING, Objects.requireNonNull(value));
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    @Deprecated
    protected static <T> ArrayList<T> toList(PreparedStatement ps, ResultSetFunction<T> factory) throws SQLException {
        ArrayList<T> result = new ArrayList<>();
        try (ResultSet rs = ps.getResultSet()) {
            while (rs.next())
                result.add(factory.apply(rs));
        }
        return result;
    }
    
    @Deprecated
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
    
    @Deprecated
    public static <T extends DataObjectImpl> ArrayList<T> loadAll(Connection connection, String baseQuery,
            ThrowableFunction<ResultSet, T, SQLException> create) throws Exception {
        return loadAll(connection, baseQuery, null, create);
    }
    
    @Deprecated
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
    
    protected abstract D fromResultSet(ResultSet resultSet) throws SQLException;
    
    public abstract M fromDataAccessObject(D dao);
    
    public abstract String getBaseQuery();
    
    public abstract Class<? extends D> getDaoClass();
    
    public final String getTableName() { return getTableName(getDaoClass()); }
    
    public final String getPrimaryKeyColName() { return getPrimaryKeyColName(getDaoClass()); }
    
    public ArrayList<M> load(Connection connection, ModelFilter<? extends M> filter, Iterable<OrderBy> orderBy) throws Exception {
        ArrayList<M> result = new ArrayList<>();
        try (SqlStatementBuilder<PreparedStatement> builder = SqlStatementBuilder.fromConnection(connection)) {
            builder.appendSql(getBaseQuery());
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
                    result.add(fromDataAccessObject(fromResultSet(rs)));
            }
        }
        return result;
    }
    
    public Optional<M> loadFirst(Connection connection, ModelFilter<? extends M> filter) throws Exception {
        try (SqlStatementBuilder<PreparedStatement> builder = SqlStatementBuilder.fromConnection(connection)) {
            builder.appendSql(getBaseQuery());
            if (null != filter) {
                String s = filter.get();
                if (!s.isEmpty())
                    builder.appendSql(" WHERE ").appendSql(s);
                filter.setParameterValues(builder.finalizeSql());
            }
            try (ResultSet rs = builder.getResult().executeQuery()) {
                if (rs.next())
                    return Optional.of(fromDataAccessObject(fromResultSet(rs)));
            }
        }
        return Optional.empty();
    }
        
    public Optional<M> loadByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s` = %%", getBaseQuery(), getPrimaryKeyColName());
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(fromDataAccessObject(fromResultSet(rs)));
            }
        }
        return Optional.empty();
    }

    public int count(Connection connection, ModelFilter<? extends M> filter) throws Exception {
        ArrayList<M> result = new ArrayList<>();
        try (SqlStatementBuilder<PreparedStatement> builder = SqlStatementBuilder.fromConnection(connection)) {
            builder.appendSql("SELECT COUNT(`").appendSql(getPrimaryKeyColName()).appendSql("`) FROM `").appendSql(getTableName()).appendSql("`");
            if (null != filter) {
                String s = filter.get();
                if (!s.isEmpty())
                    builder.appendSql(" WHERE ").appendSql(s);
                filter.setParameterValues(builder.finalizeSql());
            }
            try (ResultSet rs = builder.getResult().executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        return 0;
    }
    
    @Deprecated
    public static <T extends DataObjectImpl> ArrayList<T> load(Connection connection, String baseQuery, ModelFilter<? extends ItemModel<T>> filter,
            ThrowableFunction<ResultSet, T, SQLException> create) throws Exception {
        return load(connection, baseQuery, filter, null, create);
    }
    
    @Deprecated
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
    
}
