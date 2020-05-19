package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.AddressRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AddressDbRecord extends IAddressDAO, DbRecord, AddressRecord<Timestamp> {

}
