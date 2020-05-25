package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.Scheduler;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DbChangeType;
import scheduler.dao.event.UserDaoEvent;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.UserFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.model.ModelHelper;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Data access object for the {@code user} database table.
 * <p>
 * This object contains the login credentials for users of the current application.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.USER)
public final class UserDAO extends DataAccessObject implements UserDbRecord {

    public static final int MAX_LENGTH_USERNAME = 50;

    /**
     * The name of the 'userName' property.
     */
    public static final String PROP_USERNAME = "userName";

    public static final int MAX_LENGTH_PASSWORD = 50;

    /**
     * The name of the 'password' property.
     */
    public static final String PROP_PASSWORD = "password";

    /**
     * The name of the 'status' property.
     */
    public static final String PROP_STATUS = "status";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String userName;
    // PENDING: Change to using something that can accept raw password and produce hash.
    private String password;
    private UserStatus status;

    /**
     * Initializes a {@link DataRowState#NEW} user object.
     */
    public UserDAO() {
        super();
        userName = "";
        password = "";
        status = UserStatus.NORMAL;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * Set the value of userName
     *
     * @param value new value of userName
     */
    public void setUserName(String value) {
        String oldValue = this.userName;
        this.userName = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_USERNAME, oldValue, this.userName);
    }

    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param value new value of password
     */
    public void setPassword(String value) {
        String oldValue = this.password;
        this.password = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_PASSWORD, oldValue, this.password);
    }

    @Override
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(UserStatus status) {
        UserStatus oldValue = this.status;
        this.status = (null == status) ? UserStatus.NORMAL : status;
        firePropertyChange(PROP_STATUS, oldValue, this.status);
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.userName);
        hash = 43 * hash + Objects.hashCode(this.password);
        hash = 43 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof User && ModelHelper.areSameRecord(this, (User) obj);
    }

    @Override
    public String toString() {
        if (getRowState() == DataRowState.NEW) {
            return String.format("UserDAO{userName=%s, status=%s}", userName, status.name());
        }
        return String.format("UserDAO{primaryKey=%d, userName=%s, status=%s}", getPrimaryKey(), userName, status.name());
    }

    /**
     * Factory implementation for {@link UserDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<UserDAO> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public boolean isCompoundSelect() {
            return false;
        }

        @Override
        protected void applyColumnValue(UserDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case USER_NAME:
                    ps.setString(index, dao.userName);
                    break;
                case PASSWORD:
                    ps.setString(index, dao.password);
                    break;
                case STATUS:
                    ps.setInt(index, dao.status.getValue());
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public UserDAO createNew() {
            return new UserDAO();
        }

        @Override
        public DaoFilter<UserDAO> getAllItemsFilter() {
            return UserFilter.of(DaoFilterExpression.empty());
        }

        public DaoFilter<UserDAO> getActiveUsersFilter() {
            return UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.NOT_EQUALS));
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            return new DmlSelectQueryBuilder(DbTable.USER, SchemaHelper.getTableColumns(DbTable.USER));
        }

        @Override
        protected UserDAO fromResultSet(ResultSet rs) throws SQLException {
            UserDAO result = super.fromResultSet(rs);
            UserDAO currentUser = Scheduler.getCurrentUser();
            if (null != currentUser && currentUser.getPrimaryKey() == result.getPrimaryKey()) {
                UserDAO.getFactory().cloneProperties(currentUser, result);
                return currentUser;
            }
            return result;
        }

        @Override
        protected void onCloneProperties(UserDAO fromDAO, UserDAO toDAO) {
            String oldUserName = toDAO.userName;
            String oldPassword = toDAO.password;
            UserStatus oldStatus = toDAO.status;
            toDAO.userName = fromDAO.userName;
            toDAO.password = fromDAO.password;
            toDAO.status = fromDAO.status;
            toDAO.firePropertyChange(PROP_USERNAME, oldUserName, toDAO.userName);
            toDAO.firePropertyChange(PROP_PASSWORD, oldPassword, toDAO.password);
            toDAO.firePropertyChange(PROP_STATUS, oldStatus, toDAO.status);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(UserDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldUserName = dao.userName;
                private final String oldPassword = dao.password;
                private final UserStatus oldStatus = dao.status;

                @Override
                public void accept(PropertyChangeSupport t) {
                    if (!dao.userName.equals(oldUserName)) {
                        t.firePropertyChange(PROP_USERNAME, oldUserName, dao.userName);
                    }
                    if (!dao.password.equals(oldPassword)) {
                        t.firePropertyChange(PROP_PASSWORD, oldPassword, dao.password);
                    }
                    if (dao.status != oldStatus) {
                        t.firePropertyChange(PROP_STATUS, oldStatus, dao.status);
                    }
                }
            };

            dao.userName = asNonNullAndTrimmed(rs.getString(DbColumn.USER_NAME.toString()));
            dao.password = asNonNullAndTrimmed(rs.getString(DbColumn.PASSWORD.toString()));
            dao.status = UserStatus.of(rs.getInt(DbColumn.STATUS.toString()), (null == dao.status) ? UserStatus.NORMAL : dao.status);
            return propertyChanges;
        }

        IUserDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Related(rs.getInt(DbColumn.APPOINTMENT_USER.toString()), asNonNullAndTrimmed(rs.getString(DbColumn.USER_NAME.toString())),
                    UserStatus.of(rs.getInt(DbColumn.STATUS.toString()), UserStatus.INACTIVE));
        }

        /**
         * Loads the first {@link UserDAO} record matching the specified user name.
         *
         * @param connection The {@link Connection} to use to retrieve the data.
         * @param userName The user name value to match.
         * @return The first {@link UserDAO} record matching {@code userName} or {@link Optional#EMPTY} if no match was found..
         * @throws SQLException if unable to perform database query.
         */
        public Optional<UserDAO> findByUserName(Connection connection, String userName) throws SQLException {
            String sql = createDmlSelectQueryBuilder().build().append(" WHERE LOWER(").append(DbColumn.USER_NAME.getDbName()).append(")=?").toString();
            LOG.fine(() -> String.format("Executing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                LOG.fine(() -> String.format("Set first parameter to \"%s\"", userName));
                ps.setString(1, userName.trim().toLowerCase());
                //ps.setShort(2, Values.USER_STATUS_INACTIVE);
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        return Optional.of(fromResultSet(rs));
                    }
                    SQLWarning w = connection.getWarnings();
                    if (null == w) {
                        LOG.log(Level.WARNING, "Null results, no warnings.");
                    } else {
                        LOG.log(Level.WARNING, "Encountered warning", w);
                    }
                }
            }
            return Optional.empty();
        }

        @Override
        public Class<? extends UserDAO> getDaoClass() {
            return UserDAO.class;
        }

        @Override
        public void save(UserDAO dao, Connection connection, boolean force) throws SQLException {
            super.save(IUserDAO.assertValidUser(dao), connection, force);
        }

        @Override
        public String getSaveDbConflictMessage(UserDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Data access object already deleted");
            }

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.USER_ID.getDbName())
                    .append(") FROM ").append(DbTable.USER.getDbName())
                    .append(" WHERE LOWER(").append(DbColumn.USER_NAME.getDbName()).append(")=?");

            if (dao.getRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.USER_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();

            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, dao.getUserName());
                if (dao.getRowState() != DataRowState.NEW) {
                    ps.setInt(1, dao.getPrimaryKey());
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                }
            }
            // PENDING: Internationalize this message
            if (count > 0) {
                return "Another user has the same name";
            }
            return "";
        }

        @Override
        public String getDeleteDependencyMessage(UserDAO dao, Connection connection) throws SQLException {
            if (null == dao || !DataRowState.existsInDb(dao.getRowState())) {
                return "";
            }

            // PENDING: Internationalize messages
            if (dao.getPrimaryKey() == Scheduler.getCurrentUser().getPrimaryKey()) {
                return "Cannot delete the currently signed on user.";
            }
            int count = AppointmentDAO.getFactory().countByUser(connection, dao.getPrimaryKey(), null, null);
            switch (count) {
                case 0:
                    return "";
                case 1:
                    return "User is referenced by one appointment.";
                default:
                    return String.format("User is referenced by %d other appointments", count);
            }
        }

        @Override
        protected DataObjectEvent<? extends UserDAO> createDataObjectEvent(Object source, UserDAO dataAccessObject, DbChangeType changeAction) {
            return new UserDaoEvent(source, dataAccessObject, changeAction);
        }

    }

    private static final class Related extends PropertyBindable implements IUserDAO {

        private final int primaryKey;
        private final String userName;
        private final UserStatus status;

        Related(int primaryKey, String userName, UserStatus status) {
            this.primaryKey = primaryKey;
            this.userName = userName;
            this.status = status;
        }

        @Override
        public String getUserName() {
            return userName;
        }

        @Override
        public UserStatus getStatus() {
            return status;
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof User && ModelHelper.areSameRecord(this, (User) obj);
        }

    }
}
