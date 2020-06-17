package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;


public final class AddressEditRequestEvent extends ModelEditRequestEvent<AddressModel, AddressDAO> {

    /**
     * {@link EventType} for all {@code AddressEditRequestEvent}s.
     */
    public static final EventType<AddressEditRequestEvent> ADDRESS_EDIT_REQUEST = new EventType<>(ModelEditRequestEvent.EDIT_REQUEST_EVENT, "SCHEDULER_ADDRESS_EDIT_REQUEST");

    private AddressEditRequestEvent(AddressEditRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AddressEditRequestEvent(Object source, EventTarget target, EventType<AddressEditRequestEvent> eventType, AddressModel model) {
        super(source, target, eventType, model);
    }

    public AddressEditRequestEvent(Object source, EventTarget target, EventType<AddressEditRequestEvent> eventType, AddressDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public AddressEditRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressEditRequestEvent(this, newSource, newTarget);
    }

    @Override
    public AddressBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressEditRequestEvent#toBeginOperationEvent
    }
    
}
