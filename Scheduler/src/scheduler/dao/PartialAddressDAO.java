package scheduler.dao;

import scheduler.model.Address;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface PartialAddressDAO extends PartialDataAccessObject, Address {

    @Override
    public PartialCityDAO getCity();

}
