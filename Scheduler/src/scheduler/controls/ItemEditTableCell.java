package scheduler.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import scheduler.util.NodeUtil;
import scheduler.util.NodeUtil.SymbolButtonValue;
import static scheduler.util.NodeUtil.createSymbolButton;
import scheduler.view.event.ItemActionRequestEvent;
import scheduler.view.event.ItemActionRequestEventListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The target item type.
 */
public class ItemEditTableCell<T> extends TableCell<T, T> {
    private final HBox graphic;
    private final ObjectProperty<ItemActionRequestEventListener<T>> onItemEdit = new SimpleObjectProperty<>();

    public ItemActionRequestEventListener<T> getOnItemEdit() {
        return onItemEdit.get();
    }

    public void setOnItemEdit(ItemActionRequestEventListener<T> value) {
        onItemEdit.set(value);
    }

    public ObjectProperty onItemEditProperty() {
        return onItemEdit;
    }
    
    private void onEditButtonAction(ActionEvent event) {
        onItemActionRequest(new ItemActionRequestEvent(event, getItem(), false));
    }
    
    private void onDeleteButtonAction(ActionEvent event) {
        onItemActionRequest(new ItemActionRequestEvent(event, getItem(), true));
    }
    
    protected void onItemActionRequest(ItemActionRequestEvent event) {
        ItemActionRequestEventListener<T> listener = onItemEdit.get();
        if (null != listener)
            listener.acceptItemActionRequest(event);
    }
    
    public ItemEditTableCell() {
        graphic = NodeUtil.createCompactHBox(createSymbolButton(SymbolButtonValue.EDIT, this::onEditButtonAction), createSymbolButton(SymbolButtonValue.DELETE, this::onDeleteButtonAction));
        graphic.setSpacing(8);
        graphic.setMaxHeight(USE_PREF_SIZE);
        graphic.setPadding(new Insets(0, 0, 0, 4));
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }
    
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || null == item) {
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setText("");
        } else {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            if (graphic != getGraphic())
                setGraphic(graphic);
        }
    }

}
