package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.AddressEntity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IAddressDAO extends PartialAddressDAO, IDataAccessObject, AddressEntity<Timestamp> {

}
