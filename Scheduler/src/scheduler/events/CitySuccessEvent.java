package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.fx.CityModel;

/**
 * Represents a successful {@link AppointmentEvent}.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #BASE_EVENT_NAME "SCHEDULER_CITY_SUCCESS_EVENT"} &lArr; {@link #CHANGE_EVENT_TYPE "SCHEDULER_CITY_OP_EVENT"} &lArr;
 * {@link #CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr;
 * {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) SCHEDULER_CITY_SAVE_SUCCESS</dt>
 * <dd>&rarr; {@link #SAVE_SUCCESS}
 * <dl>
 * <dt>(inherit) SCHEDULER_CITY_INSERT_SUCCESS</dt>
 * <dd>&rarr; {@link #INSERT_SUCCESS}</dd>
 * <dt>(inherit) SCHEDULER_CITY_UPDATE_SUCCESS</dt>
 * <dd>&rarr; {@link #UPDATE_SUCCESS}</dd>
 * </dl></dd>
 * <dt>(inherit) SCHEDULER_CITY_DELETE_SUCCESS</dt>
 * <dd>&rarr; {@link #DELETE_SUCCESS}</dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CitySuccessEvent extends CityEvent {

    private static final long serialVersionUID = -2487591420575876249L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_CITY_SUCCESS_EVENT";
    private static final String SAVE_SUCCESS_EVENT_NAME = "SCHEDULER_CITY_SAVE_SUCCESS";
    private static final String INSERT_EVENT_NAME = "SCHEDULER_CITY_INSERT_SUCCESS";
    private static final String UPDATE_EVENT_NAME = "SCHEDULER_CITY_UPDATE_SUCCESS";
    private static final String DELETE_EVENT_NAME = "SCHEDULER_CITY_DELETE_SUCCESS";

    /**
     * Base {@link EventType} for all {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(CHANGE_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, SAVE_SUCCESS_EVENT_NAME);

    /**
     * {@link EventType} for database insert {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> INSERT_SUCCESS = new EventType<>(SAVE_SUCCESS, INSERT_EVENT_NAME);

    /**
     * {@link EventType} for database update {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> UPDATE_SUCCESS = new EventType<>(SAVE_SUCCESS, UPDATE_EVENT_NAME);

    /**
     * {@link EventType} for delete {@code CitySuccessEvent}s.
     */
    public static final EventType<CitySuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, DELETE_EVENT_NAME);

    private static DbOperationType toDbOperationType(EventType<CitySuccessEvent> eventType) {
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

    public CitySuccessEvent(CityEvent event, Object source, EventTarget target, EventType<CitySuccessEvent> eventType) {
        super(event, source, target, eventType, toDbOperationType(eventType));
    }

    public CitySuccessEvent(CityEvent event, EventType<CitySuccessEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType));
    }

    public CitySuccessEvent(CityModel target, Object source, EventType<CitySuccessEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType));
    }

}
