package scheduler.fx;

import java.lang.ref.WeakReference;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import scheduler.dao.DataAccessObject;
import scheduler.events.ModelEvent;
import scheduler.events.OperationRequestEvent;
import scheduler.model.fx.EntityModel;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.createSymbolButton;
import scheduler.view.SymbolText;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The underlying {@link DataAccessObject} type.
 * @param <M> The target item type.
 * @param <E> The event type.
 */
public class ItemEditTreeTableCell<D extends DataAccessObject, M extends EntityModel<D>, E extends OperationRequestEvent<D, M>>
        extends TreeTableCell<M, M> {

//    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ItemEditTableCell.class.getName()), Level.FINE);
    private static final Logger LOG = Logger.getLogger(ItemEditTreeTableCell.class.getName());

    private final EntityModel.EntityModelFactory<D, M> modelFactory;
    private final HBox graphic;
    private final ObjectProperty<EventHandler<E>> onItemActionRequest;
    private final WeakReference<? extends ItemEditTreeTableCellFactory<D, M, ? extends OperationRequestEvent<D, M>>> cellFactory;

    @SuppressWarnings("unchecked")
    public ItemEditTreeTableCell(ItemEditTreeTableCellFactory<D, M, ? extends ModelEvent<D, M>> factory) {
        LOG.entering(getClass().getName(), "<init>", factory);
        onItemActionRequest = new SimpleObjectProperty<>();
        cellFactory = new WeakReference<>(factory);
        modelFactory = factory.getFactory();
        graphic = NodeUtil.createCompactHBox(createSymbolButton(SymbolText.EDIT, this::onEditButtonAction), createSymbolButton(SymbolText.DELETE, this::onDeleteButtonAction));
        graphic.setSpacing(8);
        graphic.setMaxHeight(USE_PREF_SIZE);
        graphic.setPadding(new Insets(0, 0, 0, 4));
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        onItemActionRequest.addListener((observable, oldValue, newValue) -> {
            if (null != oldValue) {
                removeEventHandler((EventType<E>) modelFactory.getBaseRequestEventType(), oldValue);
            }
            if (null != newValue) {
                addEventHandler((EventType<E>) modelFactory.getBaseRequestEventType(), newValue);
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
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(getClass().getName(), "buildEventDispatchChain", tail);
        ItemEditTreeTableCellFactory<D, M, ? extends OperationRequestEvent<D, M>> factory = cellFactory.get();
        if (null != factory) {
            tail = factory.buildEventDispatchChain(tail);
        }
        tail = super.buildEventDispatchChain(tail);
        LOG.exiting(getClass().getName(), "buildEventDispatchChain", tail);
        return tail;
    }

    private void onEditButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onEditButtonAction", event);
        M item = getItem();
        if (null != item) {
            @SuppressWarnings("unchecked")
            E e = (E) modelFactory.createEditRequestEvent(item, event.getSource());
            LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
            fireEvent(e);
        }
        LOG.exiting(getClass().getName(), "onEditButtonAction");
    }

    private void onDeleteButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onDeleteButtonAction", event);
        M item = getItem();
        if (null != item) {
            @SuppressWarnings("unchecked")
            E e = (E) modelFactory.createDeleteRequestEvent(item, event.getSource());
            LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
            fireEvent(e);
        }
        LOG.exiting(getClass().getName(), "onDeleteButtonAction");
    }

    @Override
    protected void updateItem(M item, boolean empty) {
        LOG.entering(getClass().getName(), "updateItem", new Object[]{item, empty});
        super.updateItem(item, empty);
        if (empty || null == item) {
            LOG.finer("Initializing empty cell");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setText("");
        } else {
            LOG.finer("Setting cell graphic");
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            if (graphic != getGraphic()) {
                setGraphic(graphic);
            }
        }
        LOG.exiting(getClass().getName(), "updateIndex");
    }

}
