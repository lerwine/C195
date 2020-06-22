package scheduler.events;

import javafx.event.EventTarget;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
interface IModelEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends Cloneable, java.io.Serializable {

    Object getSource();

    EventTarget getTarget();

    boolean isConsumed();

    void consume();

    D getDataAccessObject();

    M getFxRecordModel();

    void setFxRecordModel(M fxRecordModel);
}
