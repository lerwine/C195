package scheduler.events;

import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
interface ModelFailedEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends IModelEvent<D, M> {

    String getMessage();

    Throwable getFault();

    boolean isCanceled();
}
