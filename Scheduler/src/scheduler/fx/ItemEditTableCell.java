package scheduler.fx;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import scheduler.dao.DataAccessObject;
import scheduler.events.OperationRequestEvent;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.LogHelper;
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
public final class ItemEditTableCell<D extends DataAccessObject, M extends FxRecordModel<D>, E extends OperationRequestEvent<D, M>>
        extends TableCell<M, M> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ItemEditTableCell.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ItemEditTableCell.class.getName());

    private final FxRecordModel.FxModelFactory<D, M> factory;
    private final HBox graphic;
    private final ObjectProperty<EventHandler<E>> onItemActionRequest;

    @SuppressWarnings("unchecked")
    public ItemEditTableCell(FxRecordModel.FxModelFactory<D, M> factory) {
        onItemActionRequest = new SimpleObjectProperty<>();
        this.factory = factory;
        graphic = NodeUtil.createCompactHBox(createSymbolButton(SymbolText.EDIT, this::onEditButtonAction), createSymbolButton(SymbolText.DELETE, this::onDeleteButtonAction));
        graphic.setSpacing(8);
        graphic.setMaxHeight(USE_PREF_SIZE);
        graphic.setPadding(new Insets(0, 0, 0, 4));
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        onItemActionRequest.addListener((observable, oldValue, newValue) -> {
            if (null != oldValue) {
                removeEventHandler((EventType<E>) factory.getBaseRequestEventType(), oldValue);
            }
            if (null != newValue) {
                addEventHandler((EventType<E>) factory.getBaseRequestEventType(), newValue);
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

    private void onEditButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onEditButtonAction", event);
        M item = getItem();
        if (null != item) {
            @SuppressWarnings("unchecked")
            E e = (E) factory.createEditRequestEvent(item, event.getSource());
            LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
            fireEvent(e);
        }
    }

    private void onDeleteButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDeleteButtonAction", event);
        M item = getItem();
        if (null != item) {
            @SuppressWarnings("unchecked")
            E e = (E) factory.createDeleteRequestEvent(item, event.getSource());
            LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
            fireEvent(e);
        }
    }

    @Override
    protected void updateItem(M item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || null == item) {
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setText("");
        } else {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            if (graphic != getGraphic()) {
                setGraphic(graphic);
            }
        }
    }

}
