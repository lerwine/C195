package scheduler.model.db;

import java.sql.Timestamp;
import scheduler.dao.DataRowState;
import scheduler.model.Appointment;

/**
 * Represents a data row from the "appointment" database table.
 * <dl>
 * <dt>{@link scheduler.dao.AppointmentDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.AppointmentItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AppointmentRowData extends Appointment<Timestamp>, RowData {

    public static boolean areEqual(AppointmentRowData a, AppointmentRowData b) {
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
                return b.getRowState() == DataRowState.NEW && CustomerRowData.areEqual(a.getCustomer(), b.getCustomer())
                        && UserRowData.areEqual(a.getUser(), b.getUser())
                        && a.getContact().equalsIgnoreCase(b.getContact())
                        && a.getDescription().equalsIgnoreCase(b.getDescription())
                        && a.getLocation().equalsIgnoreCase(b.getLocation())
                        && a.getType() == b.getType()
                        && a.getTitle().equalsIgnoreCase(b.getTitle())
                        && a.getUrl().equalsIgnoreCase(b.getUrl())
                        && a.getStart().equals(b.getStart())
                        && a.getEnd().equals(b.getEnd());
            default:
                return b.getRowState() == DataRowState.DELETED;
        }
    }

    public static int compareByDates(AppointmentRowData a, AppointmentRowData b) {
        if (null == a) {
            return (null == b) ? 0 : 1;
        }
        if (null == b) {
            return -1;
        }
        Timestamp x = a.getStart();
        Timestamp y = b.getStart();
        if (null == x) {
            return (null == x) ? 0 : 1;
        }
        if (null == y) {
            return -1;
        }
        int c = x.compareTo(y);
        if (c != 0) {
            return c;
        }
        x = a.getEnd();
        y = b.getEnd();
        if (null == x) {
            return (null == x) ? 0 : 1;
        }
        if (null == y) {
            return -1;
        }
        return x.compareTo(y);
    }
    
    @Override
    public CustomerRowData getCustomer();

    @Override
    public UserRowData getUser();

}
