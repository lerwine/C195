package scheduler.model.ui;

import scheduler.dao.IAddressDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface AddressDbItem<T extends IAddressDAO> extends AddressItem, FxDbModel<T> {

}
