package scheduler.dao;

import java.sql.Timestamp;
import scheduler.model.CustomerEntity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICustomerDAO extends PartialCustomerDAO, IDataAccessObject, CustomerEntity<Timestamp> {

}
