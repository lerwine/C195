package scheduler.view.event;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link AddressModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressEvent extends ModelItemEvent<AddressModel, AddressDAO> {

    private static final long serialVersionUID = 8261622802802373344L;

    public static final EventType<AddressEvent> ADDRESS_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, "ADDRESS_MODEL_EVENT");

    public static final EventType<AddressEvent> ADDRESS_EDIT_REQUEST_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_EDIT_REQUEST_EVENT");

    public static final EventType<AddressEvent> ADDRESS_DELETE_REQUEST_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_DELETE_REQUEST_EVENT");

    public static final EventType<AddressEvent> ADDRESS_INSERTING_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_INSERTING_EVENT");

    public static final EventType<AddressEvent> ADDRESS_INSERTED_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_INSERTED_EVENT");

    public static final EventType<AddressEvent> ADDRESS_UPDATING_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_UPDATING_EVENT");

    public static final EventType<AddressEvent> ADDRESS_UPDATED_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_UPDATED_EVENT");

    public static final EventType<AddressEvent> ADDRESS_DELETING_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_DELETING_EVENT");

    public static final EventType<AddressEvent> ADDRESS_DELETED_EVENT = new EventType<>(ADDRESS_MODEL_EVENT, "ADDRESS_DELETED_EVENT");

    private AddressEvent(AddressEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public AddressEvent(AddressModel model, Object source, EventTarget target, EventType<AddressEvent> type) {
        super(model, source, target, type);
    }

    public AddressEvent(Object source, AddressDAO target, EventType<AddressEvent> type) {
        super(source, target, type);
    }

    @Override
    public FxRecordModel.ModelFactory<AddressDAO, AddressModel> getModelFactory() {
        return AddressModel.getFactory();
    }

    @Override
    public synchronized AddressEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AddressEvent> getEventType() {
        return (EventType<AddressEvent>) super.getEventType();
    }

    @Override
    public boolean isDeleteRequest() {
        return getEventType().getName().equals(ADDRESS_DELETE_REQUEST_EVENT.getName());
    }

}
