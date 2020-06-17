package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;


public final class CountryDeleteRequestEvent extends ModelDeleteRequestEvent<CountryModel, CountryDAO> {

    /**
     * {@link EventType} for all {@code CountryDeleteRequestEvent}s.
     */
    public static final EventType<CountryDeleteRequestEvent> COUNTRY_DELETE_REQUEST = new EventType<>(ModelDeleteRequestEvent.DELETE_REQUEST_EVENT, "SCHEDULER_COUNTRY_DELETE_REQUEST");

    private CountryDeleteRequestEvent(CountryDeleteRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CountryDeleteRequestEvent(Object source, EventTarget target, EventType<CountryDeleteRequestEvent> eventType, CountryModel model) {
        super(source, target, eventType, model);
    }

    public CountryDeleteRequestEvent(Object source, EventTarget target, EventType<CountryDeleteRequestEvent> eventType, CountryDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public CountryDeleteRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryDeleteRequestEvent(this, newSource, newTarget);
    }

    @Override
    public CountryBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryDeleteRequestEvent#toBeginOperationEvent
    }
    
}
