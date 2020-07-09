package scheduler.events;

import javafx.event.EventTarget;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The target {@link DataAccessObject} type.
 * @param <M> The associated {@link FxRecordModel} type.
 */
interface IModelEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends Cloneable, java.io.Serializable {

    Object getSource();

    EventTarget getTarget();

    boolean isConsumed();

    void consume();

    default D getDataAccessObject() {
        return getFxRecordModel().dataObject();
    }

    M getFxRecordModel();

    DbOperationType getOperation();
}
