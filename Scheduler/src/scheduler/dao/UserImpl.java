package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.SelectList;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.view.user.UserModel;

public class UserImpl extends DataObjectImpl implements User {

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
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(UserStatus status) {
        this.status = (null == status) ? UserStatus.NORMAL : status;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<UserImpl, UserModel> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        private static final SelectList DETAIL_DML;

        static {
            DETAIL_DML = new SelectList(DbTable.USER);
            DETAIL_DML.makeUnmodifiable();
        }

        // This is a singleton instance
        private FactoryImpl() {
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
            SelectList dml = getDetailDml();
            String sql = dml.getSelectQuery().append(" WHERE `").append(DbColumn.USER_NAME.getDbName().getValue()).append("` = ?").toString();
            LOG.logp(Level.INFO, getClass().getName(), "findByUserName", String.format("Executing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                LOG.logp(Level.INFO, getClass().getName(), "findByUserName", String.format("Set first parametr to \"%s\"", userName));
                ps.setString(1, userName);
                //ps.setShort(2, Values.USER_STATUS_INACTIVE);
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        return Optional.of(fromResultSet(rs, dml));
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
        protected UserImpl fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            UserImpl r = new UserImpl();
            initializeDao(r, resultSet, columns);
            return r;
        }

        @Override
        public SelectList getDetailDml() {
            return DETAIL_DML;
        }

        @Override
        public Class<? extends UserImpl> getDaoClass() {
            return UserImpl.class;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.USER;
        }

        @Override
        protected void setSaveStatementValue(UserImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
            switch (column) {
                case USER_NAME:
                    ps.setString(index, dao.getUserName());
                    break;
                case PASSWORD:
                    ps.setString(index, dao.getPassword());
                    break;
                case STATUS:
                    ps.setInt(index, dao.getStatus().getValue());
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected column name");
            }
        }

        @Override
        protected void onInitializeDao(UserImpl target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            target.userName = columns.getString(resultSet, DbColumn.USER_NAME, "");
            target.password = columns.getString(resultSet, DbColumn.PASSWORD, "");
            target.status = UserStatus.of(columns.getInt(resultSet, DbColumn.STATUS, UserStatus.INACTIVE.getValue()), UserStatus.INACTIVE);
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
