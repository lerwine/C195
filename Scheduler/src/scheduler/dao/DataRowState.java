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
        if (null == status)
            return false;
        switch (status) {
            case MODIFIED:
            case UNMODIFIED:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isChange(DataRowState status) {
        if (null == status)
            return false;
        switch (status) {
            case MODIFIED:
            case NEW:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isNewRow(DataRowState status) {
        return null != status && status == NEW;
    }
}
