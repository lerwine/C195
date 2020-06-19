package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.CountryModel;
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
    public static final String INSERT_VALIDATION_EVENT_NAME = "SCHEDULER_CITY_INSERT_VALIDATION";
    public static final String DB_INSERT_EVENT_NAME = "SCHEDULER_CITY_DB_INSERT";
    public static final String UPDATE_VALIDATION_EVENT_NAME = "SCHEDULER_CITY_UPDATE_VALIDATION";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_CITY_DB_UPDATE";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_CITY_DELETE_VALIDATION";
    public static final String DB_DELETE_EVENT_NAME = "SCHEDULER_CITY_DB_DELETE";

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
     * {@link EventType} for {@link DbOperationType#INSERT_VALIDATION} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> INSERT_VALIDATION_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, INSERT_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_INSERT} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> DB_INSERT_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, DB_INSERT_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATE_VALIDATION} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> UPDATE_VALIDATION_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, UPDATE_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_UPDATE} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> UPDATED_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_VALIDATION} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> DELETING_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_DELETE} {@code CityEvent}s.
     */
    public static final EventType<CityEvent> DB_DELETE_EVENT_TYPE = new EventType<>(CITY_MODEL_EVENT_TYPE, DB_DELETE_EVENT_NAME);

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
    public static EventType<CityEvent> toEventType(DbOperationType operation) {
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
        return CITY_MODEL_EVENT_TYPE;
    }

    private CityEvent(CityEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    private CityEvent(CityEvent copyFrom, EventTarget target, DbOperationType operation) {
        super(copyFrom, target, Objects.requireNonNull(toEventType(operation)), operation);
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

    @Override
    public CityEvent toDbOperationType(DbOperationType operation) {
        return new CityEvent(this, getTarget(), operation);
    }

    public CountryEvent createCountryEvent(DbOperationType operation) {
        CityModel model = getModel();
        if (null != model) {
            CountryItem<? extends ICountryDAO> cm = model.getCountry();
            if (cm instanceof CountryModel) {
                return new CountryEvent((CountryModel) cm, getSource(), getTarget(), operation);
            }
        }
        ICountryDAO dao = getDataAccessObject().getCountry();
        if (dao instanceof CountryDAO) {
            return new CountryEvent(getSource(), getTarget(), (CountryDAO) dao, operation);
        }
        return null;
    }

}
