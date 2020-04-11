package scheduler.dao;

/**
 * Represents a data row from the "user" database table. This object contains the login credentials for users of the current application.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface UserElement extends DataElement {

    public static boolean areEqual(UserElement a, UserElement b) {
        if (null == a)
            return null == b;
        if (null == b)
            return false;
        if (a == b || a.getPrimaryKey() != b.getPrimaryKey())
            return true;
        switch (a.getRowState()) {
            case MODIFIED:
            case UNMODIFIED:
                switch (b.getRowState()) {
                    case MODIFIED:
                    case UNMODIFIED:
                        return true;
                    default:
                        return false;
                }
            case NEW:
                return b.getRowState() == DataRowState.NEW && a.getUserName().equalsIgnoreCase(b.getUserName()) &&
                        a.getStatus()== b.getStatus();
            default:
                return b.getRowState() == DataRowState.DELETED;
        }
    }

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
