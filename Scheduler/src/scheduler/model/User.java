package scheduler.model;

/**
 * Interface for objects that contain either partial or complete information from the {@code user} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface User extends DbDataModel {

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
