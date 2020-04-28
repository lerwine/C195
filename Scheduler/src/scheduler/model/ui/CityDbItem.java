package scheduler.model.ui;

import scheduler.model.db.CityRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CityDbItem<T extends CityRowData> extends CityItem, FxDbModel<T> {

}
