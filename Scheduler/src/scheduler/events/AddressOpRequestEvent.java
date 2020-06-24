package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;

public final class AddressOpRequestEvent extends OperationRequestEvent<AddressDAO, AddressModel> {

    /**
     * Base {@link EventType} for all {@code AddressOpRequestEvent}s.
     */
    public static final EventType<AddressOpRequestEvent> ADDRESS_OP_REQUEST = new EventType<>(OP_REQUEST_EVENT, "SCHEDULER_ADDRESS_OP_REQUEST");

    /**
     * {@link EventType} for edit {@code AddressOpRequestEvent}s.
     */
    public static final EventType<AddressOpRequestEvent> EDIT_REQUEST = new EventType<>(ADDRESS_OP_REQUEST, "SCHEDULER_ADDRESS_EDIT_REQUEST");

    /**
     * {@link EventType} for delete {@code AddressOpRequestEvent}s.
     */
    public static final EventType<AddressOpRequestEvent> DELETE_REQUEST = new EventType<>(ADDRESS_OP_REQUEST, "SCHEDULER_ADDRESS_DELETE_REQUEST");

    public AddressOpRequestEvent(ModelEvent<AddressDAO, AddressModel> event, Object source, EventTarget target, boolean isDelete) {
        super(event, source, target, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public AddressOpRequestEvent(ModelEvent<AddressDAO, AddressModel> event, boolean isDelete) {
        super(event, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public AddressOpRequestEvent(AddressModel target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public AddressOpRequestEvent(AddressDAO target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

}
