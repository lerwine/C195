package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.EntityModel;

/**
 * Base {@link ModelEvent} class for requests to edit or delete a {@link DataAccessObject} which may include a related {@link EntityModel}. The
 * {@link ModelEvent#getOperation()} method for this class will return {@link DbOperationType#NONE} for edit requests and
 * {@link DbOperationType#DB_DELETE} for delete requests.
 * <h3>Event Routing</h3>
 * <ul class="list-style:none">
 * <li>Fired on {@link scheduler.fx.ItemEditTableCell}
 * <ul>
 * <li>{@link scheduler.fx.ItemEditTableCell#onEditButtonAction(javafx.event.ActionEvent) ItemEditTableCell.onEditButtonAction(ActionEvent)}
 * <br>&rarr; {@code OperationRequestEvent} &#123; {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</li>
 * <li>{@link scheduler.fx.ItemEditTableCell#onDeleteButtonAction(javafx.event.ActionEvent) ItemEditTableCell.onDeleteButtonAction(ActionEvent)}
 * <br>&rarr; {@code OperationRequestEvent} &#123; {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</li>
 * </ul>
 * Handled by:
 * <ul class="list-style:none">
 * <li>{@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest(OperationRequestEvent)}
 * <ul class="list-style:none">
 * <li>Re-fired on {@link scheduler.fx.ItemEditTableCellFactory}
 * <br>Handled by:
 * <ul>
 * <li>{@link scheduler.fx.MainListingControl}</li>
 * <li>{@link scheduler.view.address.EditAddress} &larr; {@link CustomerOpRequestEvent}</li>
 * <li>{@link scheduler.view.city.EditCity} &larr; {@link AddressOpRequestEvent}</li>
 * <li>{@link scheduler.view.country.EditCountry} &larr; {@link CityOpRequestEvent}</li>
 * <li>{@link scheduler.view.customer.EditCustomer} &larr; {@link AppointmentOpRequestEvent}</li>
 * <li>{@link scheduler.view.user.EditUser} &larr; {@link AppointmentOpRequestEvent}</li>
 * </ul></li>
 * </ul></li>
 * </ul>
 * </li>
 * </ul>
 * <h3>Event Registration</h3>
 * <ul class="list-style:none">
 * <li>{@link javafx.event.Event} &rArr; {@link javafx.event.Event#ANY "EVENT"}
 * <ul class="list-style:none">
 * <li>{@link ModelEvent} &rArr; {@link #MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * <ul class="list-style:none">
 * <li><strong>{@code OperationRequestEvent}</strong> &rArr; {@link OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"}
 * <ul class="list-style:none">
 * <li>{@link AppointmentOpRequestEvent} &rArr; {@link AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AppointmentOpRequestEvent#EDIT_REQUEST "SCHEDULER_APPOINTMENT_EDIT_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</li>
 * <li>&rArr; {@link AppointmentOpRequestEvent#DELETE_REQUEST "SCHEDULER_APPOINTMENT_DELETE_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</li>
 * </ul></li>
 * <li>{@link CustomerOpRequestEvent} &rArr; {@link CustomerOpRequestEvent#CUSTOMER_OP_REQUEST "SCHEDULER_CUSTOMER_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CustomerOpRequestEvent#EDIT_REQUEST "SCHEDULER_CUSTOMER_EDIT_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</li>
 * <li>&rArr; {@link CustomerOpRequestEvent#DELETE_REQUEST "SCHEDULER_CUSTOMER_DELETE_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</li>
 * </ul></li>
 * <li>{@link AddressOpRequestEvent} &rArr; {@link AddressOpRequestEvent#ADDRESS_OP_REQUEST "SCHEDULER_ADDRESS_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link AddressOpRequestEvent#EDIT_REQUEST "SCHEDULER_ADDRESS_EDIT_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</li>
 * <li>&rArr; {@link AddressOpRequestEvent#DELETE_REQUEST "SCHEDULER_ADDRESS_DELETE_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</li>
 * </ul></li>
 * <li>{@link CityOpRequestEvent} &rArr; {@link CityOpRequestEvent#CITY_OP_REQUEST "SCHEDULER_CITY_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CityOpRequestEvent#EDIT_REQUEST "SCHEDULER_CITY_EDIT_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</li>
 * <li>&rArr; {@link CityOpRequestEvent#DELETE_REQUEST "SCHEDULER_CITY_DELETE_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</li>
 * </ul></li>
 * <li>{@link CountryOpRequestEvent} &rArr; {@link CountryOpRequestEvent#COUNTRY_OP_REQUEST "SCHEDULER_COUNTRY_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link CountryOpRequestEvent#EDIT_REQUEST "SCHEDULER_COUNTRY_EDIT_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</li>
 * <li>&rArr; {@link CountryOpRequestEvent#DELETE_REQUEST "SCHEDULER_COUNTRY_DELETE_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</li>
 * </ul></li>
 * <li>{@link UserOpRequestEvent} &rArr; {@link UserOpRequestEvent#USER_OP_REQUEST "SCHEDULER_USER_OP_REQUEST"}
 * <ul class="list-style:none">
 * <li>&rArr; {@link UserOpRequestEvent#EDIT_REQUEST "SCHEDULER_USER_EDIT_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#NONE} &#125;</li>
 * <li>&rArr; {@link UserOpRequestEvent#DELETE_REQUEST "SCHEDULER_USER_DELETE_REQUEST"} &#123;
 * {@link ModelEvent#getOperation() getOperation()} = {@link DbOperationType#DB_DELETE} &#125;</li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul></li>
 * </ul>
 *
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The type of {@link DataAccessObject}.
 * @param <M> The type of {@link EntityModel}.
 */
public abstract class OperationRequestEvent<D extends DataAccessObject, M extends EntityModel<D>> extends ModelEvent<D, M> {

    private static final long serialVersionUID = 6645421544057756121L;

    /**
     * Base {@link EventType} for all {@code OperationRequestEvent}s.
     */
    public static final EventType<OperationRequestEvent<? extends DataAccessObject, ? extends EntityModel<? extends DataAccessObject>>> OP_REQUEST_EVENT
            = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_OP_REQUEST_EVENT");

    protected OperationRequestEvent(ModelEvent<D, M> event, Object source, EventTarget target, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(event, source, target, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(ModelEvent<D, M> event, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(event, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(M target, Object source, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(target, source, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    public final boolean isEdit() {
        return getOperation() == DbOperationType.NONE;
    }
}
