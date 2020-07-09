package scheduler.events;

import scheduler.dao.DataAccessObject;
import scheduler.model.ui.EntityModelImpl;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D>
 * @param <M>
 */
public interface ModelFailedEvent<D extends DataAccessObject, M extends EntityModelImpl<D>> extends IModelEvent<D, M> {

    String getMessage();

    Throwable getFault();
    
    FailKind getFailKind();
}
