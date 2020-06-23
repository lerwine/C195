package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;

public abstract class CityEvent extends ModelEvent<CityDAO, CityModel> {

    private static final long serialVersionUID = -6996428374286059723L;

    /**
     * Base {@link EventType} for all {@code CityEvent}s.
     */
    public static final EventType<CityEvent> CITY_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_CITY_EVENT");

    /**
     * Base {@link EventType} for all operational {@code CityEvent}s.
     */
    public static final EventType<CityEvent> OP_EVENT_TYPE = new EventType<>(CITY_EVENT_TYPE, "SCHEDULER_CITY_OP_EVENT");

    protected CityEvent(CityEvent event, Object source, EventTarget target, EventType<? extends CityEvent> eventType) {
        super(event, source, target, eventType);
    }

    protected CityEvent(CityEvent event, EventType<? extends CityEvent> eventType) {
        super(event, eventType);
    }

    protected CityEvent(CityModel fxRecordModel, Object source, EventTarget target, EventType<? extends CityEvent> eventType) {
        super(fxRecordModel, source, target, eventType);
    }

    protected CityEvent(CityDAO dao, Object source, EventTarget target, EventType<? extends CityEvent> eventType) {
        super(dao, source, target, eventType);
    }

}
