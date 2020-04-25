package scheduler.dao;

import scheduler.model.UserStatus;
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
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.UserFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.util.InternalException;
import static scheduler.util.Values.asNonNullAndTrimmed;
import scheduler.model.db.UserRowData;

/**
 * Data access object for the {@code user} database table.
 * <p>
 * This object contains the login credentials for users of the current application.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.USER)
public class UserDAO extends DataAccessObject implements UserRowData {

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
    protected void reValidate(Consumer<ValidationResult> addValidation) {
        if (userName.trim().isEmpty()) {
            addValidation.accept(ValidationResult.NAME_EMPTY);
        }
        if (password.trim().isEmpty()) {
            addValidation.accept(ValidationResult.PASSWORD_EMPTY);
        }
        if (null == status) {
            status = UserStatus.NORMAL;
        }
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
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof UserDAO) {
            UserDAO other = (UserDAO) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && userName.equals(other.userName) && password.equals(other.password)
                        && status == other.status;
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    /**
     * Factory implementation for {@link scheduler.model.db.UserRowData} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<UserDAO> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

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

        UserRowData fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new UserRowData() {
                private final String userName = asNonNullAndTrimmed(rs.getString(DbColumn.USER_NAME.toString()));
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
                    if (null != obj && obj instanceof UserRowData) {
                        UserRowData other = (UserRowData) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
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
            LOG.log(Level.INFO, String.format("Executing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                LOG.log(Level.INFO, String.format("Set first parameter to \"%s\"", userName));
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
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                }
            }
            // PENDING: Internationalize this
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
            int count = AppointmentDAO.getFactory().countByUser(connection, dao.getPrimaryKey(), null, null);
            // PENDING: Internationalize these
            switch (count) {
                case 0:
                    return "";
                case 1:
                    return "User is referenced by one appointment.";
                default:
                    return String.format("User is referenced by %d other appointments", count);
            }
        }

    }

}
