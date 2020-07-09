package scheduler.events;

import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.EntityModelImpl;
import scheduler.util.LogHelper;

/**
 * Base class for {@code Event}s that affect {@link DataAccessObject}s and can also include an associated {@link EntityModelImpl}.
 * <h3>Event Registration</h3>
 * <ul class="list-style:none">
 * <li>{@link javafx.event.Event} &rArr; {@link javafx.event.Event#ANY "EVENT"}
 * <ul class="list-style:none">
 * <li><strong>{@code ModelEvent}</strong> &rArr; {@link #MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link OperationRequestEvent} &rArr; {@link OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link AppointmentOpRequestEvent} &rArr; {@link AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentOpRequestEvent#EDIT_REQUEST "SCHEDULER_APPOINTMENT_EDIT_REQUEST"}</li>
 * <li>&rArr; {@link AppointmentOpRequestEvent#DELETE_REQUEST "SCHEDULER_APPOINTMENT_DELETE_REQUEST"}</li>
 * </ul></li>
 * <li>{@link CustomerOpRequestEvent} &rArr; {@link CustomerOpRequestEvent#CUSTOMER_OP_REQUEST "SCHEDULER_CUSTOMER_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerOpRequestEvent#EDIT_REQUEST "SCHEDULER_CUSTOMER_EDIT_REQUEST"}</li>
 * <li>&rArr; {@link CustomerOpRequestEvent#DELETE_REQUEST "SCHEDULER_CUSTOMER_DELETE_REQUEST"}</li>
 * </ul></li>
 * <li>{@link AddressOpRequestEvent} &rArr; {@link AddressOpRequestEvent#ADDRESS_OP_REQUEST "SCHEDULER_ADDRESS_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressOpRequestEvent#EDIT_REQUEST "SCHEDULER_ADDRESS_EDIT_REQUEST"}</li>
 * <li>&rArr; {@link AddressOpRequestEvent#DELETE_REQUEST "SCHEDULER_ADDRESS_DELETE_REQUEST"}</li>
 * </ul></li>
 * <li>{@link CityOpRequestEvent} &rArr; {@link CityOpRequestEvent#CITY_OP_REQUEST "SCHEDULER_CITY_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityOpRequestEvent#EDIT_REQUEST "SCHEDULER_CITY_EDIT_REQUEST"}</li>
 * <li>&rArr; {@link CityOpRequestEvent#DELETE_REQUEST "SCHEDULER_CITY_DELETE_REQUEST"}</li>
 * </ul></li>
 * <li>{@link CountryOpRequestEvent} &rArr; {@link CountryOpRequestEvent#COUNTRY_OP_REQUEST "SCHEDULER_COUNTRY_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryOpRequestEvent#EDIT_REQUEST "SCHEDULER_COUNTRY_EDIT_REQUEST"}</li>
 * <li>&rArr; {@link CountryOpRequestEvent#DELETE_REQUEST "SCHEDULER_COUNTRY_DELETE_REQUEST"}</li>
 * </ul></li>
 * <li>{@link UserOpRequestEvent} &rArr; {@link UserOpRequestEvent#USER_OP_REQUEST "SCHEDULER_USER_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserOpRequestEvent#EDIT_REQUEST "SCHEDULER_USER_EDIT_REQUEST"}</li>
 * <li>&rArr; {@link UserOpRequestEvent#DELETE_REQUEST "SCHEDULER_USER_DELETE_REQUEST"}</li>
 * </ul></li>
 * </ul></li>
 * <li>{@link AppointmentEvent} &rArr; {@link AppointmentEvent#APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentEvent#CHANGE_EVENT_TYPE "SCHEDULER_APPOINTMENT_CHANGE_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link AppointmentSuccessEvent} &rArr; {@link AppointmentSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_APPOINTMENT_SUCCESS_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentSuccessEvent#SAVE_SUCCESS "SCHEDULER_APPOINTMENT_SAVE_SUCCESS"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentSuccessEvent#INSERT_SUCCESS "SCHEDULER_APPOINTMENT_INSERT_SUCCESS"}</li>
 * <li>&rArr; {@link AppointmentSuccessEvent#UPDATE_SUCCESS "SCHEDULER_APPOINTMENT_UPDATE_SUCCESS"}</li>
 * </ul></li>
 * <li>&rArr; {@link AppointmentSuccessEvent#DELETE_SUCCESS "SCHEDULER_APPOINTMENT_DELETE_SUCCESS"}</li>
 * </ul></li>
 * <li>{@link AppointmentFailedEvent} &rArr; {@link AppointmentFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_APPOINTMENT_FAILED_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentFailedEvent#SAVE_FAILED "SCHEDULER_APPOINTMENT_SAVE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentFailedEvent#INSERT_FAILED "SCHEDULER_APPOINTMENT_INSERT_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentFailedEvent#INSERT_FAULTED "SCHEDULER_APPOINTMENT_INSERT_FAULTED"}</li>
 * <li>&rArr; {@link AppointmentFailedEvent#INSERT_INVALID "SCHEDULER_APPOINTMENT_INSERT_INVALID"}</li>
 * <li>&rArr; {@link AppointmentFailedEvent#INSERT_CANCELED "SCHEDULER_APPOINTMENT_INSERT_CANCELED"}</li>
 * </ul></li>
 * <li>&rArr; {@link AppointmentFailedEvent#UPDATE_FAILED "SCHEDULER_APPOINTMENT_UPDATE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentFailedEvent#UPDATE_FAULTED "SCHEDULER_APPOINTMENT_UPDATE_FAULTED"}</li>
 * <li>&rArr; {@link AppointmentFailedEvent#UPDATE_INVALID "SCHEDULER_APPOINTMENT_UPDATE_INVALID"}</li>
 * <li>&rArr; {@link AppointmentFailedEvent#UPDATE_CANCELED "SCHEDULER_APPOINTMENT_UPDATE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * <li>&rArr; {@link AppointmentFailedEvent#DELETE_FAILED "SCHEDULER_APPOINTMENT_DELETE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentFailedEvent#DELETE_FAULTED "SCHEDULER_APPOINTMENT_DELETE_FAULTED"}</li>
 * <li>&rArr; {@link AppointmentFailedEvent#DELETE_INVALID "SCHEDULER_APPOINTMENT_DELETE_INVALID"}</li>
 * <li>&rArr; {@link AppointmentFailedEvent#DELETE_CANCELED "SCHEDULER_APPOINTMENT_DELETE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * <li>{@link CustomerEvent} &rArr; {@link CustomerEvent#CUSTOMER_EVENT_TYPE "SCHEDULER_CUSTOMER_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerEvent#CHANGE_EVENT_TYPE "SCHEDULER_CUSTOMER_CHANGE_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link CustomerSuccessEvent} &rArr; {@link CustomerSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_CUSTOMER_SUCCESS_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerSuccessEvent#SAVE_SUCCESS "SCHEDULER_CUSTOMER_SAVE_SUCCESS"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerSuccessEvent#INSERT_SUCCESS "SCHEDULER_CUSTOMER_INSERT_SUCCESS"}</li>
 * <li>&rArr; {@link CustomerSuccessEvent#UPDATE_SUCCESS "SCHEDULER_CUSTOMER_UPDATE_SUCCESS"}</li>
 * </ul></li>
 * <li>&rArr; {@link CustomerSuccessEvent#DELETE_SUCCESS "SCHEDULER_CUSTOMER_DELETE_SUCCESS"}</li>
 * </ul></li>
 * <li>{@link CustomerFailedEvent} &rArr; {@link CustomerFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_CUSTOMER_FAILED_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerFailedEvent#SAVE_FAILED "SCHEDULER_CUSTOMER_SAVE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerFailedEvent#INSERT_FAILED "SCHEDULER_CUSTOMER_INSERT_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerFailedEvent#INSERT_FAULTED "SCHEDULER_CUSTOMER_INSERT_FAULTED"}</li>
 * <li>&rArr; {@link CustomerFailedEvent#INSERT_INVALID "SCHEDULER_CUSTOMER_INSERT_INVALID"}</li>
 * <li>&rArr; {@link CustomerFailedEvent#INSERT_CANCELED "SCHEDULER_CUSTOMER_INSERT_CANCELED"}</li>
 * </ul></li>
 * <li>&rArr; {@link CustomerFailedEvent#UPDATE_FAILED "SCHEDULER_CUSTOMER_UPDATE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerFailedEvent#UPDATE_FAULTED "SCHEDULER_CUSTOMER_UPDATE_FAULTED"}</li>
 * <li>&rArr; {@link CustomerFailedEvent#UPDATE_INVALID "SCHEDULER_CUSTOMER_UPDATE_INVALID"}</li>
 * <li>&rArr; {@link CustomerFailedEvent#UPDATE_CANCELED "SCHEDULER_CUSTOMER_UPDATE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * <li>&rArr; {@link CustomerFailedEvent#DELETE_FAILED "SCHEDULER_CUSTOMER_DELETE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerFailedEvent#DELETE_FAULTED "SCHEDULER_CUSTOMER_DELETE_FAULTED"}</li>
 * <li>&rArr; {@link CustomerFailedEvent#DELETE_INVALID "SCHEDULER_CUSTOMER_DELETE_INVALID"}</li>
 * <li>&rArr; {@link CustomerFailedEvent#DELETE_CANCELED "SCHEDULER_CUSTOMER_DELETE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * <li>{@link AddressEvent} &rArr; {@link AddressEvent#ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressEvent#CHANGE_EVENT_TYPE "SCHEDULER_ADDRESS_CHANGE_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link AddressSuccessEvent} &rArr; {@link AddressSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_ADDRESS_SUCCESS_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressSuccessEvent#SAVE_SUCCESS "SCHEDULER_ADDRESS_SAVE_SUCCESS"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressSuccessEvent#INSERT_SUCCESS "SCHEDULER_ADDRESS_INSERT_SUCCESS"}</li>
 * <li>&rArr; {@link AddressSuccessEvent#UPDATE_SUCCESS "SCHEDULER_ADDRESS_UPDATE_SUCCESS"}</li>
 * </ul></li>
 * <li>&rArr; {@link AddressSuccessEvent#DELETE_SUCCESS "SCHEDULER_ADDRESS_DELETE_SUCCESS"}</li>
 * </ul></li>
 * <li>{@link AddressFailedEvent} &rArr; {@link AddressFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_ADDRESS_FAILED_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressFailedEvent#SAVE_FAILED "SCHEDULER_ADDRESS_SAVE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressFailedEvent#INSERT_FAILED "SCHEDULER_ADDRESS_INSERT_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressFailedEvent#INSERT_FAULTED "SCHEDULER_ADDRESS_INSERT_FAULTED"}</li>
 * <li>&rArr; {@link AddressFailedEvent#INSERT_INVALID "SCHEDULER_ADDRESS_INSERT_INVALID"}</li>
 * <li>&rArr; {@link AddressFailedEvent#INSERT_CANCELED "SCHEDULER_ADDRESS_INSERT_CANCELED"}</li>
 * </ul></li>
 * <li>&rArr; {@link AddressFailedEvent#UPDATE_FAILED "SCHEDULER_ADDRESS_UPDATE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressFailedEvent#UPDATE_FAULTED "SCHEDULER_ADDRESS_UPDATE_FAULTED"}</li>
 * <li>&rArr; {@link AddressFailedEvent#UPDATE_INVALID "SCHEDULER_ADDRESS_UPDATE_INVALID"}</li>
 * <li>&rArr; {@link AddressFailedEvent#UPDATE_CANCELED "SCHEDULER_ADDRESS_UPDATE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * <li>&rArr; {@link AddressFailedEvent#DELETE_FAILED "SCHEDULER_ADDRESS_DELETE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressFailedEvent#DELETE_FAULTED "SCHEDULER_ADDRESS_DELETE_FAULTED"}</li>
 * <li>&rArr; {@link AddressFailedEvent#DELETE_INVALID "SCHEDULER_ADDRESS_DELETE_INVALID"}</li>
 * <li>&rArr; {@link AddressFailedEvent#DELETE_CANCELED "SCHEDULER_ADDRESS_DELETE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * <li>{@link CityEvent} &rArr; {@link CityEvent#CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityEvent#CHANGE_EVENT_TYPE "SCHEDULER_CITY_CHANGE_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link CitySuccessEvent} &rArr; {@link CitySuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_CITY_SUCCESS_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CitySuccessEvent#SAVE_SUCCESS "SCHEDULER_CITY_SAVE_SUCCESS"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CitySuccessEvent#INSERT_SUCCESS "SCHEDULER_CITY_INSERT_SUCCESS"}</li>
 * <li>&rArr; {@link CitySuccessEvent#UPDATE_SUCCESS "SCHEDULER_CITY_UPDATE_SUCCESS"}</li>
 * </ul></li>
 * <li>&rArr; {@link CitySuccessEvent#DELETE_SUCCESS "SCHEDULER_CITY_DELETE_SUCCESS"}</li>
 * </ul></li>
 * <li>{@link CityFailedEvent} &rArr; {@link CityFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_CITY_FAILED_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityFailedEvent#SAVE_FAILED "SCHEDULER_CITY_SAVE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityFailedEvent#INSERT_FAILED "SCHEDULER_CITY_INSERT_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityFailedEvent#INSERT_FAULTED "SCHEDULER_CITY_INSERT_FAULTED"}</li>
 * <li>&rArr; {@link CityFailedEvent#INSERT_INVALID "SCHEDULER_CITY_INSERT_INVALID"}</li>
 * <li>&rArr; {@link CityFailedEvent#INSERT_CANCELED "SCHEDULER_CITY_INSERT_CANCELED"}</li>
 * </ul></li>
 * <li>&rArr; {@link CityFailedEvent#UPDATE_FAILED "SCHEDULER_CITY_UPDATE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityFailedEvent#UPDATE_FAULTED "SCHEDULER_CITY_UPDATE_FAULTED"}</li>
 * <li>&rArr; {@link CityFailedEvent#UPDATE_INVALID "SCHEDULER_CITY_UPDATE_INVALID"}</li>
 * <li>&rArr; {@link CityFailedEvent#UPDATE_CANCELED "SCHEDULER_CITY_UPDATE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * <li>&rArr; {@link CityFailedEvent#DELETE_FAILED "SCHEDULER_CITY_DELETE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityFailedEvent#DELETE_FAULTED "SCHEDULER_CITY_DELETE_FAULTED"}</li>
 * <li>&rArr; {@link CityFailedEvent#DELETE_INVALID "SCHEDULER_CITY_DELETE_INVALID"}</li>
 * <li>&rArr; {@link CityFailedEvent#DELETE_CANCELED "SCHEDULER_CITY_DELETE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * <li>{@link CountryEvent} &rArr; {@link CountryEvent#COUNTRY_EVENT_TYPE "SCHEDULER_COUNTRY_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryEvent#CHANGE_EVENT_TYPE "SCHEDULER_COUNTRY_CHANGE_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link CountrySuccessEvent} &rArr; {@link CountrySuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_COUNTRY_SUCCESS_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountrySuccessEvent#SAVE_SUCCESS "SCHEDULER_COUNTRY_SAVE_SUCCESS"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountrySuccessEvent#INSERT_SUCCESS "SCHEDULER_COUNTRY_INSERT_SUCCESS"}</li>
 * <li>&rArr; {@link CountrySuccessEvent#UPDATE_SUCCESS "SCHEDULER_COUNTRY_UPDATE_SUCCESS"}</li>
 * </ul></li>
 * <li>&rArr; {@link CountrySuccessEvent#DELETE_SUCCESS "SCHEDULER_COUNTRY_DELETE_SUCCESS"}</li>
 * </ul></li>
 * <li>{@link CountryFailedEvent} &rArr; {@link CountryFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_COUNTRY_FAILED_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryFailedEvent#SAVE_FAILED "SCHEDULER_COUNTRY_SAVE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryFailedEvent#INSERT_FAILED "SCHEDULER_COUNTRY_INSERT_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryFailedEvent#INSERT_FAULTED "SCHEDULER_COUNTRY_INSERT_FAULTED"}</li>
 * <li>&rArr; {@link CountryFailedEvent#INSERT_INVALID "SCHEDULER_COUNTRY_INSERT_INVALID"}</li>
 * <li>&rArr; {@link CountryFailedEvent#INSERT_CANCELED "SCHEDULER_COUNTRY_INSERT_CANCELED"}</li>
 * </ul></li>
 * <li>&rArr; {@link CountryFailedEvent#UPDATE_FAILED "SCHEDULER_COUNTRY_UPDATE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryFailedEvent#UPDATE_FAULTED "SCHEDULER_COUNTRY_UPDATE_FAULTED"}</li>
 * <li>&rArr; {@link CountryFailedEvent#UPDATE_INVALID "SCHEDULER_COUNTRY_UPDATE_INVALID"}</li>
 * <li>&rArr; {@link CountryFailedEvent#UPDATE_CANCELED "SCHEDULER_COUNTRY_UPDATE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * <li>&rArr; {@link CountryFailedEvent#DELETE_FAILED "SCHEDULER_COUNTRY_DELETE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryFailedEvent#DELETE_FAULTED "SCHEDULER_COUNTRY_DELETE_FAULTED"}</li>
 * <li>&rArr; {@link CountryFailedEvent#DELETE_INVALID "SCHEDULER_COUNTRY_DELETE_INVALID"}</li>
 * <li>&rArr; {@link CountryFailedEvent#DELETE_CANCELED "SCHEDULER_COUNTRY_DELETE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * <li>{@link UserEvent} &rArr; {@link UserEvent#USER_EVENT_TYPE "SCHEDULER_USER_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserEvent#CHANGE_EVENT_TYPE "SCHEDULER_USER_CHANGE_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link UserSuccessEvent} &rArr; {@link UserSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_USER_SUCCESS_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserSuccessEvent#SAVE_SUCCESS "SCHEDULER_USER_SAVE_SUCCESS"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserSuccessEvent#INSERT_SUCCESS "SCHEDULER_USER_INSERT_SUCCESS"}</li>
 * <li>&rArr; {@link UserSuccessEvent#UPDATE_SUCCESS "SCHEDULER_USER_UPDATE_SUCCESS"}</li>
 * </ul></li>
 * <li>&rArr; {@link UserSuccessEvent#DELETE_SUCCESS "SCHEDULER_USER_DELETE_SUCCESS"}</li>
 * </ul></li>
 * <li>{@link UserFailedEvent} &rArr; {@link UserFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_USER_FAILED_EVENT"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserFailedEvent#SAVE_FAILED "SCHEDULER_USER_SAVE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserFailedEvent#INSERT_FAILED "SCHEDULER_USER_INSERT_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserFailedEvent#INSERT_FAULTED "SCHEDULER_USER_INSERT_FAULTED"}</li>
 * <li>&rArr; {@link UserFailedEvent#INSERT_INVALID "SCHEDULER_USER_INSERT_INVALID"}</li>
 * <li>&rArr; {@link UserFailedEvent#INSERT_CANCELED "SCHEDULER_USER_INSERT_CANCELED"}</li>
 * </ul></li>
 * <li>&rArr; {@link UserFailedEvent#UPDATE_FAILED "SCHEDULER_USER_UPDATE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserFailedEvent#UPDATE_FAULTED "SCHEDULER_USER_UPDATE_FAULTED"}</li>
 * <li>&rArr; {@link UserFailedEvent#UPDATE_INVALID "SCHEDULER_USER_UPDATE_INVALID"}</li>
 * <li>&rArr; {@link UserFailedEvent#UPDATE_CANCELED "SCHEDULER_USER_UPDATE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * <li>&rArr; {@link UserFailedEvent#DELETE_FAILED "SCHEDULER_USER_DELETE_FAILED"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserFailedEvent#DELETE_FAULTED "SCHEDULER_USER_DELETE_FAULTED"}</li>
 * <li>&rArr; {@link UserFailedEvent#DELETE_INVALID "SCHEDULER_USER_DELETE_INVALID"}</li>
 * <li>&rArr; {@link UserFailedEvent#DELETE_CANCELED "SCHEDULER_USER_DELETE_CANCELED"}</li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The type of {@link DataAccessObject}.
 * @param <M> The type of {@link EntityModelImpl}.
 */
public abstract class ModelEvent<D extends DataAccessObject, M extends EntityModelImpl<D>> extends Event implements IModelEvent<D, M> {

    private static final long serialVersionUID = -6832461936768738020L;

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ModelEvent.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ModelEvent.class.getName());

    /**
     * Base {@link EventType} for all {@code ModelEvent}s.
     */
    public static final EventType<ModelEvent<? extends DataAccessObject, ? extends EntityModelImpl<? extends DataAccessObject>>> MODEL_EVENT_TYPE
            = new EventType<>(ANY, "SCHEDULER_MODEL_EVENT");

    @SuppressWarnings("unchecked")
    public static final String getMessage(ModelEvent<? extends DataAccessObject, ? extends EntityModelImpl<? extends DataAccessObject>> event) {
        if (event instanceof ModelFailedEvent) {
            return ((ModelFailedEvent<? extends DataAccessObject, ? extends EntityModelImpl<? extends DataAccessObject>>) event).getMessage();
        }
        return "";
    }

    public static final <E extends ModelEvent<? extends DataAccessObject, ? extends EntityModelImpl<? extends DataAccessObject>>, R> R withEvent(E event, Function<E, R> ifFailed,
            R otherwise) {
        if (null == event || event instanceof ModelFailedEvent) {
            return ifFailed.apply(event);
        }
        return otherwise;
    }

    private State state;

    /**
     * Creates a new {@link ModelEvent} that shares the same {@link DataAccessObject} and {@link EntityModelImpl} as another {@link ModelEvent}.
     *
     * @param event The event that will share the same {@link DataAccessObject} and {@link EntityModelImpl}.
     * @param source The object which sent the event or {@code null} to use the same source as the {@code event} parameter.
     * @param target The target to associate with the event or {@code null} to use the same target as the {@code event} parameter.
     * @param eventType The event type.
     * @param operation The database operation associated with the event {@code null} to use the same operation as the {@code event} parameter.
     * @throws NullPointerException if {@code event} or {@code eventType} is {@code null}.
     */
    protected ModelEvent(ModelEvent<D, M> event, Object source, EventTarget target, EventType<? extends ModelEvent<D, M>> eventType, DbOperationType operation) {
        super((null == target) ? event.getSource() : target, (null == target) ? event.getTarget() : target, Objects.requireNonNull(eventType));
        state = event.state.copyOf(operation);
    }

    /**
     * Creates a new {@link ModelEvent} that shares the same {@link DataAccessObject} and {@link EntityModelImpl} as another {@link ModelEvent}.
     *
     * @param event The event that will share the same {@link DataAccessObject} and {@link EntityModelImpl}.
     * @param eventType The event type.
     * @param operation The database operation associated with the event {@code null} to use the same operation as the {@code event} parameter.
     */
    protected ModelEvent(ModelEvent<D, M> event, EventType<? extends ModelEvent<D, M>> eventType, DbOperationType operation) {
        super(event.getSource(), event.getTarget(), Objects.requireNonNull(eventType));
        state = event.state.copyOf(operation);
    }

    protected ModelEvent(M target, Object source, EventType<? extends ModelEvent<D, M>> eventType, DbOperationType operation) {
        super(source, target.dataObject(), Objects.requireNonNull(eventType));
        state = new StateOriginal(target, operation);
    }

    @Override
    public D getDataAccessObject() {
        return state.getDataAccessObject();
    }

    @Override
    public M getEntityModel() {
        return state.getFxRecordModel();
    }

    @Override
    public DbOperationType getOperation() {
        return state.getOperation();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Event copyFor(Object newSource, EventTarget newTarget) {
        LOG.entering(LOG.getName(), "copyFor", new Object[]{newSource, newTarget});
        @SuppressWarnings("unchecked")
        ModelEvent<D, M> copy;
        try {
            copy = (ModelEvent<D, M>) super.copyFor(newSource, newTarget);
            copy.state = state;
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, String.format("Failure creating copy of %s", this), ex);
            throw new RuntimeException("Failure creating event copy", ex);
        }
        LOG.finer(() -> String.format("Returning %s as copy of %s", copy, this));
        return copy;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(state.getOperation());
        hash = 61 * hash + Objects.hashCode(state.getDataAccessObject());
        hash = 61 * hash + Objects.hashCode(state.getFxRecordModel());
        hash = 61 * hash + Objects.hashCode(getEventType().getName());
        hash = 61 * hash + Objects.hashCode(getTarget());
        hash = 61 * hash + Objects.hashCode(getSource());
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
        if (ModelEvent.this.getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final ModelEvent<D, M> other = (ModelEvent<D, M>) obj;
        if (this.getOperation() == other.getOperation() && Objects.equals(getEventType(), other.getEventType())) {
            M model = state.getFxRecordModel();
            if (Objects.equals(model, other.getEntityModel()) && (null != model || Objects.equals(state.getDataAccessObject(), other.getDataAccessObject()))) {
                return Objects.equals(target, other.getTarget()) && Objects.equals(source, other.getSource());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName()).append("[type=").append(getEventType().getName()).append("; operation=").append(state.getOperation());
        if (this instanceof ModelFailedEvent) {
            @SuppressWarnings("unchecked")
            ModelFailedEvent<D, M> f = (ModelFailedEvent<D, M>) this;
            sb.append("; failKind=").append(f.getFailKind());
            if (isConsumed()) {
                sb.append("; consumed=true");
            }
            String s = f.getMessage();
            if (null != s && !s.isEmpty()) {
                sb.append("; message=").append(LogHelper.toLogText(s));
            }
            Throwable ex = f.getFault();
            if (null != ex) {
                sb.append("; fault=").append(ex);
            }
        } else if (isConsumed()) {
            sb.append("; consumed=true");
        }
        M fxRecordModel = getEntityModel();
        if (null == fxRecordModel) {
            sb.append("; dataAccessObject=").append(state.getDataAccessObject());
        } else {
            sb.append("; fxRecordModel=").append(fxRecordModel);
        }
        EventTarget t = getTarget();
        if (null != t) {
            sb.append("; target=").append(t);
        }
        Object s = getSource();
        if (null != s) {
            return sb.append("; source=").append(s).append("]").toString();
        }
        return sb.append("]").toString();
    }

    private class StateOriginal extends State {

        private final DbOperationType operation;
        private final D dataAccessObject;
        private final M fxRecordModel;

        private StateOriginal(M fxRecordModel, DbOperationType operation) {
            dataAccessObject = (this.fxRecordModel = fxRecordModel).dataObject();
            this.operation = Objects.requireNonNull(operation);
        }

        @Override
        protected DbOperationType getOperation() {
            return operation;
        }

        @Override
        protected D getDataAccessObject() {
            return dataAccessObject;
        }

        @Override
        protected M getFxRecordModel() {
            return fxRecordModel;
        }

        @Override
        protected State copyOf(DbOperationType operation) {
            if (null == operation || operation == this.operation) {
                return this;
            }
            return new StateCopy(this, operation);
        }

    }

    private class StateCopy extends State {

        private final StateOriginal source;
        private final DbOperationType operation;

        private StateCopy(StateOriginal source, DbOperationType operation) {
            this.source = source;
            this.operation = Objects.requireNonNull(operation);
        }

        @Override
        protected DbOperationType getOperation() {
            return operation;
        }

        @Override
        protected D getDataAccessObject() {
            return source.getDataAccessObject();
        }

        @Override
        protected M getFxRecordModel() {
            return source.getFxRecordModel();
        }

        @Override
        protected State copyOf(DbOperationType operation) {
            if (null == operation || operation == this.operation) {
                return this;
            }
            return source.copyOf(operation);
        }

    }

    private abstract class State {

        protected abstract DbOperationType getOperation();

        protected abstract D getDataAccessObject();

        protected abstract M getFxRecordModel();

        protected abstract State copyOf(DbOperationType operation);

    }

}
