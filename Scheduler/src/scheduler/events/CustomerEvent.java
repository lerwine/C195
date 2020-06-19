package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.IAddressDAO;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link CustomerModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CustomerEvent extends DbOperationEvent<CustomerModel, CustomerDAO> {

    private static final long serialVersionUID = 2391804793246253841L;
    public static final String CUSTOMER_MODEL_EVENT_NAME = "SCHEDULER_CUSTOMER_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_CUSTOMER_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_REQUEST";
    public static final String INSERT_VALIDATION_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERT_VALIDATION";
    public static final String DB_INSERT_EVENT_NAME = "SCHEDULER_CUSTOMER_DB_INSERT";
    public static final String UPDATE_VALIDATION_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATE_VALIDATION";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_CUSTOMER_DB_UPDATE";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_VALIDATION";
    public static final String DB_DELETE_EVENT_NAME = "SCHEDULER_CUSTOMER_DB_DELETE";

    /**
     * Base {@link EventType} for all {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> CUSTOMER_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, CUSTOMER_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERT_VALIDATION} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> INSERT_VALIDATION_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, INSERT_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_INSERT} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> DB_INSERT_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, DB_INSERT_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATE_VALIDATION} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> UPDATE_VALIDATION_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, UPDATE_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_UPDATE} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> UPDATED_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_VALIDATION} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> DELETING_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_DELETE} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> DB_DELETE_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, DB_DELETE_EVENT_NAME);

    public static DbOperationType toDbOperationType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return DbOperationType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return DbOperationType.DELETE_REQUEST;
                case INSERT_VALIDATION_EVENT_NAME:
                    return DbOperationType.INSERT_VALIDATION;
                case DB_INSERT_EVENT_NAME:
                    return DbOperationType.DB_INSERT;
                case UPDATE_VALIDATION_EVENT_NAME:
                    return DbOperationType.UPDATE_VALIDATION;
                case UPDATED_EVENT_NAME:
                    return DbOperationType.DB_UPDATE;
                case DELETING_EVENT_NAME:
                    return DbOperationType.DELETE_VALIDATION;
                case DB_DELETE_EVENT_NAME:
                    return DbOperationType.DB_DELETE;
            }
        }
        return DbOperationType.NONE;
    }

    @SuppressWarnings("incomplete-switch")
    public static EventType<CustomerEvent> toEventType(DbOperationType operation) {
        if (null != operation) {
            switch (operation) {
                case EDIT_REQUEST:
                    return EDIT_REQUEST_EVENT_TYPE;
                case DELETE_REQUEST:
                    return DELETE_REQUEST_EVENT_TYPE;
                case INSERT_VALIDATION:
                    return INSERT_VALIDATION_EVENT_TYPE;
                case DB_INSERT:
                    return DB_INSERT_EVENT_TYPE;
                case UPDATE_VALIDATION:
                    return UPDATE_VALIDATION_EVENT_TYPE;
                case DB_UPDATE:
                    return UPDATED_EVENT_TYPE;
                case DELETE_VALIDATION:
                    return DELETING_EVENT_TYPE;
                case DB_DELETE:
                    return DB_DELETE_EVENT_TYPE;
            }
        }
        return CUSTOMER_MODEL_EVENT_TYPE;
    }

    private CustomerEvent(CustomerEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    private CustomerEvent(CustomerEvent copyFrom, EventTarget target, DbOperationType operation) {
        super(copyFrom, target, Objects.requireNonNull(toEventType(operation)), operation);
    }

    public CustomerEvent(CustomerModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CustomerEvent(CustomerModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public CustomerEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CustomerEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public FxRecordModel.ModelFactory<CustomerDAO, CustomerModel, ? extends DbOperationEvent<CustomerModel, CustomerDAO>> getModelFactory() {
        return CustomerModel.FACTORY;
    }

    @Override
    public synchronized CustomerEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CustomerEvent> getEventType() {
        return (EventType<CustomerEvent>) super.getEventType();
    }

    @Override
    public CustomerEvent toDbOperationType(DbOperationType operation) {
        return new CustomerEvent(this, getTarget(), operation);
    }

    public AddressEvent createAddressEvent(DbOperationType operation) {
        CustomerModel model = getModel();
        if (null != model) {
            AddressItem<? extends IAddressDAO> cm = model.getAddress();
            if (cm instanceof AddressModel) {
                return new AddressEvent((AddressModel) cm, getSource(), getTarget(), operation);
            }
        }
        IAddressDAO dao = getDataAccessObject().getAddress();
        if (dao instanceof AddressDAO) {
            return new AddressEvent(getSource(), getTarget(), (AddressDAO) dao, operation);
        }
        return null;
    }

}
