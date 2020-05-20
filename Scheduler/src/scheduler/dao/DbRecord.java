package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.DataRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface DbRecord extends DbObject, DataRecord<Timestamp> {

}
