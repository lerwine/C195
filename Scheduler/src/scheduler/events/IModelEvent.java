package scheduler.events;

import javafx.event.EventTarget;
import scheduler.dao.DataAccessObject;
import scheduler.model.fx.EntityModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The target {@link DataAccessObject} type.
 * @param <M> The associated {@link EntityModel} type.
 */
@SuppressWarnings("serial")
interface IModelEvent<D extends DataAccessObject, M extends EntityModel<D>> extends Cloneable, java.io.Serializable {

    Object getSource();

    EventTarget getTarget();

    boolean isConsumed();

    void consume();

    default D getDataAccessObject() {
        return getEntityModel().dataObject();
    }

    M getEntityModel();

    DbOperationType getOperation();
}
