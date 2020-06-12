package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;

/**
 * Base class for {@link FxRecordModel} save and delete events.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The {@link FxRecordModel} type.
 * @param <U> The {@link DataAccessObject} type.
 */
public abstract class ModelItemEvent<T extends FxRecordModel<U>, U extends DataAccessObject> extends Event {

    private static final long serialVersionUID = -6832461936768738020L;

    public static final EventType<ModelItemEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> MODEL_ITEM_EVENT
            = new EventType<>(ANY, "MODEL_ITEM_EVENT");

    private final U dataAccessObject;
    private final ActivityType activity;
    private final boolean confirmed;
    private ModelItemEvent<T, U> previousCopy;
    private ModelItemEvent<T, U> nextCopy;
    private State state;

    protected ModelItemEvent(ModelItemEvent<T, U> event, EventTarget target, EventType<? extends ModelItemEvent<T, U>> type,
            ActivityType activity, boolean confirmed) {
        super(event.getSource(), target, type);
        state = new State(event.getState().getModel());
        dataAccessObject = event.getDataAccessObject();
        this.activity = activity;
        this.confirmed = confirmed;
    }
    
    /**
     * Creates a copy of a {@code ModelItemEvent} with a new source and target.
     *
     * @param copyFrom The {@code ModelItemEvent} to copy.
     * @param source The new source for the copied event.
     * @param target The new target for the copied event.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    protected ModelItemEvent(ModelItemEvent<T, U> copyFrom, Object source, EventTarget target) {
        super(source, target, copyFrom.getEventType());
        synchronized (copyFrom) {
            if (null != (nextCopy = (previousCopy = copyFrom).nextCopy)) {
                nextCopy.previousCopy = this;
            }
            copyFrom.nextCopy = this;
        }
        state = copyFrom.state;
        dataAccessObject = copyFrom.dataAccessObject;
        activity = copyFrom.activity;
        confirmed = copyFrom.confirmed;
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
    protected ModelItemEvent(T model, Object source, EventTarget target, EventType<? extends ModelItemEvent<T, U>> type, ActivityType activity,
            boolean confirmed) {
        super((null == source) ? model : source, (null == target) ? model.dataObject() : target, type);
        state = new State(model);
        dataAccessObject = model.dataObject();
        this.activity = activity;
        this.confirmed = confirmed;
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
    protected ModelItemEvent(Object source, EventTarget target, U dao, EventType<? extends ModelItemEvent<T, U>> type, ActivityType activity, boolean confirmed) {
        super((null == source) ? dao : source, target, type);
        state = new State(null);
        dataAccessObject = dao;
        this.activity = activity;
        this.confirmed = confirmed;
    }

    public State getState() {
        return state;
    }

    public void setUnsuccessful(String title, String message) {
        state.succeeded = false;
        state.summaryTitle = (null == title || title.trim().isEmpty()) ? "" : title;
        state.detailMessage = (null == message || message.trim().isEmpty()) ? "" : message;
    }
        
    /**
     * Gets the underlying {@link DataAccessObject} associated with the {@code ModelItemEvent}.
     *
     * @return The underlying {@link DataAccessObject} associated with the {@code ModelItemEvent}.
     */
    public U getDataAccessObject() {
        return dataAccessObject;
    }

    public ActivityType getActivity() {
        return activity;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public abstract FxRecordModel.ModelFactory<U, T> getModelFactory();

    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends ModelItemEvent<T, U>> getEventType() {
        return (EventType<? extends ModelItemEvent<T, U>>) super.getEventType();
    }

    public class State {
        private T model;
        private DbConnector dbConnector;
        private String summaryTitle;
        private String detailMessage;
        private boolean succeeded;

        private State(T model) {
            this.model = model;
            summaryTitle = "";
            detailMessage = "";
            succeeded = false;
        }

        public T getModel() {
            return model;
        }

        public void setModel(T model) {
            this.model = model;
        }

        public DbConnector getDbConnector() {
            return dbConnector;
        }

        public void setDbConnector(DbConnector dbConnector) {
            this.dbConnector = dbConnector;
        }

        public String getSummaryTitle() {
            return summaryTitle;
        }

        public String getDetailMessage() {
            return detailMessage;
        }
        
        public boolean isSucceeded() {
            return succeeded;
        }

        public void setSucceeded(boolean succeeded) {
            this.succeeded = succeeded;
        }


    }
}
