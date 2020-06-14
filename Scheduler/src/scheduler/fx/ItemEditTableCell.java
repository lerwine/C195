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
import scheduler.view.event.ActivityType;
import scheduler.view.event.ModelItemEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The target item type.
 */
public class ItemEditTableCell<T extends FxRecordModel<? extends DataAccessObject>> extends TableCell<T, T> {

    private final FxRecordModel.ModelFactory<? extends DataAccessObject, T, ? extends ModelItemEvent<T, ? extends DataAccessObject>> factory;
    private final HBox graphic;
    private final ObjectProperty<EventHandler<ModelItemEvent<T, ? extends DataAccessObject>>> onItemEdit = new SimpleObjectProperty<>();

    public ItemEditTableCell(FxRecordModel.ModelFactory<? extends DataAccessObject, T, ? extends ModelItemEvent<T, ? extends DataAccessObject>> factory) {
        this.factory = factory;
        graphic = NodeUtil.createCompactHBox(createSymbolButton(SymbolText.EDIT, this::onEditButtonAction), createSymbolButton(SymbolText.DELETE, this::onDeleteButtonAction));
        graphic.setSpacing(8);
        graphic.setMaxHeight(USE_PREF_SIZE);
        graphic.setPadding(new Insets(0, 0, 0, 4));
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    public EventHandler<ModelItemEvent<T, ? extends DataAccessObject>> getOnItemEdit() {
        return onItemEdit.get();
    }

    public void setOnItemEdit(EventHandler<ModelItemEvent<T, ? extends DataAccessObject>> value) {
        onItemEdit.set(value);
    }

    public ObjectProperty<EventHandler<ModelItemEvent<T, ? extends DataAccessObject>>> onItemEditProperty() {
        return onItemEdit;
    }

    private void onEditButtonAction(ActionEvent event) {
        T item = getItem();
        if (null != item) {
            onItemActionRequest(factory.createModelItemEvent(item, event.getSource(), item.dataObject(), ActivityType.EDIT_REQUEST));
        }
    }

    private void onDeleteButtonAction(ActionEvent event) {
        T item = getItem();
        if (null != item) {
            onItemActionRequest(factory.createModelItemEvent(item, event.getSource(), item.dataObject(), ActivityType.DELETE_REQUEST));
        }
    }

    protected void onItemActionRequest(ModelItemEvent<T, ? extends DataAccessObject> event) {
        EventHandler<ModelItemEvent<T, ? extends DataAccessObject>> listener = onItemEdit.get();
        if (null != listener) {
            listener.handle(event);
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
