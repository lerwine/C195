package scheduler.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.view.event.ItemActionRequestEvent;
import scheduler.view.event.ItemActionRequestEventListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The target item type.
 */
public class ItemEditTableCellFactory<T> implements Callback<TableColumn<T, T>, TableCell<T, T>> {

    private final ObjectProperty<ItemActionRequestEventListener<T>> onItemActionRequest = new SimpleObjectProperty<>();

    public ItemActionRequestEventListener<T> getOnItemActionRequest() {
        return onItemActionRequest.get();
    }

    public void setOnItemActionRequest(ItemActionRequestEventListener<T> value) {
        onItemActionRequest.set(value);
    }

    public ObjectProperty onItemActionRequestProperty() {
        return onItemActionRequest;
    }
    
    final void fireItemActionRequest(T item, ActionEvent fxEvent, boolean isDelete) {
        onItemActionRequest(new ItemActionRequestEvent(fxEvent, item, isDelete));
    }
    
    protected void onItemActionRequest(ItemActionRequestEvent event) {
        ItemActionRequestEventListener<T> listener = onItemActionRequest.get();
        if (null != listener)
            listener.acceptItemActionRequest(event);
    }
    
    @Override
    public TableCell<T, T> call(TableColumn<T, T> param) {
        ItemEditTableCell<T> itemEditTableCell = new ItemEditTableCell<>();
        itemEditTableCell.setOnItemEdit(this::onItemActionRequest);
        return itemEditTableCell;
    }

}
