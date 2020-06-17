package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;


public final class CityDeleteRequestEvent extends ModelDeleteRequestEvent<CityModel, CityDAO> {

    /**
     * {@link EventType} for all {@code CityDeleteRequestEvent}s.
     */
    public static final EventType<CityDeleteRequestEvent> CITY_DELETE_REQUEST = new EventType<>(ModelDeleteRequestEvent.DELETE_REQUEST_EVENT, "SCHEDULER_CITY_DELETE_REQUEST");

    private CityDeleteRequestEvent(CityDeleteRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CityDeleteRequestEvent(Object source, EventTarget target, EventType<CityDeleteRequestEvent> eventType, CityModel model) {
        super(source, target, eventType, model);
    }

    public CityDeleteRequestEvent(Object source, EventTarget target, EventType<CityDeleteRequestEvent> eventType, CityDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public CityDeleteRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityDeleteRequestEvent(this, newSource, newTarget);
    }

    @Override
    public CityBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityDeleteRequestEvent#toBeginOperationEvent
    }
    
}
