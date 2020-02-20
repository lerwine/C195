package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.util.Values;

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
     * @return {@link Values#USER_STATUS_NORMAL}, {@link Values#USER_STATUS_ADMIN} or {@link Values#USER_STATUS_INACTIVE}.
     */
    int getStatus();

    /**
     * Creates a read-only Customer object from object values.
     *
     * @param pk The value of the primary key.
     * @param userName The name of the user.
     * @param password The encoded hash that is used to validate the current user's password.
     * @param status A value that indicates the status of the current user.
     * @return The read-only Customer object.
     */
    public static User of(int pk, String userName, String password, int status) {
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
            public int getStatus() {
                return status;
            }

            @Override
            public int getPrimaryKey() {
                return pk;
            }

            @Override
            public int getRowState() {
                return Values.ROWSTATE_UNMODIFIED;
            }
        };
    }

    /**
     * Creates a read-only User object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @param pkColName The name of the column containing the value of the primary key.
     * @return The read-only User object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static User of(ResultSet resultSet, String pkColName) throws SQLException {
        Objects.requireNonNull(pkColName, "Primary key column name cannot be null");
        int id = resultSet.getInt(pkColName);
        if (resultSet.wasNull()) {
            return null;
        }
        String userName = resultSet.getString(UserImpl.COLNAME_USERNAME);
        if (resultSet.wasNull()) {
            userName = "";
        }
        String password = resultSet.getString(UserImpl.COLNAME_PASSWORD);
        if (resultSet.wasNull()) {
            password = "";
        }
        int status = resultSet.getInt(UserImpl.COLNAME_ACTIVE);
        return User.of(id, userName, password, (resultSet.wasNull()) ? Values.USER_STATUS_INACTIVE : Values.asValidUserStatus(status));
    }
}
