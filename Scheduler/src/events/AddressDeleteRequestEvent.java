package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;


public final class AddressDeleteRequestEvent extends ModelDeleteRequestEvent<AddressModel, AddressDAO> {

    /**
     * {@link EventType} for all {@code AddressDeleteRequestEvent}s.
     */
    public static final EventType<AddressDeleteRequestEvent> ADDRESS_DELETE_REQUEST = new EventType<>(ModelDeleteRequestEvent.DELETE_REQUEST_EVENT, "SCHEDULER_ADDRESS_DELETE_REQUEST");

    private AddressDeleteRequestEvent(AddressDeleteRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AddressDeleteRequestEvent(Object source, EventTarget target, EventType<AddressDeleteRequestEvent> eventType, AddressModel model) {
        super(source, target, eventType, model);
    }

    public AddressDeleteRequestEvent(Object source, EventTarget target, EventType<AddressDeleteRequestEvent> eventType, AddressDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public AddressDeleteRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressDeleteRequestEvent(this, newSource, newTarget);
    }

    @Override
    public AddressBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressDeleteRequestEvent#toBeginOperationEvent
    }
    
}
