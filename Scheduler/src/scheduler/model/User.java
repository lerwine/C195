package scheduler.model;

/**
 * Base interface for objects that represent a {@code user} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface User extends PartialDataEntity {

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
