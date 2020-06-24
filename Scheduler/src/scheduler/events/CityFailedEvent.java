package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.dao.ValidationFailureException;
import scheduler.model.ui.CityModel;

public final class CityFailedEvent extends CityEvent implements ModelFailedEvent<CityDAO, CityModel> {

    private static final long serialVersionUID = 4383081185660810957L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_CITY_FAILED_EVENT";
    private static final String SAVE_FAILED_EVENT_NAME = "SCHEDULER_CITY_SAVE_FAILED";
    private static final String INSERT_FAILED_EVENT_NAME = "SCHEDULER_CITY_INSERT_FAILED";
    private static final String UPDATE_FAILED_EVENT_NAME = "SCHEDULER_CITY_UPDATE_FAILED";
    private static final String DELETE_FAILED_EVENT_NAME = "SCHEDULER_CITY_DELETE_FAILED";
    private static final String INSERT_FAULTED_EVENT_NAME = "SCHEDULER_CITY_INSERT_FAULTED";
    private static final String UPDATE_FAULTED_EVENT_NAME = "SCHEDULER_CITY_UPDATE_FAULTED";
    private static final String DELETE_FAULTED_EVENT_NAME = "SCHEDULER_CITY_DELETE_FAULTED";
    private static final String INSERT_INVALID_EVENT_NAME = "SCHEDULER_CITY_INSERT_INVALID";
    private static final String UPDATE_INVALID_EVENT_NAME = "SCHEDULER_CITY_UPDATE_INVALID";
    private static final String DELETE_INVALID_EVENT_NAME = "SCHEDULER_CITY_DELETE_INVALID";
    private static final String INSERT_CANCELED_EVENT_NAME = "SCHEDULER_CITY_INSERT_CANCELED";
    private static final String UPDATE_CANCELED_EVENT_NAME = "SCHEDULER_CITY_UPDATE_CANCELED";
    private static final String DELETE_CANCELED_EVENT_NAME = "SCHEDULER_CITY_DELETE_CANCELED";

    /**
     * Base {@link EventType} for all {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * Base {@link EventType} for save {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, SAVE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for insert {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> INSERT_FAILED = new EventType<>(SAVE_FAILED, INSERT_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for update {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> UPDATE_FAILED = new EventType<>(SAVE_FAILED, UPDATE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for delete {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, DELETE_FAILED_EVENT_NAME);

    /**
     * {@link EventType} for insert fault {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> INSERT_FAULTED = new EventType<>(INSERT_FAILED, INSERT_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for update fault {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> UPDATE_FAULTED = new EventType<>(UPDATE_FAILED, UPDATE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for delete fault {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> DELETE_FAULTED = new EventType<>(DELETE_FAILED, DELETE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for insert invalid {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> INSERT_INVALID = new EventType<>(INSERT_FAILED, INSERT_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for update invalid {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> UPDATE_INVALID = new EventType<>(UPDATE_FAILED, UPDATE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for delete invalid {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> DELETE_INVALID = new EventType<>(DELETE_FAILED, DELETE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for insert canceled {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> INSERT_CANCELED = new EventType<>(INSERT_FAILED, INSERT_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for update canceled {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> UPDATE_CANCELED = new EventType<>(UPDATE_FAILED, UPDATE_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for delete canceled {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> DELETE_CANCELED = new EventType<>(DELETE_FAILED, DELETE_CANCELED_EVENT_NAME);

    public static boolean isFaultedEvent(CityFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_FAULTED_EVENT_NAME:
            case UPDATE_FAULTED_EVENT_NAME:
            case DELETE_FAULTED_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    public static boolean isInvalidEvent(CityFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_INVALID_EVENT_NAME:
            case UPDATE_INVALID_EVENT_NAME:
            case DELETE_INVALID_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    public static boolean isCanceledEvent(CityFailedEvent event) {
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
    private static DbOperationType toDbOperationType(EventType<CityFailedEvent> eventType, Throwable fault) {
        switch (eventType.getName()) {
            case INSERT_INVALID_EVENT_NAME:
                if (!(null == fault || fault instanceof ValidationFailureException)) {
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
                if (!(null == fault || fault instanceof ValidationFailureException)) {
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
                if (!(null == fault || fault instanceof ValidationFailureException)) {
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

    private static String ensureMessage(String message, Throwable fault, EventType<CityFailedEvent> eventType) {
        if (null == message || message.trim().isEmpty()) {
            switch (eventType.getName()) {
                case INSERT_INVALID_EVENT_NAME:
                case UPDATE_INVALID_EVENT_NAME:
                case DELETE_INVALID_EVENT_NAME:
                    if (null != fault && fault instanceof ValidationFailureException && null != (message = fault.getMessage()) && !message.isEmpty()) {
                        return message;
                    }
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

    public CityFailedEvent(CityEvent event, String message, Throwable fault, Object source, EventType<CityFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
    }

    public CityFailedEvent(CityEvent event, Object source, String message, EventType<CityFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
    }

    public CityFailedEvent(CityEvent event, String message, Throwable fault, EventType<CityFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
    }

    public CityFailedEvent(CityEvent event, String message, EventType<CityFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
    }

    public CityFailedEvent(CityModel target, String message, Throwable fault, Object source, EventType<CityFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
    }

    public CityFailedEvent(CityModel target, Object source, String message, EventType<CityFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
    }

    public CityFailedEvent(CityDAO target, String message, Throwable fault, Object source, EventType<CityFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
    }

    public CityFailedEvent(CityDAO target, Object source, String message, EventType<CityFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getFault() {
        return fault;
    }

}
