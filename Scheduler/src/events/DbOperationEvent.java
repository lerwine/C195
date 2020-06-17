package events;

import java.util.Objects;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.LogHelper;

/**
 * Base class for {@link FxRecordModel} save and delete events.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <M> The {@link FxRecordModel} type.
 * @param <D> The {@link DataAccessObject} type.
 */
public abstract class DbOperationEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends Event {

    private static final long serialVersionUID = -6832461936768738020L;

    /**
     * Base {@link EventType} for all {@code DbOperationEvent}s.
     */
    public static final EventType<DbOperationEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> DB_OPERATION
            = new EventType<>(ANY, "SCHEDULER_DB_OPERATION");

    private final D dataAccessObject;
    private final DbOperationType operation;
    private final State state;

    /**
     * Creates a copy of an event with a new target, {@link EventType} and {@link DbOperationType}.
     *
     * @param copyFrom The {@code ModelItemEvent} to copy.
     * @param target The new target for the copied event.
     * @param type The new event type for the copied event.
     * @param operation The new {@link DbOperationType} for the copied event.
     */
    protected DbOperationEvent(DbOperationEvent<M, D> copyFrom, EventTarget target, EventType<? extends DbOperationEvent<M, D>> type, DbOperationType operation) {
        super(copyFrom.getSource(), target, type);
        state = new State(copyFrom.state.model);
        dataAccessObject = copyFrom.getDataAccessObject();
        this.operation = operation;
    }

    /**
     * Creates a copy of a {@code ModelItemEvent} with a new source and target.
     *
     * @param copyFrom The {@code ModelItemEvent} to copy.
     * @param source The new source for the copied event.
     * @param target The new target for the copied event.
     */
    protected DbOperationEvent(DbOperationEvent<M, D> copyFrom, Object source, EventTarget target) {
        super(source, target, copyFrom.getEventType());
        state = copyFrom.state;
        dataAccessObject = copyFrom.dataAccessObject;
        operation = copyFrom.operation;
    }

    /**
     * Creates a new {@code ModelItemEvent} for a {@link FxRecordModel} object.
     *
     * @param model The affected {@link FxRecordModel}.
     * @param source The event source which sent the event.
     * @param target The event target to associate with the event.
     * @param type The event type.
     * @param operation The {@link DbOperationType} associated with the event.
     * @param confirmed {@code true} if validation and/or conflict checking has already been confirmed; otherwise {@code false}.
     */
    protected DbOperationEvent(M model, Object source, EventTarget target, EventType<? extends DbOperationEvent<M, D>> type, DbOperationType operation, boolean confirmed) {
        super((null == source) ? model : source, (null == target) ? model.dataObject() : target, type);
        state = new State(model);
        dataAccessObject = model.dataObject();
        this.operation = operation;
    }

    /**
     * Creates a new {@code ModelItemEvent} for a {@link DataAccessObject}.
     *
     * @param source The event source which sent the event.
     * @param target The event target to associate with the event.
     * @param dao The affected {@link DataAccessObject}.
     * @param type The event type.
     * @param operation The {@link DbOperationType} associated with the event.
     * @param confirmed {@code true} if validation and/or conflict checking has already been confirmed; otherwise {@code false}.
     */
    protected DbOperationEvent(Object source, EventTarget target, D dao, EventType<? extends DbOperationEvent<M, D>> type, DbOperationType operation, boolean confirmed) {
        super((null == source) ? dao : source, target, type);
        state = new State(null);
        dataAccessObject = dao;
        this.operation = operation;
    }

    /**
     * Gets the underlying {@link DataAccessObject} associated with the {@code DbOperationEvent}.
     *
     * @return The underlying {@link DataAccessObject} associated with the {@code DbOperationEvent}.
     */
    public D getDataAccessObject() {
        return dataAccessObject;
    }

    public DbOperationType getOperation() {
        return operation;
    }

    public M getModel() {
        return state.model;
    }

    public void setModel(M model) {
        state.setModel(model);
    }

//    public DbConnector getDbConnector() {
//        return state.dbConnector;
//    }
//
//    public void setDbConnector(DbConnector dbConnector) {
//        state.setDbConnector(dbConnector);
//    }
    public String getSummaryTitle() {
        return state.summaryTitle;
    }

    public String getDetailMessage() {
        return state.detailMessage;
    }

    public Throwable getFault() {
        return state.fault;
    }

    public EventEvaluationStatus getStatus() {
        return state.status;
    }

    public void setSucceeded() {
        state.setStatus(EventEvaluationStatus.SUCCEEDED, null, null, null);
    }

    public void setCanceled() {
        state.setStatus(EventEvaluationStatus.CANCELED, null, null, null);
    }

    public void setFaulted(String title, String message, Throwable ex) {
        state.setStatus(EventEvaluationStatus.FAULTED, title, message, ex);
    }

    public void setFaulted(String title, String message) {
        setFaulted(title, message, null);
    }

    public void setInvalid(String title, String message) {
        state.setStatus(EventEvaluationStatus.INVALID, title, message, null);
    }

    public abstract FxRecordModel.ModelFactory<D, M, ? extends DbOperationEvent<M, D>> getModelFactory();

    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends DbOperationEvent<M, D>> getEventType() {
        return (EventType<? extends DbOperationEvent<M, D>>) super.getEventType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Event[").append(getEventType().getName())
                .append("] { operation=").append(operation.name())
                .append("; status=").append(state.status.name());
        String s = state.summaryTitle;
        if (!s.isEmpty()) {
            sb.append("; summaryTitle=").append(LogHelper.toLogText(s));
        }
        s = state.detailMessage;
        if (!s.isEmpty()) {
            sb.append("; detailMessage=").append(LogHelper.toLogText(s));
        }
        Throwable e = state.fault;
        if (null != e) {
            sb.append("; detailMessage=").append(e);
        }
        return sb.append(" }").toString();
    }

    private class State {

        private M model;
//        private DbConnector dbConnector;
        private String summaryTitle;
        private String detailMessage;
        private EventEvaluationStatus status;
        private Throwable fault;

        private State(M model) {
            this.model = model;
            summaryTitle = "";
            detailMessage = "";
            status = EventEvaluationStatus.EVALUATING;
            fault = null;
        }

        public synchronized void setModel(M model) {
            if (null != this.model) {
                throw new IllegalStateException();
            }
            if (model.dataObject() != dataAccessObject) {
                throw new IllegalArgumentException();
            }
            this.model = model;
        }

//        public synchronized void setDbConnector(DbConnector dbConnector) {
//            this.dbConnector = dbConnector;
//        }
        public synchronized void setStatus(EventEvaluationStatus status, String title, String message, Throwable ex) {
            if (status == EventEvaluationStatus.EVALUATING) {
                fault = ex;
                this.status = Objects.requireNonNull(status);
                summaryTitle = (null == title || title.trim().isEmpty()) ? "" : title;
                detailMessage = (null == message || message.trim().isEmpty()) ? "" : message;
            }
        }

    }
}
