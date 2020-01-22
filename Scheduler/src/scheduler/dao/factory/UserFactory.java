package scheduler.dao.factory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.dao.UserImpl;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.ValueAccessor;
import view.user.UserModel;

/**
 *
 * @author erwinel
 */
public class UserFactory extends DataObjectFactory<UserImpl, UserModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Status values">
    
    /**
     * Value of {@link #getStatus()} when the current user is inactive.
     */
    public static final short STATUS_INACTIVE = 0;
    
    /**
     * Value of {@link #getStatus()} when the current user is active and not an administrative account.
     */
    public static final short STATUS_USER = 1;
    
    /**
     * Value of {@link #getStatus()} when the current user is an active administrative account.
     */
    public static final short STATUS_ADMIN = 2;
    
    public static int asValidStatus(int value) {
        return (value < STATUS_INACTIVE) ? STATUS_INACTIVE : ((value > STATUS_ADMIN) ? STATUS_USER : value);
    }
    
    public static Optional<Integer> requireValidStatus(Optional<Integer> value) {
        if (value != null)
            value.ifPresent((t) -> {
                assert (t >= STATUS_INACTIVE && t <= STATUS_ADMIN) : "Invalid user stsatus value";
            });
        return value;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_USERID = "userId";
    
    public static final String COLNAME_USERNAME = "userName";
    
    public static final String COLNAME_PASSWORD = "password";
    
    public static final String COLNAME_ACTIVE = "active";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT `");
        sql.append(COLNAME_USERID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY, COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE).forEach((t) -> {
            sql.append("`, `").append(t);
        });
        baseSelectQuery = sql.append("` FROM `").append(getTableName(UserImpl.class)).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_USERNAME, COLNAME_ACTIVE,
                    COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE,
                    COLNAME_CREATEDBY));
        return sortOptions;
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">

    public static final ValueAccessor<UserModel, Integer> STATUS = new ValueAccessor<UserModel, Integer>() {
        @Override
        public String get() { return COLNAME_ACTIVE; }
        @Override
        public Integer apply(UserModel t) {
            return t.getStatus();
        }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException {
            u.setInt(t);
        }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    public static ModelFilter<UserModel> statusIs(int value) {
        return ModelFilter.columnIsEqualTo(STATUS, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    public static ModelFilter<UserModel> statusIsNot(int value) {
        return ModelFilter.columnIsNotEqualTo(STATUS, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    @Deprecated
    public static ArrayList<UserImpl> loadAll(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return loadAll(connection, getBaseSelectQuery(), orderBy, (rs) -> new UserImpl(rs));
    }

    @Deprecated
    public static ArrayList<UserImpl> loadAll(Connection connection) throws Exception {
        return loadAll(connection, null);
    }

    @Deprecated
    public static ArrayList<UserImpl> lookupByStatus(Connection connection, int status, boolean isNegated, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection,  getBaseSelectQuery(), (isNegated) ? statusIsNot(status) : statusIs(status), orderBy, (rs) -> new UserImpl(rs));
    }

    @Deprecated
    public static ArrayList<UserImpl> lookupByStatus(Connection connection, int status, boolean isNegated) throws Exception {
        return lookupByStatus(connection, status, isNegated, null);
    }

    @Deprecated
    public static Optional<UserImpl> getByUserName(Connection connection, String userName) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(userName, "User name cannot be null");
        if (userName.trim().isEmpty())
            return Optional.empty();
        String sql = String.format("%s WHERE `%s` = %%",  getBaseSelectQuery(), COLNAME_USERNAME);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            return toOptional(ps, (rs) -> new UserImpl(rs));
        }
    }
    
    @Deprecated
    public static Optional<UserImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE `%s` = %%",  getBaseSelectQuery(), COLNAME_USERID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new UserImpl(rs));
        }
    }

    @Override
    protected UserImpl fromResultSet(ResultSet resultSet) throws SQLException { return new UserImpl(resultSet); }

    @Override
    public UserModel fromDataAccessObject(UserImpl dao) { return (dao == null) ? null : new UserModel(dao); }

    @Override
    public String getBaseQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<? extends UserImpl> getDaoClass() { return UserImpl.class; }

}
