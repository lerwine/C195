package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.CityModel;

@Deprecated
public final class CityOpRequestEvent extends OperationRequestEvent<CityDAO, CityModel> {

    private static final long serialVersionUID = 4423298918901986199L;

    /**
     * Base {@link EventType} for all {@code CityOpRequestEvent}s.
     */
    public static final EventType<CityOpRequestEvent> CITY_OP_REQUEST = new EventType<>(OP_REQUEST_EVENT, "SCHEDULER_CITY_OP_REQUEST");

    /**
     * {@link EventType} for edit {@code CityOpRequestEvent}s.
     */
    public static final EventType<CityOpRequestEvent> EDIT_REQUEST = new EventType<>(CITY_OP_REQUEST, "SCHEDULER_CITY_EDIT_REQUEST");

    /**
     * {@link EventType} for delete {@code CityOpRequestEvent}s.
     */
    public static final EventType<CityOpRequestEvent> DELETE_REQUEST = new EventType<>(CITY_OP_REQUEST, "SCHEDULER_CITY_DELETE_REQUEST");

    public CityOpRequestEvent(ModelEvent<CityDAO, CityModel> event, Object source, EventTarget target, boolean isDelete) {
        super(event, source, target, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public CityOpRequestEvent(ModelEvent<CityDAO, CityModel> event, boolean isDelete) {
        super(event, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public CityOpRequestEvent(RecordModelContext<CityDAO, CityModel> target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public CityOpRequestEvent(CityModel target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

}
