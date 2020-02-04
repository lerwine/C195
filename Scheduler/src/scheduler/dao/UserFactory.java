package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.util.Pair;
import scheduler.App;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.ValueAccessor;
import scheduler.view.SchedulerController;
import scheduler.view.user.EditUser;
import scheduler.view.user.UserModel;

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
    
    private static final ObservableMap<Integer, String> USER_STATUS_MAP = FXCollections.observableHashMap();
    private static ObservableMap<Integer, String> userStatusMap = null;
    private static String appointmentTypesLocale = null;
    public static ObservableMap<Integer, String> getUserStatusMap() {
        synchronized(USER_STATUS_MAP) {
            if (null == userStatusMap)
                userStatusMap = FXCollections.unmodifiableObservableMap(USER_STATUS_MAP);
            else if (null != appointmentTypesLocale && appointmentTypesLocale.equals(Locale.getDefault(Locale.Category.DISPLAY).toLanguageTag()))
                return userStatusMap;
            Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
            appointmentTypesLocale = locale.toLanguageTag();
            ResourceBundle rb = ResourceBundle.getBundle(SchedulerController.getGlobalizationResourceName(EditUser.class), locale);
            Stream.of(new Pair<>((int)STATUS_INACTIVE, EditUser.RESOURCEKEY_INACTIVE), new Pair<>((int)STATUS_USER, EditUser.RESOURCEKEY_NORMALUSER),
                    new Pair<>((int)STATUS_ADMIN, EditUser.RESOURCEKEY_ADMINISTRATIVEUSER)).forEach((Pair<Integer, String> p) -> {
                    USER_STATUS_MAP.put(p.getKey(), (rb.containsKey(p.getValue())) ? rb.getString(p.getValue()) : p.getValue());
                });
        }
        return userStatusMap;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_USERID = "userId";
    
    public static final String COLNAME_USERNAME = "userName";
    
    public static final String COLNAME_PASSWORD = "password";
    
    public static final String COLNAME_ACTIVE = "active";
    
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

    public static final ValueAccessor<UserModel, Integer> ACCESSOR_STATUS = new ValueAccessor<UserModel, Integer>() {
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
    
    public static final ValueAccessor<UserModel, String> ACCESSOR_USERNAME = new ValueAccessor<UserModel, String>() {
        @Override
        public String get() { return COLNAME_USERNAME; }
        @Override
        public String apply(UserModel t) {
            return t.getUserName();
        }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException {
            u.setString(t);
        }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    public static ModelFilter<UserModel> statusIs(int value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_STATUS, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    public static ModelFilter<UserModel> statusIsNot(int value) {
        return ModelFilter.columnIsNotEqualTo(ACCESSOR_STATUS, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    public static ModelFilter<UserModel> userNameIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_USERNAME, ModelFilter.COMPARATOR_STRING, value);
    }
    
    public static ModelFilter<UserModel> userNameIsNot(String value) {
        return ModelFilter.columnIsNotEqualTo(ACCESSOR_USERNAME, ModelFilter.COMPARATOR_STRING, value);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status, boolean isNegated, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, (isNegated) ? statusIsNot(status) : statusIs(status), orderBy);
    }
    
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, statusIs(status), orderBy);
    }
    
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status, boolean isNegated) throws Exception {
        return load(connection, (isNegated) ? statusIsNot(status) : statusIs(status));
    }
    
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status) throws Exception {
        return load(connection, statusIs(status));
    }
    
    public Optional<UserImpl> findByUserName(Connection connection, String userName) throws Exception {
        return (null == userName || userName.trim().isEmpty()) ? Optional.empty() : loadFirst(connection, userNameIs(userName));
    }
    
    @Override
    protected UserImpl fromResultSet(ResultSet resultSet) throws SQLException { return new UserImpl(resultSet); }

    @Override
    public UserModel fromDataAccessObject(UserImpl dao) { return (dao == null) ? null : new UserModel(dao); }

    @Override
    public String getBaseQuery() {
        return String.format("SELECT `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s` FROM `%s`", COLNAME_USERID, COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE, COLNAME_CREATEDATE,
                COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, getTableName(UserImpl.class));
    }

    @Override
    public Class<? extends UserImpl> getDaoClass() { return UserImpl.class; }

}
