package scheduler.model.db;

import scheduler.dao.DataRowState;
import scheduler.model.User;

/**
 * Represents a data row from the "user" database table.
 * <dl>
 * <dt>{@link scheduler.dao.UserDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.UserItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface UserRowData extends User, RowData {

    public static boolean areEqual(UserRowData a, UserRowData b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        if (a == b || a.getPrimaryKey() != b.getPrimaryKey()) {
            return true;
        }
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
                return b.getRowState() == DataRowState.NEW && a.getUserName().equalsIgnoreCase(b.getUserName())
                        && a.getStatus() == b.getStatus();
            default:
                return b.getRowState() == DataRowState.DELETED;
        }
    }

}
