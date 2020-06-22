package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;

public final class CitySuccessEvent extends CityEvent {

    private static final String BASE_EVENT_NAME = "SCHEDULER_CITY_SUCCESS_EVENT";

    /**
     * Base {@link EventType} for all {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_CITY_SAVE_SUCCESS");

    /**
     * {@link EventType} for delete {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_CITY_DELETE_SUCCESS");

    private static EventType<CitySuccessEvent> assertValidEventType(EventType<CitySuccessEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    public CitySuccessEvent(CityEvent event, Object source, EventTarget target, EventType<CitySuccessEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
    }

    public CitySuccessEvent(CityEvent event, EventType<CitySuccessEvent> eventType) {
        super(event, assertValidEventType(eventType));
    }

    public CitySuccessEvent(CityModel fxRecordModel, Object source, EventTarget target, EventType<CitySuccessEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
    }

    public CitySuccessEvent(CityDAO dao, Object source, EventTarget target, EventType<CitySuccessEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
    }

}
