package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.CountryEntity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICountryDAO extends PartialCountryDAO, IDataAccessObject, CountryEntity<Timestamp> {

}
