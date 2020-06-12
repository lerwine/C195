package scheduler.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.event.ActivityType;
import scheduler.view.event.ModelItemEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The target item type.
 */
public abstract class ItemEditTableCellFactory<T extends FxRecordModel<? extends DataAccessObject>> implements Callback<TableColumn<T, T>, TableCell<T, T>> {

    private final ObjectProperty<EventHandler<ModelItemEvent<T, ? extends DataAccessObject>>> onItemActionRequest = new SimpleObjectProperty<>();

    public EventHandler<ModelItemEvent<T, ? extends DataAccessObject>> getOnItemActionRequest() {
        return onItemActionRequest.get();
    }

    public void setOnItemActionRequest(EventHandler<ModelItemEvent<T, ? extends DataAccessObject>> value) {
        onItemActionRequest.set(value);
    }

    public ObjectProperty<EventHandler<ModelItemEvent<T, ? extends DataAccessObject>>> onItemActionRequestProperty() {
        return onItemActionRequest;
    }

    final void fireItemActionRequest(T item, ActionEvent fxEvent, boolean isDelete) {
        onItemActionRequest((isDelete) ? getFactory().createModelItemEvent(item, fxEvent.getSource(), item.dataObject(), ActivityType.DELETE_REQUEST)
                : getFactory().createModelItemEvent(item, fxEvent.getSource(), item.dataObject(), ActivityType.EDIT_REQUEST));
    }

    protected void onItemActionRequest(ModelItemEvent<T, ? extends DataAccessObject> event) {
        EventHandler<ModelItemEvent<T, ? extends DataAccessObject>> listener = onItemActionRequest.get();
        if (null != listener) {
            listener.handle(event);
        }
    }

    @Override
    public TableCell<T, T> call(TableColumn<T, T> param) {
        ItemEditTableCell<T> itemEditTableCell = new ItemEditTableCell<>(getFactory());
        itemEditTableCell.setOnItemEdit(this::onItemActionRequest);
        return itemEditTableCell;
    }

    protected abstract FxRecordModel.ModelFactory<? extends DataAccessObject, T> getFactory();

}
