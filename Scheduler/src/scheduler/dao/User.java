package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a data row from the "user" database table.
 * This object contains the login credentials for users of the current application.
 * Table definition: <code>CREATE TABLE `user` (
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
 * @author erwinel
 */
public interface User extends DataObject {
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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_USERID = "userId";
    
    public static final String COLNAME_USERNAME = "userName";
    
    public static final String COLNAME_PASSWORD = "password";
    
    public static final String COLNAME_ACTIVE = "active";
    
    //</editor-fold>
    
    /**
     * Gets the current user's login name.
     * This corresponds to the "userName" database column.
     * 
     * @return The current user's login name.
     */
    String getUserName();

    /**
     * Gets the encoded hash that is used to validate the current user's password.
     * This corresponds to the "password" database column.
     * 
     * @return The encoded hash that is used to validate the current user's password.
     */
    String getPassword();
    
    /**
     * Gets a value that indicates the status of the current user.
     * This corresponds to the "active" database column.
     * 
     * @return {@link #STATUS_USER}, {@link #STATUS_ADMIN} or {@link #STATUS_INACTIVE}.
     */
    int getStatus();
    
    /**
     * Creates a read-only Customer object from object values.
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
            public String getUserName() { return userName; }
            @Override
            public String getPassword() { return password; }
            @Override
            public int getStatus() { return status; }
            @Override
            public int getPrimaryKey() { return pk; }
            @Override
            public int getRowState() { return ROWSTATE_UNMODIFIED; }
        };
    }
    
    /**
     * Creates a read-only User object from a result set.
     * @param resultSet The data retrieved from the database.
     * @param pkColName The name of the column containing the value of the primary key.
     * @return The read-only User object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static User of(ResultSet resultSet, String pkColName) throws SQLException {
        Objects.requireNonNull(pkColName, "Primary key column name cannot be null");
        int id = resultSet.getInt(pkColName);
        if (resultSet.wasNull())
            return null;
        String userName = resultSet.getString(COLNAME_USERNAME);
        if (resultSet.wasNull())
            userName = "";
        String password = resultSet.getString(COLNAME_PASSWORD);
        if (resultSet.wasNull())
            password = "";
        int status = resultSet.getInt(COLNAME_ACTIVE);
        return User.of(id, userName, password, (resultSet.wasNull()) ? STATUS_INACTIVE : asValidStatus(status));
    }
}
