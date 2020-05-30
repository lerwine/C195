package scheduler.model;

import java.io.Serializable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of object for date/time values
 */
public interface CityRecord<T extends Serializable & Comparable<? super T>> extends DataRecord<T>, City {

}
