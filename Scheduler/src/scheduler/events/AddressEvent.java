package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.ICityDAO;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link AddressModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressEvent extends DbOperationEvent<AddressModel, AddressDAO> {

    private static final long serialVersionUID = 8261622802802373344L;
    public static final String ADDRESS_MODEL_EVENT_NAME = "SCHEDULER_ADDRESS_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_ADDRESS_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_ADDRESS_DELETE_REQUEST";
    public static final String INSERT_VALIDATION_EVENT_NAME = "SCHEDULER_ADDRESS_INSERT_VALIDATION";
    public static final String DB_INSERT_EVENT_NAME = "SCHEDULER_ADDRESS_DB_INSERT";
    public static final String UPDATE_VALIDATION_EVENT_NAME = "SCHEDULER_ADDRESS_UPDATE_VALIDATION";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_ADDRESS_DB_UPDATE";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_ADDRESS_DELETE_VALIDATION";
    public static final String DB_DELETE_EVENT_NAME = "SCHEDULER_ADDRESS_DB_DELETE";

    /**
     * Base {@link EventType} for all {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> ADDRESS_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, ADDRESS_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERT_VALIDATION} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> INSERT_VALIDATION_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, INSERT_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_INSERT} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> DB_INSERT_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DB_INSERT_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATE_VALIDATION} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> UPDATE_VALIDATION_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, UPDATE_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_UPDATE} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> UPDATED_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_VALIDATION} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> DELETING_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_DELETE} {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> DB_DELETE_EVENT_TYPE = new EventType<>(ADDRESS_MODEL_EVENT_TYPE, DB_DELETE_EVENT_NAME);

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
    public static EventType<AddressEvent> toEventType(DbOperationType operation) {
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
        return ADDRESS_MODEL_EVENT_TYPE;
    }

    private AddressEvent(AddressEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    private AddressEvent(AddressEvent copyFrom, EventTarget target, DbOperationType operation) {
        super(copyFrom, target, Objects.requireNonNull(toEventType(operation)), operation);
    }

    public AddressEvent(AddressModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AddressEvent(AddressModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public AddressEvent(Object source, EventTarget target, AddressDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AddressEvent(Object source, EventTarget target, AddressDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public FxRecordModel.ModelFactory<AddressDAO, AddressModel, ? extends DbOperationEvent<AddressModel, AddressDAO>> getModelFactory() {
        return AddressModel.FACTORY;
    }

    @Override
    public synchronized AddressEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AddressEvent> getEventType() {
        return (EventType<AddressEvent>) super.getEventType();
    }

    @Override
    public AddressEvent toDbOperationType(DbOperationType operation) {
        return new AddressEvent(this, getTarget(), operation);
    }

    public CityEvent createCityEvent(DbOperationType operation) {
        AddressModel model = getModel();
        if (null != model) {
            CityItem<? extends ICityDAO> cm = model.getCity();
            if (cm instanceof CityModel) {
                return new CityEvent((CityModel) cm, getSource(), getTarget(), operation);
            }
        }
        ICityDAO dao = getDataAccessObject().getCity();
        if (dao instanceof CityDAO) {
            return new CityEvent(getSource(), getTarget(), (CityDAO) dao, operation);
        }
        return null;
    }

}
