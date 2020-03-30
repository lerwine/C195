package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Represents a data row from the "user" database table. This object contains the login credentials for users of the current application.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface User extends DataObject {

    /**
     * Gets the current user's login name. This corresponds to the "userName" database column.
     *
     * @return The current user's login name.
     */
    String getUserName();

    /**
     * Gets a value that indicates the status of the current user. This corresponds to the "active" database column.
     *
     * @return {@link UserStatus} value indicating the current user's status.
     */
    UserStatus getStatus();

}
