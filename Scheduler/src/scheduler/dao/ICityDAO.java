package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.CityEntity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICityDAO extends PartialCityDAO, IDataAccessObject, CityEntity<Timestamp> {

}
