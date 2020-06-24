package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.dao.IFxModelOptional;
import scheduler.dao.OperationFailureException;
import scheduler.dao.ValidationFailureException;
import scheduler.model.ui.CustomerModel;

public abstract class CustomerEvent extends ModelEvent<CustomerDAO, CustomerModel> {

    private static final long serialVersionUID = -6549414287990595572L;

    /**
     * Base {@link EventType} for all {@code AppointmentEvent}s.
     */
    public static final EventType<CustomerEvent> CUSTOMER_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_CUSTOMER_EVENT");

    /**
     * Base {@link EventType} for all operational {@code AppointmentEvent}s.
     */
    public static final EventType<CustomerEvent> OP_EVENT_TYPE = new EventType<>(CUSTOMER_EVENT_TYPE, "SCHEDULER_CUSTOMER_OP_EVENT");

    public static final boolean isSuccess(CustomerEvent event) {
        return event instanceof CustomerSuccessEvent;
    }

    public static final boolean isInvalid(CustomerEvent event) {
        return event instanceof CustomerFailedEvent && CustomerFailedEvent.isInvalidEvent((CustomerFailedEvent) event);
    }

    public static final boolean isCanceled(CustomerEvent event) {
        return event instanceof CustomerFailedEvent && CustomerFailedEvent.isCanceledEvent((CustomerFailedEvent) event);
    }

    public static final boolean isFaulted(CustomerEvent event) {
        return event instanceof CustomerFailedEvent && CustomerFailedEvent.isFaultedEvent((CustomerFailedEvent) event);
    }

    public static final CustomerEvent createInsertSuccessEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            return new CustomerSuccessEvent(target.getDataAccessObject(), source, CustomerSuccessEvent.INSERT_SUCCESS);
        }
        return new CustomerSuccessEvent(model, source, CustomerSuccessEvent.INSERT_SUCCESS);
    }

    public static final CustomerEvent createUpdateSuccessEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            return new CustomerSuccessEvent(target.getDataAccessObject(), source, CustomerSuccessEvent.UPDATE_SUCCESS);
        }
        return new CustomerSuccessEvent(model, source, CustomerSuccessEvent.UPDATE_SUCCESS);
    }

    public static final CustomerEvent createDeleteSuccessEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            return new CustomerSuccessEvent(target.getDataAccessObject(), source, CustomerSuccessEvent.DELETE_SUCCESS);
        }
        return new CustomerSuccessEvent(model, source, CustomerSuccessEvent.DELETE_SUCCESS);
    }

    public static final CustomerEvent createInsertInvalidEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, ValidationFailureException ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_INVALID);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), null, null, source, CustomerFailedEvent.INSERT_INVALID);
        }
        if (null != ex) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_INVALID);
        }
        return new CustomerFailedEvent(model, null, null, source, CustomerFailedEvent.INSERT_INVALID);
    }

    public static final CustomerEvent createUpdateInvalidEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, ValidationFailureException ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_INVALID);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), null, null, source, CustomerFailedEvent.UPDATE_INVALID);
        }
        if (null != ex) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_INVALID);
        }
        return new CustomerFailedEvent(model, null, null, source, CustomerFailedEvent.UPDATE_INVALID);
    }

    public static final CustomerEvent createDeleteInvalidEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, ValidationFailureException ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_INVALID);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), null, null, source, CustomerFailedEvent.DELETE_INVALID);
        }
        if (null != ex) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_INVALID);
        }
        return new CustomerFailedEvent(model, null, null, source, CustomerFailedEvent.DELETE_INVALID);
    }

    public static final CustomerEvent createInsertFaultedEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, Throwable ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_FAULTED);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), null, ex, source, CustomerFailedEvent.INSERT_INVALID);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_FAULTED);
        }
        return new CustomerFailedEvent(model, null, ex, source, CustomerFailedEvent.INSERT_INVALID);
    }

    public static final CustomerEvent createUpdateFaultedEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, Throwable ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_FAULTED);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), null, ex, source, CustomerFailedEvent.UPDATE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_FAULTED);
        }
        return new CustomerFailedEvent(model, null, ex, source, CustomerFailedEvent.UPDATE_FAULTED);
    }

    public static final CustomerEvent createDeleteFaultedEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, Throwable ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_FAULTED);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), null, ex, source, CustomerFailedEvent.DELETE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_FAULTED);
        }
        return new CustomerFailedEvent(model, null, ex, source, CustomerFailedEvent.DELETE_FAULTED);
    }

    public static final CustomerEvent createInsertCanceledEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, InterruptedException ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CustomerFailedEvent(target.getDataAccessObject(), null, null, source, CustomerFailedEvent.INSERT_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_CANCELED);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CustomerFailedEvent.INSERT_CANCELED);
        }
        if (null == ex) {
            return new CustomerFailedEvent(model, null, null, source, CustomerFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.INSERT_CANCELED);
        }
        return new CustomerFailedEvent(model, ex.getMessage(), ex, source, CustomerFailedEvent.INSERT_CANCELED);
    }

    public static final CustomerEvent createUpdateCanceledEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, InterruptedException ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CustomerFailedEvent(target.getDataAccessObject(), null, null, source, CustomerFailedEvent.UPDATE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_CANCELED);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CustomerFailedEvent.UPDATE_CANCELED);
        }
        if (null == ex) {
            return new CustomerFailedEvent(model, null, null, source, CustomerFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.UPDATE_CANCELED);
        }
        return new CustomerFailedEvent(model, ex.getMessage(), ex, source, CustomerFailedEvent.UPDATE_CANCELED);
    }

    public static final CustomerEvent createDeleteCanceledEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source, InterruptedException ex) {
        CustomerModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CustomerFailedEvent(target.getDataAccessObject(), null, null, source, CustomerFailedEvent.DELETE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_CANCELED);
            }
            return new CustomerFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CustomerFailedEvent.DELETE_CANCELED);
        }
        if (null == ex) {
            return new CustomerFailedEvent(model, null, null, source, CustomerFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CustomerFailedEvent(model, ex.getMessage(), ex.getCause(), source, CustomerFailedEvent.DELETE_CANCELED);
        }
        return new CustomerFailedEvent(model, ex.getMessage(), ex, source, CustomerFailedEvent.DELETE_CANCELED);
    }

    public static final CustomerEvent createInsertCanceledEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CustomerEvent createUpdateCanceledEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CustomerEvent createDeleteCanceledEvent(IFxModelOptional<CustomerDAO, CustomerModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    protected CustomerEvent(CustomerEvent event, Object source, EventTarget target, EventType<? extends CustomerEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected CustomerEvent(CustomerEvent event, EventType<? extends CustomerEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected CustomerEvent(CustomerModel target, Object source, EventType<? extends CustomerEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

    protected CustomerEvent(CustomerDAO target, Object source, EventType<? extends CustomerEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
