package scheduler.events;

import javafx.event.EventTarget;
import scheduler.dao.DataAccessObject;
import scheduler.dao.IFxModelOptional;
import scheduler.model.ui.FxRecordModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The target {@link DataAccessObject} type.
 * @param <M> The associated {@link FxRecordModel} type.
 */
interface IModelEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends IFxModelOptional<D, M>, Cloneable, java.io.Serializable {

    Object getSource();

    EventTarget getTarget();

    boolean isConsumed();

    void consume();

    @Override
    D getDataAccessObject();

    @Override
    M getFxRecordModel();

    void setFxRecordModel(M fxRecordModel);

    DbOperationType getOperation();
}
