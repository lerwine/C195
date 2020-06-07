package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.ui.AddressModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AddressMutateEvent extends ItemMutateEvent<AddressModel> {

    private static final long serialVersionUID = 8261622802802373344L;

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

    public AddressMutateEvent(AddressModel source, EventTarget target, EventType<AddressMutateEvent> type, Event fxEvent) {
        super(source, target, type, fxEvent);
    }

}
