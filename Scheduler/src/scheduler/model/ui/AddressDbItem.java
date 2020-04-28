package scheduler.model.ui;

import scheduler.model.db.AddressRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface AddressDbItem<T extends AddressRowData> extends AddressItem, FxDbModel<T> {

}
