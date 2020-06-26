package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.CustomerModel;

public final class CustomerFailedEvent extends CustomerEvent implements ModelFailedEvent<CustomerDAO, CustomerModel> {

    private static final long serialVersionUID = -7166159668722232485L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_CUSTOMER_FAILED_EVENT";
    private static final String SAVE_FAILED_EVENT_NAME = "SCHEDULER_CUSTOMER_SAVE_FAILED";
    private static final String INSERT_FAILED_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERT_FAILED";
    private static final String UPDATE_FAILED_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATE_FAILED";
    private static final String DELETE_FAILED_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_FAILED";
    private static final String INSERT_FAULTED_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERT_FAULTED";
    private static final String UPDATE_FAULTED_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATE_FAULTED";
    private static final String DELETE_FAULTED_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_FAULTED";
    private static final String INSERT_INVALID_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERT_INVALID";
    private static final String UPDATE_INVALID_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATE_INVALID";
    private static final String DELETE_INVALID_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_INVALID";
    private static final String INSERT_CANCELED_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERT_CANCELED";
    private static final String UPDATE_CANCELED_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATE_CANCELED";
    private static final String DELETE_CANCELED_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_CANCELED";

    /**
     * Base {@link EventType} for all {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * Base {@link EventType} for save {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, SAVE_FAILED_EVENT_NAME);

    /**
     * {Base @link EventType} for insert {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> INSERT_FAILED = new EventType<>(SAVE_FAILED, INSERT_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for update {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> UPDATE_FAILED = new EventType<>(SAVE_FAILED, UPDATE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for delete {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, DELETE_FAILED_EVENT_NAME);

    /**
     * {@link EventType} for insert fault {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> INSERT_FAULTED = new EventType<>(INSERT_FAILED, INSERT_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for update fault {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> UPDATE_FAULTED = new EventType<>(UPDATE_FAILED, UPDATE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for delete fault {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> DELETE_FAULTED = new EventType<>(DELETE_FAILED, DELETE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for insert invalid {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> INSERT_INVALID = new EventType<>(INSERT_FAILED, INSERT_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for update invalid {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> UPDATE_INVALID = new EventType<>(UPDATE_FAILED, UPDATE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for delete invalid {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> DELETE_INVALID = new EventType<>(DELETE_FAILED, DELETE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for insert canceled {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> INSERT_CANCELED = new EventType<>(INSERT_FAILED, INSERT_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for update canceled {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> UPDATE_CANCELED = new EventType<>(UPDATE_FAILED, UPDATE_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for delete canceled {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> DELETE_CANCELED = new EventType<>(DELETE_FAILED, DELETE_CANCELED_EVENT_NAME);

    public static boolean isFaultedEvent(CustomerFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_FAULTED_EVENT_NAME:
            case UPDATE_FAULTED_EVENT_NAME:
            case DELETE_FAULTED_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    public static boolean isInvalidEvent(CustomerFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_INVALID_EVENT_NAME:
            case UPDATE_INVALID_EVENT_NAME:
            case DELETE_INVALID_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    public static boolean isCanceledEvent(CustomerFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_CANCELED_EVENT_NAME:
            case UPDATE_CANCELED_EVENT_NAME:
            case DELETE_CANCELED_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    @SuppressWarnings("incomplete-switch")
    private static DbOperationType toDbOperationType(EventType<CustomerFailedEvent> eventType, Throwable fault) {
        switch (eventType.getName()) {
            case INSERT_INVALID_EVENT_NAME:
                if (null != fault) {
                    break;
                }
            case INSERT_FAULTED_EVENT_NAME:
                return DbOperationType.DB_INSERT;
            case INSERT_CANCELED_EVENT_NAME:
                if (null == fault || fault instanceof InterruptedException) {
                    return DbOperationType.DB_INSERT;
                }
                break;
            case UPDATE_INVALID_EVENT_NAME:
                if (null != fault) {
                    break;
                }
            case UPDATE_FAULTED_EVENT_NAME:
                return DbOperationType.DB_UPDATE;
            case UPDATE_CANCELED_EVENT_NAME:
                if (null == fault || fault instanceof InterruptedException) {
                    return DbOperationType.DB_UPDATE;
                }
                break;
            case DELETE_INVALID_EVENT_NAME:
                if (null != fault) {
                    break;
                }
            case DELETE_FAULTED_EVENT_NAME:
                return DbOperationType.DB_DELETE;
            case DELETE_CANCELED_EVENT_NAME:
                if (null == fault || fault instanceof InterruptedException) {
                    return DbOperationType.DB_DELETE;
                }
                break;
        }
        throw new IllegalArgumentException();
    }

    private static String ensureMessage(String message, Throwable fault, EventType<CustomerFailedEvent> eventType) {
        if (null == message || message.trim().isEmpty()) {
            switch (eventType.getName()) {
                case INSERT_INVALID_EVENT_NAME:
                case UPDATE_INVALID_EVENT_NAME:
                case DELETE_INVALID_EVENT_NAME:
                    return "Validation failed.";
                case INSERT_CANCELED_EVENT_NAME:
                case UPDATE_CANCELED_EVENT_NAME:
                case DELETE_CANCELED_EVENT_NAME:
                    if (null != fault && fault instanceof InterruptedException && null != (message = fault.getMessage()) && !message.isEmpty()) {
                        return message;
                    }
                    return "Operation canceled.";
                default:
                    return "An unexpected error has occured.";
            }
        }
        return message;
    }

    private final String message;
    private final Throwable fault;
    private final AddressFailedEvent addressEvent;

    public CustomerFailedEvent(CustomerEvent event, String message, Throwable fault, Object source, EventType<CustomerFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        addressEvent = (event instanceof CustomerFailedEvent) ? ((CustomerFailedEvent) event).addressEvent : null;
    }

    public CustomerFailedEvent(CustomerEvent event, Object source, String message, EventType<CustomerFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
        addressEvent = (event instanceof CustomerFailedEvent) ? ((CustomerFailedEvent) event).addressEvent : null;
    }

    public CustomerFailedEvent(CustomerEvent event, String message, Throwable fault, EventType<CustomerFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        addressEvent = (event instanceof CustomerFailedEvent) ? ((CustomerFailedEvent) event).addressEvent : null;
    }

    public CustomerFailedEvent(CustomerEvent event, String message, EventType<CustomerFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
        addressEvent = (event instanceof CustomerFailedEvent) ? ((CustomerFailedEvent) event).addressEvent : null;
    }

    public CustomerFailedEvent(RecordModelContext<CustomerDAO, CustomerModel> target, String message, Throwable fault, Object source, EventType<CustomerFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        addressEvent = null;
    }

    public CustomerFailedEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, String message, EventType<CustomerFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
        addressEvent = null;
    }

    CustomerFailedEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, EventType<CustomerFailedEvent> eventType, AddressFailedEvent event) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = "Invalid address";
        fault = null;
        addressEvent = event;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getFault() {
        return fault;
    }

    public AddressFailedEvent getAddressEvent() {
        return addressEvent;
    }

}
