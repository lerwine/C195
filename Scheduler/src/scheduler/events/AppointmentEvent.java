package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserItem;
import scheduler.model.ui.UserModel;

/**
 * Event that is fired when a {@link AppointmentModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentEvent extends DbOperationEvent<AppointmentModel, AppointmentDAO> {

    private static final long serialVersionUID = -1145658585716643269L;

    public static final String APPOINTMENT_MODEL_EVENT_NAME = "SCHEDULER_APPOINTMENT_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_APPOINTMENT_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETE_REQUEST";
    public static final String INSERT_VALIDATION_EVENT_NAME = "SCHEDULER_APPOINTMENT_INSERT_VALIDATION";
    public static final String DB_INSERT_EVENT_NAME = "SCHEDULER_APPOINTMENT_DB_INSERT";
    public static final String UPDATE_VALIDATION_EVENT_NAME = "SCHEDULER_APPOINTMENT_UPDATE_VALIDATION";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_APPOINTMENT_DB_UPDATE";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_APPOINTMENT_DELETE_VALIDATION";
    public static final String DB_DELETE_EVENT_NAME = "SCHEDULER_APPOINTMENT_DB_DELETE";

    /**
     * Base {@link EventType} for all {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> APPOINTMENT_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, APPOINTMENT_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERT_VALIDATION} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> INSERT_VALIDATION_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, INSERT_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_INSERT} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> DB_INSERT_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, DB_INSERT_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATE_VALIDATION} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> UPDATE_VALIDATION_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, UPDATE_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_UPDATE} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> UPDATED_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_VALIDATION} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> DELETING_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_DELETE} {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> DB_DELETE_EVENT_TYPE = new EventType<>(APPOINTMENT_MODEL_EVENT_TYPE, DB_DELETE_EVENT_NAME);

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
    public static EventType<AppointmentEvent> toEventType(DbOperationType operation) {
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
        return APPOINTMENT_MODEL_EVENT_TYPE;
    }

    private AppointmentEvent(AppointmentEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    private AppointmentEvent(AppointmentEvent copyFrom, EventTarget target, DbOperationType operation) {
        super(copyFrom, target, Objects.requireNonNull(toEventType(operation)), operation);
    }

    public AppointmentEvent(AppointmentModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AppointmentEvent(AppointmentModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public AppointmentEvent(Object source, EventTarget target, AppointmentDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public AppointmentEvent(Object source, EventTarget target, AppointmentDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public FxRecordModel.ModelFactory<AppointmentDAO, AppointmentModel, ? extends DbOperationEvent<AppointmentModel, AppointmentDAO>> getModelFactory() {
        return AppointmentModel.FACTORY;
    }

    @Override
    public synchronized AppointmentEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AppointmentEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AppointmentEvent> getEventType() {
        return (EventType<AppointmentEvent>) super.getEventType();
    }

    @Override
    public AppointmentEvent toDbOperationType(DbOperationType operation) {
        return new AppointmentEvent(this, getTarget(), operation);
    }

    public CustomerEvent createCustomerEvent(DbOperationType operation) {
        AppointmentModel model = getModel();
        if (null != model) {
            CustomerItem<? extends ICustomerDAO> cm = model.getCustomer();
            if (cm instanceof CustomerModel) {
                return new CustomerEvent((CustomerModel) cm, getSource(), getTarget(), operation);
            }
        }
        ICustomerDAO dao = getDataAccessObject().getCustomer();
        if (dao instanceof CustomerDAO) {
            return new CustomerEvent(getSource(), getTarget(), (CustomerDAO) dao, operation);
        }
        return null;
    }

    public UserEvent createUserEvent(DbOperationType operation) {
        AppointmentModel model = getModel();
        if (null != model) {
            UserItem<? extends IUserDAO> cm = model.getUser();
            if (cm instanceof UserModel) {
                return new UserEvent((UserModel) cm, getSource(), getTarget(), operation);
            }
        }
        IUserDAO dao = getDataAccessObject().getUser();
        if (dao instanceof UserDAO) {
            return new UserEvent(getSource(), getTarget(), (UserDAO) dao, operation);
        }
        return null;
    }

}
