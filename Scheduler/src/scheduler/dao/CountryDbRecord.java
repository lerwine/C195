package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.CountryRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryDbRecord extends ICountryDAO, DbRecord, CountryRecord<Timestamp> {

}
