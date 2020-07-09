package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.UserEntity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IUserDAO extends PartialUserDAO, IDataAccessObject, UserEntity<Timestamp> {

}
