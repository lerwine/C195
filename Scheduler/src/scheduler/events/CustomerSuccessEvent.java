package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;

public final class CustomerSuccessEvent extends CustomerEvent {

    private static final long serialVersionUID = -2592604437430298083L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_CUSTOMER_SUCCESS_EVENT";
    private static final String SAVE_SUCCESS_EVENT_NAME = "SCHEDULER_CUSTOMER_SAVE_SUCCESS";
    private static final String INSERT_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERT_SUCCESS";
    private static final String UPDATE_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATE_SUCCESS";
    private static final String DELETE_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_SUCCESS";

    /**
     * Base {@link EventType} for all {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, SAVE_SUCCESS_EVENT_NAME);

    /**
     * {@link EventType} for database insert {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> INSERT_SUCCESS = new EventType<>(SAVE_SUCCESS, INSERT_EVENT_NAME);

    /**
     * {@link EventType} for database update {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> UPDATE_SUCCESS = new EventType<>(SAVE_SUCCESS, UPDATE_EVENT_NAME);

    /**
     * {@link EventType} for delete {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, DELETE_EVENT_NAME);

    private static DbOperationType toDbOperationType(EventType<CustomerSuccessEvent> eventType) {
        switch (eventType.getName()) {
            case INSERT_EVENT_NAME:
                return DbOperationType.DB_INSERT;
            case UPDATE_EVENT_NAME:
                return DbOperationType.DB_UPDATE;
            case DELETE_EVENT_NAME:
                return DbOperationType.DB_DELETE;
            default:
                throw new IllegalArgumentException();
        }
    }

    public CustomerSuccessEvent(CustomerEvent event, Object source, EventTarget target, EventType<CustomerSuccessEvent> eventType) {
        super(event, source, target, eventType, toDbOperationType(eventType));
    }

    public CustomerSuccessEvent(CustomerEvent event, EventType<CustomerSuccessEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType));
    }

    public CustomerSuccessEvent(CustomerModel target, Object source, EventType<CustomerSuccessEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType));
    }

    public CustomerSuccessEvent(CustomerDAO target, Object source, EventType<CustomerSuccessEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType));
    }

}
