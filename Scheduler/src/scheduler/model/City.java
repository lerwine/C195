package scheduler.model;

/**
 * Base interface for objects that represent a {@code city} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface City extends CityProperties, PartialDataEntity {

    @Override
    public Country getCountry();

}
