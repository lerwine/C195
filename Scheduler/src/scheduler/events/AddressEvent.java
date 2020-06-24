package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.dao.IFxModelOptional;
import scheduler.dao.OperationFailureException;
import scheduler.dao.ValidationFailureException;
import scheduler.model.ui.AddressModel;

public abstract class AddressEvent extends ModelEvent<AddressDAO, AddressModel> {

    private static final long serialVersionUID = -3650516330020602507L;

    /**
     * Base {@link EventType} for all {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> ADDRESS_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_ADDRESS_EVENT");

    /**
     * Base {@link EventType} for all operational {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> OP_EVENT_TYPE = new EventType<>(ADDRESS_EVENT_TYPE, "SCHEDULER_ADDRESS_OP_EVENT");

    public static final boolean isSuccess(AddressEvent event) {
        return event instanceof AddressSuccessEvent;
    }

    public static final boolean isInvalid(AddressEvent event) {
        return event instanceof AddressFailedEvent && AddressFailedEvent.isInvalidEvent((AddressFailedEvent) event);
    }

    public static final boolean isCanceled(AddressEvent event) {
        return event instanceof AddressFailedEvent && AddressFailedEvent.isCanceledEvent((AddressFailedEvent) event);
    }

    public static final boolean isFaulted(AddressEvent event) {
        return event instanceof AddressFailedEvent && AddressFailedEvent.isFaultedEvent((AddressFailedEvent) event);
    }

    public static final AddressEvent createInsertSuccessEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            return new AddressSuccessEvent(target.getDataAccessObject(), source, AddressSuccessEvent.INSERT_SUCCESS);
        }
        return new AddressSuccessEvent(model, source, AddressSuccessEvent.INSERT_SUCCESS);
    }

    public static final AddressEvent createUpdateSuccessEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            return new AddressSuccessEvent(target.getDataAccessObject(), source, AddressSuccessEvent.UPDATE_SUCCESS);
        }
        return new AddressSuccessEvent(model, source, AddressSuccessEvent.UPDATE_SUCCESS);
    }

    public static final AddressEvent createDeleteSuccessEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            return new AddressSuccessEvent(target.getDataAccessObject(), source, AddressSuccessEvent.DELETE_SUCCESS);
        }
        return new AddressSuccessEvent(model, source, AddressSuccessEvent.DELETE_SUCCESS);
    }

    public static final AddressEvent createInsertInvalidEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, ValidationFailureException ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_INVALID);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), null, null, source, AddressFailedEvent.INSERT_INVALID);
        }
        if (null != ex) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_INVALID);
        }
        return new AddressFailedEvent(model, null, null, source, AddressFailedEvent.INSERT_INVALID);
    }

    public static final AddressEvent createUpdateInvalidEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, ValidationFailureException ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_INVALID);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), null, null, source, AddressFailedEvent.UPDATE_INVALID);
        }
        if (null != ex) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_INVALID);
        }
        return new AddressFailedEvent(model, null, null, source, AddressFailedEvent.UPDATE_INVALID);
    }

    public static final AddressEvent createDeleteInvalidEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, ValidationFailureException ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_INVALID);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), null, null, source, AddressFailedEvent.DELETE_INVALID);
        }
        if (null != ex) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_INVALID);
        }
        return new AddressFailedEvent(model, null, null, source, AddressFailedEvent.DELETE_INVALID);
    }

    public static final AddressEvent createInsertFaultedEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, Throwable ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_FAULTED);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), null, ex, source, AddressFailedEvent.INSERT_INVALID);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_FAULTED);
        }
        return new AddressFailedEvent(model, null, ex, source, AddressFailedEvent.INSERT_INVALID);
    }

    public static final AddressEvent createUpdateFaultedEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, Throwable ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_FAULTED);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), null, ex, source, AddressFailedEvent.UPDATE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_FAULTED);
        }
        return new AddressFailedEvent(model, null, ex, source, AddressFailedEvent.UPDATE_FAULTED);
    }

    public static final AddressEvent createDeleteFaultedEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, Throwable ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_FAULTED);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), null, ex, source, AddressFailedEvent.DELETE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_FAULTED);
        }
        return new AddressFailedEvent(model, null, ex, source, AddressFailedEvent.DELETE_FAULTED);
    }

    public static final AddressEvent createInsertCanceledEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, InterruptedException ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new AddressFailedEvent(target.getDataAccessObject(), null, null, source, AddressFailedEvent.INSERT_CANCELED);
            }
            if (null != ex.getCause()) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_CANCELED);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, AddressFailedEvent.INSERT_CANCELED);
        }
        if (null == ex) {
            return new AddressFailedEvent(model, null, null, source, AddressFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.INSERT_CANCELED);
        }
        return new AddressFailedEvent(model, ex.getMessage(), ex, source, AddressFailedEvent.INSERT_CANCELED);
    }

    public static final AddressEvent createUpdateCanceledEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, InterruptedException ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new AddressFailedEvent(target.getDataAccessObject(), null, null, source, AddressFailedEvent.UPDATE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_CANCELED);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, AddressFailedEvent.UPDATE_CANCELED);
        }
        if (null == ex) {
            return new AddressFailedEvent(model, null, null, source, AddressFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.UPDATE_CANCELED);
        }
        return new AddressFailedEvent(model, ex.getMessage(), ex, source, AddressFailedEvent.UPDATE_CANCELED);
    }

    public static final AddressEvent createDeleteCanceledEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source, InterruptedException ex) {
        AddressModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new AddressFailedEvent(target.getDataAccessObject(), null, null, source, AddressFailedEvent.DELETE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_CANCELED);
            }
            return new AddressFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, AddressFailedEvent.DELETE_CANCELED);
        }
        if (null == ex) {
            return new AddressFailedEvent(model, null, null, source, AddressFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AddressFailedEvent(model, ex.getMessage(), ex.getCause(), source, AddressFailedEvent.DELETE_CANCELED);
        }
        return new AddressFailedEvent(model, ex.getMessage(), ex, source, AddressFailedEvent.DELETE_CANCELED);
    }

    public static final AddressEvent createInsertCanceledEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final AddressEvent createUpdateCanceledEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final AddressEvent createDeleteCanceledEvent(IFxModelOptional<AddressDAO, AddressModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    protected AddressEvent(AddressEvent event, Object source, EventTarget target, EventType<? extends AddressEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected AddressEvent(AddressEvent event, EventType<? extends AddressEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected AddressEvent(AddressModel fxRecordModel, Object source, EventType<? extends AddressEvent> eventType, DbOperationType operation) {
        super(fxRecordModel, source, eventType, operation);
    }

    protected AddressEvent(AddressDAO dao, Object source, EventType<? extends AddressEvent> eventType, DbOperationType operation) {
        super(dao, source, eventType, operation);
    }

}
