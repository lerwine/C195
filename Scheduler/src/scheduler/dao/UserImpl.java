package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.util.Pair;
import scheduler.util.Values;
import scheduler.view.SchedulerController;
import scheduler.view.user.EditUser;
import scheduler.view.user.UserModel;

public class UserImpl extends DataObjectImpl implements User {

    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
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
    private static final ObservableMap<Integer, String> USER_STATUS_MAP = FXCollections.observableHashMap();
    private static ObservableMap<Integer, String> userStatusMap = null;
    private static String appointmentTypesLocale = null;

    public static ObservableMap<Integer, String> getUserStatusMap() {
        synchronized (USER_STATUS_MAP) {
            if (null == userStatusMap) {
                userStatusMap = FXCollections.unmodifiableObservableMap(USER_STATUS_MAP);
            } else if (null != appointmentTypesLocale && appointmentTypesLocale.equals(Locale.getDefault(Locale.Category.DISPLAY).toLanguageTag())) {
                return userStatusMap;
            }
            Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
            appointmentTypesLocale = locale.toLanguageTag();
            ResourceBundle rb = ResourceBundle.getBundle(SchedulerController.getGlobalizationResourceName(EditUser.class), locale);
            Stream.of(new Pair<>((int) Values.USER_STATUS_INACTIVE, EditUser.RESOURCEKEY_INACTIVE), new Pair<>((int) Values.USER_STATUS_NORMAL, EditUser.RESOURCEKEY_NORMALUSER),
                    new Pair<>((int) Values.USER_STATUS_ADMIN, EditUser.RESOURCEKEY_ADMINISTRATIVEUSER)).forEach((Pair<Integer, String> p) -> {
                        USER_STATUS_MAP.put(p.getKey(), (rb.containsKey(p.getValue())) ? rb.getString(p.getValue()) : p.getValue());
                    });
        }
        return userStatusMap;
    }

    //<editor-fold defaultstate="collapsed" desc="userName property">
    private String userName;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * Set the value of userName
     *
     * @param userName new value of userName
     */
    public void setUserName(String userName) {
        this.userName = (userName == null) ? "" : userName;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="password property">
    private String password;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = (password == null) ? "" : password;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="status property">
    private int status;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return status;
    }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(int status) {
        this.status = Values.asValidUserStatus(status);
    }

    //</editor-fold>
    //</editor-fold>
    /**
     * Initializes a {@link Values#ROWSTATE_NEW} user object.
     */
    public UserImpl() {
        super();
        userName = "";
        password = "";
        status = Values.USER_STATUS_NORMAL;
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<UserImpl, UserModel> {

        // This is a singleton instance
        private FactoryImpl() {
        }

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        /**
         * Loads the first {@link UserImpl} record matching the specified user name.
         *
         * @param connection The {@link Connection} to use to retrieve the data.
         * @param userName The user name value to match.
         * @return The first {@link UserImpl} record matching {@code userName} or {@link Optional#EMPTY} if no match was found..
         * @throws SQLException if unable to perform database query.
         */
        public Optional<UserImpl> findByUserName(Connection connection, String userName) throws SQLException {
            String sql = String.format("%s WHERE `%s` = ?", getBaseSelectQuery(), COLNAME_USERNAME);
            LOG.logp(Level.INFO, getClass().getName(), "findByUserName", String.format("Executing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                LOG.logp(Level.INFO, getClass().getName(), "findByUserName", String.format("Set first parametr to \"%s\"", userName));
                ps.setString(1, userName);
                //ps.setShort(2, Values.USER_STATUS_INACTIVE);
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        return Optional.of(fromResultSet(rs));
                    }
                    SQLWarning w = connection.getWarnings();
                    if (null == w) {
                        LOG.logp(Level.WARNING, getClass().getName(), "findByUserName", "Null results, no warnings.");
                    } else {
                        LOG.logp(Level.WARNING, getClass().getName(), "findByUserName", "Encountered warning", w);
                    }
                }
            }
            return Optional.empty();
        }

        @Override
        protected UserImpl fromResultSet(ResultSet resultSet) throws SQLException {
            UserImpl r = new UserImpl();
            initializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseSelectQuery() {
            return String.format("SELECT `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s` FROM `%s`", COLNAME_USERID, COLNAME_USERNAME,
                    COLNAME_PASSWORD, COLNAME_ACTIVE, COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, getTableName());
        }

        @Override
        public Class<? extends UserImpl> getDaoClass() {
            return UserImpl.class;
        }

        @Override
        public String getTableName() {
            return TABLENAME_USER;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_USERID;
        }

        @Override
        protected List<String> getExtendedColNames() {
            return Arrays.asList(COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE);
        }

        @Override
        protected void setSaveStatementValues(UserImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getUserName());
            ps.setString(1, dao.getPassword());
            ps.setInt(3, dao.getStatus());
        }

        @Override
        protected void onInitializeDao(UserImpl target, ResultSet resultSet) throws SQLException {
            target.userName = resultSet.getString(UserImpl.COLNAME_USERNAME);
            if (resultSet.wasNull()) {
                target.userName = "";
            }
            target.password = resultSet.getString(UserImpl.COLNAME_PASSWORD);
            if (resultSet.wasNull()) {
                target.password = "";
            }
            target.status = Values.asValidUserStatus(resultSet.getInt(UserImpl.COLNAME_ACTIVE));
            if (resultSet.wasNull()) {
                target.status = Values.USER_STATUS_INACTIVE;
            }
        }

        @Override
        public UserFilter getAllItemsFilter() {
            return UserFilter.all();
        }

        @Override
        public UserFilter getDefaultFilter() {
            return UserFilter.active(true);
        }

        @Override
        public String getSaveConflictMessage(UserImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getDeleteDependencyMessage(UserImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

    }

}
