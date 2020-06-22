package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;

public abstract class CountryEvent extends ModelEvent<CountryDAO, CountryModel> {

    /**
     * Base {@link EventType} for all {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> COUNTRY_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_COUNTRY_EVENT");

    /**
     * Base {@link EventType} for all operational {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> OP_EVENT_TYPE = new EventType<>(COUNTRY_EVENT_TYPE, "SCHEDULER_COUNTRY_OP_EVENT");

    protected CountryEvent(CountryEvent event, Object source, EventTarget target, EventType<? extends CountryEvent> eventType) {
        super(event, source, target, eventType);
    }

    protected CountryEvent(CountryEvent event, EventType<? extends CountryEvent> eventType) {
        super(event, eventType);
    }

    protected CountryEvent(CountryModel fxRecordModel, Object source, EventTarget target, EventType<? extends CountryEvent> eventType) {
        super(fxRecordModel, source, target, eventType);
    }

    protected CountryEvent(CountryDAO dao, Object source, EventTarget target, EventType<? extends CountryEvent> eventType) {
        super(dao, source, target, eventType);
    }

}
