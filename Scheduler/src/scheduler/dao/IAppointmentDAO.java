package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.AppointmentEntity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IAppointmentDAO extends PartialAppointmentDAO, IDataAccessObject, AppointmentEntity<Timestamp> {

}
