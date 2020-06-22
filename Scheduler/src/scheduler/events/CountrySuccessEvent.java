package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;

public final class CountrySuccessEvent extends CountryEvent {

    private static final String BASE_EVENT_NAME = "SCHEDULER_COUNTRY_SUCCESS_EVENT";

    /**
     * Base {@link EventType} for all {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_COUNTRY_SAVE_SUCCESS");

    /**
     * {@link EventType} for delete {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_COUNTRY_DELETE_SUCCESS");

    private static EventType<CountrySuccessEvent> assertValidEventType(EventType<CountrySuccessEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    public CountrySuccessEvent(CountryEvent event, Object source, EventTarget target, EventType<CountrySuccessEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
    }

    public CountrySuccessEvent(CountryEvent event, EventType<CountrySuccessEvent> eventType) {
        super(event, assertValidEventType(eventType));
    }

    public CountrySuccessEvent(CountryModel fxRecordModel, Object source, EventTarget target, EventType<CountrySuccessEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
    }

    public CountrySuccessEvent(CountryDAO dao, Object source, EventTarget target, EventType<CountrySuccessEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
    }

}
