package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.ui.CountryModel;

/**
 * Represents a successful {@link AppointmentEvent}.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #BASE_EVENT_NAME "SCHEDULER_COUNTRY_SUCCESS_EVENT"} &lArr; {@link #CHANGE_EVENT_TYPE "SCHEDULER_COUNTRY_OP_EVENT"} &lArr;
 * {@link #COUNTRY_EVENT_TYPE "SCHEDULER_COUNTRY_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr;
 * {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) SCHEDULER_COUNTRY_SAVE_SUCCESS</dt>
 * <dd>&rarr; {@link #SAVE_SUCCESS}
 * <dl>
 * <dt>(inherit) SCHEDULER_COUNTRY_INSERT_SUCCESS</dt>
 * <dd>&rarr; {@link #INSERT_SUCCESS}</dd>
 * <dt>(inherit) SCHEDULER_COUNTRY_UPDATE_SUCCESS</dt>
 * <dd>&rarr; {@link #UPDATE_SUCCESS}</dd>
 * </dl></dd>
 * <dt>(inherit) SCHEDULER_COUNTRY_DELETE_SUCCESS</dt>
 * <dd>&rarr; {@link #DELETE_SUCCESS}</dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountrySuccessEvent extends CountryEvent {

    private static final long serialVersionUID = -2962871552359511026L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_COUNTRY_SUCCESS_EVENT";
    private static final String SAVE_SUCCESS_EVENT_NAME = "SCHEDULER_COUNTRY_SAVE_SUCCESS";
    private static final String INSERT_EVENT_NAME = "SCHEDULER_COUNTRY_INSERT_SUCCESS";
    private static final String UPDATE_EVENT_NAME = "SCHEDULER_COUNTRY_UPDATE_SUCCESS";
    private static final String DELETE_EVENT_NAME = "SCHEDULER_COUNTRY_DELETE_SUCCESS";

    /**
     * Base {@link EventType} for all {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(CHANGE_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, SAVE_SUCCESS_EVENT_NAME);

    /**
     * {@link EventType} for database insert {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> INSERT_SUCCESS = new EventType<>(SAVE_SUCCESS, INSERT_EVENT_NAME);

    /**
     * {@link EventType} for database update {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> UPDATE_SUCCESS = new EventType<>(SAVE_SUCCESS, UPDATE_EVENT_NAME);

    /**
     * {@link EventType} for delete {@code CountrySuccessEvent}s.
     */
    public static final EventType<CountrySuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, DELETE_EVENT_NAME);

    private static DbOperationType toDbOperationType(EventType<CountrySuccessEvent> eventType) {
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

    public CountrySuccessEvent(CountryEvent event, Object source, EventTarget target, EventType<CountrySuccessEvent> eventType) {
        super(event, source, target, eventType, toDbOperationType(eventType));
    }

    public CountrySuccessEvent(CountryEvent event, EventType<CountrySuccessEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType));
    }

    public CountrySuccessEvent(CountryModel target, Object source, EventType<CountrySuccessEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType));
    }

}
