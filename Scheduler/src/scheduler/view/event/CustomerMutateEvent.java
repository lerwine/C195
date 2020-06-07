package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.ui.CustomerModel;

/**
 * Event that is fired when a {@link CustomerModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CustomerMutateEvent extends ItemMutateEvent<CustomerModel> {

    private static final long serialVersionUID = 2391804793246253841L;

    public static final EventType<CustomerMutateEvent> CUSTOMER_MUTATE_EVENT = new EventType<>(
            ITEM_MUTATE_EVENT,
            "CUSTOMER_MUTATE_EVENT");

    public static final EventType<CustomerMutateEvent> CUSTOMER_INSERT_EVENT = new EventType<>(
            CUSTOMER_MUTATE_EVENT,
            "CUSTOMER_INSERT_EVENT");

    public static final EventType<CustomerMutateEvent> CUSTOMER_UPDATE_EVENT = new EventType<>(
            CUSTOMER_MUTATE_EVENT,
            "CUSTOMER_UPDATE_EVENT");

    public static final EventType<CustomerMutateEvent> CUSTOMER_DELETE_EVENT = new EventType<>(
            CUSTOMER_MUTATE_EVENT,
            "CUSTOMER_DELETE_EVENT");

    public CustomerMutateEvent(CustomerModel source, EventTarget target, EventType<CustomerMutateEvent> type, Event fxEvent) {
        super(source, target, type, fxEvent);
    }

}
