package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link CustomerModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CustomerEvent extends ModelItemEvent<CustomerModel, CustomerDAO> {

    private static final long serialVersionUID = 2391804793246253841L;

    public static final EventType<CustomerEvent> CUSTOMER_MODEL_EVENT = new EventType<>(
            MODEL_ITEM_EVENT,
            "CUSTOMER_MODEL_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_EDIT_REQUEST_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_EDIT_REQUEST_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_DELETE_REQUEST_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_DELETE_REQUEST_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_INSERTING_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_INSERTING_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_INSERTED_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_INSERTED_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_UPDATING_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_UPDATING_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_UPDATED_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_UPDATED_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_DELETING_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_DELETING_EVENT");

    public static final EventType<CustomerEvent> CUSTOMER_DELETED_EVENT = new EventType<>(
            CUSTOMER_MODEL_EVENT,
            "CUSTOMER_DELETED_EVENT");

    private CustomerEvent(CustomerEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CustomerEvent(CustomerModel model, Object source, EventTarget target, EventType<CustomerEvent> type, Event fxEvent) {
        super(model, source, target, type, fxEvent);
    }

    public CustomerEvent(Object source, CustomerDAO target, EventType<CustomerEvent> type, Event fxEvent) {
        super(source, target, type, fxEvent);
    }

    @Override
    public FxRecordModel.ModelFactory<CustomerDAO, CustomerModel> getModelFactory() {
        return CustomerModel.getFactory();
    }

    @Override
    public synchronized CustomerEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerEvent(this, newSource, newTarget);
    }

    @Override
    public EventType<CustomerEvent> getEventType() {
        return (EventType<CustomerEvent>) super.getEventType();
    }

}
