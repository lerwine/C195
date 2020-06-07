package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;
import static scheduler.view.event.ItemMutateEvent.ITEM_MUTATE_EVENT;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AddressMutateEvent extends ItemMutateEvent<AddressModel> {

    public static final EventType<AddressMutateEvent> ADDRESS_MUTATE_EVENT = new EventType<>(
            ITEM_MUTATE_EVENT,
            "ADDRESS_MUTATE_EVENT");

    public static final EventType<AddressMutateEvent> ADDRESS_INSERT_EVENT = new EventType<>(
            ADDRESS_MUTATE_EVENT,
            "ADDRESS_INSERT_EVENT");

    public static final EventType<AddressMutateEvent> ADDRESS_UPDATE_EVENT = new EventType<>(
            ADDRESS_MUTATE_EVENT,
            "ADDRESS_UPDATE_EVENT");

    public static final EventType<AddressMutateEvent> ADDRESS_DELETE_EVENT = new EventType<>(
            ADDRESS_MUTATE_EVENT,
            "ADDRESS_DELETE_EVENT");

    public AddressMutateEvent(AddressModel source, EventType<AddressMutateEvent> type, Event fxEvent) {
        super(source, type, fxEvent);
    }

    @Override
    public AddressDAO getTarget() {
        return (AddressDAO)super.getTarget();
    }

}
