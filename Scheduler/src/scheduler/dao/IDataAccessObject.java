package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.DataEntity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IDataAccessObject extends PartialDataAccessObject, DataEntity<Timestamp> {

}
