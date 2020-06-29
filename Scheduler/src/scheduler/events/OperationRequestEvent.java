package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.FxRecordModel;

/**
 * Base {@link ModelEvent} class for requests to edit or delete a {@link DataAccessObject} which may include a related {@link FxRecordModel}. The {@link ModelEvent#getOperation()}
 * method for this class will return {@link DbOperationType#NONE} for edit requests and {@link DbOperationType#DB_DELETE} for delete requests.
 * <dl>
 * <dt>{@link #OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr; {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>{@link scheduler.fx.ItemEditTableCell#onEditButtonAction(javafx.event.ActionEvent) ItemEditTableCell.onEditButtonAction(ActionEvent)} &#x21DD; {@code OperationRequestEvent}
 * &#123; {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</dt>
 * <dd>
 * <dl>
 * <dt>&#x26A1; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest(OperationRequestEvent)}</dt>
 * <dd>
 * <dt>&#x21B3; {@link AppointmentOpRequestEvent#EDIT_REQUEST "SCHEDULER_APPOINTMENT_EDIT_REQUEST"} &lArr;
 * {@link AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.customer.EditCustomer}</dd>
 * <dt>&#x21B3; {@link AddressOpRequestEvent#EDIT_REQUEST "SCHEDULER_ADDRESS_EDIT_REQUEST"} &lArr;
 * {@link AddressOpRequestEvent#ADDRESS_OP_REQUEST "SCHEDULER_ADDRESS_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.address.EditAddress}</dd>
 * <dt>&#x21B3; {@link CityOpRequestEvent#EDIT_REQUEST "SCHEDULER_CITY_EDIT_REQUEST"} &lArr; {@link CityOpRequestEvent#CITY_OP_REQUEST "SCHEDULER_CITY_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.city.EditCity}</dd>
 * <dt>&#x21B3; {@link CountryOpRequestEvent#EDIT_REQUEST "SCHEDULER_COUNTRY_EDIT_REQUEST"} &lArr;
 * {@link CountryOpRequestEvent#COUNTRY_OP_REQUEST "SCHEDULER_COUNTRY_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.country.EditCountry}</dd>
 * <dt>&#x21B3; {@link UserOpRequestEvent#EDIT_REQUEST "SCHEDULER_USER_EDIT_REQUEST"} &lArr; {@link UserOpRequestEvent#USER_OP_REQUEST "SCHEDULER_USER_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.user.EditUser}</dd>
 * </dl>
 * &rArr; {@link scheduler.fx.MainListingControl}
 * </dd>
 * </dl>
 * </dd>
 * <dt>{@link scheduler.fx.ItemEditTableCell#onDeleteButtonAction(javafx.event.ActionEvent) ItemEditTableCell.onDeleteButtonAction(ActionEvent)} &#x21DD;
 * {@code OperationRequestEvent} &#123; {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</dt>
 * <dd>
 * <dl>
 * <dt>&#x26A1; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest(OperationRequestEvent)}</dt>
 * <dd>
 * <dl>
 * <dt>&#x26A1; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest(OperationRequestEvent)}</dt>
 * <dd>
 * <dt>&#x21B3; {@link AppointmentOpRequestEvent#DELETE_REQUEST "SCHEDULER_APPOINTMENT_DELETE_REQUEST"} &lArr;
 * {@link AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.customer.EditCustomer}</dd>
 * <dt>&#x21B3; {@link AddressOpRequestEvent#DELETE_REQUEST "SCHEDULER_ADDRESS_DELETE_REQUEST"} &lArr;
 * {@link AddressOpRequestEvent#ADDRESS_OP_REQUEST "SCHEDULER_ADDRESS_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.address.EditAddress}</dd>
 * <dt>&#x21B3; {@link CityOpRequestEvent#DELETE_REQUEST "SCHEDULER_CITY_DELETE_REQUEST"} &lArr; {@link CityOpRequestEvent#CITY_OP_REQUEST "SCHEDULER_CITY_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.city.EditCity}</dd>
 * <dt>&#x21B3; {@link CountryOpRequestEvent#DELETE_REQUEST "SCHEDULER_COUNTRY_DELETE_REQUEST"} &lArr;
 * {@link CountryOpRequestEvent#COUNTRY_OP_REQUEST "SCHEDULER_COUNTRY_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.country.EditCountry}</dd>
 * <dt>&#x21B3; {@link UserOpRequestEvent#DELETE_REQUEST "SCHEDULER_USER_DELETE_REQUEST"} &lArr; {@link UserOpRequestEvent#USER_OP_REQUEST "SCHEDULER_USER_OP_REQUEST"}</dt>
 * <dd>&rArr; {@link scheduler.view.user.EditUser}</dd>
 * </dl>
 * &rArr; {@link scheduler.fx.MainListingControl}
 * </dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The type of {@link DataAccessObject}.
 * @param <M> The type of {@link FxRecordModel}.
 */
public abstract class OperationRequestEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends ModelEvent<D, M> {

    private static final long serialVersionUID = 6645421544057756121L;

    /**
     * Base {@link EventType} for all {@code OperationRequestEvent}s.
     */
    public static final EventType<OperationRequestEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>> OP_REQUEST_EVENT
            = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_OP_REQUEST_EVENT");

    protected OperationRequestEvent(ModelEvent<D, M> event, Object source, EventTarget target, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(event, source, target, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(ModelEvent<D, M> event, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(event, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(RecordModelContext<D, M> target, Object source, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(target, source, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(M target, Object source, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(target, source, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    public final boolean isEdit() {
        return getOperation() == DbOperationType.NONE;
    }
}
