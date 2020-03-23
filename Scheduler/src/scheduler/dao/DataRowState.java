package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum DataRowState {
    NEW,
    UNMODIFIED,
    MODIFIED,
    DELETED;
    
    public static boolean existsInDb(DataRowState status) {
        return status == DataRowState.UNMODIFIED || status == DataRowState.MODIFIED;
    }
}
