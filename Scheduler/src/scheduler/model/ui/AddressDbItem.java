package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.Address;
import scheduler.model.db.AddressRowData;
import scheduler.model.db.CityRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface AddressDbItem<T extends AddressRowData> extends AddressItem, UIDbModel<T> {

}
