package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;

/**
 * Represents a failed {@link AppointmentEvent}.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #BASE_EVENT_NAME "SCHEDULER_ADDRESS_FAILED_EVENT"} &lArr; {@link #CHANGE_EVENT_TYPE "SCHEDULER_ADDRESS_OP_EVENT"} &lArr;
 * {@link #ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr;
 * {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) SCHEDULER_ADDRESS_SAVE_FAILED</dt>
 * <dd>&rarr; {@link #SAVE_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_ADDRESS_INSERT_FAILED</dt>
 * <dd>&rarr; {@link #INSERT_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_ADDRESS_INSERT_FAULTED</dt>
 * <dd>&rarr; {@link #INSERT_FAULTED}</dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_INSERT_INVALID</dt>
 * <dd>&rarr; {@link #INSERT_INVALID}</dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_INSERT_CANCELED</dt>
 * <dd>&rarr; {@link #INSERT_CANCELED}</dd>
 * </dl></dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_UPDATE_FAILED</dt>
 * <dd>&rarr; {@link #UPDATE_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_ADDRESS_UPDATE_FAULTED</dt>
 * <dd>&rarr; {@link #UPDATE_FAULTED}</dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_UPDATE_INVALID</dt>
 * <dd>&rarr; {@link #UPDATE_INVALID}</dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_UPDATE_CANCELED</dt>
 * <dd>&rarr; {@link #UPDATE_CANCELED}</dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_DELETE_FAILED</dt>
 * <dd>&rarr; {@link #DELETE_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_ADDRESS_DELETE_FAULTED</dt>
 * <dd>&rarr; {@link #DELETE_FAULTED}</dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_DELETE_INVALID</dt>
 * <dd>&rarr; {@link #DELETE_INVALID}</dd>
 * <dt>(inherit) SCHEDULER_ADDRESS_DELETE_CANCELED</dt>
 * <dd>&rarr; {@link #DELETE_CANCELED}</dd>
 * </dl></dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressFailedEvent extends AddressEvent implements ModelFailedEvent<AddressDAO, AddressModel> {

    private static final long serialVersionUID = 3057696288962212229L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_ADDRESS_FAILED_EVENT";
    private static final String SAVE_FAILED_EVENT_NAME = "SCHEDULER_ADDRESS_SAVE_FAILED";
    private static final String INSERT_FAILED_EVENT_NAME = "SCHEDULER_ADDRESS_INSERT_FAILED";
    private static final String UPDATE_FAILED_EVENT_NAME = "SCHEDULER_ADDRESS_UPDATE_FAILED";
    private static final String DELETE_FAILED_EVENT_NAME = "SCHEDULER_ADDRESS_DELETE_FAILED";
    private static final String INSERT_FAULTED_EVENT_NAME = "SCHEDULER_ADDRESS_INSERT_FAULTED";
    private static final String UPDATE_FAULTED_EVENT_NAME = "SCHEDULER_ADDRESS_UPDATE_FAULTED";
    private static final String DELETE_FAULTED_EVENT_NAME = "SCHEDULER_ADDRESS_DELETE_FAULTED";
    private static final String INSERT_INVALID_EVENT_NAME = "SCHEDULER_ADDRESS_INSERT_INVALID";
    private static final String UPDATE_INVALID_EVENT_NAME = "SCHEDULER_ADDRESS_UPDATE_INVALID";
    private static final String DELETE_INVALID_EVENT_NAME = "SCHEDULER_ADDRESS_DELETE_INVALID";
    private static final String INSERT_CANCELED_EVENT_NAME = "SCHEDULER_ADDRESS_INSERT_CANCELED";
    private static final String UPDATE_CANCELED_EVENT_NAME = "SCHEDULER_ADDRESS_UPDATE_CANCELED";
    private static final String DELETE_CANCELED_EVENT_NAME = "SCHEDULER_ADDRESS_DELETE_CANCELED";

    /**
     * Base {@link EventType} for all {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> FAILED_EVENT_TYPE = new EventType<>(CHANGE_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * Base {@link EventType} for save {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, SAVE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for insert {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> INSERT_FAILED = new EventType<>(SAVE_FAILED, INSERT_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for update {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> UPDATE_FAILED = new EventType<>(SAVE_FAILED, UPDATE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for delete {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, DELETE_FAILED_EVENT_NAME);

    /**
     * {@link EventType} for insert fault {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> INSERT_FAULTED = new EventType<>(INSERT_FAILED, INSERT_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for update fault {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> UPDATE_FAULTED = new EventType<>(UPDATE_FAILED, UPDATE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for delete fault {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> DELETE_FAULTED = new EventType<>(DELETE_FAILED, DELETE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for insert invalid {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> INSERT_INVALID = new EventType<>(INSERT_FAILED, INSERT_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for update invalid {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> UPDATE_INVALID = new EventType<>(UPDATE_FAILED, UPDATE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for delete invalid {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> DELETE_INVALID = new EventType<>(DELETE_FAILED, DELETE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for insert canceled {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> INSERT_CANCELED = new EventType<>(INSERT_FAILED, INSERT_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for update canceled {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> UPDATE_CANCELED = new EventType<>(UPDATE_FAILED, UPDATE_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for delete canceled {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> DELETE_CANCELED = new EventType<>(DELETE_FAILED, DELETE_CANCELED_EVENT_NAME);

    @SuppressWarnings({"fallthrough", "incomplete-switch"})
    private static DbOperationType toDbOperationType(EventType<AddressFailedEvent> eventType, Throwable fault) {
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
            default:
                throw new IllegalArgumentException(String.format("%s is an unsupported event name", eventType.getName()));
        }
        throw new IllegalArgumentException();
    }

    private static String ensureMessage(String message, Throwable fault, EventType<AddressFailedEvent> eventType) {
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

    public static FailKind toFailKind(EventType<AddressFailedEvent> eventType) {
        switch (eventType.getName()) {
            case INSERT_CANCELED_EVENT_NAME:
            case UPDATE_CANCELED_EVENT_NAME:
            case DELETE_CANCELED_EVENT_NAME:
                return FailKind.CANCELED;
            case INSERT_INVALID_EVENT_NAME:
            case UPDATE_INVALID_EVENT_NAME:
            case DELETE_INVALID_EVENT_NAME:
                return FailKind.INVALID;
            default:
                return FailKind.INVALID;
        }
    }

    private String message;
    private Throwable fault;
    private CityFailedEvent cityEvent;
    private FailKind failKind;

    public AddressFailedEvent(AddressEvent event, String message, Throwable fault, Object source, EventType<AddressFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        cityEvent = (event instanceof AddressFailedEvent) ? ((AddressFailedEvent) event).cityEvent : null;
        failKind = toFailKind(eventType);
    }

    public AddressFailedEvent(AddressEvent event, Object source, String message, EventType<AddressFailedEvent> eventType, EventTarget target) {
        this(event, message, (Throwable) null, source, eventType, target);
    }

    public AddressFailedEvent(AddressEvent event, String message, Throwable fault, EventType<AddressFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        cityEvent = (event instanceof AddressFailedEvent) ? ((AddressFailedEvent) event).cityEvent : null;
        failKind = toFailKind(eventType);
    }

    public AddressFailedEvent(AddressEvent event, String message, EventType<AddressFailedEvent> eventType) {
        this(event, message, (Throwable) null, eventType);
    }

    public AddressFailedEvent(AddressModel target, String message, Throwable fault, Object source, EventType<AddressFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        cityEvent = null;
        failKind = toFailKind(eventType);
    }

    public AddressFailedEvent(AddressModel target, Object source, String message, EventType<AddressFailedEvent> eventType) {
        this(target, message, (Throwable) null, source, eventType);
    }

    AddressFailedEvent(AddressModel target, Object source, EventType<AddressFailedEvent> eventType, CityFailedEvent event) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = "Invalid city";
        fault = null;
        cityEvent = event;
        failKind = toFailKind(eventType);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getFault() {
        return fault;
    }

    public CityFailedEvent getCityEvent() {
        return cityEvent;
    }

    @Override
    public FailKind getFailKind() {
        return failKind;
    }

    @Override
    public AddressFailedEvent copyFor(Object newSource, EventTarget newTarget) {
        AddressFailedEvent event = (AddressFailedEvent) super.copyFor(newSource, newTarget);
        event.message = message;
        event.fault = fault;
        event.cityEvent = cityEvent;
        event.failKind = failKind;
        return event;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(message);
        hash = 79 * hash + Objects.hashCode(fault);
        hash = 79 * hash + Objects.hashCode(cityEvent);
        hash = 79 * hash + Objects.hashCode(failKind);
        hash = 79 * hash + Objects.hashCode(getOperation());
        hash = 79 * hash + Objects.hashCode(getDataAccessObject());
        hash = 79 * hash + Objects.hashCode(getEntityModel());
        hash = 79 * hash + Objects.hashCode(getEventType().getName());
        hash = 79 * hash + Objects.hashCode(getTarget());
        hash = 79 * hash + Objects.hashCode(getSource());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AddressFailedEvent other = (AddressFailedEvent) obj;
        return failKind == other.failKind && Objects.equals(message, other.message) && getEventType().getName().equals(other.getEventType().getName())
                && Objects.equals(fault, other.fault) && Objects.equals(cityEvent, other.cityEvent) && super.equals(obj);
    }

}
