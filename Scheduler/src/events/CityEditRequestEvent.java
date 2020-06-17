package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;


public final class CityEditRequestEvent extends ModelEditRequestEvent<CityModel, CityDAO> {

    /**
     * {@link EventType} for all {@code CityEditRequestEvent}s.
     */
    public static final EventType<CityEditRequestEvent> CITY_EDIT_REQUEST = new EventType<>(ModelEditRequestEvent.EDIT_REQUEST_EVENT, "SCHEDULER_CITY_EDIT_REQUEST");

    private CityEditRequestEvent(CityEditRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CityEditRequestEvent(Object source, EventTarget target, EventType<CityEditRequestEvent> eventType, CityModel model) {
        super(source, target, eventType, model);
    }

    public CityEditRequestEvent(Object source, EventTarget target, EventType<CityEditRequestEvent> eventType, CityDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public CityEditRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityEditRequestEvent(this, newSource, newTarget);
    }

    @Override
    public CityBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityEditRequestEvent#toBeginOperationEvent
    }
    
}
