package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import scheduler.util.Values;
import scheduler.view.SchedulerController;
import scheduler.view.user.EditUser;
import scheduler.view.user.UserModel;

/**
 * Factory class for loading {@link UserImpl} records from the database.
 * @author erwinel
 */
public class UserFactory extends DataObjectFactory<UserFactory.UserImpl, UserModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">

    /**
     * The name of the database column that is mapped to the {@link DataObjectImpl#primaryKey} property.
     */
    public static final String COLNAME_USERID = "userId";
    
    /**
     * The name of the database column that is mapped to {@link UserImpl#userName} property.
     */
    public static final String COLNAME_USERNAME = "userName";
    
    /**
     * The name of the database column that is mapped to {@link UserImpl#password} property.
     */
    public static final String COLNAME_PASSWORD = "password";
    
    /**
     * The name of the database column that is mapped to {@link UserImpl#status} property.
     */
    public static final String COLNAME_ACTIVE = "active";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    
    /**
     * Gets the names of the sortable columns.
     * @return The names of the sortable columns.
     */
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
    
    /**
     * Loads {@link UserImpl} records according to the user status.
     * @param connection The {@link Connection} to use to retrieve the data.
     * @param status The user status value to match.
     * @param isNegated {@code true} to get all records whose status does not match; otherwise {@code false} to load matching records.
     * @param orderBy Specifies the Order-by clause to use.
     * @return {@link UserImpl} records loaded according to the user status.
     * @throws Exception if unable to perform database query.
     */
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status, boolean isNegated, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, (isNegated) ? statusIsNot(status) : statusIs(status), orderBy);
    }
    
    /**
     * Loads {@link UserImpl} records according to the user status.
     * @param connection The {@link Connection} to use to retrieve the data.
     * @param status The user status value to match.
     * @param orderBy Specifies the Order-by clause to use.
     * @return {@link UserImpl} records loaded according to the user status.
     * @throws Exception if unable to perform database query.
     */
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, statusIs(status), orderBy);
    }
    
    /**
     * Loads {@link UserImpl} records according to the user status.
     * @param connection The {@link Connection} to use to retrieve the data.
     * @param status The user status value to match.
     * @param isNegated {@code true} to get all records whose status does not match; otherwise {@code false} to load matching records.
     * @return {@link UserImpl} records loaded according to the user status.
     * @throws Exception if unable to perform database query.
     */
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status, boolean isNegated) throws Exception {
        return load(connection, (isNegated) ? statusIsNot(status) : statusIs(status));
    }
    
    /**
     * Loads {@link UserImpl} records according to the user status.
     * @param connection The {@link Connection} to use to retrieve the data.
     * @param status The user status value to match.
     * @return {@link UserImpl} records loaded according to the user status.
     * @throws Exception if unable to perform database query.
     */
    public ArrayList<UserImpl> loadByStatus(Connection connection, int status) throws Exception {
        return load(connection, statusIs(status));
    }
    
    /**
     * Loads the first {@link UserImpl} record matching the specified user name.
     * @param connection The {@link Connection} to use to retrieve the data.
     * @param userName The user name value to match.
     * @return The first {@link UserImpl} record matching {@code userName} or {@link Optional#EMPTY} if no match was found..
     * @throws Exception if unable to perform database query.
     */
    public Optional<UserImpl> findByUserName(Connection connection, String userName) throws Exception {
        return (null == userName || userName.trim().isEmpty()) ? Optional.empty() : loadFirst(connection, userNameIs(userName));
    }
    
    @Override
    protected UserImpl fromResultSet(ResultSet resultSet) throws SQLException {
        UserImpl r = new UserImpl();
        initializeDao(r, resultSet);
        return r;
    }

    @Override
    public UserModel fromDataAccessObject(UserImpl dao) { return (dao == null) ? null : new UserModel(dao); }

    @Override
    public String getBaseQuery() {
        return String.format("SELECT `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s` FROM `%s`", COLNAME_USERID, COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE, COLNAME_CREATEDATE,
                COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, getTableName());
    }

    @Override
    public Class<? extends UserImpl> getDaoClass() { return UserImpl.class; }

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
            Stream.of(new Pair<>((int)Values.USER_STATUS_INACTIVE, EditUser.RESOURCEKEY_INACTIVE), new Pair<>((int)Values.USER_STATUS_NORMAL, EditUser.RESOURCEKEY_NORMALUSER),
                    new Pair<>((int)Values.USER_STATUS_ADMIN, EditUser.RESOURCEKEY_ADMINISTRATIVEUSER)).forEach((Pair<Integer, String> p) -> {
                    USER_STATUS_MAP.put(p.getKey(), (rb.containsKey(p.getValue())) ? rb.getString(p.getValue()) : p.getValue());
                });
        }
        return userStatusMap;
    }

    @Override
    protected Stream<String> getExtendedColNames() {
        return Stream.of(COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE);
    }

    @Override
    protected void setStatementValues(UserImpl dao, PreparedStatement ps) throws SQLException {
        ps.setString(1, dao.getUserName());
        ps.setString(1, dao.getPassword());
        ps.setInt(3, dao.getStatus());
    }

    @Override
    protected void initializeDao(UserImpl target, ResultSet resultSet) throws SQLException {
        super.initializeDao(target, resultSet);
        target.userName = resultSet.getString(UserFactory.COLNAME_USERNAME);
        if (resultSet.wasNull())
            target.userName = "";
        target.password = resultSet.getString(UserFactory.COLNAME_PASSWORD);
        if (resultSet.wasNull())
            target.password = "";
        target.status = Values.asValidUserStatus(resultSet.getInt(UserFactory.COLNAME_ACTIVE));
        if (resultSet.wasNull())
            target.status = Values.USER_STATUS_INACTIVE;
    }

    @Override
    public String getTableName() { return TABLENAME_USER; }

    @Override
    public String getPrimaryKeyColName() { return COLNAME_USERID; }
    
    /**
    *
    * @author erwinel
    */
//   @TableName(DataObjectFactory.TABLENAME_USER)
//   @PrimaryKeyColumn(UserFactory.COLNAME_USERID)
   public static final class UserImpl extends DataObjectImpl implements User {
       //<editor-fold defaultstate="collapsed" desc="Properties and Fields">

       //<editor-fold defaultstate="collapsed" desc="userName property">

       private String userName;

       /**
        * {@inheritDoc}
        */
       @Override
       public String getUserName() { return userName; }

       /**
        * Set the value of userName
        *
        * @param userName new value of userName
        */
       public void setUserName(String userName) { this.userName = (userName == null) ? "" : userName; }


       //</editor-fold>

       //<editor-fold defaultstate="collapsed" desc="password property">

       private String password;

       /**
        * {@inheritDoc}
        */
       @Override
       public String getPassword() { return password; }

       /**
        * Set the value of password
        *
        * @param password new value of password
        */
       public void setPassword(String password) { this.password = (password == null) ? "" : password; }

       //</editor-fold>

       //<editor-fold defaultstate="collapsed" desc="status property">

       private int status;

       /**
        * {@inheritDoc}
        */
       @Override
       public int getStatus() { return status; }

       /**
        * Set the value of status
        *
        * @param status new value of status
        */
       public void setStatus(int status) { this.status = Values.asValidUserStatus(status); }

       //</editor-fold>

       //</editor-fold>

       /**
        * Initializes a {@link DataObject.ROWSTATE_NEW} user object.
        */
       public UserImpl() {
           super();
           userName = "";
           password = "";
           status = Values.USER_STATUS_NORMAL;
       }

       @Override
       public void saveChanges(Connection connection) throws Exception {
           (new UserFactory()).save(this, connection);
       }

       @Override
       public void delete(Connection connection) throws Exception {
           (new UserFactory()).delete(this, connection);
       }

   }
}
