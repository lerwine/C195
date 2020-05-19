package scheduler.dao;

import scheduler.model.Address;

/**
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IAddressDAO extends DAO, Address {

    @Override
    public ICityDAO getCity();

}
