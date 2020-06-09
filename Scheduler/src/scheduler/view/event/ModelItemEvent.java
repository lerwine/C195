package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ModelHelper;
import scheduler.model.ui.FxRecordModel;

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
    private T model;
    private boolean handled;
    private ModelItemEvent<T, U> previousCopy;
    private ModelItemEvent<T, U> nextCopy;

    @SuppressWarnings("LeakingThisInConstructor")
    protected ModelItemEvent(ModelItemEvent<T, U> copyFrom, Object source, EventTarget target) {
        super(source, target, copyFrom.getEventType());
        synchronized (copyFrom) {
            if (null != (nextCopy = (previousCopy = copyFrom).nextCopy)) {
                nextCopy.previousCopy = this;
            }
            copyFrom.nextCopy = this;
        }
        this.dataAccessObject = copyFrom.dataAccessObject;
    }

    protected ModelItemEvent(T model, Object source, EventTarget target, EventType<? extends ModelItemEvent<T, U>> type) {
        super((null == source) ? model : source, (null == target) ? model.dataObject() : target, type);
        this.dataAccessObject = (this.model = model).dataObject();
        this.handled = false;
    }

    protected ModelItemEvent(Object source, U target, EventType<? extends ModelItemEvent<T, U>> type) {
        super((null == source) ? target : source, target, type);
        dataAccessObject = target;
        this.handled = false;
    }

    /**
     * Gets the underlying {@link DataAccessObject} associated with the {@code ModelItemEvent}.
     *
     * @return The underlying {@link DataAccessObject} associated with the {@code ModelItemEvent}.
     */
    public U getDataAccessObject() {
        return dataAccessObject;
    }

    /**
     * Gets the {@link FxRecordModel} for the {@code ModelItemEvent}.
     *
     * @return The {@link FxRecordModel} for the {@code ModelItemEvent}.
     */
    public T getModel() {
        return (null != previousCopy) ? previousCopy.getModel() : model;
    }

    public abstract FxRecordModel.ModelFactory<U, T> getModelFactory();

    /**
     * Sets the {@link FxRecordModel} for the {@code ModelItemEvent}. This can only be set once, and may only be set if only the
     * {@link DataAccessObject} was provided in the constructor.
     *
     * @param model The {@link FxRecordModel} for the {@code ModelItemEvent}.
     * @throws IllegalStateException if the {@link #model} has already been set.
     * @throws IllegalArgumentException if the {@link FxRecordModel#dataObject} of the {@code model} does not represent the same data record as the
     * current {@link #dataAccessObject}.
     */
    public synchronized void setModel(T model) {
        if (null != previousCopy) {
            previousCopy.setModel(model);
        } else {
            if (null != this.model) {
                throw new IllegalStateException();
            }
            if (!ModelHelper.areSameRecord(model.dataObject(), dataAccessObject)) {
                throw new IllegalArgumentException();
            }
            this.model = model;
        }
    }

    /**
     * Gets a value that indicates whether the represented save or delete operation was handled.
     *
     * @return {@code true} if the represented save or delete operation was handled; otherwise {@code false}.
     */
    public boolean isHandled() {
        return (null != previousCopy) ? previousCopy.isHandled() : handled;
    }

    /**
     * Specifies whether the {@code ModelItemEvent} was already handled.
     *
     * @param value {@code true} to mark the {@code ModelItemEvent} as being handled; otherwise {@code false} to mark it as being un-handled.
     */
    public synchronized void setHandled(boolean value) {
        if (null == previousCopy) {
            handled = value;
        } else {
            previousCopy.setHandled(value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends ModelItemEvent<T, U>> getEventType() {
        return (EventType<? extends ModelItemEvent<T, U>>) super.getEventType();
    }

    public abstract boolean isDeleteRequest();

}
