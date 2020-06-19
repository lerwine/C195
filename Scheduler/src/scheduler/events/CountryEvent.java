package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link CountryModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryEvent extends DbOperationEvent<CountryModel, CountryDAO> {

    private static final long serialVersionUID = 6220482582846221386L;
    public static final String COUNTRY_MODEL_EVENT_NAME = "SCHEDULER_COUNTRY_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_COUNTRY_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_COUNTRY_DELETE_REQUEST";
    public static final String INSERT_VALIDATION_EVENT_NAME = "SCHEDULER_COUNTRY_INSERT_VALIDATION";
    public static final String DB_INSERT_EVENT_NAME = "SCHEDULER_COUNTRY_DB_INSERT";
    public static final String UPDATE_VALIDATION_EVENT_NAME = "SCHEDULER_COUNTRY_UPDATE_VALIDATION";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_COUNTRY_DB_UPDATE";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_COUNTRY_DELETE_VALIDATION";
    public static final String DB_DELETE_EVENT_NAME = "SCHEDULER_COUNTRY_DB_DELETE";

    /**
     * Base {@link EventType} for all {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> COUNTRY_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, COUNTRY_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERT_VALIDATION} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> INSERT_VALIDATION_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, INSERT_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_INSERT} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> DB_INSERT_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, DB_INSERT_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATE_VALIDATION} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> UPDATE_VALIDATION_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, UPDATE_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_UPDATE} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> UPDATED_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_VALIDATION} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> DELETING_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_DELETE} {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> DB_DELETE_EVENT_TYPE = new EventType<>(COUNTRY_MODEL_EVENT_TYPE, DB_DELETE_EVENT_NAME);

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
    public static EventType<CountryEvent> toEventType(DbOperationType operation) {
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
        return COUNTRY_MODEL_EVENT_TYPE;
    }

    private CountryEvent(CountryEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    private CountryEvent(CountryEvent copyFrom, EventTarget target, DbOperationType operation) {
        super(copyFrom, target, Objects.requireNonNull(toEventType(operation)), operation);
    }

    public CountryEvent(CountryModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CountryEvent(CountryModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public CountryEvent(Object source, EventTarget target, CountryDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CountryEvent(Object source, EventTarget target, CountryDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public FxRecordModel.ModelFactory<CountryDAO, CountryModel, ? extends DbOperationEvent<CountryModel, CountryDAO>> getModelFactory() {
        return CountryModel.FACTORY;
    }

    @Override
    public synchronized CountryEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CountryEvent> getEventType() {
        return (EventType<CountryEvent>) super.getEventType();
    }

    @Override
    public CountryEvent toDbOperationType(DbOperationType operation) {
        return new CountryEvent(this, getTarget(), operation);
    }

}
