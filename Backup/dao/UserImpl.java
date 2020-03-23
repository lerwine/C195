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
import scheduler.util.ResourceBundleLoader;
import scheduler.util.Values;
import scheduler.view.user.EditUser;
import scheduler.view.user.UserModel;

public class UserImpl extends DataObjectImpl implements User, UserColumns {

    private static final ObservableMap<Integer, String> USER_STATUS_MAP = FXCollections.observableHashMap();
    private static ObservableMap<Integer, String> userStatusMap = null;
    private static String appointmentTypesLocale = null;
    private static final String BASE_SELECT_QUERY = String.format("SELECT %s, %s, %s, %s as %s, %s, %s, %s, %s FROM %s", COLNAME_USERID, COLNAME_USERNAME, COLNAME_PASSWORD,
            COLNAME_ACTIVE_STATUS, COLALIAS_ACTIVE_STATUS, COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY,
            TABLENAME_USER);
    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static ObservableMap<Integer, String> getUserStatusMap() {
        synchronized (USER_STATUS_MAP) {
            if (null == userStatusMap) {
                userStatusMap = FXCollections.unmodifiableObservableMap(USER_STATUS_MAP);
            } else if (null != appointmentTypesLocale && appointmentTypesLocale.equals(Locale.getDefault(Locale.Category.DISPLAY).toLanguageTag())) {
                return userStatusMap;
            }
            Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
            appointmentTypesLocale = locale.toLanguageTag();
            ResourceBundle rb = ResourceBundleLoader.getBundle(EditUser.class);
            Stream.of(new Pair<>((int) Values.USER_STATUS_INACTIVE, EditUser.RESOURCEKEY_INACTIVE), new Pair<>((int) Values.USER_STATUS_NORMAL, EditUser.RESOURCEKEY_NORMALUSER),
                    new Pair<>((int) Values.USER_STATUS_ADMIN, EditUser.RESOURCEKEY_ADMINISTRATIVEUSER)).forEach((Pair<Integer, String> p) -> {
                        USER_STATUS_MAP.put(p.getKey(), (rb.containsKey(p.getValue())) ? rb.getString(p.getValue()) : p.getValue());
                    });
        }
        return userStatusMap;
    }

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String userName;
    private String password;
    private int status;

    /**
     * Initializes a {@link Values#ROWSTATE_NEW} user object.
     */
    public UserImpl() {
        super();
        userName = "";
        password = "";
        status = Values.USER_STATUS_NORMAL;
    }

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
            return BASE_SELECT_QUERY;
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
            return Arrays.asList(COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE_STATUS);
        }

        @Override
        protected void setSaveStatementValues(UserImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getUserName());
            ps.setString(1, dao.getPassword());
            ps.setInt(3, dao.getStatus());
        }

        @Override
        protected void onInitializeDao(UserImpl target, ResultSet resultSet) throws SQLException {
            target.userName = resultSet.getString(COLNAME_USERNAME);
            if (resultSet.wasNull()) {
                target.userName = "";
            }
            target.password = resultSet.getString(COLNAME_PASSWORD);
            if (resultSet.wasNull()) {
                target.password = "";
            }
            target.status = Values.asValidUserStatus(resultSet.getInt(COLALIAS_ACTIVE_STATUS));
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
