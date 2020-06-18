package scheduler.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.createSymbolButton;
import scheduler.view.SymbolText;
import scheduler.events.DbOperationType;
import scheduler.events.DbOperationEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The target item type.
 * @param <E> The event type.
 */
public final class ItemEditTableCell<T extends FxRecordModel<? extends DataAccessObject>, E extends DbOperationEvent<T, ? extends DataAccessObject>>
        extends TableCell<T, T> {

    private final FxRecordModel.ModelFactory<? extends DataAccessObject, T, E> factory;
    private final HBox graphic;
    private final ObjectProperty<EventHandler<E>> onItemActionRequest;

    public ItemEditTableCell(FxRecordModel.ModelFactory<? extends DataAccessObject, T, E> factory) {
        onItemActionRequest = new SimpleObjectProperty<>();
        this.factory = factory;
        graphic = NodeUtil.createCompactHBox(createSymbolButton(SymbolText.EDIT, this::onEditButtonAction), createSymbolButton(SymbolText.DELETE, this::onDeleteButtonAction));
        graphic.setSpacing(8);
        graphic.setMaxHeight(USE_PREF_SIZE);
        graphic.setPadding(new Insets(0, 0, 0, 4));
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        onItemActionRequest.addListener((observable, oldValue, newValue) -> {
            if (null != oldValue) {
                removeEventHandler(factory.toEventType(DbOperationType.EDIT_REQUEST), oldValue);
            }
            if (null != newValue) {
                addEventHandler(factory.toEventType(DbOperationType.EDIT_REQUEST), newValue);
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
        T item = getItem();
        if (null != item) {
            fireEvent(factory.createDbOperationEvent(item, event.getSource(), item.dataObject(), DbOperationType.EDIT_REQUEST));
        }
    }

    private void onDeleteButtonAction(ActionEvent event) {
        T item = getItem();
        if (null != item) {
            fireEvent(factory.createDbOperationEvent(item, event.getSource(), item.dataObject(), DbOperationType.DELETE_REQUEST));
        }
    }

    @Override
    protected void updateItem(T item, boolean empty) {
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
