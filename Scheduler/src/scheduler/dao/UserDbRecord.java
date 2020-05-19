package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.UserRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface UserDbRecord extends IUserDAO, DbRecord, UserRecord<Timestamp> {

}
