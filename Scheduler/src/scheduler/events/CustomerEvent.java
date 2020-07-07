package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.dao.OperationFailureException;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.CustomerModel;

// FIXME: Discontinue use of CustomerEvent
/**
 * Base {@link ModelEvent} for appointment events.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #CUSTOMER_EVENT_TYPE "SCHEDULER_CUSTOMER_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr; {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link #CUSTOMER_EVENT_TYPE "SCHEDULER_CUSTOMER_OP_EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link CustomerSuccessEvent#BASE_EVENT_NAME "SCHEDULER_CUSTOMER_SUCCESS_EVENT"}</dt>
 * <dd>&rarr; {@link CustomerSuccessEvent}</dd>
 * <dt>(inherit) {@link CustomerFailedEvent#BASE_EVENT_NAME "SCHEDULER_CUSTOMER_FAILED_EVENT"}</dt>
 * <dd>&rarr; {@link CustomerFailedEvent}</dd>
 * </dl>
 * </dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@Deprecated
public abstract class CustomerEvent extends ModelEvent<CustomerDAO, CustomerModel> {

    private static final long serialVersionUID = -6549414287990595572L;

    /**
     * Base {@link EventType} for all {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> CUSTOMER_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_CUSTOMER_EVENT");

    /**
     * Base {@link EventType} for all change {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> CHANGE_EVENT_TYPE = new EventType<>(CUSTOMER_EVENT_TYPE, "SCHEDULER_CUSTOMER_CHANGE_EVENT");

    public static final boolean isSuccess(CustomerEvent event) {
        return event instanceof CustomerSuccessEvent;
    }

    public static final boolean isInvalid(CustomerEvent event) {
        return event instanceof CustomerFailedEvent && ((CustomerFailedEvent) event).getFailKind() == FailKind.INVALID;
    }

    public static final boolean isCanceled(CustomerEvent event) {
        return event instanceof CustomerFailedEvent && ((CustomerFailedEvent) event).getFailKind() == FailKind.CANCELED;
    }

    public static final boolean isFaulted(CustomerEvent event) {
        return event instanceof CustomerFailedEvent && ((CustomerFailedEvent) event).getFailKind() == FailKind.FAULT;
    }

    public static final CustomerEvent createInsertSuccessEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source) {
        return new CustomerSuccessEvent(target, source, CustomerSuccessEvent.INSERT_SUCCESS);
    }

    public static final CustomerEvent createUpdateSuccessEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source) {
        return new CustomerSuccessEvent(target, source, CustomerSuccessEvent.UPDATE_SUCCESS);
    }

    public static final CustomerEvent createDeleteSuccessEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source) {
        return new CustomerSuccessEvent(target, source, CustomerSuccessEvent.DELETE_SUCCESS);
    }

    public static final CustomerEvent createInsertInvalidEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, String message) {
        return new CustomerFailedEvent(target, message, null, source, CustomerFailedEvent.INSERT_INVALID);
    }

    public static final CustomerEvent createUpdateInvalidEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, String message) {
        return new CustomerFailedEvent(target, message, null, source, CustomerFailedEvent.UPDATE_INVALID);
    }

    public static final CustomerEvent createDeleteInvalidEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, String message) {
        return new CustomerFailedEvent(target, message, null, source, CustomerFailedEvent.DELETE_INVALID);
    }

    public static final CustomerEvent createInsertFaultedEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CustomerFailedEvent(target, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_FAULTED);
        }
        return new CustomerFailedEvent(target, null, ex, source, CustomerFailedEvent.INSERT_INVALID);
    }

    public static final CustomerEvent createUpdateFaultedEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CustomerFailedEvent(target, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_FAULTED);
        }
        return new CustomerFailedEvent(target, null, ex, source, CustomerFailedEvent.UPDATE_FAULTED);
    }

    public static final CustomerEvent createDeleteFaultedEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CustomerFailedEvent(target, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_FAULTED);
        }
        return new CustomerFailedEvent(target, null, ex, source, CustomerFailedEvent.DELETE_FAULTED);
    }

    public static final CustomerEvent createInsertCanceledEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CustomerFailedEvent(target, null, null, source, CustomerFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CustomerFailedEvent(target, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_CANCELED);
        }
        return new CustomerFailedEvent(target, ex.getMessage(), ex, source, CustomerFailedEvent.INSERT_CANCELED);
    }

    public static final CustomerEvent createUpdateCanceledEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CustomerFailedEvent(target, null, null, source, CustomerFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CustomerFailedEvent(target, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_CANCELED);
        }
        return new CustomerFailedEvent(target, ex.getMessage(), ex, source, CustomerFailedEvent.UPDATE_CANCELED);
    }

    public static final CustomerEvent createDeleteCanceledEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CustomerFailedEvent(target, null, null, source, CustomerFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CustomerFailedEvent(target, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_CANCELED);
        }
        return new CustomerFailedEvent(target, ex.getMessage(), ex, source, CustomerFailedEvent.DELETE_CANCELED);
    }

    public static final CustomerEvent createInsertCanceledEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CustomerEvent createUpdateCanceledEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source) {
        return createUpdateCanceledEvent(target, source, null);
    }

    public static final CustomerEvent createDeleteCanceledEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source) {
        return createDeleteCanceledEvent(target, source, null);
    }

    public static CustomerEvent createInsertInvalidEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, AddressFailedEvent event) {
        return new CustomerFailedEvent(target, source, CustomerFailedEvent.INSERT_INVALID, event);
    }

    public static CustomerEvent createUpdateInvalidEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, AddressFailedEvent event) {
        return new CustomerFailedEvent(target, source, CustomerFailedEvent.UPDATE_INVALID, event);
    }

    protected CustomerEvent(CustomerEvent event, Object source, EventTarget target, EventType<? extends CustomerEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected CustomerEvent(CustomerEvent event, EventType<? extends CustomerEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected CustomerEvent(RecordModelContext<CustomerDAO, CustomerModel> target, Object source, EventType<? extends CustomerEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
