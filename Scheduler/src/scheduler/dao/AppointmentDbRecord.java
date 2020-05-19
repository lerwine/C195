package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.AppointmentRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AppointmentDbRecord extends IAppointmentDAO, DbRecord, AppointmentRecord<Timestamp> {

}
