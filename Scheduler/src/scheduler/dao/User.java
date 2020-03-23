package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;

/**
 * Represents a data row from the "user" database table. This object contains the login credentials for users of the current application. Table definition: <code>CREATE TABLE `user` (
 *   `userId` int(11) NOT NULL AUTO_INCREMENT,
 *   `userName` varchar(50) NOT NULL,
 *   `password` varchar(50) NOT NULL,
 *   `active` tinyint(4) NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`userId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;</code>
 *
 * @author erwinel
 */
public interface User extends DataObject {

    /**
     * Gets the current user's login name. This corresponds to the "userName" database column.
     *
     * @return The current user's login name.
     */
    String getUserName();

    /**
     * Gets the encoded hash that is used to validate the current user's password. This corresponds to the "password" database column.
     *
     * @return The encoded hash that is used to validate the current user's password.
     */
    String getPassword();

    /**
     * Gets a value that indicates the status of the current user. This corresponds to the "active" database column.
     *
     * @return {@link UserStatus} value indicating the current user's status.
     */
    UserStatus getStatus();

    /**
     * Creates a read-only Customer object from object values.
     *
     * @param pk The value of the primary key.
     * @param userName The name of the user.
     * @param password The encoded hash that is used to validate the current user's password.
     * @param status A value that indicates the status of the current user.
     * @return The read-only Customer object.
     */
    public static User of(int pk, String userName, String password, UserStatus status) {
        Objects.requireNonNull(userName, "User name cannot be null");
        Objects.requireNonNull(password, "Password name cannot be null");
        return new User() {
            @Override
            public String getUserName() {
                return userName;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public UserStatus getStatus() {
                return status;
            }

            @Override
            public int getPrimaryKey() {
                return pk;
            }

            @Override
            public DataRowState getRowState() {
                return DataRowState.UNMODIFIED;
            }

        };
    }

    /**
     * Creates a read-only User object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @param columns The {@link TableColumnList} that created the current lookup query.
     * @return The read-only User object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static User of(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
        Optional<Integer> id = columns.tryGetInt(resultSet, DbName.CUSTOMER_ID);
        if (id.isPresent()) {
            return User.of(id.get(), columns.getString(resultSet, DbColumn.USER_NAME, ""), columns.getString(resultSet, DbColumn.PASSWORD, ""),
                    UserStatus.of(columns.getInt(resultSet, DbColumn.STATUS, UserStatus.INACTIVE.getValue()), UserStatus.INACTIVE));
        }
        return null;
    }
}
