package scheduler.dao;

import scheduler.model.Customer;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICustomerDAO extends DbObject, Customer {

    @Override
    public IAddressDAO getAddress();

}
