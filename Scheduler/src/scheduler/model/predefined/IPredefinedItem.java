package scheduler.model.predefined;

import scheduler.dao.DbRecord;
import scheduler.model.DataObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IPredefinedItem extends DataObject {

    PredefinedItem<? extends DbRecord> getPredefinedData();
}
