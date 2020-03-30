package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppResourceBundleConstants;
import scheduler.AppResources;
import scheduler.dao.schema.DbColumn;

public class UserImpl extends DataObjectImpl implements User {

    /**
     * The name of the 'userName' property.
     */
    public static final String PROP_USERNAME = "userName";

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
    private String password;
    private UserStatus status;

    /**
     * Initializes a {@link DataRowState#NEW} user object.
     */
    public UserImpl() {
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
     * @param userName new value of userName
     */
    public void setUserName(String userName) {
        String oldValue = this.userName;
        this.userName = (userName == null) ? "" : userName;
        firePropertyChange(PROP_USERNAME, oldValue, this.userName);
    }

    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        String oldValue = this.password;
        this.password = (password == null) ? "" : password;
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
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof UserImpl) {
            UserImpl other = (UserImpl) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && userName.equals(other.userName) && password.equals(other.password)
                        && status == other.status;
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    public static final class FactoryImpl extends DataObjectImpl.DaoFactory<UserImpl> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public boolean isAssignableFrom(DataObjectImpl dao) {
            return null != dao && dao instanceof UserImpl;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.USER;
        }

        @Override
        public DbColumn getPrimaryKeyColumn() {
            return DbColumn.USER_ID;
        }

        @Override
        public UserImpl createNew() {
            return new UserImpl();
        }

        @Override
        public DaoFilter<UserImpl> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        public DaoFilter<UserImpl> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public StringBuilder getBaseSelectQuery() {
            StringBuilder sb = new StringBuilder();
            return sb.append("SELECT ").append(DbColumn.USER_ID).append(", ").append(DbColumn.USER_NAME).append(", ").append(DbColumn.PASSWORD)
                    .append(", ").append(DbColumn.STATUS.getDbName()).append(" AS ").append(DbColumn.STATUS)
                    .append(", ").append(DbColumn.USER_CREATE_DATE).append(", ").append(DbColumn.USER_CREATED_BY).append(", ")
                    .append(DbColumn.USER_LAST_UPDATE).append(", ").append(DbColumn.USER_LAST_UPDATE_BY);
        }

        void appendSelectColumns(StringBuilder sb) {
            sb.append(", ").append(DbTable.USER).append(".").append(DbColumn.USER_NAME).append(" AS ").append(DbColumn.USER_NAME)
                    .append(", ").append(DbTable.USER).append(".").append(DbColumn.STATUS.getDbName()).append(" AS ").append(DbColumn.STATUS);
        }

        void appendJoinStatement(StringBuilder sb) {
            sb.append(" LEFT JOIN ").append(DbTable.USER.getDbName()).append(" ").append(DbTable.USER)
                    .append(" ON ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_USER).append(" = ")
                    .append(DbTable.USER).append(".").append(DbColumn.USER_ID);
        }

        @Override
        protected void onInitializeFromResultSet(UserImpl dao, ResultSet rs) throws SQLException {
            String oldUserName = dao.userName;
            dao.userName = rs.getString(DbColumn.USER_NAME.toString());
            String oldPassword = dao.password;
            dao.password = rs.getString(DbColumn.PASSWORD.toString());
            UserStatus oldStatus = dao.status;
            dao.status = UserStatus.of(rs.getInt(DbColumn.STATUS.toString()), oldStatus);
            dao.firePropertyChange(PROP_USERNAME, oldUserName, dao.userName);
            dao.firePropertyChange(PROP_PASSWORD, oldPassword, dao.password);
            dao.firePropertyChange(PROP_STATUS, oldStatus, dao.status);
        }

        User fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new User() {
                private final String userName = rs.getString(DbColumn.USER_NAME.toString());
                private final UserStatus status = UserStatus.of(rs.getInt(DbColumn.STATUS.toString()), UserStatus.INACTIVE);
                private final int primaryKey = rs.getInt(DbColumn.APPOINTMENT_USER.toString());

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
                public DataRowState getRowState() {
                    return DataRowState.UNMODIFIED;
                }

                @Override
                public boolean isExisting() {
                    return true;
                }

                @Override
                public int hashCode() {
                    return primaryKey;
                }

                @Override
                public boolean equals(Object obj) {
                    if (null != obj && obj instanceof User) {
                        User other = (User) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
        }

        /**
         * Loads the first {@link UserImpl} record matching the specified user name.
         *
         * @param connection The {@link Connection} to use to retrieve the data.
         * @param userName The user name value to match.
         * @return The first {@link UserImpl} record matching {@code userName} or {@link Optional#EMPTY} if no match was found..
         * @throws SQLException if unable to perform database query.
         */
        public Optional<UserImpl> findByUserName(Connection connection, String userName) throws SQLException {
            String sql = getBaseSelectQuery().append(" WHERE ").append(DbColumn.USER_NAME.getDbName()).append(" = ?").toString();
            LOG.logp(Level.INFO, getClass().getName(), "findByUserName", String.format("Executing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                LOG.logp(Level.INFO, getClass().getName(), "findByUserName", String.format("Set first parametr to \"%s\"", userName));
                ps.setString(1, userName);
                //ps.setShort(2, Values.USER_STATUS_INACTIVE);
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        UserImpl result = new UserImpl();
                        initializeFromResultSet(result, rs);
                        return Optional.of(result);
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
        public Class<? extends UserImpl> getDaoClass() {
            return UserImpl.class;
        }

        @Override
        public String getSaveConflictMessage(UserImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getDeleteDependencyMessage(UserImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
