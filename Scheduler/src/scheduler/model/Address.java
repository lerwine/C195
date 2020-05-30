package scheduler.model;

/**
 * Interface for objects that contain either partial or complete information from the {@code address} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Address extends AddressProperties, DataObject {

    @Override
    public City getCity();

}
