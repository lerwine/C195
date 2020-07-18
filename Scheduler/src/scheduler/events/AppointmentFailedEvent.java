package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.fx.AppointmentModel;

/**
 * Represents a failed {@link AppointmentEvent}.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #BASE_EVENT_NAME "SCHEDULER_APPOINTMENT_FAILED_EVENT"} &lArr; {@link #CHANGE_EVENT_TYPE "SCHEDULER_APPOINTMENT_OP_EVENT"} &lArr;
 * {@link #APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr;
 * {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_SAVE_FAILED</dt>
 * <dd>&rarr; {@link #SAVE_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_INSERT_FAILED</dt>
 * <dd>&rarr; {@link #INSERT_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_INSERT_FAULTED</dt>
 * <dd>&rarr; {@link #INSERT_FAULTED}</dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_INSERT_INVALID</dt>
 * <dd>&rarr; {@link #INSERT_INVALID}</dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_INSERT_CANCELED</dt>
 * <dd>&rarr; {@link #INSERT_CANCELED}</dd>
 * </dl></dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_UPDATE_FAILED</dt>
 * <dd>&rarr; {@link #UPDATE_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_UPDATE_FAULTED</dt>
 * <dd>&rarr; {@link #UPDATE_FAULTED}</dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_UPDATE_INVALID</dt>
 * <dd>&rarr; {@link #UPDATE_INVALID}</dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_UPDATE_CANCELED</dt>
 * <dd>&rarr; {@link #UPDATE_CANCELED}</dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_DELETE_FAILED</dt>
 * <dd>&rarr; {@link #DELETE_FAILED}
 * <dl>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_DELETE_FAULTED</dt>
 * <dd>&rarr; {@link #DELETE_FAULTED}</dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_DELETE_INVALID</dt>
 * <dd>&rarr; {@link #DELETE_INVALID}</dd>
 * <dt>(inherit) SCHEDULER_APPOINTMENT_DELETE_CANCELED</dt>
 * <dd>&rarr; {@link #DELETE_CANCELED}</dd>
 * </dl></dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentFailedEvent extends AppointmentEvent implements ModelFailedEvent<AppointmentDAO, AppointmentModel> {

    private static final long serialVersionUID = 1875761190733893550L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_APPOINTMENT_FAILED_EVENT";
    private static final String SAVE_FAILED_EVENT_NAME = "SCHEDULER_APPOINTMENT_SAVE_FAILED";
    private static final String INSERT_FAILED_EVENT_NAME = "SCHEDULER_APPOINTMENT_INSERT_FAILED";
    private static final String UPDATE_FAILED_EVENT_NAME = "SCHEDULER_APPOINTMENT_UPDATE_FAILED";
    private static final String DELETE_FAILED_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETE_FAILED";
    private static final String INSERT_FAULTED_EVENT_NAME = "SCHEDULER_APPOINTMENT_INSERT_FAULTED";
    private static final String UPDATE_FAULTED_EVENT_NAME = "SCHEDULER_APPOINTMENT_UPDATE_FAULTED";
    private static final String DELETE_FAULTED_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETE_FAULTED";
    private static final String INSERT_INVALID_EVENT_NAME = "SCHEDULER_APPOINTMENT_INSERT_INVALID";
    private static final String UPDATE_INVALID_EVENT_NAME = "SCHEDULER_APPOINTMENT_UPDATE_INVALID";
    private static final String DELETE_INVALID_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETE_INVALID";
    private static final String INSERT_CANCELED_EVENT_NAME = "SCHEDULER_APPOINTMENT_INSERT_CANCELED";
    private static final String UPDATE_CANCELED_EVENT_NAME = "SCHEDULER_APPOINTMENT_UPDATE_CANCELED";
    private static final String DELETE_CANCELED_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETE_CANCELED";

    /**
     * Base {@link EventType} for all {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> FAILED_EVENT_TYPE = new EventType<>(CHANGE_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * Base {@link EventType} for save {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, SAVE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for insert {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> INSERT_FAILED = new EventType<>(SAVE_FAILED, INSERT_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for update {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> UPDATE_FAILED = new EventType<>(SAVE_FAILED, UPDATE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for delete {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, DELETE_FAILED_EVENT_NAME);

    /**
     * {@link EventType} for insert fault {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> INSERT_FAULTED = new EventType<>(INSERT_FAILED, INSERT_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for update fault {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> UPDATE_FAULTED = new EventType<>(UPDATE_FAILED, UPDATE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for delete fault {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> DELETE_FAULTED = new EventType<>(DELETE_FAILED, DELETE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for insert invalid {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> INSERT_INVALID = new EventType<>(INSERT_FAILED, INSERT_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for update invalid {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> UPDATE_INVALID = new EventType<>(UPDATE_FAILED, UPDATE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for delete invalid {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> DELETE_INVALID = new EventType<>(DELETE_FAILED, DELETE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for insert canceled {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> INSERT_CANCELED = new EventType<>(INSERT_FAILED, INSERT_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for update canceled {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> UPDATE_CANCELED = new EventType<>(UPDATE_FAILED, UPDATE_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for delete canceled {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> DELETE_CANCELED = new EventType<>(DELETE_FAILED, DELETE_CANCELED_EVENT_NAME);

    private static DbOperationType toDbOperationType(EventType<AppointmentFailedEvent> eventType, Throwable fault) {
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

    private static String ensureMessage(String message, Throwable fault, EventType<AppointmentFailedEvent> eventType) {
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

    public static FailKind toFailKind(EventType<AppointmentFailedEvent> eventType) {
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
    private CustomerFailedEvent customerEvent;
    private UserFailedEvent userEvent;
    private FailKind failKind;

    public AppointmentFailedEvent(AppointmentEvent event, String message, Throwable fault, Object source, EventType<AppointmentFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        if (event instanceof AppointmentFailedEvent) {
            AppointmentFailedEvent fe = (AppointmentFailedEvent) event;
            customerEvent = fe.customerEvent;
            userEvent = fe.userEvent;
        } else {
            customerEvent = null;
            userEvent = null;
        }
        failKind = toFailKind(eventType);
    }

    public AppointmentFailedEvent(AppointmentEvent event, Object source, String message, EventType<AppointmentFailedEvent> eventType, EventTarget target) {
        this(event, message, (Throwable) null, source, eventType, target);
    }

    public AppointmentFailedEvent(AppointmentEvent event, String message, Throwable fault, EventType<AppointmentFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        if (event instanceof AppointmentFailedEvent) {
            AppointmentFailedEvent fe = (AppointmentFailedEvent) event;
            customerEvent = fe.customerEvent;
            userEvent = fe.userEvent;
        } else {
            customerEvent = null;
            userEvent = null;
        }
        failKind = toFailKind(eventType);
    }

    public AppointmentFailedEvent(AppointmentEvent event, String message, EventType<AppointmentFailedEvent> eventType) {
        this(event, message, (Throwable) null, eventType);
    }

    public AppointmentFailedEvent(AppointmentModel target, String message, Throwable fault, Object source, EventType<AppointmentFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
        customerEvent = null;
        userEvent = null;
        failKind = toFailKind(eventType);
    }

    public AppointmentFailedEvent(AppointmentModel target, Object source, String message, EventType<AppointmentFailedEvent> eventType) {
        this(target, message, (Throwable) null, source, eventType);
    }

    AppointmentFailedEvent(AppointmentModel target, Object source, EventType<AppointmentFailedEvent> eventType, CustomerFailedEvent event) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = "Invalid address";
        fault = null;
        customerEvent = event;
        userEvent = null;
        failKind = toFailKind(eventType);
    }

    AppointmentFailedEvent(AppointmentModel target, Object source, EventType<AppointmentFailedEvent> eventType, UserFailedEvent event) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = "Invalid address";
        fault = null;
        customerEvent = null;
        userEvent = event;
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

    public CustomerFailedEvent getCustomerEvent() {
        return customerEvent;
    }

    public UserFailedEvent getUserEvent() {
        return userEvent;
    }

    @Override
    public FailKind getFailKind() {
        return failKind;
    }

    @Override
    public AppointmentFailedEvent copyFor(Object newSource, EventTarget newTarget) {
        AppointmentFailedEvent event = (AppointmentFailedEvent) super.copyFor(newSource, newTarget);
        event.message = message;
        event.fault = fault;
        event.customerEvent = customerEvent;
        event.userEvent = userEvent;
        event.failKind = failKind;
        return event;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(message);
        hash = 79 * hash + Objects.hashCode(fault);
        hash = 79 * hash + Objects.hashCode(customerEvent);
        hash = 79 * hash + Objects.hashCode(userEvent);
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
        final AppointmentFailedEvent other = (AppointmentFailedEvent) obj;
        return failKind == other.failKind && Objects.equals(message, other.message) && getEventType().getName().equals(other.getEventType().getName())
                && Objects.equals(fault, other.fault) && Objects.equals(customerEvent, other.customerEvent) && Objects.equals(userEvent, other.userEvent) && super.equals(obj);
    }

}
