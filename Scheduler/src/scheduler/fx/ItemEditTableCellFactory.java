package scheduler.fx;

import com.sun.javafx.event.EventHandlerManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;
import scheduler.events.DbOperationType;
import scheduler.events.ModelEvent;
import scheduler.util.LogHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The target item type.
 * @param <E> The event type.
 */
public abstract class ItemEditTableCellFactory<T extends FxRecordModel<? extends DataAccessObject>, E extends ModelEvent<? extends DataAccessObject, T>>
        implements Callback<TableColumn<T, T>, TableCell<T, T>>, EventTarget {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ItemEditTableCellFactory.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ItemEditTableCellFactory.class.getName());

    private final EventHandlerManager eventHandlerManager;
    private final ObjectProperty<EventHandler<E>> onItemActionRequest;

    public ItemEditTableCellFactory() {
        eventHandlerManager = new EventHandlerManager(this);
        onItemActionRequest = new SimpleObjectProperty<>();
        onItemActionRequest.addListener((observable, oldValue, newValue) -> {
            FxRecordModel.ModelFactory<? extends DataAccessObject, T, E> factory = getFactory();
            if (null != oldValue) {
                eventHandlerManager.removeEventHandler(factory.toEventType(DbOperationType.EDIT_REQUEST), oldValue);
            }
            if (null != newValue) {
                eventHandlerManager.addEventHandler(factory.toEventType(DbOperationType.EDIT_REQUEST), newValue);
            }
        });
    }

    public EventHandler<E> getOnItemActionRequest() {
        return onItemActionRequest.get();
    }

    public void setOnItemActionRequest(EventHandler<E> value) {
        onItemActionRequest.set(value);
    }

    public ObjectProperty<EventHandler<E>> onItemActionRequestProperty() {
        return onItemActionRequest;
    }

    protected void onItemActionRequest(E event) {
        EventHandler<E> listener = onItemActionRequest.get();
        if (null != listener) {
            LOG.fine(() -> String.format("Relaying event %s", event));
            listener.handle(event);
        } else {
            LOG.fine(() -> String.format("Handled event %s, but no listener", event));
        }
    }

    @Override
    public ItemEditTableCell<T, E> call(TableColumn<T, T> param) {
        ItemEditTableCell<T, E> itemEditTableCell = new ItemEditTableCell<>(getFactory());
        itemEditTableCell.setOnItemActionRequest(new WeakEventHandler<>(this::onItemActionRequest));
        return itemEditTableCell;
    }

    protected abstract FxRecordModel.ModelFactory<? extends DataAccessObject, T, E> getFactory();

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.append(eventHandlerManager);
    }

    /**
     * Registers a {@link DbOperationEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
     * {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void addEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    /**
     * Registers a {@link DbOperationEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
     * {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void addEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    /**
     * Unregisters a {@link DbOperationEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
     * {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void removeEventHandler(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    /**
     * Unregisters a {@link DbOperationEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
     * {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void removeEventFilter(EventType<E> type, WeakEventHandler<E> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

}
