package scheduler.model;

/**
 * Base interface for objects that represent an {@code address} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Address extends AddressProperties, PartialDataEntity {

    @Override
    public City getCity();

}
