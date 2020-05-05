package scheduler.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.view.event.ItemActionRequestEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The target item type.
 */
public class ItemEditTableCellFactory<T> implements Callback<TableColumn<T, T>, TableCell<T, T>> {

    private final ObjectProperty<EventHandler<ItemActionRequestEvent<T>>> onItemActionRequest = new SimpleObjectProperty<>();

    public EventHandler<ItemActionRequestEvent<T>> getOnItemActionRequest() {
        return onItemActionRequest.get();
    }

    public void setOnItemActionRequest(EventHandler<ItemActionRequestEvent<T>> value) {
        onItemActionRequest.set(value);
    }

    public ObjectProperty onItemActionRequestProperty() {
        return onItemActionRequest;
    }

    final void fireItemActionRequest(T item, ActionEvent fxEvent, boolean isDelete) {
        onItemActionRequest(new ItemActionRequestEvent<>(fxEvent, item, isDelete));
    }

    protected void onItemActionRequest(ItemActionRequestEvent<T> event) {
        EventHandler<ItemActionRequestEvent<T>> listener = onItemActionRequest.get();
        if (null != listener) {
            listener.handle(event);
        }
    }

    @Override
    public TableCell<T, T> call(TableColumn<T, T> param) {
        ItemEditTableCell<T> itemEditTableCell = new ItemEditTableCell<>();
        itemEditTableCell.setOnItemEdit(this::onItemActionRequest);
        return itemEditTableCell;
    }

}
