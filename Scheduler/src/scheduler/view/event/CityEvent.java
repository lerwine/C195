package scheduler.view.event;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link CityModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityEvent extends ModelItemEvent<CityModel, CityDAO> {

    private static final long serialVersionUID = 2299267381558318300L;

    public static final EventType<CityEvent> CITY_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, "CITY_MODEL_EVENT");

    public static final EventType<CityEvent> CITY_EDIT_REQUEST_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_EDIT_REQUEST_EVENT");

    public static final EventType<CityEvent> CITY_DELETE_REQUEST_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_DELETE_REQUEST_EVENT");

    public static final EventType<CityEvent> CITY_INSERTING_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_INSERTING_EVENT");

    public static final EventType<CityEvent> CITY_INSERTED_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_INSERTED_EVENT");

    public static final EventType<CityEvent> CITY_UPDATING_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_UPDATING_EVENT");

    public static final EventType<CityEvent> CITY_UPDATED_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_UPDATED_EVENT");

    public static final EventType<CityEvent> CITY_DELETING_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_DELETING_EVENT");

    public static final EventType<CityEvent> CITY_DELETED_EVENT = new EventType<>(CITY_MODEL_EVENT, "CITY_DELETED_EVENT");

    private CityEvent(CityEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CityEvent(CityModel model, Object source, EventTarget target, EventType<CityEvent> type) {
        super(model, source, target, type);
    }

    public CityEvent(Object source, CityDAO target, EventType<CityEvent> type) {
        super(source, target, type);
    }

    @Override
    public FxRecordModel.ModelFactory<CityDAO, CityModel> getModelFactory() {
        return CityModel.getFactory();
    }

    @Override
    public synchronized CityEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CityEvent> getEventType() {
        return (EventType<CityEvent>) super.getEventType();
    }

    @Override
    public boolean isDeleteRequest() {
        return getEventType().getName().equals(CITY_DELETE_REQUEST_EVENT.getName());
    }

}
