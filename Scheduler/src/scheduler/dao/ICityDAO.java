package scheduler.dao;

import scheduler.model.City;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICityDAO extends DAO, City {

    @Override
    public ICountryDAO getCountry();
    
}
