package scheduler.view.event;

import java.util.Objects;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * Base class for {@link FxRecordModel} save and delete events.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <M> The {@link FxRecordModel} type.
 * @param <D> The {@link DataAccessObject} type.
 */
public abstract class ModelItemEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends Event {

    private static final long serialVersionUID = -6832461936768738020L;

    public static final EventType<ModelItemEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> MODEL_ITEM_EVENT
            = new EventType<>(ANY, "MODEL_ITEM_EVENT");

    private final D dataAccessObject;
    private final ActivityType activity;
    private final State state;

    /**
     * Creates a copy of an event with a new target, {@link EventType} and {@link ActivityType}.
     *
     * @param copyFrom The {@code ModelItemEvent} to copy.
     * @param target The new target for the copied event.
     * @param type The new event type for the copied event.
     * @param activity The new {@link ActivityType} for the copied event.
     */
    protected ModelItemEvent(ModelItemEvent<M, D> copyFrom, EventTarget target, EventType<? extends ModelItemEvent<M, D>> type,
            ActivityType activity) {
        super(copyFrom.getSource(), target, type);
        state = new State(copyFrom.state.model);
        dataAccessObject = copyFrom.getDataAccessObject();
        this.activity = activity;
    }

    /**
     * Creates a copy of a {@code ModelItemEvent} with a new source and target.
     *
     * @param copyFrom The {@code ModelItemEvent} to copy.
     * @param source The new source for the copied event.
     * @param target The new target for the copied event.
     */
    protected ModelItemEvent(ModelItemEvent<M, D> copyFrom, Object source, EventTarget target) {
        super(source, target, copyFrom.getEventType());
        state = copyFrom.state;
        dataAccessObject = copyFrom.dataAccessObject;
        activity = copyFrom.activity;
    }

    /**
     * Creates a new {@code ModelItemEvent} for a {@link FxRecordModel} object.
     *
     * @param model The affected {@link FxRecordModel}.
     * @param source The event source which sent the event.
     * @param target The event target to associate with the event.
     * @param type The event type.
     * @param activity The activity associated with the event.
     * @param confirmed {@code true} if validation and/or conflict checking has already been confirmed; otherwise {@code false}.
     */
    protected ModelItemEvent(M model, Object source, EventTarget target, EventType<? extends ModelItemEvent<M, D>> type, ActivityType activity,
            boolean confirmed) {
        super((null == source) ? model : source, (null == target) ? model.dataObject() : target, type);
        state = new State(model);
        dataAccessObject = model.dataObject();
        this.activity = activity;
    }

    /**
     * Creates a new {@code ModelItemEvent} for a {@link DataAccessObject}.
     *
     * @param source The event source which sent the event.
     * @param target The event target to associate with the event.
     * @param dao The affected {@link DataAccessObject}.
     * @param type The event type.
     * @param activity The activity associated with the event.
     * @param confirmed {@code true} if validation and/or conflict checking has already been confirmed; otherwise {@code false}.
     */
    protected ModelItemEvent(Object source, EventTarget target, D dao, EventType<? extends ModelItemEvent<M, D>> type, ActivityType activity, boolean confirmed) {
        super((null == source) ? dao : source, target, type);
        state = new State(null);
        dataAccessObject = dao;
        this.activity = activity;
    }

    /**
     * Gets the underlying {@link DataAccessObject} associated with the {@code ModelItemEvent}.
     *
     * @return The underlying {@link DataAccessObject} associated with the {@code ModelItemEvent}.
     */
    public D getDataAccessObject() {
        return dataAccessObject;
    }

    public ActivityType getActivity() {
        return activity;
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

    public EventEvaluationStatus getStatus() {
        return state.status;
    }

    public void setSucceeded() {
        state.setStatus(EventEvaluationStatus.SUCCEEDED, null, null);
    }

    public void setCanceled() {
        state.setStatus(EventEvaluationStatus.CANCELED, null, null);
    }

    public void setFaulted(String title, String message) {
        state.setStatus(EventEvaluationStatus.FAULTED, title, message);
    }

    public void setInvalid(String title, String message) {
        state.setStatus(EventEvaluationStatus.INVALID, title, message);
    }

    public abstract <E extends ModelItemEvent<M, D>> FxRecordModel.ModelFactory<D, M, E> getModelFactory();

    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends ModelItemEvent<M, D>> getEventType() {
        return (EventType<? extends ModelItemEvent<M, D>>) super.getEventType();
    }

    private class State {

        private M model;
//        private DbConnector dbConnector;
        private String summaryTitle;
        private String detailMessage;
        private EventEvaluationStatus status;

        private State(M model) {
            this.model = model;
            summaryTitle = "";
            detailMessage = "";
            status = EventEvaluationStatus.EVALUATING;
        }

        public synchronized void setModel(M model) {
            if (null != model) {
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
        public synchronized void setStatus(EventEvaluationStatus status, String title, String message) {
            if (status == EventEvaluationStatus.EVALUATING) {
                this.status = Objects.requireNonNull(status);
                summaryTitle = (null == title || title.trim().isEmpty()) ? "" : title;
                detailMessage = (null == message || message.trim().isEmpty()) ? "" : message;
            }
        }

    }
}
