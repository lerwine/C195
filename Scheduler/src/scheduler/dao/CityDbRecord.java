package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.CityRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityDbRecord extends ICityDAO, DbRecord, CityRecord<Timestamp> {

}
