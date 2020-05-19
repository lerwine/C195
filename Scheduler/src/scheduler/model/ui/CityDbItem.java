package scheduler.model.ui;

import scheduler.dao.ICityDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CityDbItem<T extends ICityDAO> extends CityItem, FxDbModel<T> {

}
