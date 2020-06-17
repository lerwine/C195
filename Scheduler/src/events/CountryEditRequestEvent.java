package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;


public final class CountryEditRequestEvent extends ModelEditRequestEvent<CountryModel, CountryDAO> {

    /**
     * {@link EventType} for all {@code CountryEditRequestEvent}s.
     */
    public static final EventType<CountryEditRequestEvent> COUNTRY_EDIT_REQUEST = new EventType<>(ModelEditRequestEvent.EDIT_REQUEST_EVENT, "SCHEDULER_COUNTRY_EDIT_REQUEST");

    private CountryEditRequestEvent(CountryEditRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CountryEditRequestEvent(Object source, EventTarget target, EventType<CountryEditRequestEvent> eventType, CountryModel model) {
        super(source, target, eventType, model);
    }

    public CountryEditRequestEvent(Object source, EventTarget target, EventType<CountryEditRequestEvent> eventType, CountryDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public CountryEditRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryEditRequestEvent(this, newSource, newTarget);
    }

    @Override
    public CountryBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryEditRequestEvent#toBeginOperationEvent
    }
    
}
