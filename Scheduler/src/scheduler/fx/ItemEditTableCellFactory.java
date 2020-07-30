package scheduler.fx;

import com.sun.javafx.event.EventHandlerManager;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.DataAccessObject;
import scheduler.events.OperationRequestEvent;
import scheduler.model.fx.EntityModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The underlying {@link DataAccessObject} type.
 * @param <M> The target item type.
 * @param <E> The event type.
 */
public abstract class ItemEditTableCellFactory<D extends DataAccessObject, M extends EntityModel<D>, E extends OperationRequestEvent<D, M>>
        implements Callback<TableColumn<M, M>, TableCell<M, M>>, EventTarget {

//    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ItemEditTableCellFactory.class.getName()), Level.FINE);
    private static final Logger LOG = Logger.getLogger(ItemEditTableCellFactory.class.getName());

    private final EventHandlerManager eventHandlerManager;
    private final ObjectProperty<EventHandler<E>> onItemActionRequest;

    @SuppressWarnings("unchecked")
    public ItemEditTableCellFactory() {
        LOG.entering(getClass().getName(), "<init>");
        eventHandlerManager = new EventHandlerManager(this);
        onItemActionRequest = new SimpleObjectProperty<>(this, "onItemActionRequest");
        onItemActionRequest.addListener((observable, oldValue, newValue) -> {
            EntityModel.EntityModelFactory<D, M> factory = getFactory();
            if (null != oldValue) {
                eventHandlerManager.removeEventHandler((EventType<E>) factory.getBaseRequestEventType(), oldValue);
            }
            if (null != newValue) {
                eventHandlerManager.addEventHandler((EventType<E>) factory.getBaseRequestEventType(), newValue);
            }
        });
        LOG.exiting(getClass().getName(), "<init>");
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

    @Override
    public ItemEditTableCell<D, M, E> call(TableColumn<M, M> param) {
        LOG.entering(LOG.getName(), "call", param);
        ItemEditTableCell<D, M, E> tableCell = new ItemEditTableCell<>(this);
        LOG.exiting(getClass().getName(), "call", tableCell);
        return tableCell;
    }

    public abstract EntityModel.EntityModelFactory<D, M> getFactory();

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        tail = tail.append(eventHandlerManager);
        LOG.exiting(getClass().getName(), "buildEventDispatchChain", tail);
        return tail;
    }

    /**
     * Registers a {@link OperationRequestEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void addEventHandler(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    /**
     * Registers a {@link OperationRequestEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void addEventFilter(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    /**
     * Unregisters a {@link OperationRequestEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void removeEventHandler(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    /**
     * Unregisters a {@link OperationRequestEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
     *
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public void removeEventFilter(EventType<E> type, EventHandler<? super E> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

}
