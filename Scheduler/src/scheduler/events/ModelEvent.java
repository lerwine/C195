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
 * <h3>Event Dispatch Chains</h3>
 * <dl>
 * <dt>{@link scheduler.dao.DataAccessObject.DaoTask}: {@link scheduler.dao.DataAccessObject.DaoTask#succeeded() succeeded()} | {@link scheduler.dao.DataAccessObject.DaoTask#cancelled() cancelled()} | {@link scheduler.dao.DataAccessObject.DaoTask#failed() failed()}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CustomerEvent} | {@link UserEvent} | {@link AddressEvent} | {@link CityEvent} | {@link CountryEvent}</dt>
 * <dd>{@link scheduler.dao.DataAccessObject} &rArr; {@link scheduler.dao.DataAccessObject.DaoFactory} &rArr;
 * {@link scheduler.model.ui.EntityModelImpl.FxModelFactory}</dd>
 * <dt>{@link AppointmentEvent}</dt>
 * <dd>{@link scheduler.dao.DataAccessObject} &rArr; {@link scheduler.dao.DataAccessObject.DaoFactory} &rArr;
 * {@link scheduler.model.ui.EntityModelImpl.FxModelFactory} &rArr; {@link scheduler.AppointmentAlertManager}</dd>
 * </dl></dd>
 * <dt>{@link scheduler.fx.ItemEditTableCell}: {@link scheduler.fx.ItemEditTableCell#onEditButtonAction(javafx.event.ActionEvent) onEditButtonAction(ActionEvent)} | {@link scheduler.fx.ItemEditTableCell#onDeleteButtonAction(javafx.event.ActionEvent) onDeleteButtonAction(ActionEvent)}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link OperationRequestEvent}</dt>
 * <dd>{@link scheduler.fx.ItemEditTableCell} &rArr; {@link scheduler.fx.ItemEditTableCellFactory}</dd>
 * </dl>
 * </dd>
 * </dl>
 * <h3>Event Type Hierarchy</h3>
 * <dl style="margin-top:0px">
 * <dt>{@link javafx.event.Event#ANY "EVENT"} &rarr; {@link javafx.event.Event}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link #MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &rarr; <strong>{@code ModelEvent}</strong></dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &rarr; {@link OperationRequestEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"} &rarr; {@link AppointmentOpRequestEvent}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AppointmentOpRequestEvent#EDIT_REQUEST "SCHEDULER_APPOINTMENT_EDIT_REQUEST"}</li>
 * <li>{@link AppointmentOpRequestEvent#DELETE_REQUEST "SCHEDULER_APPOINTMENT_DELETE_REQUEST"}</li>
 * </ul></dd>
 * </dl>
 * <dl style="margin-top:0px">
 * <dt>{@link CustomerOpRequestEvent#CUSTOMER_OP_REQUEST "SCHEDULER_CUSTOMER_OP_REQUEST"} &rarr; {@link CustomerOpRequestEvent}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CustomerOpRequestEvent#EDIT_REQUEST "SCHEDULER_CUSTOMER_EDIT_REQUEST"}</li>
 * <li>{@link CustomerOpRequestEvent#DELETE_REQUEST "SCHEDULER_CUSTOMER_DELETE_REQUEST"}</li>
 * </ul></dd>
 * </dl>
 * <dl style="margin-top:0px">
 * <dt>{@link UserOpRequestEvent#USER_OP_REQUEST "SCHEDULER_USER_OP_REQUEST"} &rarr; {@link UserOpRequestEvent}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link UserOpRequestEvent#EDIT_REQUEST "SCHEDULER_USER_EDIT_REQUEST"}</li>
 * <li>{@link UserOpRequestEvent#DELETE_REQUEST "SCHEDULER_USER_DELETE_REQUEST"}</li>
 * </ul></dd>
 * </dl>
 * <dl style="margin-top:0px">
 * <dt>{@link AddressOpRequestEvent#ADDRESS_OP_REQUEST "SCHEDULER_ADDRESS_OP_REQUEST"} &rarr; {@link AddressOpRequestEvent}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AddressOpRequestEvent#EDIT_REQUEST "SCHEDULER_ADDRESS_EDIT_REQUEST"}</li>
 * <li>{@link AddressOpRequestEvent#DELETE_REQUEST "SCHEDULER_ADDRESS_DELETE_REQUEST"}</li>
 * </ul></dd>
 * </dl>
 * <dl style="margin-top:0px">
 * <dt>{@link CityOpRequestEvent#CITY_OP_REQUEST "SCHEDULER_CITY_OP_REQUEST"} &rarr; {@link CityOpRequestEvent}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CityOpRequestEvent#EDIT_REQUEST "SCHEDULER_CITY_EDIT_REQUEST"}</li>
 * <li>{@link CityOpRequestEvent#DELETE_REQUEST "SCHEDULER_CITY_DELETE_REQUEST"}</li>
 * </ul></dd>
 * </dl>
 * <dl style="margin-top:0px">
 * <dt>{@link CountryOpRequestEvent#COUNTRY_OP_REQUEST "SCHEDULER_COUNTRY_OP_REQUEST"} &rarr; {@link CountryOpRequestEvent}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CountryOpRequestEvent#EDIT_REQUEST "SCHEDULER_COUNTRY_EDIT_REQUEST"}</li>
 * <li>{@link CountryOpRequestEvent#DELETE_REQUEST "SCHEDULER_COUNTRY_DELETE_REQUEST"}</li>
 * </ul></dd>
 * </dl>
 * </dd>
 * <dt>{@link AppointmentEvent#APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"} &rarr; {@link AppointmentEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AppointmentEvent#CHANGE_EVENT_TYPE "SCHEDULER_APPOINTMENT_CHANGE_EVENT"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AppointmentSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_APPOINTMENT_SUCCESS_EVENT"} &rarr; {@link AppointmentSuccessEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AppointmentSuccessEvent#SAVE_SUCCESS "SCHEDULER_APPOINTMENT_SAVE_SUCCESS"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AppointmentSuccessEvent#INSERT_SUCCESS "SCHEDULER_APPOINTMENT_INSERT_SUCCESS"}</li>
 * <li>{@link AppointmentSuccessEvent#UPDATE_SUCCESS "SCHEDULER_APPOINTMENT_UPDATE_SUCCESS"}</li>
 * </ul></dd>
 * <dt>{@link AppointmentSuccessEvent#DELETE_SUCCESS "SCHEDULER_APPOINTMENT_DELETE_SUCCESS"}</dt>
 * </dl></dd>
 * <dt>{@link AppointmentFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_APPOINTMENT_FAILED_EVENT"} &rarr; {@link AppointmentFailedEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AppointmentFailedEvent#SAVE_FAILED "SCHEDULER_APPOINTMENT_SAVE_FAILED"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AppointmentFailedEvent#INSERT_FAILED "SCHEDULER_APPOINTMENT_INSERT_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AppointmentFailedEvent#INSERT_FAULTED "SCHEDULER_APPOINTMENT_INSERT_FAULTED"}</li>
 * <li>{@link AppointmentFailedEvent#INSERT_INVALID "SCHEDULER_APPOINTMENT_INSERT_INVALID"}</li>
 * <li>{@link AppointmentFailedEvent#INSERT_CANCELED "SCHEDULER_APPOINTMENT_INSERT_CANCELED"}</li>
 * </ul></dd>
 * <dt>{@link AppointmentFailedEvent#UPDATE_FAILED "SCHEDULER_APPOINTMENT_UPDATE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AppointmentFailedEvent#UPDATE_FAULTED "SCHEDULER_APPOINTMENT_UPDATE_FAULTED"}</li>
 * <li>{@link AppointmentFailedEvent#UPDATE_INVALID "SCHEDULER_APPOINTMENT_UPDATE_INVALID"}</li>
 * <li>{@link AppointmentFailedEvent#UPDATE_CANCELED "SCHEDULER_APPOINTMENT_UPDATE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * <dt>{@link AppointmentFailedEvent#DELETE_FAILED "SCHEDULER_APPOINTMENT_DELETE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AppointmentFailedEvent#DELETE_FAULTED "SCHEDULER_APPOINTMENT_DELETE_FAULTED"}</li>
 * <li>{@link AppointmentFailedEvent#DELETE_INVALID "SCHEDULER_APPOINTMENT_DELETE_INVALID"}</li>
 * <li>{@link AppointmentFailedEvent#DELETE_CANCELED "SCHEDULER_APPOINTMENT_DELETE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>{@link CustomerEvent#CUSTOMER_EVENT_TYPE "SCHEDULER_CUSTOMER_EVENT"} &rarr; {@link CustomerEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CustomerEvent#CHANGE_EVENT_TYPE "SCHEDULER_CUSTOMER_CHANGE_EVENT"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CustomerSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_CUSTOMER_SUCCESS_EVENT"} &rarr; {@link CustomerSuccessEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CustomerSuccessEvent#SAVE_SUCCESS "SCHEDULER_CUSTOMER_SAVE_SUCCESS"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CustomerSuccessEvent#INSERT_SUCCESS "SCHEDULER_CUSTOMER_INSERT_SUCCESS"}</li>
 * <li>{@link CustomerSuccessEvent#UPDATE_SUCCESS "SCHEDULER_CUSTOMER_UPDATE_SUCCESS"}</li>
 * </ul></dd>
 * <dt>{@link CustomerSuccessEvent#DELETE_SUCCESS "SCHEDULER_CUSTOMER_DELETE_SUCCESS"}</dt>
 * </dl></dd>
 * <dt>{@link CustomerFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_CUSTOMER_FAILED_EVENT"} &rarr; {@link CustomerFailedEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CustomerFailedEvent#SAVE_FAILED "SCHEDULER_CUSTOMER_SAVE_FAILED"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CustomerFailedEvent#INSERT_FAILED "SCHEDULER_CUSTOMER_INSERT_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CustomerFailedEvent#INSERT_FAULTED "SCHEDULER_CUSTOMER_INSERT_FAULTED"}</li>
 * <li>{@link CustomerFailedEvent#INSERT_INVALID "SCHEDULER_CUSTOMER_INSERT_INVALID"}</li>
 * <li>{@link CustomerFailedEvent#INSERT_CANCELED "SCHEDULER_CUSTOMER_INSERT_CANCELED"}</li>
 * </ul></dd>
 * <dt>{@link CustomerFailedEvent#UPDATE_FAILED "SCHEDULER_CUSTOMER_UPDATE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CustomerFailedEvent#UPDATE_FAULTED "SCHEDULER_CUSTOMER_UPDATE_FAULTED"}</li>
 * <li>{@link CustomerFailedEvent#UPDATE_INVALID "SCHEDULER_CUSTOMER_UPDATE_INVALID"}</li>
 * <li>{@link CustomerFailedEvent#UPDATE_CANCELED "SCHEDULER_CUSTOMER_UPDATE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * <dt>{@link CustomerFailedEvent#DELETE_FAILED "SCHEDULER_CUSTOMER_DELETE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CustomerFailedEvent#DELETE_FAULTED "SCHEDULER_CUSTOMER_DELETE_FAULTED"}</li>
 * <li>{@link CustomerFailedEvent#DELETE_INVALID "SCHEDULER_CUSTOMER_DELETE_INVALID"}</li>
 * <li>{@link CustomerFailedEvent#DELETE_CANCELED "SCHEDULER_CUSTOMER_DELETE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>{@link UserEvent#USER_EVENT_TYPE "SCHEDULER_USER_EVENT"} &rarr; {@link UserEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link UserEvent#CHANGE_EVENT_TYPE "SCHEDULER_USER_CHANGE_EVENT"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link UserSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_USER_SUCCESS_EVENT"} &rarr; {@link UserSuccessEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link UserSuccessEvent#SAVE_SUCCESS "SCHEDULER_USER_SAVE_SUCCESS"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link UserSuccessEvent#INSERT_SUCCESS "SCHEDULER_USER_INSERT_SUCCESS"}</li>
 * <li>{@link UserSuccessEvent#UPDATE_SUCCESS "SCHEDULER_USER_UPDATE_SUCCESS"}</li>
 * </ul></dd>
 * <dt>{@link UserSuccessEvent#DELETE_SUCCESS "SCHEDULER_USER_DELETE_SUCCESS"}</dt>
 * </dl></dd>
 * <dt>{@link UserFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_USER_FAILED_EVENT"} &rarr; {@link UserFailedEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link UserFailedEvent#SAVE_FAILED "SCHEDULER_USER_SAVE_FAILED"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link UserFailedEvent#INSERT_FAILED "SCHEDULER_USER_INSERT_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link UserFailedEvent#INSERT_FAULTED "SCHEDULER_USER_INSERT_FAULTED"}</li>
 * <li>{@link UserFailedEvent#INSERT_INVALID "SCHEDULER_USER_INSERT_INVALID"}</li>
 * <li>{@link UserFailedEvent#INSERT_CANCELED "SCHEDULER_USER_INSERT_CANCELED"}</li>
 * </ul></dd>
 * <dt>{@link UserFailedEvent#UPDATE_FAILED "SCHEDULER_USER_UPDATE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link UserFailedEvent#UPDATE_FAULTED "SCHEDULER_USER_UPDATE_FAULTED"}</li>
 * <li>{@link UserFailedEvent#UPDATE_INVALID "SCHEDULER_USER_UPDATE_INVALID"}</li>
 * <li>{@link UserFailedEvent#UPDATE_CANCELED "SCHEDULER_USER_UPDATE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * <dt>{@link UserFailedEvent#DELETE_FAILED "SCHEDULER_USER_DELETE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link UserFailedEvent#DELETE_FAULTED "SCHEDULER_USER_DELETE_FAULTED"}</li>
 * <li>{@link UserFailedEvent#DELETE_INVALID "SCHEDULER_USER_DELETE_INVALID"}</li>
 * <li>{@link UserFailedEvent#DELETE_CANCELED "SCHEDULER_USER_DELETE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>{@link AddressEvent#ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"} &rarr; {@link AddressEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AddressEvent#CHANGE_EVENT_TYPE "SCHEDULER_ADDRESS_CHANGE_EVENT"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AddressSuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_ADDRESS_SUCCESS_EVENT"} &rarr; {@link AddressSuccessEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AddressSuccessEvent#SAVE_SUCCESS "SCHEDULER_ADDRESS_SAVE_SUCCESS"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AddressSuccessEvent#INSERT_SUCCESS "SCHEDULER_ADDRESS_INSERT_SUCCESS"}</li>
 * <li>{@link AddressSuccessEvent#UPDATE_SUCCESS "SCHEDULER_ADDRESS_UPDATE_SUCCESS"}</li>
 * </ul></dd>
 * <dt>{@link AddressSuccessEvent#DELETE_SUCCESS "SCHEDULER_ADDRESS_DELETE_SUCCESS"}</dt>
 * </dl></dd>
 * <dt>{@link AddressFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_ADDRESS_FAILED_EVENT"} &rarr; {@link AddressFailedEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AddressFailedEvent#SAVE_FAILED "SCHEDULER_ADDRESS_SAVE_FAILED"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link AddressFailedEvent#INSERT_FAILED "SCHEDULER_ADDRESS_INSERT_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AddressFailedEvent#INSERT_FAULTED "SCHEDULER_ADDRESS_INSERT_FAULTED"}</li>
 * <li>{@link AddressFailedEvent#INSERT_INVALID "SCHEDULER_ADDRESS_INSERT_INVALID"}</li>
 * <li>{@link AddressFailedEvent#INSERT_CANCELED "SCHEDULER_ADDRESS_INSERT_CANCELED"}</li>
 * </ul></dd>
 * <dt>{@link AddressFailedEvent#UPDATE_FAILED "SCHEDULER_ADDRESS_UPDATE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AddressFailedEvent#UPDATE_FAULTED "SCHEDULER_ADDRESS_UPDATE_FAULTED"}</li>
 * <li>{@link AddressFailedEvent#UPDATE_INVALID "SCHEDULER_ADDRESS_UPDATE_INVALID"}</li>
 * <li>{@link AddressFailedEvent#UPDATE_CANCELED "SCHEDULER_ADDRESS_UPDATE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * <dt>{@link AddressFailedEvent#DELETE_FAILED "SCHEDULER_ADDRESS_DELETE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link AddressFailedEvent#DELETE_FAULTED "SCHEDULER_ADDRESS_DELETE_FAULTED"}</li>
 * <li>{@link AddressFailedEvent#DELETE_INVALID "SCHEDULER_ADDRESS_DELETE_INVALID"}</li>
 * <li>{@link AddressFailedEvent#DELETE_CANCELED "SCHEDULER_ADDRESS_DELETE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>{@link CityEvent#CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"} &rarr; {@link CityEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CityEvent#CHANGE_EVENT_TYPE "SCHEDULER_CITY_CHANGE_EVENT"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CitySuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_CITY_SUCCESS_EVENT"} &rarr; {@link CitySuccessEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CitySuccessEvent#SAVE_SUCCESS "SCHEDULER_CITY_SAVE_SUCCESS"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CitySuccessEvent#INSERT_SUCCESS "SCHEDULER_CITY_INSERT_SUCCESS"}</li>
 * <li>{@link CitySuccessEvent#UPDATE_SUCCESS "SCHEDULER_CITY_UPDATE_SUCCESS"}</li>
 * </ul></dd>
 * <dt>{@link CitySuccessEvent#DELETE_SUCCESS "SCHEDULER_CITY_DELETE_SUCCESS"}</dt>
 * </dl></dd>
 * <dt>{@link CityFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_CITY_FAILED_EVENT"} &rarr; {@link CityFailedEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CityFailedEvent#SAVE_FAILED "SCHEDULER_CITY_SAVE_FAILED"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CityFailedEvent#INSERT_FAILED "SCHEDULER_CITY_INSERT_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CityFailedEvent#INSERT_FAULTED "SCHEDULER_CITY_INSERT_FAULTED"}</li>
 * <li>{@link CityFailedEvent#INSERT_INVALID "SCHEDULER_CITY_INSERT_INVALID"}</li>
 * <li>{@link CityFailedEvent#INSERT_CANCELED "SCHEDULER_CITY_INSERT_CANCELED"}</li>
 * </ul></dd>
 * <dt>{@link CityFailedEvent#UPDATE_FAILED "SCHEDULER_CITY_UPDATE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CityFailedEvent#UPDATE_FAULTED "SCHEDULER_CITY_UPDATE_FAULTED"}</li>
 * <li>{@link CityFailedEvent#UPDATE_INVALID "SCHEDULER_CITY_UPDATE_INVALID"}</li>
 * <li>{@link CityFailedEvent#UPDATE_CANCELED "SCHEDULER_CITY_UPDATE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * <dt>{@link CityFailedEvent#DELETE_FAILED "SCHEDULER_CITY_DELETE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CityFailedEvent#DELETE_FAULTED "SCHEDULER_CITY_DELETE_FAULTED"}</li>
 * <li>{@link CityFailedEvent#DELETE_INVALID "SCHEDULER_CITY_DELETE_INVALID"}</li>
 * <li>{@link CityFailedEvent#DELETE_CANCELED "SCHEDULER_CITY_DELETE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>{@link CountryEvent#COUNTRY_EVENT_TYPE "SCHEDULER_COUNTRY_EVENT"} &rarr; {@link CountryEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CountryEvent#CHANGE_EVENT_TYPE "SCHEDULER_COUNTRY_CHANGE_EVENT"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CountrySuccessEvent#SUCCESS_EVENT_TYPE "SCHEDULER_COUNTRY_SUCCESS_EVENT"} &rarr; {@link CountrySuccessEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CountrySuccessEvent#SAVE_SUCCESS "SCHEDULER_COUNTRY_SAVE_SUCCESS"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CountrySuccessEvent#INSERT_SUCCESS "SCHEDULER_COUNTRY_INSERT_SUCCESS"}</li>
 * <li>{@link CountrySuccessEvent#UPDATE_SUCCESS "SCHEDULER_COUNTRY_UPDATE_SUCCESS"}</li>
 * </ul></dd>
 * <dt>{@link CountrySuccessEvent#DELETE_SUCCESS "SCHEDULER_COUNTRY_DELETE_SUCCESS"}</dt>
 * </dl></dd>
 * <dt>{@link CountryFailedEvent#FAILED_EVENT_TYPE "SCHEDULER_COUNTRY_FAILED_EVENT"} &rarr; {@link CountryFailedEvent}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CountryFailedEvent#SAVE_FAILED "SCHEDULER_COUNTRY_SAVE_FAILED"}</dt>
 * <dd>
 * <dl style="margin-top:0px">
 * <dt>{@link CountryFailedEvent#INSERT_FAILED "SCHEDULER_COUNTRY_INSERT_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CountryFailedEvent#INSERT_FAULTED "SCHEDULER_COUNTRY_INSERT_FAULTED"}</li>
 * <li>{@link CountryFailedEvent#INSERT_INVALID "SCHEDULER_COUNTRY_INSERT_INVALID"}</li>
 * <li>{@link CountryFailedEvent#INSERT_CANCELED "SCHEDULER_COUNTRY_INSERT_CANCELED"}</li>
 * </ul></dd>
 * <dt>{@link CountryFailedEvent#UPDATE_FAILED "SCHEDULER_COUNTRY_UPDATE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CountryFailedEvent#UPDATE_FAULTED "SCHEDULER_COUNTRY_UPDATE_FAULTED"}</li>
 * <li>{@link CountryFailedEvent#UPDATE_INVALID "SCHEDULER_COUNTRY_UPDATE_INVALID"}</li>
 * <li>{@link CountryFailedEvent#UPDATE_CANCELED "SCHEDULER_COUNTRY_UPDATE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * <dt>{@link CountryFailedEvent#DELETE_FAILED "SCHEDULER_COUNTRY_DELETE_FAILED"}</dt>
 * <dd>
 * <ul style="list-style-type:none;margin-top:0px">
 * <li>{@link CountryFailedEvent#DELETE_FAULTED "SCHEDULER_COUNTRY_DELETE_FAULTED"}</li>
 * <li>{@link CountryFailedEvent#DELETE_INVALID "SCHEDULER_COUNTRY_DELETE_INVALID"}</li>
 * <li>{@link CountryFailedEvent#DELETE_CANCELED "SCHEDULER_COUNTRY_DELETE_CANCELED"}</li>
 * </ul></dd>
 * </dl></dd>
 * </dl></dd>
 * </dl></dd>
 * </dl>
 * </dd>
 * </dl></dd>
 * </dl>
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
        return state.getEntityModel();
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
        hash = 61 * hash + Objects.hashCode(state.getEntityModel());
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
            M model = state.getEntityModel();
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
        protected M getEntityModel() {
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
        protected M getEntityModel() {
            return source.getEntityModel();
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

        protected abstract M getEntityModel();

        protected abstract State copyOf(DbOperationType operation);

    }

}
