package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum DataRowState {
    NEW,
    UNMODIFIED,
    MODIFIED,
    DELETED;
    
    public static boolean existsInDb(DataRowState status) {
        switch (status) {
            case MODIFIED:
            case UNMODIFIED:
                return true;
            default:
                return false;
        }
    }
}
