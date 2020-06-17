package events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link CityModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityEvent extends DbOperationEvent<CityModel, CityDAO> {

    private static final long serialVersionUID = 2299267381558318300L;
    public static final String CITY_MODEL_EVENT_NAME = "SCHEDULER_CITY_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_CITY_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_CITY_DELETE_REQUEST";
    public static final String INSERTING_EVENT_NAME = "SCHEDULER_CITY_INSERTING";
    public static final String INSERTED_EVENT_NAME = "SCHEDULER_CITY_INSERTED";
    public static final String UPDATING_EVENT_NAME = "SCHEDULER_CITY_UPDATING";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_CITY_UPDATED";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_CITY_DELETING";
    public static final String DELETED_EVENT_NAME = "SCHEDULER_CITY_DELETED";

    /**
     * Base {@link EventType} for all {@code CityEvent}s.
     */
    public static final EventType<CityEvent> CITY_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, CITY_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTING} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> INSERTING_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, INSERTING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTED} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> INSERTED_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, INSERTED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATING} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> UPDATING_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, UPDATING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATED} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> UPDATED_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETING} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> DELETING_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETED} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> DELETED_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, DELETED_EVENT_NAME);

    public static DbOperationType toDbOperationType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return DbOperationType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return DbOperationType.DELETE_REQUEST;
                case INSERTING_EVENT_NAME:
                    return DbOperationType.INSERTING;
                case INSERTED_EVENT_NAME:
                    return DbOperationType.INSERTED;
                case UPDATING_EVENT_NAME:
                    return DbOperationType.UPDATING;
                case UPDATED_EVENT_NAME:
                    return DbOperationType.UPDATED;
                case DELETING_EVENT_NAME:
                    return DbOperationType.DELETING;
                case DELETED_EVENT_NAME:
                    return DbOperationType.DELETED;
            }
        }
        return DbOperationType.NONE;
    }

    @SuppressWarnings("incomplete-switch")
    public static EventType<CityEvent> toEventType(DbOperationType operation) {
        if (null != operation) {
            switch (operation) {
                case EDIT_REQUEST:
                    return EDIT_REQUEST_EVENT_TYPE;
                case DELETE_REQUEST:
                    return DELETE_REQUEST_EVENT_TYPE;
                case INSERTING:
                    return INSERTING_EVENT_TYPE;
                case INSERTED:
                    return INSERTED_EVENT_TYPE;
                case UPDATING:
                    return UPDATING_EVENT_TYPE;
                case UPDATED:
                    return UPDATED_EVENT_TYPE;
                case DELETING:
                    return DELETING_EVENT_TYPE;
                case DELETED:
                    return DELETED_EVENT_TYPE;
            }
        }
        return CITY_MODEL_EVENT_TYPE;
    }

    private CityEvent(CityEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CityEvent(CityModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CityEvent(CityModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public CityEvent(Object source, EventTarget target, CityDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CityEvent(Object source, EventTarget target, CityDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public FxRecordModel.ModelFactory<CityDAO, CityModel, ? extends DbOperationEvent<CityModel, CityDAO>> getModelFactory() {
        return CityModel.FACTORY;
    }

    @Override
    public synchronized CityEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CityEvent> getEventType() {
        return (EventType<CityEvent>) super.getEventType();
    }

}
